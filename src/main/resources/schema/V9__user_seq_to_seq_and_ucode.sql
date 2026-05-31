-- tb_user: user_seq → seq, ucode 컬럼 추가 및 트리거 등록
ALTER TABLE tb_user CHANGE user_seq seq BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE tb_user
    ADD COLUMN ucode VARCHAR(8) DEFAULT NULL AFTER seq,
    ADD UNIQUE KEY uk_ucode (ucode);

UPDATE tb_user SET ucode = CONCAT('U', LPAD(seq, 7, '0'));

ALTER TABLE tb_user MODIFY COLUMN ucode VARCHAR(8) NOT NULL;

CREATE TRIGGER trg_user_ucode
BEFORE INSERT ON tb_user
FOR EACH ROW
BEGIN
    DECLARE next_seq BIGINT;
    SELECT AUTO_INCREMENT INTO next_seq
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tb_user';
    SET NEW.ucode = CONCAT('U', LPAD(next_seq, 7, '0'));
END;
