<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>

<div class="col-12 p-3 bg-light">
	<h1>Quản lý người dùng</h1>

	<c:if test="${not empty error}">
		<div class="alert alert-danger">
			<i class="bi bi-exclamation-circle"></i> ${error}
		</div>
	</c:if>

	<button class="btn btn-primary mb-3" onclick="openModal()">
		<i class="bi bi-person-plus"></i> Thêm người dùng
	</button>

	<div class="table-responsive bg-white p-3 rounded shadow-sm">
		<table class="table table-hover">
			<thead class="table-light">
				<tr>
					<th>ID</th>
					<th>Họ tên</th>
					<th>Email</th>
					<th>Vai trò</th>
					<th>Trạng thái</th>
					<th>Điểm</th>
					<th>Thao tác</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="u" items="${users}">
					<tr>
						<td>${u.userId}</td>
						<td>${u.fullName}</td>
						<td>${u.email}</td>
						<td><span
							class="badge ${u.roleId == 1 ? 'bg-danger' : u.roleId == 2 ? 'bg-warning' : 'bg-success'}">
								${u.roleId == 1 ? 'ADMIN' : u.roleId == 2 ? 'STAFF' : 'CUSTOMER'}
						</span></td>
						<td><span
							class="badge ${u.active ? 'bg-success' : 'bg-secondary'}">${u.active ? 'Hoạt động' : 'Khóa'}</span></td>
						<td>${u.customerPoint}</td>
						<td><a
							href="${pageContext.request.contextPath}/admin/users?delete=${u.userId}"
							class="btn btn-sm btn-outline-danger"
							onclick="return confirm('Xóa người dùng này?')"><i
								class="bi bi-trash3"></i></a></td>
					</tr>
				</c:forEach>
				<c:if test="${empty users}">
					<tr>
						<td colspan="7" class="text-center text-muted">Chưa có người
							dùng</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</div>

<!-- Modal thêm người dùng -->
<div class="modal fade" id="userModal" tabindex="-1">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">Thêm người dùng mới</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>
			<form action="${pageContext.request.contextPath}/admin/users"
				method="post" onsubmit="return validateUserForm()">
				<div class="modal-body">
					<input type="hidden" name="formAction" value="create">
					<div class="row">
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Họ tên <span
									class="text-danger">*</span></label> <input type="text"
									class="form-control" name="fullName" id="uName" required>
							</div>
						</div>
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Email <span
									class="text-danger">*</span></label> <input type="email"
									class="form-control" name="email" id="uEmail" required>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Số điện thoại</label> <input
									type="text" class="form-control" name="phone" id="uPhone"
									placeholder="0xxxxxxxxx">
							</div>
						</div>
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Địa chỉ</label> <input
									type="text" class="form-control" name="address">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Mật khẩu <span
									class="text-danger">*</span></label> <input type="password"
									class="form-control" name="password" id="uPass" required
									minlength="6">
							</div>
						</div>
						<div class="col-md-6">
							<div class="mb-3">
								<label class="form-label fw-bold">Xác nhận <span
									class="text-danger">*</span></label> <input type="password"
									class="form-control" name="confirmPassword" id="uConfirm"
									required>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4">
							<div class="mb-3">
								<label class="form-label fw-bold">Vai trò</label> <select
									class="form-select" name="roleId">
									<option value="3">Khách hàng</option>
									<option value="2">Nhân viên</option>
									<option value="1">Admin</option>
								</select>
							</div>
						</div>
						<div class="col-md-4">
							<div class="mb-3">
								<label class="form-label fw-bold">Chức vụ</label> <input
									type="text" class="form-control" name="staffPosition">
							</div>
						</div>
						<div class="col-md-4">
							<div class="mb-3">
								<label class="form-label fw-bold">Ngày tuyển</label> <input
									type="date" class="form-control" name="hireDate">
							</div>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="submit" class="btn btn-primary">Tạo tài
						khoản</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
	function validateUserForm() {
		var name = document.getElementById('uName').value.trim();
		var email = document.getElementById('uEmail').value.trim();
		var phone = document.getElementById('uPhone').value.trim();
		var pass = document.getElementById('uPass').value;
		var confirm = document.getElementById('uConfirm').value;

		if (name.length < 2) {
			alert('Họ tên phải có ít nhất 2 ký tự.');
			return false;
		}
		if (!/^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email)) {
			alert('Email phải là Gmail hợp lệ (ví dụ: ten@gmail.com).');
			return false;
		}
		if (phone && !/^0[0-9]{8,14}$/.test(phone)) {
			alert('Số điện thoại phải bắt đầu bằng 0 và có 9-15 chữ số.');
			return false;
		}
		if (pass.length < 6) {
			alert('Mật khẩu phải có ít nhất 6 ký tự.');
			return false;
		}
		if (pass !== confirm) {
			alert('Xác nhận mật khẩu không khớp.');
			return false;
		}
		return true;
	}

	function openModal() {
		document.getElementById('userModal').querySelector('form').reset();
		new bootstrap.Modal(document.getElementById('userModal')).show();
	}
</script>

<%@ include file="/WEB-INF/includes/footer.jsp"%>