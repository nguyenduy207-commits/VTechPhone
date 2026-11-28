<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>VTechPhone - Quên mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f0f2f5; display: flex; align-items: center; justify-content: center; height: 100vh; }
        .card { width: 420px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border-radius: 16px; padding: 30px; }
    </style>
</head>
<body>
<div class="card bg-white">
    <h3 class="text-center fw-bold mb-3 text-primary">Khôi phục mật khẩu</h3>
    <p class="text-center text-muted mb-4">Nhập email của bạn. Hệ thống sẽ gửi mã OTP nếu email đã được đăng ký.</p>

    <%
        String error = (String) request.getAttribute("error");
        if (error != null) { %>
            <div class="alert alert-danger text-center"> <%= error %> </div>
        <% } 
        String msg = (String) request.getAttribute("message");
        if (msg != null) { %>
            <div class="alert alert-success text-center"> <%= msg %> </div>
        <% } 
    %>

    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
        <div class="mb-3">
            <label class="form-label fw-bold">Email tài khoản</label>
            <input type="email" class="form-control" name="email" placeholder="Nhập email đã đăng ký" required>
        </div>
        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">Gửi mã xác thực</button>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small">Quay lại đăng nhập</a>
    </div>
</div>
</body>
</html>