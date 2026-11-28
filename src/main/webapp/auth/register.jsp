<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VTechPhone - Đăng ký</title>
<!-- Thêm thư viện Bootstrap Icons để dùng Icon con mắt -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<style>
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
	background: #f0f2f5;
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20px;
}

.register-wrapper {
	display: flex;
	max-width: 1100px;
	width: 100%;
	background: #fff;
	border-radius: 16px;
	box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
	overflow: hidden;
}

.register-left {
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

.register-left img {
	width: 100%;
	height: 100%;
	min-height: 480px;
	max-height: 560px;
	max-width: 1000px;
	object-fit: cover;
	display: block;
}

.register-right {
	flex: 1;
	padding: 22px 26px 28px 26px;
	display: flex;
	flex-direction: column;
	justify-content: center;
	overflow-y: auto;
	max-height: 560px;
}

.register-right h2 {
	font-size: 20px;
	color: #1a1a2e;
	margin-bottom: 1px;
	font-weight: 700;
}

.register-right .sub {
	color: #666;
	font-size: 13px;
	margin-bottom: 12px;
	line-height: 1.4;
}

.form-group {
	margin-bottom: 6px;
}

.form-group label {
	display: block;
	font-weight: 600;
	font-size: 12px;
	color: #333;
	margin-bottom: 1px;
}

.form-group input {
	width: 100%;
	padding: 6px 10px;
	border: 1px solid #ddd;
	border-radius: 6px;
	font-size: 13px;
	transition: 0.3s;
	background: #fafafa;
}

.form-group input:focus {
	border-color: #a6f36b;
	outline: none;
	box-shadow: 0 0 0 3px rgba(166, 243, 107, 0.15);
	background: #fff;
}

/* 🔥 Style cho ô input password và nút show/hide */
.password-wrapper {
	position: relative;
}

.password-wrapper input {
	padding-right: 35px !important;
}

.password-toggle {
	position: absolute;
	right: 10px;
	top: 50%;
	transform: translateY(-50%);
	cursor: pointer;
	color: #666;
	font-size: 18px;
	background: transparent;
	border: none;
}

.form-group.checkbox {
	display: flex;
	align-items: center;
	gap: 6px;
	margin-top: 2px;
	margin-bottom: 4px;
}

.form-group.checkbox label {
	display: inline;
	font-weight: 400;
	font-size: 12px;
	color: #555;
	cursor: pointer;
}

.form-group.checkbox input {
	width: 14px;
	height: 14px;
	margin: 0;
	accent-color: #a6f36b;
	cursor: pointer;
}

.btn-primary {
	width: 100%;
	padding: 9px;
	background: #a6f36b;
	border: none;
	border-radius: 6px;
	font-size: 14px;
	font-weight: 600;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	margin-top: 2px;
}

.btn-primary:hover {
	background: #93e857;
	transform: translateY(-1px);
}

.divider {
	display: flex;
	align-items: center;
	gap: 10px;
	margin: 10px 0 4px;
	color: #888;
	font-size: 12px;
}

.divider::before, .divider::after {
	content: "";
	flex: 1;
	height: 1px;
	background: #ddd;
}

.login-link {
	text-align: center;
	margin-top: 10px;
	font-size: 13px;
	color: #555;
}

.login-link a {
	color: #1a1a2e;
	font-weight: 600;
	text-decoration: none;
}

.login-link a:hover {
	text-decoration: underline;
}

.alert {
	padding: 6px 10px;
	border-radius: 6px;
	font-size: 12px;
	margin-bottom: 10px;
}

.alert-danger {
	background: #ffebee;
	color: #b71c1c;
	border: 1px solid #ef9a9a;
}

@media ( max-width : 768px) {
	.register-wrapper {
		flex-direction: column;
		max-width: 100%;
	}
	.register-left {
		min-height: 200px;
		max-height: 260px;
	}
	.register-left img {
		min-height: 200px;
		max-height: 260px;
	}
	.register-right {
		padding: 18px 16px 22px 16px;
		max-height: none;
	}
}

.register-right::-webkit-scrollbar {
	width: 4px;
}

.register-right::-webkit-scrollbar-thumb {
	background: #ccc;
	border-radius: 4px;
}

.register-right::-webkit-scrollbar-track {
	background: transparent;
}
</style>
</head>
<body>
	<div class="register-wrapper">
		<div class="register-left">
			<img
				src="${pageContext.request.contextPath}/assets/images/register.jpg"
				alt="VTechPhone" onerror="this.style.display='none'">
		</div>

		<div class="register-right">
			<h2>Tạo tài khoản</h2>
			<p class="sub">Chào mừng bạn đến với VTechPhone! Vui lòng điền
				đầy đủ thông tin.</p>

			<%
			String error = (String) request.getAttribute("error");
			if (error != null)
				out.print("<div class='alert alert-danger'>" + error + "</div>");
			%>

			<form action="${pageContext.request.contextPath}/register"
				method="post" id="registerForm">
				<div class="form-group">
					<label for="fullname">Họ và tên</label> <input type="text"
						id="fullname" name="fullname" placeholder="Nhập họ và tên"
						value="${fullname}" required>
				</div>
				<div class="form-group">
					<label for="email">Email</label> <input type="email" id="email"
						name="email" placeholder="Nhập email của bạn" value="${email}"
						required>
				</div>

				<!-- 🔥 SỐ ĐIỆN THOẠI: Chặn nhập chữ, giới hạn 10 số -->
				<div class="form-group">
					<label for="phone">Số điện thoại</label> <input type="text"
						id="phone" name="phone" placeholder="Nhập 10 chữ số"
						maxlength="10" value="${phone}"
						oninput="this.value = this.value.replace(/[^0-9]/g, '')">
				</div>

				<div class="form-group">
					<label for="address">Địa chỉ</label> <input type="text"
						id="address" name="address" placeholder="Nhập địa chỉ của bạn"
						value="${address}">
				</div>

				<!-- 🔥 MẬT KHẨU: Thêm nút con mắt để hiện/ẩn -->
				<div class="form-group">
					<label for="password">Mật khẩu</label>
					<div class="password-wrapper">
						<input type="password" id="password" name="password"
							placeholder="Nhập mật khẩu" required>
						<button type="button" class="password-toggle"
							onclick="togglePassword('password', 'togglePassIcon')">
							<i class="bi bi-eye-slash" id="togglePassIcon"></i>
						</button>
					</div>
				</div>

				<!-- 🔥 XÁC NHẬN MẬT KHẨU: Thêm nút con mắt -->
				<div class="form-group">
					<label for="confirmPassword">Xác nhận mật khẩu</label>
					<div class="password-wrapper">
						<input type="password" id="confirmPassword" name="confirmPassword"
							placeholder="Nhập lại mật khẩu" required>
						<button type="button" class="password-toggle"
							onclick="togglePassword('confirmPassword', 'toggleConfirmIcon')">
							<i class="bi bi-eye-slash" id="toggleConfirmIcon"></i>
						</button>
					</div>
				</div>

				<div class="form-group checkbox">
					<input type="checkbox" id="agree" name="agree" value="true"
						required> <label for="agree">Tôi đồng ý với điều
						khoản</label>
				</div>
				<button type="submit" class="btn-primary">Tạo tài khoản</button>
			</form>

			<div class="login-link">
				Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng
					nhập</a>
			</div>
		</div>
	</div>

	<!-- 🔥 JAVASCRIPT: Xử lý show/hide mật khẩu và kiểm tra khớp mật khẩu -->
	<script>
		// Hàm bật/tắt hiện mật khẩu
		function togglePassword(inputId, iconId) {
			const input = document.getElementById(inputId);
			const icon = document.getElementById(iconId);
			if (input.type === "password") {
				input.type = "text";
				icon.classList.remove("bi-eye-slash");
				icon.classList.add("bi-eye");
			} else {
				input.type = "password";
				icon.classList.remove("bi-eye");
				icon.classList.add("bi-eye-slash");
			}
		}

		// Kiểm tra mật khẩu khớp trước khi submit
		document
				.getElementById('registerForm')
				.addEventListener(
						'submit',
						function(e) {
							var pwd = document.getElementById('password').value;
							var confirm = document
									.getElementById('confirmPassword').value;
							if (pwd !== confirm) {
								e.preventDefault();
								alert('Mật khẩu và xác nhận mật khẩu không khớp.');
							}
						});
	</script>

</body>
</html>