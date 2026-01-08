<%@ page import="org.example.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>欢迎页面</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Arial', sans-serif;
        }
        
        body {
            background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        
        .navbar {
            background-color: rgba(255, 255, 255, 0.9);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .navbar h1 {
            color: #2575fc;
            font-size: 24px;
        }
        
        .navbar .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .navbar .user-info span {
            color: #333;
            font-weight: bold;
        }
        
        .navbar .logout-btn {
            background-color: #e74c3c;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.3s;
        }
        
        .navbar .logout-btn:hover {
            background-color: #c0392b;
        }
        
        .welcome-container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .welcome-card {
            background-color: rgba(255, 255, 255, 0.9);
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 25px rgba(0, 0, 0, 0.2);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        
        .welcome-card h2 {
            color: #333;
            font-size: 32px;
            margin-bottom: 20px;
        }
        
        .welcome-card p {
            color: #666;
            font-size: 18px;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        
        .user-details {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            text-align: left;
        }
        
        .user-details h3 {
            color: #2575fc;
            margin-bottom: 15px;
            font-size: 20px;
        }
        
        .user-details table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .user-details td {
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }
        
        .user-details td:first-child {
            font-weight: bold;
            color: #555;
            width: 120px;
        }
        
        .action-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .action-btn {
            background-color: #2575fc;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.3s;
            font-size: 16px;
        }
        
        .action-btn:hover {
            background-color: #1a5dcc;
        }
        
        .action-btn.secondary {
            background-color: #6c757d;
        }
        
        .action-btn.secondary:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>
    <%
        // 检查用户是否已登录
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("index.jsp");
            return;
        }
    %>
    
    <nav class="navbar">
        <h1>用户管理系统</h1>
        <div class="user-info">
            <span>欢迎, <%= user.getUsername() %></span>
            <a href="login?action=logout" class="logout-btn">退出登录</a>
        </div>
    </nav>
    
    <div class="welcome-container">
        <div class="welcome-card">
            <h2>登录成功！</h2>
            <p>欢迎使用用户管理系统，您已成功登录系统。</p>
            
            <div class="user-details">
                <h3>用户信息</h3>
                <table>
                    <tr>
                        <td>用户名：</td>
                        <td><%= user.getUsername() %></td>
                    </tr>
                    <tr>
                        <td>真实姓名：</td>
                        <td><%= user.getRealName() != null ? user.getRealName() : "未设置" %></td>
                    </tr>
                    <tr>
                        <td>邮箱：</td>
                        <td><%= user.getEmail() != null ? user.getEmail() : "未设置" %></td>
                    </tr>
                    <tr>
                        <td>注册时间：</td>
                        <td><%= user.getCreateTime() != null ? user.getCreateTime() : "未知" %></td>
                    </tr>
                    <tr>
                        <td>最后登录：</td>
                        <td><%= user.getLastLoginTime() != null ? user.getLastLoginTime() : "首次登录" %></td>
                    </tr>
                </table>
            </div>
            
            <div class="action-buttons">
                <a href="#" class="action-btn">修改密码</a>
                <a href="#" class="action-btn">个人信息</a>
                <a href="#" class="action-btn secondary">系统设置</a>
            </div>
        </div>
    </div>
</body>
</html>