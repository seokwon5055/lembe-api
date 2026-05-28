# Lembe Backend — CLAUDE.md

## 프로젝트 개요
체중 감량 목표 추적 앱 Lembe의 Spring Boot 백엔드.
사용자는 목표 체중을 설정하고, 마일스톤(-3kg 단위 등)을 달성하면 AI 변신 사진을 포인트로 잠금 해제한다.

## 기술 스택
- Java 17, Spring Boot 3.x
- MyBatis (XML 매퍼)
- MySQL 8.x, Flyway 마이그레이션 (`src/main/resources/schema/`)
- Redis (토큰 블랙리스트)
- JWT 인증 (Access + Refresh), Kakao 소셜 로그인
- 파일 스토리지: 로컬(`LocalStorageService`) → 추후 S3 전환 예정

## 아키텍처
```
controller → service → mapper(MyBatis XML) → DB
```
- 도메인 패키지: `auth`, `goal`, `photo`, `point`, `user`, `weight`
- 공통: `common/{config,dto,exception,jwt}`
- 모든 API 응답: `ApiResponse<T>` 래퍼
- 에러: `LembeException(ErrorCode)` → `GlobalExceptionHandler`

## DB 마이그레이션 규칙
- Flyway V순번 파일 (`V1__`, `V2__`, ...) — 순서 엄수
- 현재 최신: **V6** (`V6__notification_setting.sql`)
- 다음 마이그레이션: `V7__...`

---

## 도메인 정책

### 사진 (Photo)

#### photo_type ENUM
| 값 | 설명 |
|---|---|
| `CURRENT` | 사용자가 직접 올린 현재 사진 (얼굴/전신 구분 없음, 1장) |
| `AI_GENERATED` | AI가 생성한 변신 예측 사진 |
| `PROGRESS` | 경과 사진 |

- 구 `FACE`, `BODY` 타입은 V5 마이그레이션으로 `CURRENT` 통합 완료
- 업로드 시 자르기(Crop) 없음
- 갤러리 뷰: 프론트에서 `weight_kg` 기준 그룹핑 (백엔드는 `weight_kg` 포함 응답)

#### AI 사진 생성 (점진 + 잠금)
- 가입 시: 최종 목표 AI 사진 + 첫 번째 중간 마일스톤(-3kg) AI 사진 생성
  - 중간 사진은 `status = LOCKED` 상태로 생성
- 포인트로 잠금 해제 → `UNLOCKED` + 다음 마일스톤 AI 사진 자동 생성
- **현재 AI 미연동**: `photo_type = AI_GENERATED` 레코드 생성하지 않음, status 관리만
- 추후 fal.ai 연동 시 `MilestoneService.unlock()` 내 TODO 지점에 생성 로직 추가

---

### 마일스톤 (Milestone)

#### status 흐름
```
LOCKED → READY_TO_UNLOCK → UNLOCKED
```
| 상태 | 전이 조건 |
|---|---|
| `LOCKED` | 초기 상태 |
| `READY_TO_UNLOCK` | 체중 기록 시 `target_weight` 도달 확인 (`WeightService`) |
| `UNLOCKED` | `POST /api/v1/milestones/{id}/unlock` — 15P 차감 |

#### 마일스톤 열기 API
- `POST /api/v1/milestones/{milestoneSeq}/unlock`
- 비용: **15P** (상수 `MilestoneService.UNLOCK_COST`)
- `@Transactional`: 포인트 차감 + 상태 변경 원자적 처리
- 실패 조건: 포인트 부족(`INSUFFICIENT_POINTS`), 미도달(`MILESTONE_NOT_READY`), 이미 열림(`MILESTONE_ALREADY_UNLOCKED`)

---

### 포인트 (Point)
- 적립: 체중 기록 +5P, 룰렛 등
- 차감: 마일스톤 열기 -15P, 기타
- `PointService.deduct()` / `PointService.add()` — 내부적으로 `SELECT FOR UPDATE` 사용

