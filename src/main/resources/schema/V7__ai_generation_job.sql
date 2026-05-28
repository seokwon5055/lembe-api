-- ============================================================
-- Lembe DB Schema V7 — AI generation job 확장
-- tb_ai_generation: 컬럼 추가 / 리네임 / status 값 변경
-- ============================================================

-- source_photo_seq → input_photo_seq (nullable: 초기 생성 시 입력 사진 없음)
ALTER TABLE tb_ai_generation
    CHANGE COLUMN source_photo_seq input_photo_seq BIGINT NULL,
    MODIFY COLUMN milestone_seq    BIGINT          NULL,
    ADD COLUMN job_type       VARCHAR(20)    NOT NULL DEFAULT 'INITIAL' AFTER user_seq,
    ADD COLUMN fal_request_id VARCHAR(255)   NULL                       AFTER status,
    ADD COLUMN prompt         TEXT           NULL                       AFTER fal_request_id,
    ADD COLUMN cost_usd       DECIMAL(8,4)   NOT NULL DEFAULT 0.0000    AFTER point_used,
    ADD COLUMN retry_count    INT            NOT NULL DEFAULT 0         AFTER cost_usd;

-- status 값 DONE → COMPLETED 통일
UPDATE tb_ai_generation SET status = 'COMPLETED' WHERE status = 'DONE';

-- 일일 비용 조회 인덱스
ALTER TABLE tb_ai_generation
    ADD KEY idx_aig_requested_at (requested_at);
