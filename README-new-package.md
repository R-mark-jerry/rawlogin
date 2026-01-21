# 项目包名变更说明

## 变更内容

原项目使用 `org.example` 包名，现已更改为 `com.rawlogin` 包名。

## 新的包结构

```
com.rawlogin
├── RawloginApplication.java     # 主应用程序类
├── api
│   ├── UserController.java       # 用户API控制器
│   └── PageController.java       # 页面API控制器
├── common
│   └── Result.java             # 统一返回结果类
├── config
│   └── SecurityConfig.java      # 安全配置类
├── repository
│   ├── UserRepository.java       # 用户数据访问接口
│   └── UserRepositoryImpl.java  # 用户数据访问实现
├── service
│   ├── UserService.java         # 用户服务接口
│   ├── entity
│   │   └── User.java           # 用户实体类
│   └── impl
│       └── UserServiceImpl.java # 用户服务实现
```

## 如何运行

### 1. 运行后端

使用新的启动脚本：
```bash
start-backend.bat
```

或者直接使用Maven命令：
```bash
mvn spring-boot:run -Dspring-boot.run.main-class=com.rawlogin.RawloginApplication
```

### 2. 运行前端

```bash
start-frontend.bat
```

## 访问地址

- 前端应用：http://localhost:3000
- 后端API：http://localhost:8080/myfirst

## 默认账户

- 用户名：admin
- 密码：123456

## 主要变更

1. 包名从 `org.example` 更改为 `com.rawlogin`
2. 主应用程序类从 `MyfirstApplication` 更改为 `RawloginApplication`
3. 所有Java文件中的包声明已更新
4. 组件扫描配置已更新为扫描 `com.rawlogin` 包
5. 创建了新的启动脚本 `start-backend.bat`

## 注意事项

- 确保数据库连接配置正确
- 前端应用需要后端API支持
- 后端已配置CORS支持，允许前端跨域访问