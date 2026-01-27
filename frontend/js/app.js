// 应用程序主逻辑
class App {
    constructor() {
        this.currentPage = 'login';
        this.currentUser = null;
        this.init();
    }
    
    // 初始化应用
    init() {
        this.bindEvents();
        this.checkLoginStatus();
    }
    
    // 绑定事件
    bindEvents() {
        // 登录表单提交
        document.getElementById('loginForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });
        
        // 注册表单提交
        document.getElementById('registerForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleRegister();
        });
        
        // 显示注册页面链接
        document.getElementById('showRegisterLink').addEventListener('click', (e) => {
            e.preventDefault();
            this.showPage('register');
        });
        
        // 显示登录页面链接
        document.getElementById('showLoginLink').addEventListener('click', (e) => {
            e.preventDefault();
            this.showPage('login');
        });
        
        // 登出按钮
        document.getElementById('logoutButton').addEventListener('click', () => {
            this.handleLogout();
        });
        
        // 用户管理按钮
        document.getElementById('userManagementBtn').addEventListener('click', () => {
            this.showUserManagement();
        });
        
        // 角色管理按钮
        document.getElementById('roleManagementBtn').addEventListener('click', () => {
            this.showRoleManagement();
        });
        
        // 用户管理页面登出按钮
        document.getElementById('mgmtLogoutButton').addEventListener('click', () => {
            this.handleLogout();
        });
        
        // 角色管理页面登出按钮
        document.getElementById('roleLogoutButton').addEventListener('click', () => {
            this.handleLogout();
        });
        
        // 添加用户按钮
        document.getElementById('addUserBtn').addEventListener('click', () => {
            this.showAddUserModal();
        });
        
        // 搜索按钮
        document.getElementById('searchBtn').addEventListener('click', () => {
            this.searchUsers();
        });
        
        // 重置搜索按钮
        document.getElementById('resetSearchBtn').addEventListener('click', () => {
            this.resetSearch();
        });
        
        // 全选复选框
        document.getElementById('selectAll').addEventListener('change', (e) => {
            this.toggleSelectAll(e.target.checked);
        });
        
        // 批量删除按钮
        document.getElementById('batchDeleteBtn').addEventListener('click', () => {
            this.batchDeleteUsers();
        });
        
        // 模态框关闭按钮
        document.querySelectorAll('.close').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const modal = e.target.closest('.modal');
                if (modal && modal.id === 'userModal') {
                    this.closeUserModal();
                } else if (modal && modal.id === 'roleModal') {
                    this.closeRoleModal();
                }
            });
        });
        
        // 保存用户按钮
        document.getElementById('saveUserBtn').addEventListener('click', () => {
            this.saveUser();
        });
        
        // 取消用户按钮
        document.getElementById('cancelUserBtn').addEventListener('click', () => {
            this.closeUserModal();
        });
        
        // 添加角色按钮
        document.getElementById('addRoleBtn').addEventListener('click', () => {
            this.showAddRoleModal();
        });
        
        // 保存角色按钮
        document.getElementById('saveRoleBtn').addEventListener('click', () => {
            this.saveRole();
        });
        
        // 取消角色按钮
        document.getElementById('cancelRoleBtn').addEventListener('click', () => {
            this.closeRoleModal();
        });
        
        // 分配角色保存按钮
        const assignRoleSaveBtn = document.querySelector('#assignRoleModal .save-btn');
        if (assignRoleSaveBtn) {
            assignRoleSaveBtn.addEventListener('click', () => {
                this.saveUserRoleAssignment();
            });
        }
        
        // 分配角色取消按钮
        const assignRoleCancelBtn = document.querySelector('#assignRoleModal .cancel-btn');
        if (assignRoleCancelBtn) {
            assignRoleCancelBtn.addEventListener('click', () => {
                this.closeAssignRoleModal();
            });
        }
        
        // 输入框获得焦点时隐藏错误消息
        document.getElementById('username').addEventListener('focus', () => {
            this.hideFieldError('username');
            this.hideErrorMessage();
        });
        
        document.getElementById('password').addEventListener('focus', () => {
            this.hideFieldError('password');
            this.hideErrorMessage();
        });
        
        document.getElementById('regUsername').addEventListener('focus', () => {
            this.hideFieldError('regUsername');
            this.hideRegisterErrorMessage();
        });
        
        document.getElementById('regPassword').addEventListener('focus', () => {
            this.hideFieldError('regPassword');
            this.hideRegisterErrorMessage();
        });
    }
    
    // 检查登录状态
    async checkLoginStatus() {
        try {
            // 检查是否有JWT令牌
            const token = apiClient.getToken();
            if (!token) {
                this.showPage('login');
                return;
            }
            
            const result = await apiClient.getCurrentUser();
            if (result.success) {
                this.currentUser = result.data;
                this.showWelcomePage();
            } else {
                this.showPage('login');
            }
        } catch (error) {
            console.log('用户未登录:', error.message);
            this.showPage('login');
        }
    }
    
    // 显示指定页面
    showPage(pageName) {
        console.log('showPage被调用，页面名称:', pageName);
        // 隐藏所有页面
        document.querySelectorAll('.page').forEach(page => {
            page.style.display = 'none';
        });
        
        // 显示指定页面
        const targetPage = document.getElementById(pageName + 'Page');
        if (targetPage) {
            targetPage.style.display = 'block';
            this.currentPage = pageName;
            console.log('成功显示页面:', pageName);
        } else {
            console.error('找不到页面元素:', pageName + 'Page');
        }
    }
    
    // 显示欢迎页面
    showWelcomePage() {
        console.log('showWelcomePage被调用，当前用户:', this.currentUser);
        if (this.currentUser) {
            // 更新用户信息显示
            document.getElementById('displayUsername').textContent = this.currentUser.username;
            document.getElementById('displayUsername2').textContent = this.currentUser.username;
            document.getElementById('displayRealName').textContent = '未设置';
            document.getElementById('displayEmail').textContent = this.currentUser.email || '未设置';
            
            // 格式化日期
            if (this.currentUser.createTime) {
                document.getElementById('displayCreateTime').textContent = new Date(this.currentUser.createTime).toLocaleString();
            }
            
            if (this.currentUser.lastLoginTime) {
                document.getElementById('displayLastLoginTime').textContent = new Date(this.currentUser.lastLoginTime).toLocaleString();
            }
            
            // 根据用户角色显示或隐藏管理按钮
            const userManagementBtn = document.getElementById('userManagementBtn');
            const roleManagementBtn = document.getElementById('roleManagementBtn');
            
            if (userManagementBtn) {
                if (this.currentUser.role === 'ADMIN') {
                    userManagementBtn.style.display = 'inline-block';
                } else {
                    userManagementBtn.style.display = 'none';
                }
            }
            
            if (roleManagementBtn) {
                if (this.currentUser.role === 'ADMIN') {
                    roleManagementBtn.style.display = 'inline-block';
                } else {
                    roleManagementBtn.style.display = 'none';
                }
            }
            
            console.log('准备显示欢迎页面');
            this.showPage('welcome');
        } else {
            console.error('currentUser为空，无法显示欢迎页面');
        }
    }
    
    // 处理登录
    async handleLogin() {
        if (!this.validateLoginForm()) {
            return;
        }
        
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();
        const remember = document.getElementById('remember').checked;
        
        // 显示加载状态
        const loginButton = document.getElementById('loginButton');
        const originalText = loginButton.textContent;
        loginButton.textContent = '登录中...';
        loginButton.disabled = true;
        
        try {
            console.log('开始登录请求...');
            const result = await apiClient.login(username, password, remember);
            console.log('登录响应:', result);
            
            if (result.success) {
                console.log('登录成功，设置用户信息:', result.data);
                // 从响应数据中提取用户信息
                this.currentUser = result.data.user || result.data;
                this.showWelcomePage();
                this.showMessage('登录成功', 'success');
            } else {
                console.log('登录失败:', result.message);
                this.showMessage(result.message || '登录失败', 'error');
            }
        } catch (error) {
            console.error('登录异常:', error);
            this.showMessage(error.message, 'error');
        } finally {
            // 恢复按钮状态
            loginButton.textContent = originalText;
            loginButton.disabled = false;
        }
    }
    
    // 处理注册
    async handleRegister() {
        if (!this.validateRegisterForm()) {
            return;
        }
        
        const username = document.getElementById('regUsername').value.trim();
        const password = document.getElementById('regPassword').value.trim();
        const email = document.getElementById('regEmail').value.trim();
        // 显示加载状态
        const registerButton = document.getElementById('registerButton');
        const originalText = registerButton.textContent;
        registerButton.textContent = '注册中...';
        registerButton.disabled = true;
        
        try {
            const result = await apiClient.register(username, password, email);
            
            if (result.success) {
                this.showRegisterMessage('注册成功！请使用您的账户登录', 'success');
                // 清空表单
                document.getElementById('regUsername').value = '';
                document.getElementById('regPassword').value = '';
                document.getElementById('regEmail').value = '';
                
                // 3秒后跳转到登录页面
                setTimeout(() => {
                    this.showPage('login');
                }, 3000);
            } else {
                this.showRegisterMessage(result.message || '注册失败', 'error');
            }
        } catch (error) {
            this.showRegisterMessage(error.message, 'error');
        } finally {
            // 恢复按钮状态
            registerButton.textContent = originalText;
            registerButton.disabled = false;
        }
    }
    
    // 处理登出
    async handleLogout() {
        try {
            await apiClient.logout();
            this.currentUser = null;
            this.showPage('login');
            this.showMessage('已成功登出', 'success');
        } catch (error) {
            console.error('登出失败:', error.message);
            // 即使登出API失败，也清除本地状态并跳转到登录页面
            this.currentUser = null;
            this.showPage('login');
            this.showMessage('已登出', 'success');
        }
    }
    
    // 验证登录表单
    validateLoginForm() {
        let isValid = true;
        
        // 验证用户名
        const username = document.getElementById('username').value.trim();
        if (username === '') {
            this.showFieldError('username', '用户名不能为空');
            isValid = false;
        } else {
            this.hideFieldError('username');
        }
        
        // 验证密码
        const password = document.getElementById('password').value.trim();
        if (password === '') {
            this.showFieldError('password', '密码不能为空');
            isValid = false;
        } else if (password.length < 6) {
            this.showFieldError('password', '密码长度不能少于6位');
            isValid = false;
        } else {
            this.hideFieldError('password');
        }
        
        return isValid;
    }
    
    // 验证注册表单
    validateRegisterForm() {
        let isValid = true;
        
        // 验证用户名
        const username = document.getElementById('regUsername').value.trim();
        if (username === '') {
            this.showFieldError('regUsername', '用户名不能为空');
            isValid = false;
        } else if (username.length < 3 || username.length > 50) {
            this.showFieldError('regUsername', '用户名长度必须在3-50个字符之间');
            isValid = false;
        } else {
            this.hideFieldError('regUsername');
        }
        
        // 验证密码
        const password = document.getElementById('regPassword').value.trim();
        if (password === '') {
            this.showFieldError('regPassword', '密码不能为空');
            isValid = false;
        } else if (password.length < 6) {
            this.showFieldError('regPassword', '密码长度不能少于6位');
            isValid = false;
        } else {
            this.hideFieldError('regPassword');
        }
        
        return isValid;
    }
    
    // 显示字段错误
    showFieldError(fieldId, message) {
        const errorElement = document.getElementById(fieldId + 'Error');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }
    
    // 隐藏字段错误
    hideFieldError(fieldId) {
        const errorElement = document.getElementById(fieldId + 'Error');
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }
    
    // 显示错误消息
    showMessage(message, type) {
        const errorElement = document.getElementById('errorMessage');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
            
            if (type === 'success') {
                errorElement.className = 'success-message';
            } else {
                errorElement.className = 'error-message';
            }
        }
    }
    
    // 隐藏错误消息
    hideErrorMessage() {
        const errorElement = document.getElementById('errorMessage');
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }
    
    // 显示注册页面的消息
    showRegisterMessage(message, type) {
        const errorElement = document.getElementById('registerErrorMessage');
        const successElement = document.getElementById('registerSuccessMessage');
        
        // 隐藏所有消息
        errorElement.style.display = 'none';
        successElement.style.display = 'none';
        
        if (type === 'success') {
            successElement.textContent = message;
            successElement.style.display = 'block';
        } else {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }
    
    // 隐藏注册页面的错误消息
    hideRegisterErrorMessage() {
        const errorElement = document.getElementById('registerErrorMessage');
        const successElement = document.getElementById('registerSuccessMessage');
        
        errorElement.style.display = 'none';
        successElement.style.display = 'none';
    }
    
    // ===== 用户管理相关方法 =====
    
    // 显示用户管理页面
    async showUserManagement() {
        try {
            // 检查用户是否已登录
            if (!this.currentUser) {
                this.showMessage('请先登录', 'error');
                this.showPage('login');
                return;
            }
           
            // 检查用户是否为管理员
            if (this.currentUser.role !== 'ADMIN') {
                this.showMessage('权限不足，只有管理员可以访问用户管理', 'error');
                return;
            }
           
            // 显示用户管理页面
            this.showPage('userManagement');
           
            // 加载用户列表
            await this.loadUsers();
        } catch (error) {
            console.error('显示用户管理页面失败:', error);
            this.showMessage('加载用户管理页面失败', 'error');
        }
    }
    
    // 加载用户列表
    async loadUsers() {
        try {
            const result = await apiClient.getAllUsers();
            if (result.success) {
                this.renderUserTable(result.data);
            } else {
                this.showMessage(result.message || '获取用户列表失败', 'error');
            }
        } catch (error) {
            console.error('加载用户列表失败:', error);
            this.showMessage('获取用户列表失败', 'error');
        }
    }
    
    // 渲染用户表格
    renderUserTable(users) {
        const tbody = document.querySelector('#userTable tbody');
        if (!tbody) return;
        
        // 清空表格
        tbody.innerHTML = '';
        
        // 更新表格信息
        const tableInfo = document.getElementById('tableInfo');
        if (tableInfo) {
            tableInfo.textContent = `共 ${users ? users.length : 0} 个用户`;
        }
        
        if (!users || users.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell(0);
            cell.colSpan = 6;
            cell.innerHTML = '<div style="text-align: center; padding: 20px; color: #6c757d;">📭 暂无用户数据</div>';
            return;
        }
        
        // 添加用户数据
        users.forEach(user => {
            const row = tbody.insertRow();
            
            // 复选框
            const checkboxCell = row.insertCell(0);
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.className = 'user-checkbox';
            checkbox.value = user.id;
            checkbox.addEventListener('change', () => this.updateBatchDeleteButton());
            checkboxCell.appendChild(checkbox);
            
            // 用户名
            row.insertCell(1).textContent = user.username;
            
            // 真实姓名（已移除，使用空字符串代替）
            row.insertCell(2).textContent = '-';
            
            // 邮箱
            row.insertCell(3).textContent = user.email || '-';
            
            // 角色
            const roleCell = row.insertCell(4);
            const roleBadge = document.createElement('span');
            roleBadge.className = user.role === 'ADMIN' ? 'badge admin' : 'badge user';
            roleBadge.textContent = user.role === 'ADMIN' ? '管理员' : '普通用户';
            roleCell.appendChild(roleBadge);
            
            // 操作按钮
            const actionCell = row.insertCell(5);
            const editBtn = document.createElement('button');
            editBtn.className = 'btn-edit';
            editBtn.textContent = '✏️ 编辑';
            editBtn.onclick = () => this.editUser(user);
            
            const assignRoleBtn = document.createElement('button');
            assignRoleBtn.className = 'btn-role';
            assignRoleBtn.textContent = '👥 分配角色';
            assignRoleBtn.addEventListener('click', () => {
                console.log('分配角色按钮被点击，用户:', user);
                this.showAssignRoleModal(user);
            });
            
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn-delete';
            deleteBtn.textContent = '🗑️ 删除';
            deleteBtn.onclick = () => this.deleteUser(user.id);
            
            actionCell.appendChild(editBtn);
            actionCell.appendChild(assignRoleBtn);
            actionCell.appendChild(deleteBtn);
        });
        
        // 更新批量删除按钮状态
        this.updateBatchDeleteButton();
    }
    
    // 搜索用户
    async searchUsers() {
        try {
            const keyword = document.getElementById('searchKeyword').value.trim();
            const role = document.getElementById('searchRole').value;
            
            const result = await apiClient.searchUsers(keyword, role);
            if (result.success) {
                this.renderUserTable(result.data);
            } else {
                this.showMessage(result.message || '搜索用户失败', 'error');
            }
        } catch (error) {
            console.error('搜索用户失败:', error);
            this.showMessage('搜索用户失败', 'error');
        }
    }
    
    // 重置搜索条件
    async resetSearch() {
        document.getElementById('searchKeyword').value = '';
        document.getElementById('searchRole').value = '';
        await this.loadUsers();
    }
    
    // 显示添加用户模态框
    showAddUserModal() {
        // 重置表单
        document.getElementById('userId').value = '';
        document.getElementById('userUsername').value = '';
        document.getElementById('userPassword').value = '';
        document.getElementById('userEmail').value = '';
        document.getElementById('userRole').value = 'USER';
        
        // 显示密码字段
        document.getElementById('userPasswordGroup').style.display = 'block';
        
        // 更新模态框标题
        document.getElementById('userModalTitle').textContent = '添加用户';
        
        // 显示模态框
        document.getElementById('userModal').style.display = 'block';
    }
    
    // 编辑用户
    async editUser(user) {
        try {
            // 获取用户详细信息
            const result = await apiClient.getUserById(user.id);
            if (result.success) {
                const userData = result.data;
                
                // 填充表单
                document.getElementById('userId').value = userData.id;
                document.getElementById('userUsername').value = userData.username;
                document.getElementById('userEmail').value = userData.email || '';
                document.getElementById('userRole').value = userData.role;
                
                // 编辑时隐藏密码字段
                document.getElementById('userPasswordGroup').style.display = 'none';
                
                // 更新模态框标题
                document.getElementById('userModalTitle').textContent = '编辑用户';
                
                // 显示模态框
                document.getElementById('userModal').style.display = 'block';
            } else {
                this.showMessage(result.message || '获取用户信息失败', 'error');
            }
        } catch (error) {
            console.error('编辑用户失败:', error);
            this.showMessage('获取用户信息失败', 'error');
        }
    }
    
    // 关闭用户模态框
    closeUserModal() {
        document.getElementById('userModal').style.display = 'none';
        // 清除表单验证错误
        this.hideUserFieldErrors();
    }
    
    // 保存用户
    async saveUser() {
        if (!this.validateUserForm()) {
            return;
        }
        
        const userId = document.getElementById('userId').value;
        const userData = {
            username: document.getElementById('userUsername').value.trim(),
            email: document.getElementById('userEmail').value.trim(),
            role: document.getElementById('userRole').value
        };
        
        // 添加用户时需要密码
        if (!userId) {
            userData.password = document.getElementById('userPassword').value.trim();
        }
        
        try {
            let result;
            if (userId) {
                // 更新用户
                result = await apiClient.updateUser(userId, userData);
            } else {
                // 添加用户
                result = await apiClient.addUser(userData);
            }
            
            if (result.success) {
                this.showMessage(userId ? '用户更新成功' : '用户添加成功', 'success');
                this.closeUserModal();
                await this.loadUsers();
            } else {
                this.showMessage(result.message || '保存用户失败', 'error');
            }
        } catch (error) {
            console.error('保存用户失败:', error);
            this.showMessage('保存用户失败', 'error');
        }
    }
    
    // 验证用户表单
    validateUserForm() {
        let isValid = true;
        this.hideUserFieldErrors();
        
        // 验证用户名
        const username = document.getElementById('userUsername').value.trim();
        if (username === '') {
            this.showUserFieldError('userUsername', '用户名不能为空');
            isValid = false;
        } else if (username.length < 3 || username.length > 50) {
            this.showUserFieldError('userUsername', '用户名长度必须在3-50个字符之间');
            isValid = false;
        }
        
        // 验证密码（仅添加用户时）
        const userId = document.getElementById('userId').value;
        if (!userId) {
            const password = document.getElementById('userPassword').value.trim();
            if (password === '') {
                this.showUserFieldError('userPassword', '密码不能为空');
                isValid = false;
            } else if (password.length < 6) {
                this.showUserFieldError('userPassword', '密码长度不能少于6位');
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    // 显示用户表单字段错误
    showUserFieldError(fieldId, message) {
        const errorElement = document.getElementById(fieldId + 'Error');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }
    
    // 隐藏所有用户表单字段错误
    hideUserFieldErrors() {
        const fieldIds = ['userUsername', 'userPassword'];
        fieldIds.forEach(fieldId => {
            const errorElement = document.getElementById(fieldId + 'Error');
            if (errorElement) {
                errorElement.style.display = 'none';
            }
        });
    }
    
    // 删除用户
    async deleteUser(userId) {
        if (!confirm('确定要删除这个用户吗？此操作不可恢复。')) {
            return;
        }
        
        try {
            const result = await apiClient.deleteUser(userId);
            if (result.success) {
                this.showMessage('用户删除成功', 'success');
                await this.loadUsers();
            } else {
                this.showMessage(result.message || '删除用户失败', 'error');
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            this.showMessage('删除用户失败', 'error');
        }
    }
    
    // 全选/取消全选
    toggleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.user-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
        this.updateBatchDeleteButton();
    }
    
    // 更新批量删除按钮状态
    updateBatchDeleteButton() {
        const checkboxes = document.querySelectorAll('.user-checkbox:checked');
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        
        if (checkboxes.length > 0) {
            batchDeleteBtn.disabled = false;
            batchDeleteBtn.textContent = `批量删除 (${checkboxes.length})`;
        } else {
            batchDeleteBtn.disabled = true;
            batchDeleteBtn.textContent = '批量删除';
        }
    }
    
    // 批量删除用户
    async batchDeleteUsers() {
        const checkboxes = document.querySelectorAll('.user-checkbox:checked');
        if (checkboxes.length === 0) {
            this.showMessage('请选择要删除的用户', 'error');
            return;
        }
        
        if (!confirm(`确定要删除选中的 ${checkboxes.length} 个用户吗？此操作不可恢复。`)) {
            return;
        }
        
        const userIds = Array.from(checkboxes).map(checkbox => checkbox.value);
        
        try {
            const result = await apiClient.batchDeleteUsers(userIds);
            if (result.success) {
                this.showMessage(`成功删除 ${result.data} 个用户`, 'success');
                await this.loadUsers();
            } else {
                this.showMessage(result.message || '批量删除用户失败', 'error');
            }
        } catch (error) {
            console.error('批量删除用户失败:', error);
            this.showMessage('批量删除用户失败', 'error');
        }
    }
    
    // ===== 角色管理相关方法 =====
    
    // 显示角色管理页面
    async showRoleManagement() {
        try {
            // 检查用户是否已登录
            if (!this.currentUser) {
                this.showMessage('请先登录', 'error');
                this.showPage('login');
                return;
            }
           
            // 检查用户是否为管理员或具有角色管理权限
            // 目前只有管理员可以访问角色管理
            if (this.currentUser.role !== 'ADMIN') {
                this.showMessage('权限不足，只有管理员可以访问角色管理', 'error');
                return;
            }
           
            // 显示角色管理页面
            this.showPage('roleManagement');
           
            // 加载角色列表
            await this.loadRoles();
        } catch (error) {
            console.error('显示角色管理页面失败:', error);
            this.showMessage('加载角色管理页面失败', 'error');
        }
    }
    
    // 加载角色列表
    async loadRoles() {
        try {
            console.log('开始加载角色列表...');
            const result = await apiClient.getAllRoles();
            console.log('API响应结果:', result);
            if (result.success) {
                console.log('角色数据:', result.data);
                this.renderRoleTable(result.data);
            } else {
                this.showMessage(result.message || '获取角色列表失败', 'error');
            }
        } catch (error) {
            console.error('加载角色列表失败:', error);
            this.showMessage('获取角色列表失败', 'error');
        }
    }
    
    // 渲染角色表格
    renderRoleTable(roles) {
        const tbody = document.querySelector('#roleTable tbody');
        if (!tbody) return;
        
        // 清空表格
        tbody.innerHTML = '';
        
        // 更新表格信息
        const tableInfo = document.getElementById('roleTableInfo');
        if (tableInfo) {
            tableInfo.textContent = `共 ${roles ? roles.length : 0} 个角色`;
        }
        
        if (!roles || roles.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell(0);
            cell.colSpan = 6;
            cell.innerHTML = '<div style="text-align: center; padding: 20px; color: #6c757d;">📭 暂无角色数据</div>';
            return;
        }
        
        // 添加角色数据
        roles.forEach(role => {
            const row = tbody.insertRow();
           
            // ID
            row.insertCell(0).textContent = role.id;
            
            // 角色名称
            row.insertCell(1).textContent = role.name;
            
            // 角色代码
            const codeCell = row.insertCell(2);
            const codeBadge = document.createElement('span');
            codeBadge.className = 'badge';
            codeBadge.style.backgroundColor = role.code === 'ADMIN' ? '#dc3545' : '#28a745';
            codeBadge.style.color = 'white';
            codeBadge.textContent = role.code;
            codeCell.appendChild(codeBadge);
            
            // 描述
            row.insertCell(3).textContent = role.description || '-';
            
            // 权限
            const permissionCell = row.insertCell(4);
            if (role.permissions && role.permissions.length > 0) {
                const permissionContainer = document.createElement('div');
                role.permissions.forEach(permission => {
                    const tag = document.createElement('span');
                    tag.className = 'permission-tag';
                    
                    // 处理权限对象（包含详细信息）或权限代码字符串
                    let permissionCode;
                    if (typeof permission === 'string') {
                        // 如果是字符串格式
                        permissionCode = permission;
                    } else if (permission && typeof permission === 'object' && permission.code) {
                        // 如果是对象格式，提取code字段
                        permissionCode = permission.code;
                    } else {
                        // 无法识别的格式，跳过
                        return;
                    }
                    
                    // 根据权限类型设置不同的样式
                    if (permissionCode.startsWith('sys:user:')) {
                        tag.classList.add('user-management');
                    } else if (permissionCode.startsWith('sys:role:')) {
                        tag.classList.add('role-management');
                    } else if (permissionCode.startsWith('sys:')) {
                        tag.classList.add('system-management');
                    }
                    
                    tag.textContent = this.getPermissionDisplayName(permissionCode);
                    permissionContainer.appendChild(tag);
                });
                permissionCell.appendChild(permissionContainer);
            } else {
                permissionCell.textContent = '-';
            }
            
            // 操作按钮
            const actionCell = row.insertCell(5);
            const editBtn = document.createElement('button');
            editBtn.className = 'btn-edit';
            editBtn.textContent = '✏️ 编辑';
            editBtn.onclick = () => this.editRole(role);
            
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn-delete';
            deleteBtn.textContent = '🗑️ 删除';
            deleteBtn.onclick = () => this.deleteRole(role.id);
            
            actionCell.appendChild(editBtn);
            // 只有非内置角色才能删除
            if (role.code !== 'ADMIN' && role.code !== 'USER') {
                actionCell.appendChild(deleteBtn);
            }
        });
    }
    
    // 获取权限显示名称
    getPermissionDisplayName(permission) {
        const permissionMap = {
            'sys:user:list': '查看用户',
            'sys:user:view': '查看用户详情',
            'sys:user:create': '创建用户',
            'sys:user:edit': '编辑用户',
            'sys:user:delete': '删除用户',
            'sys:role:view': '查看角色',
            'sys:role:create': '创建角色',
            'sys:role:edit': '编辑角色',
            'sys:role:delete': '删除角色',
            'sys:config:view': '查看配置',
            'sys:config:edit': '编辑配置',
            'sys:log:view': '查看日志'
        };
        return permissionMap[permission] || permission;
    }
    
    // 显示添加角色模态框
    showAddRoleModal() {
        // 重置表单
        document.getElementById('roleId').value = '';
        document.getElementById('roleName').value = '';
        document.getElementById('roleCode').value = '';
        document.getElementById('roleDescription').value = '';
        
        // 重置权限复选框
        document.querySelectorAll('input[name="permissions"]').forEach(checkbox => {
            checkbox.checked = false;
        });
        
        // 更新模态框标题
        document.getElementById('roleModalTitle').textContent = '添加角色';
        
        // 显示模态框
        document.getElementById('roleModal').style.display = 'block';
    }
    
    // 编辑角色
    async editRole(role) {
        try {
            console.log('开始编辑角色:', role);
            
            // 填充表单
            document.getElementById('roleId').value = role.id;
            document.getElementById('roleName').value = role.name;
            document.getElementById('roleCode').value = role.code;
            document.getElementById('roleDescription').value = role.description || '';
            
            // 设置权限复选框
            console.log('角色权限数据:', role.permissions);
            document.querySelectorAll('input[name="permissions"]').forEach(checkbox => {
                console.log('处理权限复选框:', checkbox.value);
                if (role.permissions && role.permissions.length > 0) {
                    // 检查权限码是否存在于角色权限中
                    const isChecked = role.permissions.some(permission => 
                        typeof permission === 'string' ? 
                            permission === checkbox.value : 
                            (permission && permission.code === checkbox.value)
                    );
                    console.log('权限匹配结果:', checkbox.value, '->', isChecked);
                    checkbox.checked = isChecked;
                } else {
                    checkbox.checked = false;
                    console.log('权限列表为空，取消所有复选框');
                }
            });
            
            // 更新模态框标题
            document.getElementById('roleModalTitle').textContent = '编辑角色';
            
            // 显示模态框
            document.getElementById('roleModal').style.display = 'block';
        } catch (error) {
            console.error('编辑角色失败:', error);
            this.showMessage('编辑角色失败', 'error');
        }
    }
    
    // 关闭角色模态框
    closeRoleModal() {
        document.getElementById('roleModal').style.display = 'none';
        // 清除表单验证错误
        this.hideRoleFieldErrors();
    }
    
    // 保存角色
    async saveRole() {
        if (!this.validateRoleForm()) {
            return;
        }
        
        const roleId = document.getElementById('roleId').value;
        const roleData = {
            name: document.getElementById('roleName').value.trim(),
            code: document.getElementById('roleCode').value.trim(),
            description: document.getElementById('roleDescription').value.trim(),
            permissions: Array.from(document.querySelectorAll('input[name="permissions"]:checked'))
                .map(checkbox => checkbox.value)
        };
        
        try {
            let result;
            if (roleId) {
                // 更新角色
                result = await apiClient.updateRole(roleId, roleData);
            } else {
                // 添加角色
                result = await apiClient.addRole(roleData);
            }
            
            if (result.success) {
                this.showMessage(roleId ? '角色更新成功' : '角色添加成功', 'success');
                this.closeRoleModal();
                await this.loadRoles();
            } else {
                this.showMessage(result.message || '保存角色失败', 'error');
            }
        } catch (error) {
            console.error('保存角色失败:', error);
            this.showMessage('保存角色失败', 'error');
        }
    }
    
    // 验证角色表单
    validateRoleForm() {
        let isValid = true;
        this.hideRoleFieldErrors();
        
        // 验证角色名称
        const roleName = document.getElementById('roleName').value.trim();
        if (roleName === '') {
            this.showRoleFieldError('roleName', '角色名称不能为空');
            isValid = false;
        }
        
        // 验证角色代码
        const roleCode = document.getElementById('roleCode').value.trim();
        if (roleCode === '') {
            this.showRoleFieldError('roleCode', '角色代码不能为空');
            isValid = false;
        }
        
        // 验证权限
        const permissions = document.querySelectorAll('input[name="permissions"]:checked');
        if (permissions.length === 0) {
            alert('请至少选择一个权限');
            isValid = false;
        }
        
        return isValid;
    }
    
    // 显示角色表单字段错误
    showRoleFieldError(fieldId, message) {
        const errorElement = document.getElementById(fieldId + 'Error');
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }
    
    // 隐藏所有角色表单字段错误
    hideRoleFieldErrors() {
        const fieldIds = ['roleName', 'roleCode'];
        fieldIds.forEach(fieldId => {
            const errorElement = document.getElementById(fieldId + 'Error');
            if (errorElement) {
                errorElement.style.display = 'none';
            }
        });
    }
    
    // 删除角色
    async deleteRole(roleId) {
        if (!confirm('确定要删除这个角色吗？此操作不可恢复。')) {
            return;
        }
        
        try {
            const result = await apiClient.deleteRole(roleId);
            if (result.success) {
                this.showMessage('角色删除成功', 'success');
                await this.loadRoles();
            } else {
                this.showMessage(result.message || '删除角色失败', 'error');
            }
        } catch (error) {
            console.error('删除角色失败:', error);
            this.showMessage('删除角色失败', 'error');
        }
    }
    
    // ===== 用户角色管理相关方法 =====
    
    // 显示分配角色模态框
    async showAssignRoleModal(user) {
        try {
            // 获取所有角色
            const rolesResult = await apiClient.getAllRoles();
            if (!rolesResult.success) {
                this.showMessage('获取角色列表失败', 'error');
                return;
            }
            
            // 获取用户当前角色
            const userRolesResult = await apiClient.getUserRoles(user.id);
            const userRoleIds = userRolesResult.success ?
                userRolesResult.data.map(role => role.id) : [];
            
            // 填充角色列表
            const roleListContainer = document.getElementById('roleListContainer');
            roleListContainer.innerHTML = '';
            
            rolesResult.data.forEach(role => {
                const roleItem = document.createElement('div');
                roleItem.className = 'role-item';
                
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.id = `role_${role.id}`;
                checkbox.value = role.id;
                checkbox.checked = userRoleIds.includes(role.id);
                
                const label = document.createElement('label');
                label.htmlFor = `role_${role.id}`;
                label.textContent = `${role.name} (${role.code})`;
                
                roleItem.appendChild(checkbox);
                roleItem.appendChild(label);
                roleListContainer.appendChild(roleItem);
            });
            
            // 保存用户ID
            document.getElementById('assignRoleUserId').value = user.id;
            document.getElementById('assignRoleUsername').textContent = user.username;
            
            // 显示模态框
            document.getElementById('assignRoleModal').style.display = 'block';
        } catch (error) {
            console.error('显示分配角色模态框失败:', error);
            this.showMessage('显示分配角色界面失败', 'error');
        }
    }
    
    // 关闭分配角色模态框
    closeAssignRoleModal() {
        document.getElementById('assignRoleModal').style.display = 'none';
    }
    
    // 保存用户角色分配
    async saveUserRoleAssignment() {
        try {
            const userId = document.getElementById('assignRoleUserId').value;
            const checkedRoles = document.querySelectorAll('#roleListContainer input[type="checkbox"]:checked');
            const roleIds = Array.from(checkedRoles).map(checkbox => parseInt(checkbox.value));
            
            const result = await apiClient.assignRolesToUser(userId, roleIds);
            if (result.success) {
                this.showMessage('角色分配成功', 'success');
                this.closeAssignRoleModal();
                await this.loadUsers(); // 刷新用户列表
            } else {
                this.showMessage(result.message || '角色分配失败', 'error');
            }
        } catch (error) {
            console.error('保存用户角色分配失败:', error);
            this.showMessage('角色分配失败', 'error');
        }
    }
}

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    window.app = new App();
});