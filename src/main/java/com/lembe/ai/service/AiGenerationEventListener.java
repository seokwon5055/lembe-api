package com.lembe.ai.service;

import com.lembe.ai.event.GoalCreatedEvent;
import com.lembe.ai.event.MilestoneUnlockedEvent;
import com.lembe.ai.event.ProgressPhotoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @TransactionalEventListener(AFTER_COMMIT): 발행 트랜잭션 커밋 후 실행
 * @Async("aiExecutor"): 별도 스레드풀에서 비동기 처리
 * → 조합으로 "DB 커밋 완료 후 비동기 AI 생성" 보장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiGenerationEventListener {

    private final AiGenerationService aiGenerationService;

    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGoalCreated(GoalCreatedEvent event) {
        log.info("[AI Event] onGoalCreated: userSeq={} goalSeq={}", event.userSeq(), event.goalSeq());
        try {
            aiGenerationService.generateInitial(event.userSeq(), event.goalSeq());
        } catch (Exception e) {
            log.error("[AI Event] generateInitial failed: userSeq={}", event.userSeq(), e);
        }
    }

    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMilestoneUnlocked(MilestoneUnlockedEvent event) {
        log.info("[AI Event] onMilestoneUnlocked: milestoneSeq={}", event.milestoneSeq());
        try {
            aiGenerationService.generateNext(event.milestoneSeq());
        } catch (Exception e) {
            log.error("[AI Event] generateNext failed: milestoneSeq={}", event.milestoneSeq(), e);
        }
    }

    @Async("aiExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProgressPhotoUploaded(ProgressPhotoUploadedEvent event) {
        log.info("[AI Event] onProgressPhoto: milestoneSeq={} photoSeq={}",
                event.milestoneSeq(), event.photoSeq());
        try {
            aiGenerationService.adaptiveUpdate(event.userSeq(), event.milestoneSeq(), event.photoSeq());
        } catch (Exception e) {
            log.error("[AI Event] adaptiveUpdate failed: milestoneSeq={}", event.milestoneSeq(), e);
        }
    }
}
