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
        
        // 用户管理页面登出按钮
        document.getElementById('mgmtLogoutButton').addEventListener('click', () => {
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
        document.querySelector('.close').addEventListener('click', () => {
            this.closeUserModal();
        });
        
        // 保存用户按钮
        document.getElementById('saveUserBtn').addEventListener('click', () => {
            this.saveUser();
        });
        
        // 取消用户按钮
        document.getElementById('cancelUserBtn').addEventListener('click', () => {
            this.closeUserModal();
        });
        
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
            document.getElementById('displayRealName').textContent = this.currentUser.realName || '未设置';
            document.getElementById('displayEmail').textContent = this.currentUser.email || '未设置';
            
            // 格式化日期
            if (this.currentUser.createTime) {
                document.getElementById('displayCreateTime').textContent = new Date(this.currentUser.createTime).toLocaleString();
            }
            
            if (this.currentUser.lastLoginTime) {
                document.getElementById('displayLastLoginTime').textContent = new Date(this.currentUser.lastLoginTime).toLocaleString();
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
        const realName = document.getElementById('regRealName').value.trim();
        
        // 显示加载状态
        const registerButton = document.getElementById('registerButton');
        const originalText = registerButton.textContent;
        registerButton.textContent = '注册中...';
        registerButton.disabled = true;
        
        try {
            const result = await apiClient.register(username, password, email, realName);
            
            if (result.success) {
                this.showRegisterMessage('注册成功！请使用您的账户登录', 'success');
                // 清空表单
                document.getElementById('regUsername').value = '';
                document.getElementById('regPassword').value = '';
                document.getElementById('regEmail').value = '';
                document.getElementById('regRealName').value = '';
                
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
        
        if (!users || users.length === 0) {
            const row = tbody.insertRow();
            const cell = row.insertCell(0);
            cell.colSpan = 6;
            cell.textContent = '暂无用户数据';
            cell.style.textAlign = 'center';
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
            checkboxCell.appendChild(checkbox);
            
            // 用户名
            row.insertCell(1).textContent = user.username;
            
            // 真实姓名
            row.insertCell(2).textContent = user.realName || '-';
            
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
            editBtn.textContent = '编辑';
            editBtn.onclick = () => this.editUser(user);
            
            const deleteBtn = document.createElement('button');
            deleteBtn.className = 'btn-delete';
            deleteBtn.textContent = '删除';
            deleteBtn.onclick = () => this.deleteUser(user.id);
            
            actionCell.appendChild(editBtn);
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
        document.getElementById('userRealName').value = '';
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
                document.getElementById('userRealName').value = userData.realName || '';
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
            realName: document.getElementById('userRealName').value.trim(),
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
}

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    window.app = new App();
});