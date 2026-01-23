-- 添加update_time字段到users表
ALTER TABLE users ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 更新现有记录的update_time为create_time
UPDATE users SET update_time = create_time WHERE update_time IS NULL;