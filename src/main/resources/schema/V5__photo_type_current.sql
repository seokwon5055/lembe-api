-- ============================================================
-- Lembe DB Schema V5 — photo_type 정리 (FACE/BODY → CURRENT)
-- ============================================================

-- 기존 FACE, BODY 타입을 CURRENT 로 통합
-- (얼굴/전신 분리 정책 폐지, 1장 현재 사진으로 통일)
UPDATE tb_photo
SET photo_type = 'CURRENT'
WHERE photo_type IN ('FACE', 'BODY');
