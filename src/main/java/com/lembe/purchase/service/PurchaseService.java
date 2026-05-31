package com.lembe.purchase.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.point.domain.PointLog;
import com.lembe.point.mapper.PointLogMapper;
import com.lembe.point.service.PointService;
import com.lembe.purchase.domain.ProductType;
import com.lembe.purchase.domain.Purchase;
import com.lembe.purchase.dto.PurchaseHistoryResponse;
import com.lembe.purchase.dto.PurchaseRequest;
import com.lembe.purchase.dto.PurchaseResponse;
import com.lembe.purchase.dto.RefundWebhookRequest;
import com.lembe.purchase.dto.VerificationResult;
import com.lembe.purchase.mapper.PurchaseMapper;
import com.lembe.user.mapper.PointWalletMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private static final Set<String> VALID_PLATFORMS = Set.of("IOS", "ANDROID");

    private final PurchaseMapper purchaseMapper;
    private final PointService pointService;
    private final PointWalletMapper pointWalletMapper;
    private final PointLogMapper pointLogMapper;
    private final List<ReceiptVerifier> verifiers;

    // ── 결제 처리 ─────────────────────────────────────────────────────────────

    /**
     * 영수증 검증 + 포인트 적립 (원자적 처리)
     *
     * 흐름: 중복 확인 → 상품 조회 → PENDING 기록 → 영수증 검증 → 포인트 적립 → VERIFIED
     *
     * Note: 실제 연동 시 영수증 검증(외부 HTTP)을 트랜잭션 밖으로 빼는 것을 권장.
     * 현재 mock은 즉시 반환하므로 단일 @Transactional로 처리.
     */
    @Transactional
    public PurchaseResponse purchase(String ucode, PurchaseRequest request) {
        String platform = request.platform().toUpperCase();
        if (!VALID_PLATFORMS.contains(platform)) {
            throw new LembeException(ErrorCode.INVALID_INPUT,
                    "플랫폼은 IOS 또는 ANDROID 여야 합니다.");
        }

        // 1. 상품 조회 (유효하지 않은 productId면 예외)
        ProductType product = ProductType.findByProductId(request.productId());

        // 2. 중복 transaction_id 사전 확인
        if (purchaseMapper.existsByPlatformAndTransactionId(platform, request.transactionId())) {
            throw new LembeException(ErrorCode.DUPLICATE_RECEIPT);
        }

        // 3. PENDING 상태로 구매 기록 (DB unique key가 race condition 최종 방어)
        Purchase purchase = Purchase.builder()
                .ucode(ucode)
                .platform(platform)
                .productId(request.productId())
                .transactionId(request.transactionId())
                .receipt(request.receipt())
                .pointsGranted(product.getPoints())
                .bonusPoints(product.getBonusPoints())
                .amountKrw(product.getAmountKrw())
                .status("PENDING")
                .build();

        try {
            purchaseMapper.insert(purchase);
        } catch (DataIntegrityViolationException e) {
            // unique key 충돌: 동시 요청으로 이미 처리됨
            throw new LembeException(ErrorCode.DUPLICATE_RECEIPT);
        }

        // 4. 영수증 검증 (mock: 즉시 성공 반환)
        VerificationResult result = resolveVerifier(platform)
                .verify(request.productId(), request.transactionId(), request.receipt());

        if (!result.valid()) {
            purchaseMapper.updateFailed(purchase.getPurchaseSeq());
            throw new LembeException(ErrorCode.RECEIPT_VERIFICATION_FAILED, result.errorMessage());
        }

        // 5. 포인트 적립
        int totalPoints = product.getTotalPoints();
        int balanceAfter = pointService.add(
                ucode, totalPoints, "PURCHASE",
                String.valueOf(purchase.getPurchaseSeq()));

        // 6. VERIFIED 처리
        purchaseMapper.updateVerified(purchase.getPurchaseSeq());

        log.info("[Purchase] ucode={} product={} points={}+{} balance={}",
                ucode, product.getProductId(),
                product.getPoints(), product.getBonusPoints(), balanceAfter);

        return PurchaseResponse.from(
                purchaseMapper.findById(purchase.getPurchaseSeq()), product, balanceAfter);
    }

    // ── 구매 내역 ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PurchaseHistoryResponse getHistory(String ucode, int page, int size) {
        int offset = (page - 1) * size;
        List<PurchaseHistoryResponse.Item> items = purchaseMapper
                .findByUcode(ucode, offset, size)
                .stream().map(PurchaseHistoryResponse.Item::from).toList();
        int total = purchaseMapper.countByUserSeq(ucode);
        return new PurchaseHistoryResponse(items, total, page, size);
    }

    // ── 환불 웹훅 ─────────────────────────────────────────────────────────────

    /**
     * 애플/구글 환불 통지 처리
     *
     * - 멱등 처리: 이미 REFUNDED면 무시
     * - 잔액 부족해도 마이너스 허용 (환불 정책)
     *
     * TODO: 실제 연동 시 웹훅 서명 검증 필수
     *   - 애플: JWS 서명 검증 (App Store Server Notifications v2)
     *   - 구글: RTDN 메시지 서명 검증 (Google Cloud Pub/Sub)
     */
    @Transactional
    public void processRefund(RefundWebhookRequest request) {
        String platform = request.platform().toUpperCase();
        Purchase purchase = purchaseMapper.findByPlatformAndTransactionId(
                platform, request.transactionId());

        if (purchase == null) {
            log.warn("[Refund] unknown transactionId: platform={} txId={}",
                    platform, request.transactionId());
            return;
        }
        if ("REFUNDED".equals(purchase.getStatus())) {
            log.info("[Refund] already refunded: purchaseSeq={}", purchase.getPurchaseSeq());
            return;
        }

        // 포인트 차감 (마이너스 허용 — 잔액이 부족해도 차감)
        int totalPoints = purchase.getPointsGranted() + purchase.getBonusPoints();
        deductForRefund(purchase.getUcode(), totalPoints, purchase.getPurchaseSeq());

        purchaseMapper.updateRefunded(purchase.getPurchaseSeq(), request.reason());

        log.info("[Refund] processed: purchaseSeq={} ucode={} points=-{}",
                purchase.getPurchaseSeq(), purchase.getUcode(), totalPoints);
    }

    // ── internal helpers ───────────────────────────────────────────────────────

    private ReceiptVerifier resolveVerifier(String platform) {
        return verifiers.stream()
                .filter(v -> v.platform().equals(platform))
                .findFirst()
                .orElseThrow(() -> new LembeException(ErrorCode.INVALID_INPUT,
                        "지원하지 않는 플랫폼: " + platform));
    }

    /**
     * 환불 전용 포인트 차감 — 잔액 부족해도 마이너스 허용
     * (일반 deduct와 달리 잔액 >= 차감액 조건 없음)
     */
    private void deductForRefund(String ucode, int amount, Long purchaseSeq) {
        pointWalletMapper.updateBalanceForRefund(ucode, -amount);
        int balanceAfter = 0;
        var wallet = pointWalletMapper.findByUcode(ucode);
        if (wallet != null) balanceAfter = wallet.getBalance();

        pointLogMapper.insert(PointLog.builder()
                .ucode(ucode)
                .delta(-amount)
                .balanceAfter(balanceAfter)
                .reason("PURCHASE_REFUND")
                .refId(String.valueOf(purchaseSeq))
                .build());
    }
}
