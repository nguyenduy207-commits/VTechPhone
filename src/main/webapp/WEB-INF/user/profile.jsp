<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Hồ sơ của tôi - VTechPhone</title>
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
	background: #f4f6f9;
	color: #1a1a2e;
}

.container {
	max-width: 960px;
	margin: 0 auto;
	padding: 20px 16px;
}

.profile-wrapper {
	background: #fff;
	border-radius: 20px;
	padding: 32px;
	box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
	margin-top: 20px;
}

.profile-header {
	display: flex;
	align-items: center;
	gap: 24px;
	flex-wrap: wrap;
	margin-bottom: 28px;
	border-bottom: 2px solid #f0f2f5;
	padding-bottom: 20px;
}

.profile-header .avatar-big {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	background: #f0f2f5;
	overflow: hidden;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 48px;
	color: #aaa;
	flex-shrink: 0;
}

.profile-header .avatar-big img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.profile-header .info h2 {
	font-size: 24px;
	font-weight: 700;
}

.profile-header .info p {
	color: #888;
}

.profile-header .info .rank {
	display: inline-block;
	background: #a6f36b;
	padding: 2px 14px;
	border-radius: 12px;
	font-weight: 600;
	font-size: 14px;
	color: #1a1a2e;
	margin-top: 4px;
}

.form-row {
	display: flex;
	gap: 20px;
	flex-wrap: wrap;
}

.form-group {
	flex: 1;
	min-width: 200px;
	margin-bottom: 16px;
}

.form-group label {
	display: block;
	font-weight: 600;
	font-size: 14px;
	color: #333;
	margin-bottom: 4px;
}

.form-group input, .form-group select {
	width: 100%;
	padding: 10px 14px;
	border: 2px solid #e8ecf1;
	border-radius: 10px;
	font-size: 14px;
	transition: 0.3s;
	background: #fafbfc;
}

.form-group input:focus, .form-group select:focus {
	border-color: #a6f36b;
	outline: none;
	box-shadow: 0 0 0 4px rgba(166, 243, 107, 0.2);
	background: #fff;
}

.form-group .file-input {
	padding: 8px;
	border: 2px dashed #e8ecf1;
	border-radius: 10px;
	background: #fafbfc;
	cursor: pointer;
	width: 100%;
}

.form-group .file-input:hover {
	border-color: #a6f36b;
}

.btn-primary {
	padding: 10px 32px;
	background: #a6f36b;
	border: none;
	border-radius: 10px;
	font-weight: 700;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	display: inline-flex;
	align-items: center;
	gap: 6px;
}

.btn-primary:hover {
	background: #93e857;
	transform: translateY(-2px);
	box-shadow: 0 8px 24px rgba(166, 243, 107, 0.3);
}

.btn-secondary {
	padding: 10px 24px;
	border: 2px solid #e8ecf1;
	border-radius: 10px;
	background: #fff;
	font-weight: 600;
	cursor: pointer;
	transition: 0.3s;
}

.btn-secondary:hover {
	border-color: #a6f36b;
	background: #f8fff5;
}

.actions {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
	margin-top: 16px;
	justify-content: flex-end;
}

.alert {
	padding: 12px 20px;
	border-radius: 12px;
	margin-bottom: 16px;
	display: flex;
	align-items: center;
	gap: 10px;
}

.alert-danger {
	background: #fef2f2;
	color: #b91c1c;
	border: 1px solid #fecaca;
}

.alert-success {
	background: #f0fdf4;
	color: #166534;
	border: 1px solid #bbf7d0;
}

.section-title {
	font-size: 18px;
	font-weight: 700;
	margin: 24px 0 12px;
	border-bottom: 2px solid #f0f2f5;
	padding-bottom: 8px;
}

