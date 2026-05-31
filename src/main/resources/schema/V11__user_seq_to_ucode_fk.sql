-- ============================================================
-- Lembe DB Schema V11
-- 모든 테이블의 user_seq(BIGINT FK) → ucode(VARCHAR(8) FK) 전환
-- friend 테이블: friend_user_seq → friend_ucode
-- ============================================================

-- ── goal ────────────────────────────────────────────────────────────────────
ALTER TABLE goal ADD COLUMN ucode VARCHAR(8) NULL AFTER goal_seq;
UPDATE goal g JOIN `user` u ON g.user_seq = u.seq SET g.ucode = u.ucode;
ALTER TABLE goal MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE goal DROP COLUMN user_seq;      -- uq_goal_user, idx_goal_user_seq 자동 삭제
ALTER TABLE goal ADD UNIQUE KEY uq_goal_ucode (ucode);

-- ── milestone ───────────────────────────────────────────────────────────────
ALTER TABLE milestone ADD COLUMN ucode VARCHAR(8) NULL AFTER goal_seq;
UPDATE milestone m JOIN `user` u ON m.user_seq = u.seq SET m.ucode = u.ucode;
ALTER TABLE milestone MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE milestone DROP COLUMN user_seq;
ALTER TABLE milestone ADD KEY idx_ms_ucode (ucode);

-- ── photo ────────────────────────────────────────────────────────────────────
ALTER TABLE photo ADD COLUMN ucode VARCHAR(8) NULL AFTER photo_seq;
UPDATE photo p JOIN `user` u ON p.user_seq = u.seq SET p.ucode = u.ucode;
ALTER TABLE photo MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE photo DROP COLUMN user_seq;
ALTER TABLE photo ADD KEY idx_photo_ucode (ucode);

-- ── ai_generation ────────────────────────────────────────────────────────────
ALTER TABLE ai_generation ADD COLUMN ucode VARCHAR(8) NULL AFTER gen_seq;
UPDATE ai_generation ag JOIN `user` u ON ag.user_seq = u.seq SET ag.ucode = u.ucode;
ALTER TABLE ai_generation MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE ai_generation DROP COLUMN user_seq;
ALTER TABLE ai_generation ADD KEY idx_aig_ucode (ucode);

-- ── weight_log ───────────────────────────────────────────────────────────────
ALTER TABLE weight_log ADD COLUMN ucode VARCHAR(8) NULL AFTER log_seq;
UPDATE weight_log wl JOIN `user` u ON wl.user_seq = u.seq SET wl.ucode = u.ucode;
ALTER TABLE weight_log MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE weight_log DROP COLUMN user_seq;
ALTER TABLE weight_log ADD KEY idx_wl_ucode_date (ucode, logged_at);

-- ── point_log ────────────────────────────────────────────────────────────────
ALTER TABLE point_log ADD COLUMN ucode VARCHAR(8) NULL AFTER point_log_seq;
UPDATE point_log pl JOIN `user` u ON pl.user_seq = u.seq SET pl.ucode = u.ucode;
ALTER TABLE point_log MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE point_log DROP COLUMN user_seq;
ALTER TABLE point_log ADD KEY idx_pl_ucode (ucode);

-- ── roulette_log ─────────────────────────────────────────────────────────────
ALTER TABLE roulette_log ADD COLUMN ucode VARCHAR(8) NULL AFTER roulette_log_seq;
UPDATE roulette_log rl JOIN `user` u ON rl.user_seq = u.seq SET rl.ucode = u.ucode;
ALTER TABLE roulette_log MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE roulette_log DROP COLUMN user_seq;
ALTER TABLE roulette_log ADD KEY idx_rl_ucode_spun (ucode, spun_at);

-- ── purchase ─────────────────────────────────────────────────────────────────
ALTER TABLE purchase ADD COLUMN ucode VARCHAR(8) NULL AFTER purchase_seq;
UPDATE purchase p JOIN `user` u ON p.user_seq = u.seq SET p.ucode = u.ucode;
ALTER TABLE purchase MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE purchase DROP COLUMN user_seq;
ALTER TABLE purchase ADD KEY idx_pur_ucode (ucode);

-- ── notification ─────────────────────────────────────────────────────────────
ALTER TABLE notification ADD COLUMN ucode VARCHAR(8) NULL AFTER notif_seq;
UPDATE notification n JOIN `user` u ON n.user_seq = u.seq SET n.ucode = u.ucode;
ALTER TABLE notification MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE notification DROP COLUMN user_seq;
ALTER TABLE notification ADD KEY idx_notif_ucode (ucode);

