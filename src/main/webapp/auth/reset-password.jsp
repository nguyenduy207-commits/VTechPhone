<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>VTechPhone - Đặt lại mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f0f2f5; display: flex; align-items: center; justify-content: center; height: 100vh; }
        .card { width: 420px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border-radius: 16px; padding: 30px; }
    </style>
</head>
<body>
<div class="card bg-white">
    <h3 class="text-center fw-bold mb-3 text-primary">Đặt lại mật khẩu</h3>
    <p class="text-center text-muted mb-4">Nhập mã OTP và mật khẩu mới để hoàn tất.</p>

    <%
        String error = (String) request.getAttribute("error");
        if (error != null) { %>
            <div class="alert alert-danger text-center"> <%= error %> </div>
        <% } 
        String msg = (String) request.getAttribute("message");
        if (msg != null) { %>
            <div class="alert alert-success text-center"> <%= msg %> </div>
        <% } 
        String email = (String) request.getAttribute("email");
    %>

    <form action="${pageContext.request.contextPath}/reset-password" method="post">
        <div class="mb-3">
            <label class="form-label fw-bold">Email</label>
            <input type="email" class="form-control" name="email" value="<%= email != null ? email : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Mã OTP</label>
            <input type="text" class="form-control" name="otp" placeholder="6 chữ số" required>
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Mật khẩu mới</label>
            <input type="password" class="form-control" name="newPassword" placeholder="Ít nhất 6 ký tự" required>
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Xác nhận mật khẩu</label>
            <input type="password" class="form-control" name="confirmPassword" placeholder="Nhập lại mật khẩu mới" required>
        </div>
        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">Đặt lại mật khẩu</button>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small">Quay lại đăng nhập</a>
    </div>
</div>
</body>
</html>