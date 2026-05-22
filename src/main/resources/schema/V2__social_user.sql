-- ============================================================
-- Lembe DB Schema V2 — social account, user profile, wallet, friend
-- ============================================================

-- tb_user: 소셜 로그인 대응 컬럼 정리
ALTER TABLE tb_user
    MODIFY COLUMN email       VARCHAR(255) NULL,
    MODIFY COLUMN password    VARCHAR(255) NULL,
    DROP COLUMN provider,
    DROP COLUMN provider_id,
    DROP COLUMN point_balance;

-- 소셜 로그인 계정 (유저 1명 = 여러 소셜 계정 가능)
CREATE TABLE IF NOT EXISTS tb_social_account (
    social_account_seq  BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq            BIGINT          NOT NULL,
    provider            VARCHAR(20)     NOT NULL,   -- KAKAO | APPLE | GOOGLE
    provider_id         VARCHAR(255)    NOT NULL,
    email               VARCHAR(255),
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (social_account_seq),
    UNIQUE KEY uq_social_provider (provider, provider_id),
    KEY idx_sa_user_seq (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 온보딩 상세 프로필
CREATE TABLE IF NOT EXISTS tb_user_profile (
    profile_seq             BIGINT          NOT NULL AUTO_INCREMENT,
    user_seq                BIGINT          NOT NULL,
    gender                  CHAR(1),                    -- M | F | N
    birth_year              SMALLINT,
    height_cm               DECIMAL(5,2),
    notification_enabled    TINYINT(1)      NOT NULL DEFAULT 1,
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (profile_seq),
    UNIQUE KEY uq_profile_user (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 포인트 지갑 (동시성 안전, row-level lock)
CREATE TABLE IF NOT EXISTS tb_point_wallet (
    wallet_seq  BIGINT      NOT NULL AUTO_INCREMENT,
    user_seq    BIGINT      NOT NULL,
    balance     INT         NOT NULL DEFAULT 0,
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (wallet_seq),
    UNIQUE KEY uq_wallet_user (user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 친구 관계 (홈화면 "친구 진행 상황")
CREATE TABLE IF NOT EXISTS tb_friend (
    friend_seq      BIGINT      NOT NULL AUTO_INCREMENT,
    user_seq        BIGINT      NOT NULL,
    friend_user_seq BIGINT      NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING | ACCEPTED | BLOCKED
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (friend_seq),
    UNIQUE KEY uq_friend_pair (user_seq, friend_user_seq),
    KEY idx_friend_user (user_seq),
    KEY idx_friend_target (friend_user_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
