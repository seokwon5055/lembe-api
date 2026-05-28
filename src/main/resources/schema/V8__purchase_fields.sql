-- ============================================================
-- Lembe DB Schema V8 — tb_purchase 확장
-- bonus_points, refunded_at, refund_reason 추가
-- ============================================================

ALTER TABLE tb_purchase
    ADD COLUMN bonus_points  INT          NOT NULL DEFAULT 0   AFTER points_granted,
    ADD COLUMN refunded_at   DATETIME     NULL                 AFTER verified_at,
    ADD COLUMN refund_reason VARCHAR(255) NULL                 AFTER refunded_at;
