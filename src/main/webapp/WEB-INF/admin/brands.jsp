<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>

<div class="col-12 p-3 bg-light">
	<h1>Quản lý thương hiệu</h1>

	<c:if test="${not empty error}">
		<div class="alert alert-danger">
			<i class="bi bi-exclamation-circle"></i> ${error}
		</div>
	</c:if>

	<button class="btn btn-primary mb-3" onclick="openModal('create')">
		<i class="bi bi-plus-circle"></i> Thêm thương hiệu
	</button>

	<div class="table-responsive bg-white p-3 rounded shadow-sm">
		<table class="table table-hover">
			<thead class="table-light">
				<tr>
					<th>ID</th>
					<th>Tên</th>
					<th>Mô tả</th>
					<th>Thao tác</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="c" items="${brands}">
					<tr>
						<td>${c.brandId}</td>
						<td>${c.name}</td>
						<td>${c.description}</td>
						<td>
							<button class="btn btn-sm btn-outline-primary"
								onclick="openEditModal('${c.brandId}', '${c.name}', '${c.description}')">
								<i class="bi bi-pencil"></i>
							</button> <a
							href="${pageContext.request.contextPath}/admin/brands?delete=${c.brandId}"
							class="btn btn-sm btn-outline-danger"
							onclick="return confirm('Xóa?')"><i class="bi bi-trash3"></i></a>
						</td>
					</tr>
				</c:forEach>
				<c:if test="${empty brands}">
					<tr>
						<td colspan="4" class="text-center text-muted">Chưa có thương hiệu</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>
</div>

<!-- Modal -->
<div class="modal fade" id="brandModal" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="modalTitle">Thêm thương hiệu</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>
			<form action="${pageContext.request.contextPath}/admin/brands"
				method="post" onsubmit="return validatebrand()">
				<div class="modal-body">
					<input type="hidden" name="formAction" id="formAction"
						value="create"> <input type="hidden" name="id"
						id="brandId" value="">
					<div class="mb-3">
						<label class="form-label fw-bold">Tên thương hiệu <span
							class="text-danger">*</span></label> <input type="text"
							class="form-control" name="name" id="catName" required>
					</div>
					<div class="mb-3">
						<label class="form-label fw-bold">Mô tả</label>
						<textarea class="form-control" name="description" id="catDesc"
							rows="2"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="submit" class="btn btn-primary">Lưu</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
	function validatebrand() {
		var name = document.getElementById('catName').value.trim();
		if (name.length < 2) {
			alert('Tên thương hiệu phải có ít nhất 2 ký tự.');
			return false;
		}
		return true;
	}

	function openModal(action) {
		if (action === 'create') {
			document.getElementById('modalTitle').textContent = 'Thêm thương hiệu mới';
			document.getElementById('formAction').value = 'create';
			document.getElementById('brandId').value = '';
			document.getElementById('catName').value = '';
			document.getElementById('catDesc').value = '';
			new bootstrap.Modal(document.getElementById('brandModal'))
					.show();
		}
	}
	function openEditModal(id, name, desc) {
		document.getElementById('modalTitle').textContent = 'Sửa thương hiệu';
		document.getElementById('formAction').value = 'update';
		document.getElementById('brandId').value = id;
		document.getElementById('catName').value = name;
		document.getElementById('catDesc').value = desc || '';
		new bootstrap.Modal(document.getElementById('brandModal')).show();
	}
</script>

<%@ include file="/WEB-INF/includes/footer.jsp"%>