---

### 목표 (Goal)
- `mode`: `SIMPLE` | `DETAIL`
- `status`: `ACTIVE` | `ABANDONED` | `COMPLETED`
- 목표 생성 시 마일스톤 자동 계산 (`MilestoneCalculator`) — -3kg 단위 분할
- 목표 수정 시 `LOCKED` 마일스톤만 삭제·재계산 (`READY_TO_UNLOCK`, `UNLOCKED` 보존)

---

## API 엔드포인트 요약

| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/goals` | 목표 생성 |
| GET | `/api/v1/goals/current` | 현재 목표 조회 |
| PUT | `/api/v1/goals/{goalSeq}` | 목표 수정 |
| GET | `/api/v1/milestones` | 마일스톤 목록 |
| POST | `/api/v1/milestones/{milestoneSeq}/unlock` | 마일스톤 잠금 해제 (15P) |
| POST | `/api/v1/photos` | 사진 업로드 (multipart) |
| GET | `/api/v1/photos` | 사진 목록 (photoType 필터) |
| PUT | `/api/v1/photos/{photoSeq}/main` | 대표 사진 지정 |
| DELETE | `/api/v1/photos/{photoSeq}` | 사진 삭제 |
| POST | `/api/v1/weight` | 체중 기록 |
| GET | `/api/v1/weight/history` | 체중 히스토리 |
| GET | `/api/v1/points/wallet` | 포인트 잔액 |
| POST | `/api/v1/points/roulette` | 룰렛 스핀 |

---

## 푸시 알림 시스템 (notification 패키지)

### 구조
```
notification/
  controller/  DeviceController, NotificationController
  domain/      DeviceToken, NotificationSetting, NotificationType(enum)
  dto/         DeviceTokenRequest, NotificationSettingResponse, UpdateNotificationSettingRequest
  mapper/      DeviceTokenMapper, NotificationSettingMapper, SchedulerQueryMapper
  service/     FcmService(mock), NotificationService(@Async), NotificationScheduler(@Scheduled)
               DeviceTokenService, NotificationSettingService
```

### FCM 연동 상태
- 현재 **mock** — `FcmService.send()` 는 `log.info`만 출력
- `application.yml`: `fcm.server-key: ${FCM_SERVER_KEY:mock}`
- 실제 연동 절차는 `FcmService.java` TODO 주석 참조

### 알림 타입 (NotificationType enum)
| 타입 | 트리거 | 발송 방식 |
|---|---|---|
| `MILESTONE_READY` | 체중 기록 시 마일스톤 도달 | 이벤트 (@Async) |
| `MILESTONE_UNLOCKED` | 마일스톤 unlock API | 이벤트 (@Async) |
| `ROULETTE_READY` | 매일 10:00 @Scheduled | 스케줄러 |
| `STREAK_WARNING` | 매일 21:00 @Scheduled | 스케줄러 |
| `WEIGHT_REMINDER` | 매일 20:00 @Scheduled | 스케줄러 |

### 비동기 처리
- `AsyncConfig`: `notificationExecutor` 스레드풀 (core=2, max=10)
- `@EnableAsync` / `@EnableScheduling`: `AsyncConfig.java`

### API
| Method | Path | 설명 |
|---|---|---|
| POST | `/api/v1/devices/token` | FCM 토큰 등록/갱신 |
| GET | `/api/v1/notifications/settings` | 알림 설정 조회 |
| PUT | `/api/v1/notifications/settings` | 알림 설정 변경 |

---

## 코딩 컨벤션
- 서비스 메서드: `@Transactional` 명시 (읽기 전용은 `readOnly = true`)
- MyBatis: 동적 쿼리는 XML, 단순 쿼리도 XML 통일
- DTO: Java record 사용
- 예외: `LembeException(ErrorCode)` — 새 에러 코드 추가 시 `ErrorCode.java`에 번호 순 추가
