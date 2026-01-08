<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户登录</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Arial', sans-serif;
        }
        
        body {
            background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .login-container {
            background-color: rgba(255, 255, 255, 0.9);
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 15px 25px rgba(0, 0, 0, 0.2);
            width: 400px;
            max-width: 90%;
            transition: transform 0.3s ease;
        }
        
        .login-container:hover {
            transform: translateY(-5px);
        }
        
        .login-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .login-header h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 10px;
        }
        
        .login-header p {
            color: #666;
            font-size: 16px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: bold;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus {
            border-color: #2575fc;
            outline: none;
        }
        
        .error-message {
            color: #e74c3c;
            font-size: 14px;
            margin-top: 5px;
            display: none;
        }
        
        .server-error {
            color: #e74c3c;
            font-size: 14px;
            margin-top: 10px;
            text-align: center;
            padding: 8px;
            background-color: #ffeaea;
            border-radius: 4px;
            border: 1px solid #f5c6cb;
        }
        
        .login-button {
            width: 100%;
            padding: 12px;
            background-color: #2575fc;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .login-button:hover {
            background-color: #1a5dcc;
        }
        
        .login-footer {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        
        .login-footer a {
            color: #2575fc;
            text-decoration: none;
        }
        
        .login-footer a:hover {
            text-decoration: underline;
        }
        
        .remember-me {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .remember-me input {
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>用户登录</h1>
            <p>请输入您的账号和密码</p>
        </div>
        
        <!-- 显示服务器返回的错误信息 -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="server-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form id="loginForm" action="login" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" name="username" placeholder="请输入用户名">
                <div id="usernameError" class="error-message">用户名不能为空</div>
            </div>
            
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password" placeholder="请输入密码">
                <div id="passwordError" class="error-message">密码不能为空</div>
            </div>
            
            <div class="remember-me">
                <input type="checkbox" id="remember" name="remember">
                <label for="remember">记住我</label>
            </div>
            
            <button type="submit" class="login-button">登录</button>
        </form>
        
        <div class="login-footer">
            <p>还没有账号? <a href="#">立即注册</a></p>
            <p><a href="#">忘记密码?</a></p>
        </div>
    </div>

    <script>
        function validateForm() {
            let isValid = true;
            
            // 验证用户名
            const username = document.getElementById('username').value.trim();
            const usernameError = document.getElementById('usernameError');
            
            if (username === '') {
                usernameError.style.display = 'block';
                isValid = false;
            } else {
                usernameError.style.display = 'none';
            }
            
            // 验证密码
            const password = document.getElementById('password').value.trim();
            const passwordError = document.getElementById('passwordError');
            
            if (password === '') {
                passwordError.style.display = 'block';
                isValid = false;
            } else if (password.length < 6) {
                passwordError.textContent = '密码长度不能少于6位';
                passwordError.style.display = 'block';
                isValid = false;
            } else {
                passwordError.style.display = 'none';
            }
            
            // 如果验证通过，允许表单提交到后端
            if (isValid) {
                // 表单将提交到后端Servlet处理
                return true;
            }
            
            return false; // 验证不通过，阻止表单提交
        }
        
        // 输入框获得焦点时隐藏错误消息
        document.getElementById('username').addEventListener('focus', function() {
            document.getElementById('usernameError').style.display = 'none';
        });
        
        document.getElementById('password').addEventListener('focus', function() {
            document.getElementById('passwordError').style.display = 'none';
        });
    </script>
</body>
</html>
