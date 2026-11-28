<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>VTechPhone - Đăng nhập</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f0f2f5;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .login-wrapper {
            display: flex;
            max-width: 960px;
            width: 100%;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.12);
            overflow: hidden;
        }
        .login-left {
            flex: 1;
            background: #1a1a2e;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            padding: 0;
            min-height: 480px;
            max-height: 560px;
        }
        .login-left img {
            width: 100%;
            height: 100%;
            min-height: 480px;
            max-height: 560px;
            max-width: 1000px;
            object-fit: cover;
            display: block;
        }
        .login-right {
            flex: 1;
            padding: 22px 26px 28px 26px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            overflow-y: auto;
            max-height: 560px;
        }
        .login-right h2 {
            font-size: 24px;
            color: #1a1a2e;
            margin-bottom: 2px;
        }
        .login-right .sub {
            color: #666;
            font-size: 14px;
            margin-bottom: 22px;
        }
        .form-group {
            margin-bottom: 16px;
        }
        .form-group label {
            display: block;
            font-weight: 600;
            font-size: 14px;
            color: #333;
            margin-bottom: 3px;
        }
        .form-group input {
            width: 100%;
            padding: 9px 14px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 14px;
            transition: 0.3s;
            background: #fafafa;
        }
        .form-group input:focus {
            border-color: #a6f36b;
            outline: none;
            box-shadow: 0 0 0 3px rgba(166,243,107,0.2);
            background: #fff;
        }
        .form-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 14px;
            margin-bottom: 18px;
        }
        .form-options .remember {
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .form-options .remember input {
            width: auto;
        }
        .form-options a {
            color: #1a1a2e;
            text-decoration: none;
            font-weight: 500;
        }
        .form-options a:hover {
            text-decoration: underline;
        }
        .btn-primary {
            width: 100%;
            padding: 11px;
            background: #a6f36b;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            color: #1a1a2e;
            cursor: pointer;
            transition: 0.3s;
        }
        .btn-primary:hover {
            background: #93e857;
            transform: translateY(-1px);
        }
        .divider {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 14px 0 8px;
            color: #888;
            font-size: 14px;
        }
        .divider::before, .divider::after {
            content: "";
            flex: 1;
            height: 1px;
            background: #ddd;
        }
        .register-link {
            text-align: center;
            margin-top: 16px;
            font-size: 14px;
        }
        .register-link a {
            color: #1a1a2e;
            font-weight: 600;
            text-decoration: none;
        }
        .register-link a:hover {
            text-decoration: underline;
        }
        .alert {
            padding: 10px 14px;
            border-radius: 8px;
            font-size: 14px;
            margin-bottom: 16px;
        }
        .alert-danger {
            background: #ffebee;
            color: #b71c1c;
            border: 1px solid #ef9a9a;
        }
        .alert-success {
            background: #e8f5e9;
            color: #1b5e20;
            border: 1px solid #a5d6a7;
        }
        @media (max-width: 768px) {
            .login-wrapper { flex-direction: column; }
            .login-left { min-height: 200px; }
            .login-left img { min-height: 200px; }
            .login-right { padding: 24px 20px; }
        }
    </style>
</head>
<body>
<div class="login-wrapper">
    <div class="login-left">
        <img src="${pageContext.request.contextPath}/assets/images/phone-illustration.jpg" 
             alt="VTechPhone" 
             onerror="this.style.display='none'">
    </div>
    <div class="login-right">
        <h2>Đăng nhập</h2>
        <p class="sub">Chào mừng bạn trở lại! Vui lòng đăng nhập để tiếp tục mua sắm.</p>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) out.print("<div class='alert alert-danger'>" + error + "</div>");
            String msg = (String) session.getAttribute("message");
            if (msg != null) {
                out.print("<div class='alert alert-success'>" + msg + "</div>");
                session.removeAttribute("message");
            }
        %>

        <%
            String savedEmail = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("userEmail".equals(c.getName())) {
                        savedEmail = c.getValue();
                        break;
                    }
                }
            }
        %>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="Nhập email của bạn" value="<%= savedEmail %>" required>
            </div>
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn" required>
            </div>
            <div class="form-options">
                <label class="remember">
                    <input type="checkbox" name="remember" value="true" <%= !savedEmail.isEmpty() ? "checked" : "" %>> Ghi nhớ tôi
                </label>
                <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
            </div>
            <button type="submit" class="btn-primary">Đăng nhập</button>
        </form>

        <div class="register-link">
            Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
        </div>
    </div>
</div>
</body>
</html>