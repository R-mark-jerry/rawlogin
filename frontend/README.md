# 前端登录应用

这是一个独立的前端应用程序，与后端API进行通信，提供用户登录和注册功能。

## 功能特性

- 用户登录
- 用户注册
- 登录状态保持
- 用户信息展示
- 响应式设计

## 技术栈

- HTML5
- CSS3
- JavaScript (ES6+)
- Axios (HTTP客户端)
- Live Server (开发服务器)

## 安装和运行

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm start
```

或者

```bash
npm run dev
```

### 3. 访问应用

在浏览器中打开 `http://localhost:3000`

## 配置

### API地址配置

在 `js/api.js` 文件中修改 `API_BASE_URL` 常量来配置后端API地址：

```javascript
const API_BASE_URL = 'http://localhost:8080/myfirst';
```

### 默认账户

系统预置了一个管理员账户：
- 用户名：admin
- 密码：123456

## 项目结构

```
frontend/
├── css/
│   └── style.css      # 样式文件
├── js/
│   ├── api.js         # API通信模块
│   └── app.js         # 应用主逻辑
├── index.html         # 主页面
├── package.json       # 项目配置
└── README.md          # 说明文档
```

## API接口

前端应用使用以下后端API接口：

- `POST /api/ajax-login` - 用户登录
- `POST /register` - 用户注册
- `GET /api/user/current` - 获取当前用户信息
- `POST /logout` - 用户登出

## 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8080/myfirst`
2. 前端应用运行在 `http://localhost:3000`
3. 由于跨域限制，后端需要配置CORS以支持前端请求
4. 登录状态通过Session和Cookie维护

## 开发说明

- 前端应用采用单页面应用(SPA)架构
- 使用原生JavaScript实现，无需额外框架
- 采用模块化设计，分离API通信和应用逻辑
- 支持表单验证和错误处理
- 响应式设计，支持移动端访问