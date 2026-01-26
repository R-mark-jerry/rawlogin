-- 创建角色相关表

-- 1. 创建角色表
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(100) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    description VARCHAR(255) COMMENT '角色描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用'
) COMMENT '角色表';

-- 2. 创建权限表
CREATE TABLE IF NOT EXISTS permissions (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    description VARCHAR(255) COMMENT '权限描述',
    module VARCHAR(50) NOT NULL COMMENT '所属模块'
) COMMENT '权限表';

-- 3. 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    role_id INT NOT NULL COMMENT '角色ID',
    permission_id INT NOT NULL COMMENT '权限ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 4. 创建用户角色关联表（可选，用于多角色支持）
CREATE TABLE IF NOT EXISTS user_roles (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role_id INT NOT NULL COMMENT '角色ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 插入基础权限数据
INSERT IGNORE INTO permissions (name, code, description, module) VALUES
-- 用户管理权限
('查看用户列表', 'sys:user:list', '查看系统中的所有用户列表', 'user'),
('查看用户详情', 'sys:user:view', '查看用户的详细信息', 'user'),
('创建用户', 'sys:user:create', '创建新用户账户', 'user'),
('编辑用户', 'sys:user:edit', '修改用户信息', 'user'),
('删除用户', 'sys:user:delete', '删除用户账户', 'user'),

-- 角色管理权限
('查看角色列表', 'sys:role:view', '查看系统中的所有角色列表', 'role'),
('创建角色', 'sys:role:create', '创建新的角色', 'role'),
('编辑角色', 'sys:role:edit', '修改角色信息', 'role'),
('删除角色', 'sys:role:delete', '删除角色', 'role'),

-- 系统管理权限
('查看系统配置', 'sys:config:view', '查看系统配置信息', 'system'),
('编辑系统配置', 'sys:config:edit', '修改系统配置', 'system'),
('查看系统日志', 'sys:log:view', '查看系统操作日志', 'system');

-- 插入基础角色数据
INSERT IGNORE INTO roles (name, code, description) VALUES
('管理员', 'ADMIN', '系统管理员，拥有所有权限'),
('普通用户', 'USER', '普通用户，拥有基本权限');

-- 为管理员角色分配所有权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id as role_id, p.id as permission_id
FROM roles r, permissions p
WHERE r.code = 'ADMIN';

-- 为普通用户角色分配基础权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id as role_id, p.id as permission_id
FROM roles r, permissions p
WHERE r.code = 'USER' AND p.code IN ('sys:user:list', 'sys:user:view');

-- 为现有用户分配角色（如果用户表中有role字段）
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id as user_id, r.id as role_id
FROM users u, roles r
WHERE u.role = r.code;