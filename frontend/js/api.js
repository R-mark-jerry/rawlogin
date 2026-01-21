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
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true // 重要：支持跨域cookie
        });
        
        // 请求拦截器
        this.axios.interceptors.request.use(
            config => {
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
                return Promise.reject(error);
            }
        );
    }
    
    // 登录
    async login(username, password, remember = false) {
        try {
            console.log('API客户端：准备发送登录请求', { username, remember });
            const response = await this.axios.post('/api/ajax-login',
                new URLSearchParams({
                    username: username,
                    password: password,
                    remember: remember
                })
            );
            console.log('API客户端：收到登录响应', response.data);
            return response.data;
        } catch (error) {
            console.error('API客户端：登录请求失败', error);
            throw this.handleError(error);
        }
    }
    
    // 注册
    async register(username, password, email = '', realName = '') {
        try {
            const response = await this.axios.post('/api/user/register',
                new URLSearchParams({
                    username: username,
                    password: password,
                    email: email,
                    realName: realName
                })
            );
            return response.data;
        } catch (error) {
            throw this.handleError(error);
        }
    }
    
    // 获取当前用户信息
    async getCurrentUser() {
        try {
            console.log('API客户端：获取当前用户信息');
            const response = await this.axios.get('/api/user/current');
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
            const response = await this.axios.post('/api/user/logout');
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