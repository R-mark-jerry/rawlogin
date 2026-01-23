-- 添加默认管理员用户
-- 密码 123456 使用BCrypt加密后的值
INSERT INTO users (username, password, email, role, create_time, update_time, status)
VALUES ('admin', '$2a$10$QnR.DWs7qAR1c.Voe3DmDegBkeBOv3pVRb4eIlAnElERP/NHT6I8O', 'admin@example.com', 'ADMIN', '2026-01-20 10:42:00', '2026-01-20 10:42:00', 1)
ON DUPLICATE KEY UPDATE username=username;