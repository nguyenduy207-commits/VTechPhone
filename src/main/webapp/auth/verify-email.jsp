<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>VTechPhone - Xác thực email</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background: #f0f2f5; display: flex; align-items: center; justify-content: center; height: 100vh; }
        .card { width: 420px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border-radius: 16px; padding: 30px; }
    </style>
</head>
<body>
<div class="card bg-white">
    <h3 class="text-center fw-bold mb-3 text-primary">Xác thực tài khoản</h3>
    <p class="text-center text-muted mb-4">Mã OTP đã được gửi đến email của bạn. Vui lòng nhập mã để kích hoạt tài khoản.</p>

    <%
        String error = (String) request.getAttribute("error");
        if (error != null) { %>
            <div class="alert alert-danger text-center"> <%= error %> </div>
        <% } 
        String message = (String) request.getAttribute("message");
        if (message != null) { %>
            <div class="alert alert-success text-center"> <%= message %> </div>
        <% } 
        String email = (String) request.getAttribute("email");
        if (email == null) email = (String) session.getAttribute("pendingEmail");
    %>

    <form action="${pageContext.request.contextPath}/verify-email" method="post">
        <div class="mb-3">
            <label class="form-label fw-bold">Email</label>
            <input type="email" class="form-control" value="<%= email != null ? email : "" %>" name="email" readonly style="background: #e9ecef;">
        </div>
        <div class="mb-3">
            <label class="form-label fw-bold">Mã OTP (6 chữ số)</label>
            <input type="text" class="form-control" name="otp" placeholder="Nhập mã OTP" required>
        </div>
        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold">Xác thực</button>
    </form>

    <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/resend-otp?email=<%= email != null ? email : "" %>" class="text-decoration-none small">Gửi lại OTP</a>
        <br>
        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small mt-2 d-block">Quay lại đăng nhập</a>
    </div>
</div>
</body>
</html>