@media ( max-width : 768px) {
	.profile-header {
		flex-direction: column;
		text-align: center;
	}
	.profile-header .info {
		text-align: center;
	}
	.form-row {
		flex-direction: column;
	}
	.actions {
		flex-direction: column;
		align-items: stretch;
	}
}
</style>
</head>
<body>

	<div class="container">
		<div class="profile-wrapper">
			<c:if test="${not empty error}">
				<div class="alert alert-danger">
					<i class="bi bi-exclamation-circle"></i> ${error}
				</div>
			</c:if>
			<c:if test="${not empty success}">
				<div class="alert alert-success">
					<i class="bi bi-check-circle"></i> ${success}
				</div>
			</c:if>

			<form action="${pageContext.request.contextPath}/user/profile"
				method="post" enctype="multipart/form-data">
				<!-- Thông tin cơ bản -->
				<div class="profile-header">
					<div class="avatar-big">
						<c:choose>
							<c:when test="${not empty sessionScope.user.avatarUrl}">
								<img src="${sessionScope.user.avatarUrl}" alt="avatar">
							</c:when>
							<c:otherwise>
								<i class="bi bi-person"></i>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="info">
						<h2>${sessionScope.user.fullName}</h2>
						<p>${sessionScope.user.email}</p>
						<span class="rank">${sessionScope.user.customerRank}</span>
					</div>
				</div>

				<div class="form-row">
					<div class="form-group">
						<label>Họ tên</label> <input type="text" name="fullName"
							value="${sessionScope.user.fullName}" required>
					</div>
					<div class="form-group">
						<label>Giới tính</label> <select name="gender">
							<option value="">Chọn</option>
							<option value="Nam"
								${sessionScope.user.gender == 'Nam' ? 'selected' : ''}>Nam</option>
							<option value="Nữ"
								${sessionScope.user.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
						</select>
					</div>
				</div>
				<div class="form-row">
					<div class="form-group">
						<label>Ngày sinh</label> <input type="date" name="dob"
							value="${sessionScope.user.dob}">
					</div>
					<div class="form-group">
						<label>Số điện thoại</label> <input type="text" name="phone"
							value="${sessionScope.user.phone}">
					</div>
				</div>
				<div class="form-group">
					<label>Địa chỉ</label> <input type="text" name="address"
						value="${sessionScope.user.address}">
				</div>

				<div class="form-group">
					<label>Ảnh đại diện</label> <input type="file" name="avatarFile"
						accept="image/*" class="file-input">
				</div>

				<!-- Đổi mật khẩu -->
				<div class="section-title">Đổi mật khẩu</div>
				<div class="form-row">
					<div class="form-group">
						<label>Mật khẩu hiện tại</label> <input type="password"
							name="oldPassword" placeholder="Nhập mật khẩu cũ">
					</div>
					<div class="form-group">
						<label>Mật khẩu mới</label> <input type="password"
							name="newPassword" placeholder="Ít nhất 6 ký tự">
					</div>
					<div class="form-group">
						<label>Xác nhận mật khẩu</label> <input type="password"
							name="confirmPassword" placeholder="Nhập lại mật khẩu mới">
					</div>
				</div>

				<div class="actions">
					<button type="submit" class="btn-primary">
						<i class="bi bi-save"></i> Cập nhật
					</button>
					<a href="${pageContext.request.contextPath}/shop/products"
						class="btn-secondary">Quay lại</a>
				</div>
			</form>
		</div>
	</div>
	<%@ include file="/WEB-INF/includes/footer.jsp"%>
	<script>
		function validateProfileForm() {
			var oldPass = document.getElementById('oldPass').value;
			var newPass = document.getElementById('newPass').value;
			var confirmPass = document.getElementById('confirmPass').value;

			if (oldPass || newPass || confirmPass) { // nếu có nhập thì phải đủ điều kiện
				if (!oldPass) {
					alert('Vui lòng nhập mật khẩu hiện tại để đổi.');
					return false;
				}
				if (newPass.length < 6) {
					alert('Mật khẩu mới phải có ít nhất 6 ký tự.');
					return false;
				}
				if (newPass !== confirmPass) {
					alert('Xác nhận mật khẩu không khớp.');
					return false;
				}
			}
			// Kiểm tra họ tên
			var fullName = document.getElementById('fullName').value.trim();
			if (fullName.length < 2) {
				alert('Họ tên phải có ít nhất 2 ký tự.');
				return false;
			}
			return true;
		}
		document
				.addEventListener(
						'DOMContentLoaded',
						function() {
							<c:if test="${not empty sessionScope.cartItems}">
							document.getElementById('cartCount').textContent = '${sessionScope.cartItems.size()}';
							</c:if>
						});
	</script>
</body>
</html>