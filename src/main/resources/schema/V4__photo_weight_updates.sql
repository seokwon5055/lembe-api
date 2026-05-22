-- ============================================================
-- Lembe DB Schema V4 — Photo type/version/parent, Weight source
-- ============================================================

-- tb_photo: category → photo_type 명확화, version/parent_photo_seq 추가
ALTER TABLE tb_photo
    CHANGE COLUMN category photo_type VARCHAR(30) NOT NULL,
    ADD COLUMN version          INT     NOT NULL DEFAULT 1   AFTER is_main,
    ADD COLUMN parent_photo_seq BIGINT  NULL                 AFTER version;
