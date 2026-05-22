-- ============================================================
-- Lembe DB Schema V1
-- charset: utf8mb4, engine: InnoDB
-- ============================================================

CREATE TABLE IF NOT EXISTS tb_user (
    user_seq        BIGINT          NOT NULL AUTO_INCREMENT,
    email           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    nickname        VARCHAR(50)     NOT NULL,
    profile_img_url VARCHAR(512),
    provider        VARCHAR(20)     NOT NULL DEFAULT 'LOCAL',  -- LOCAL | KAKAO | APPLE | GOOGLE
    provider_id     VARCHAR(255),
    streak_count    INT             NOT NULL DEFAULT 0,
    last_streak_dt  DATE,
    point_balance   INT             NOT NULL DEFAULT 0,
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_seq),
    UNIQUE KEY uq_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_refresh_token (
    token_seq       BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq        BIGINT          NOT NULL,
    token           VARCHAR(512)    NOT NULL,
    device_info     VARCHAR(255),
    expires_at      DATETIME        NOT NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (token_seq),
    UNIQUE KEY uq_refresh_token (token),
    KEY idx_rt_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_goal (
    goal_seq            BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    start_weight        DECIMAL(5,2)    NOT NULL,
    target_weight       DECIMAL(5,2)    NOT NULL,
    body_fat_pct        DECIMAL(4,1),
    muscle_mass_kg      DECIMAL(5,2),
    is_active           TINYINT(1)      NOT NULL DEFAULT 1,
    ai_regen_cost       INT             NOT NULL DEFAULT 30,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (goal_seq),
    KEY idx_goal_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_milestone (
    milestone_seq       BIGINT          NOT NULL AUTO_INCREMENT,
    goal_seq            BIGINT          NOT NULL,
    user_seq            BIGINT          NOT NULL,
    label               VARCHAR(20)     NOT NULL,   -- e.g. '-3kg'
    target_weight       DECIMAL(5,2)    NOT NULL,
    loss_kg             DECIMAL(5,2)    NOT NULL,
    is_final            TINYINT(1)      NOT NULL DEFAULT 0,
    is_unlocked         TINYINT(1)      NOT NULL DEFAULT 0,
    unlocked_at         DATETIME,
    sort_order          INT             NOT NULL DEFAULT 0,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (milestone_seq),
    KEY idx_ms_goal_seq (goal_seq),
    KEY idx_ms_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_photo (
    photo_seq           BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    milestone_seq       BIGINT,
    category            VARCHAR(20)     NOT NULL,   -- FACE | BODY | PROGRESS | AI
    storage_key         VARCHAR(512)    NOT NULL,
    cdn_url             VARCHAR(512)    NOT NULL,
    weight_kg           DECIMAL(5,2),
    is_main             TINYINT(1)      NOT NULL DEFAULT 0,
    is_deleted          TINYINT(1)      NOT NULL DEFAULT 0,
    taken_at            DATETIME,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_seq),
    KEY idx_photo_user_seq (user_seq),
    KEY idx_photo_milestone_seq (milestone_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_ai_generation (
    gen_seq             BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    milestone_seq       BIGINT          NOT NULL,
    source_photo_seq    BIGINT          NOT NULL,
    result_photo_seq    BIGINT,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',  -- PENDING | PROCESSING | DONE | FAILED
    point_used          INT             NOT NULL DEFAULT 0,
    is_free_retry       TINYINT(1)      NOT NULL DEFAULT 0,
    error_msg           VARCHAR(500),
    requested_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        DATETIME,
    PRIMARY KEY (gen_seq),
    KEY idx_aig_user_seq (user_seq),
    KEY idx_aig_milestone_seq (milestone_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_weight_log (
    log_seq             BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    weight_kg           DECIMAL(5,2)    NOT NULL,
    body_fat_pct        DECIMAL(4,1),
    muscle_mass_kg      DECIMAL(5,2),
    source              VARCHAR(20)     NOT NULL DEFAULT 'MANUAL',  -- MANUAL | HEALTHKIT | HEALTH_CONNECT
    logged_at           DATE            NOT NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_seq),
    KEY idx_wl_user_date (user_seq, logged_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_point_log (
    point_log_seq       BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    delta               INT             NOT NULL,       -- positive = earn, negative = spend
    balance_after       INT             NOT NULL,
    reason              VARCHAR(50)     NOT NULL,       -- ROULETTE | PURCHASE | AI_GEN | REFUND | etc.
    ref_id              VARCHAR(100),                   -- external order ID or internal ref
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (point_log_seq),
    KEY idx_pl_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_roulette_log (
    roulette_log_seq    BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    spin_type           VARCHAR(20)     NOT NULL DEFAULT 'FREE',  -- FREE | AD_BONUS
    points_won          INT             NOT NULL DEFAULT 0,
    spun_at             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (roulette_log_seq),
    KEY idx_rl_user_spun (user_seq, spun_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_purchase (
    purchase_seq        BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    platform            VARCHAR(10)     NOT NULL,   -- IOS | ANDROID
    product_id          VARCHAR(100)    NOT NULL,
    transaction_id      VARCHAR(255)    NOT NULL,
    receipt             TEXT,
    points_granted      INT             NOT NULL,
    amount_krw          INT             NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',  -- PENDING | VERIFIED | FAILED
    verified_at         DATETIME,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (purchase_seq),
    UNIQUE KEY uq_purchase_tx (platform, transaction_id),
    KEY idx_pur_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_notification (
    notif_seq           BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    type                VARCHAR(30)     NOT NULL,   -- MILESTONE | STREAK | ROULETTE | WEEKLY | NEAR_GOAL
    title               VARCHAR(255)    NOT NULL,
    body                VARCHAR(500)    NOT NULL,
    is_read             TINYINT(1)      NOT NULL DEFAULT 0,
    sent_at             DATETIME,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notif_seq),
    KEY idx_notif_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_device_token (
    device_token_seq    BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    fcm_token           VARCHAR(512)    NOT NULL,
    platform            VARCHAR(10)     NOT NULL,   -- IOS | ANDROID
    is_active           TINYINT(1)      NOT NULL DEFAULT 1,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (device_token_seq),
    UNIQUE KEY uq_fcm_token (fcm_token),
    KEY idx_dt_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS tb_mission (
    mission_seq         BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    mission_type        VARCHAR(30)     NOT NULL,   -- WEIGHT_LOG | PHOTO_UPLOAD | ROULETTE | etc.
    mission_date        DATE            NOT NULL,
    is_completed        TINYINT(1)      NOT NULL DEFAULT 0,
    completed_at        DATETIME,
    points_rewarded     INT             NOT NULL DEFAULT 0,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (mission_seq),
    KEY idx_mission_user_date (user_seq, mission_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
