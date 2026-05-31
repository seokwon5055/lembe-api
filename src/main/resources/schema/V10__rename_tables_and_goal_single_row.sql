-- ============================================================
-- Lembe DB Schema V10
-- 1. 모든 테이블에서 tb_ 접두사 제거
-- 2. user 테이블 트리거 재생성 (테이블명 변경 반영)
-- 3. goal 테이블 회원당 1행 구조로 변경 (UNIQUE + ABANDONED 정리)
-- ============================================================

-- 1. 테이블 전체 이름 변경
RENAME TABLE
    tb_user                 TO `user`,
    tb_refresh_token        TO refresh_token,
    tb_goal                 TO goal,
    tb_milestone            TO milestone,
    tb_photo                TO photo,
    tb_ai_generation        TO ai_generation,
    tb_weight_log           TO weight_log,
    tb_point_log            TO point_log,
    tb_roulette_log         TO roulette_log,
    tb_purchase             TO purchase,
    tb_notification         TO notification,
    tb_device_token         TO device_token,
    tb_mission              TO mission,
    tb_social_account       TO social_account,
    tb_user_profile         TO user_profile,
    tb_point_wallet         TO point_wallet,
    tb_friend               TO friend,
    tb_notification_setting TO notification_setting;

-- 2. ucode 트리거: 테이블명 `user`로 재생성
DROP TRIGGER IF EXISTS trg_user_ucode;

CREATE TRIGGER trg_user_ucode
BEFORE INSERT ON `user`
FOR EACH ROW
BEGIN
    DECLARE next_seq BIGINT;
    SELECT AUTO_INCREMENT INTO next_seq
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'user';
    SET NEW.ucode = CONCAT('U', LPAD(next_seq, 7, '0'));
END;

-- 3. goal: ABANDONED 행 삭제 후 UNIQUE 제약 추가
DELETE FROM milestone
WHERE goal_seq IN (
    SELECT goal_seq FROM goal WHERE status = 'ABANDONED'
);
DELETE FROM goal WHERE status = 'ABANDONED';

ALTER TABLE goal
    ADD UNIQUE KEY uq_goal_user (user_seq);
