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
                this.currentUser = result.data;
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
}

// 页面加载完成后初始化应用
document.addEventListener('DOMContentLoaded', () => {
    new App();
});