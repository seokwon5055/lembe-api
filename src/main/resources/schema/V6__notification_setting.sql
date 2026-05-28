-- ============================================================
-- Lembe DB Schema V6 — 알림 설정 (tb_notification_setting)
-- tb_device_token, tb_notification 은 V1에 이미 존재
-- ============================================================

CREATE TABLE IF NOT EXISTS tb_notification_setting (
    setting_seq         BIGINT      NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT      NOT NULL,
    milestone_ready     TINYINT(1)  NOT NULL DEFAULT 1,
    milestone_unlocked  TINYINT(1)  NOT NULL DEFAULT 1,
    roulette_ready      TINYINT(1)  NOT NULL DEFAULT 1,
    streak_warning      TINYINT(1)  NOT NULL DEFAULT 1,
    weight_reminder     TINYINT(1)  NOT NULL DEFAULT 1,
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (setting_seq),
    UNIQUE KEY uq_ns_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
