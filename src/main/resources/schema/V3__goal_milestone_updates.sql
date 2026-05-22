-- ============================================================
-- Lembe DB Schema V3 — Goal mode/status, Milestone status/photo
-- ============================================================

-- tb_goal: mode, status 추가 / body composition 컬럼 명확화 / is_active 제거
ALTER TABLE tb_goal
    ADD COLUMN mode   VARCHAR(10)  NOT NULL DEFAULT 'SIMPLE' AFTER user_seq,
    ADD COLUMN status VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' AFTER mode,
    CHANGE COLUMN body_fat_pct   start_body_fat_pct   DECIMAL(4,1),
    CHANGE COLUMN muscle_mass_kg start_muscle_mass_kg DECIMAL(5,2),
    ADD COLUMN target_body_fat_pct   DECIMAL(4,1)  AFTER start_body_fat_pct,
    ADD COLUMN target_muscle_mass_kg DECIMAL(5,2)  AFTER start_muscle_mass_kg;

-- 기존 is_active → status 값 마이그레이션
UPDATE tb_goal SET status = CASE WHEN is_active = 1 THEN 'ACTIVE' ELSE 'ABANDONED' END;

ALTER TABLE tb_goal DROP COLUMN is_active;

-- tb_milestone: status, photo_seq 추가 / is_unlocked 제거
ALTER TABLE tb_milestone
    ADD COLUMN status    VARCHAR(20) NOT NULL DEFAULT 'LOCKED' AFTER sort_order,
    ADD COLUMN photo_seq BIGINT      NULL     AFTER unlocked_at;

-- 기존 is_unlocked → status 값 마이그레이션
UPDATE tb_milestone SET status = CASE WHEN is_unlocked = 1 THEN 'UNLOCKED' ELSE 'LOCKED' END;

ALTER TABLE tb_milestone DROP COLUMN is_unlocked;