-- ── device_token ─────────────────────────────────────────────────────────────
ALTER TABLE device_token ADD COLUMN ucode VARCHAR(8) NULL AFTER device_token_seq;
UPDATE device_token dt JOIN `user` u ON dt.user_seq = u.seq SET dt.ucode = u.ucode;
ALTER TABLE device_token MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE device_token DROP COLUMN user_seq;
ALTER TABLE device_token ADD KEY idx_dt_ucode (ucode);

-- ── mission ──────────────────────────────────────────────────────────────────
ALTER TABLE mission ADD COLUMN ucode VARCHAR(8) NULL AFTER mission_seq;
UPDATE mission m JOIN `user` u ON m.user_seq = u.seq SET m.ucode = u.ucode;
ALTER TABLE mission MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE mission DROP COLUMN user_seq;
ALTER TABLE mission ADD KEY idx_mission_ucode_date (ucode, mission_date);

-- ── social_account ───────────────────────────────────────────────────────────
ALTER TABLE social_account ADD COLUMN ucode VARCHAR(8) NULL AFTER social_account_seq;
UPDATE social_account sa JOIN `user` u ON sa.user_seq = u.seq SET sa.ucode = u.ucode;
ALTER TABLE social_account MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE social_account DROP COLUMN user_seq;
ALTER TABLE social_account ADD KEY idx_sa_ucode (ucode);

-- ── user_profile ─────────────────────────────────────────────────────────────
ALTER TABLE user_profile ADD COLUMN ucode VARCHAR(8) NULL AFTER profile_seq;
UPDATE user_profile up JOIN `user` u ON up.user_seq = u.seq SET up.ucode = u.ucode;
ALTER TABLE user_profile MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE user_profile DROP COLUMN user_seq;
ALTER TABLE user_profile ADD UNIQUE KEY uq_profile_ucode (ucode);

-- ── point_wallet ─────────────────────────────────────────────────────────────
ALTER TABLE point_wallet ADD COLUMN ucode VARCHAR(8) NULL AFTER wallet_seq;
UPDATE point_wallet pw JOIN `user` u ON pw.user_seq = u.seq SET pw.ucode = u.ucode;
ALTER TABLE point_wallet MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE point_wallet DROP COLUMN user_seq;
ALTER TABLE point_wallet ADD UNIQUE KEY uq_wallet_ucode (ucode);

-- ── notification_setting ─────────────────────────────────────────────────────
ALTER TABLE notification_setting ADD COLUMN ucode VARCHAR(8) NULL AFTER setting_seq;
UPDATE notification_setting ns JOIN `user` u ON ns.user_seq = u.seq SET ns.ucode = u.ucode;
ALTER TABLE notification_setting MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE notification_setting DROP COLUMN user_seq;
ALTER TABLE notification_setting ADD UNIQUE KEY uq_ns_ucode (ucode);

-- ── refresh_token ────────────────────────────────────────────────────────────
ALTER TABLE refresh_token ADD COLUMN ucode VARCHAR(8) NULL AFTER token_seq;
UPDATE refresh_token rt JOIN `user` u ON rt.user_seq = u.seq SET rt.ucode = u.ucode;
ALTER TABLE refresh_token MODIFY COLUMN ucode VARCHAR(8) NOT NULL;
ALTER TABLE refresh_token DROP COLUMN user_seq;
ALTER TABLE refresh_token ADD KEY idx_rt_ucode (ucode);

-- ── friend ───────────────────────────────────────────────────────────────────
ALTER TABLE friend
    ADD COLUMN ucode        VARCHAR(8) NULL AFTER friend_seq,
    ADD COLUMN friend_ucode VARCHAR(8) NULL AFTER ucode;
UPDATE friend f
    JOIN `user` u1 ON f.user_seq        = u1.seq
    JOIN `user` u2 ON f.friend_user_seq = u2.seq
    SET f.ucode = u1.ucode, f.friend_ucode = u2.ucode;
ALTER TABLE friend
    MODIFY COLUMN ucode        VARCHAR(8) NOT NULL,
    MODIFY COLUMN friend_ucode VARCHAR(8) NOT NULL;
ALTER TABLE friend DROP COLUMN user_seq, DROP COLUMN friend_user_seq;
ALTER TABLE friend
    ADD UNIQUE KEY uq_friend_pair (ucode, friend_ucode),
    ADD KEY idx_friend_ucode (ucode),
    ADD KEY idx_friend_ucode_target (friend_ucode);
