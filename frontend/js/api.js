// API配置
const API_BASE_URL = 'http://localhost:8080/myfirst';

// API请求封装
class ApiClient {
    constructor(baseURL) {
        this.baseURL = baseURL;
        this.axios = axios.create({
            baseURL: baseURL,
            timeout: 10000,
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: false // JWT认证不需要cookie
        });
        
        // 请求拦截器
        this.axios.interceptors.request.use(
            config => {
                // 自动添加JWT令牌到请求头
                const token = this.getToken();
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                console.log('发送请求:', config.url);
                return config;
            },
            error => {
                console.error('请求错误:', error);
                return Promise.reject(error);
            }
        );
        
        // 响应拦截器
        this.axios.interceptors.response.use(
            response => {
                console.log('收到响应:', response.config.url, response.data);
                return response;
            },
            error => {
                console.error('响应错误:', error.response ? error.response.data : error.message);
                
                // 如果是401错误，清除令牌并跳转到登录页
                if (error.response && error.response.status === 401) {
                    this.clearToken();
                    if (window.app && window.app.showPage) {
                        window.app.showPage('login');
                        window.app.showMessage('登录已过期，请重新登录', 'error');
                    }
                }
                
                return Promise.reject(error);
            }
        );
    }
    
    // 获取存储的令牌
    getToken() {
        return localStorage.getItem('jwtToken');
    }
    
    // 存储令牌
    setToken(token) {
        localStorage.setItem('jwtToken', token);
    }
    
    // 清除令牌
    clearToken() {
        localStorage.removeItem('jwtToken');
    }
    
    // 登录
    async login(username, password, remember = false) {
        try {
            console.log('API客户端：准备发送登录请求', { username, remember });
            const response = await this.axios.post('/api/auth/login', {
                username: username,
                password: password
            });
            console.log('API客户端：收到登录响应', response.data);
            
            // 如果登录成功，存储令牌
            if (response.data.success && response.data.data && response.data.data.token) {
                this.setToken(response.data.data.token);
            }
            
            return response.data;
        } catch (error) {
            console.error('API客户端：登录请求失败', error);
            throw this.handleError(error);
        }
    }
    
    // 注册
    async register(username, password, email = '') {
        try {
            const response = await this.axios.post('/api/auth/register', {
                username: username,
                password: password,
                email: email
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 获取当前用户信息
    async getCurrentUser() {
        try {
            console.log('API客户端：获取当前用户信息');
            const response = await this.axios.get('/api/auth/current');
            console.log('API客户端：获取用户信息响应', response.data);
            return response.data;
        } catch (error) {
            console.error('API客户端：获取用户信息失败', error);
            throw this.handleError(error);
        }
    }
    
    // 登出
    async logout() {
        try {
            const response = await this.axios.post('/api/auth/logout');
            // 无论API调用是否成功，都清除本地令牌
            this.clearToken();
            return response.data;
        } catch (error) {
            // 即使API调用失败，也清除本地令牌
            this.clearToken();
            throw this.handleError(error);
        }
    }
    
    // ===== 用户管理相关API =====
    
    // 获取所有用户
    async getAllUsers() {
        try {
            const response = await this.axios.get('/api/users');
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 根据ID获取用户
    async getUserById(userId) {
        try {
            const response = await this.axios.get(`/api/users/${userId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 添加用户
    async addUser(userData) {
        try {
            const response = await this.axios.post('/api/users', userData);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 更新用户
    async updateUser(userId, userData) {
        try {
            const response = await this.axios.put(`/api/users/${userId}`, userData);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 删除用户
    async deleteUser(userId) {
        try {
            const response = await this.axios.delete(`/api/users/${userId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 批量删除用户
    async batchDeleteUsers(userIds) {
        try {
            const response = await this.axios.delete('/api/users/batch', {
                data: userIds
            });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 搜索用户
    async searchUsers(keyword, role) {
        try {
            const params = {};
            if (keyword) params.username = keyword; // 后端期望的参数名是username
            if (role) params.role = role;
            
            const response = await this.axios.get('/api/users/search', { params });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // ===== 角色管理相关API =====
    
    // 获取所有角色
    async getAllRoles() {
        try {
            const response = await this.axios.get('/api/roles/list');
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 添加角色
    async addRole(roleData) {
        try {
            const response = await this.axios.post('/api/roles/create', roleData);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 更新角色
    async updateRole(roleId, roleData) {
        try {
            const response = await this.axios.put(`/api/roles/${roleId}`, roleData);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 删除角色
    async deleteRole(roleId) {
        try {
            const response = await this.axios.delete(`/api/roles/${roleId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // ===== 用户角色管理相关API =====
    
    // 获取用户的所有角色
    async getUserRoles(userId) {
        try {
            const response = await this.axios.get(`/api/user-roles/user/${userId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 为用户分配角色
    async assignRolesToUser(userId, roleIds) {
        try {
            const response = await this.axios.post(`/api/user-roles/user/${userId}/assign`, { roleIds });
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 移除用户的所有角色
    async removeAllRolesFromUser(userId) {
        try {
            const response = await this.axios.delete(`/api/user-roles/user/${userId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 移除用户的指定角色
    async removeRoleFromUser(userId, roleId) {
        try {
            const response = await this.axios.delete(`/api/user-roles/user/${userId}/role/${roleId}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 检查用户是否拥有指定角色
    async checkUserHasRole(userId, roleCode) {
        try {
            const response = await this.axios.get(`/api/user-roles/user/${userId}/check/${roleCode}`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 获取角色的所有用户ID
    async getUserIdsByRoleId(roleId) {
        try {
            const response = await this.axios.get(`/api/user-roles/role/${roleId}/users`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 获取用户的所有角色代码
    async getUserRoleCodes(userId) {
        try {
            const response = await this.axios.get(`/api/user-roles/user/${userId}/codes`);
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 错误处理
    handleError(error) {
        if (error.response) {
            // 服务器返回了错误状态码
            const status = error.response.status;
            const data = error.response.data;
            
            if (status === 401) {
                return new Error(data.message || '认证失败，请重新登录');
            } else if (status === 403) {
                return new Error(data.message || '权限不足');
            } else if (status === 404) {
                return new Error(data.message || '请求的资源不存在');
            } else if (status >= 500) {
                return new Error(data.message || '服务器内部错误');
            } else {
                return new Error(data.message || `请求失败 (${status})`);
            }
        } else if (error.request) {
            // 请求已发出但没有收到响应
            return new Error('网络连接失败，请检查网络或服务器是否正常运行');
        } else {
            // 其他错误
            return new Error(error.message || '未知错误');
        }
    }
}

// 创建API客户端实例
const apiClient = new ApiClient(API_BASE_URL);

// 导出API客户端
window.apiClient = apiClient;