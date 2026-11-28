<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>

<!-- Nội dung chính -->
<div class="main-content">
	<h1>📋 Quản lý đơn hàng</h1>
	<p class="text-muted">Xem và cập nhật trạng thái đơn hàng của khách
		hàng</p>

	<!-- Hiển thị thông báo -->
	<c:if test="${not empty param.success}">
		<div class="alert alert-success alert-dismissible fade show"
			role="alert">
			<i class="bi bi-check-circle"></i> ${param.success}
			<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
		</div>
	</c:if>
	<c:if test="${not empty param.error}">
		<div class="alert alert-danger alert-dismissible fade show"
			role="alert">
			<i class="bi bi-exclamation-circle"></i> ${param.error}
			<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
		</div>
	</c:if>

	<!-- Bảng đơn hàng -->
	<div class="table-responsive bg-white p-3 rounded-3 shadow-sm">
		<table class="table table-hover align-middle mb-0">
			<thead class="table-light">
				<tr>
					<th>Mã ĐH</th>
					<th>Khách hàng</th>
					<th>Ngày đặt</th>
					<th>Tổng tiền</th>
					<th>Trạng thái</th>
					<th>Người xử lý</th>
					<th>Hành động</th>
				</tr>
			</thead>
			<tbody>
				<c:choose>
					<c:when test="${not empty orders}">
						<c:forEach var="o" items="${orders}">
							<tr>
								<td><span class="fw-bold">#${o.orderId}</span></td>
								<td>${o.customer.fullName}</td>
								<!-- Sửa lỗi: hiển thị trực tiếp vì o.orderDate là LocalDateTime -->
								<td>${o.orderDate}</td>
								<td class="fw-bold text-danger"><fmt:formatNumber
										value="${o.totalAmount}" type="currency" currencySymbol="₫"
										maxFractionDigits="0" /></td>
								<td><span
									class="badge 
                                        ${o.status == 'PENDING' ? 'bg-warning text-dark' : 
                                          o.status == 'CONFIRMED' ? 'bg-info text-dark' : 
                                          o.status == 'SHIPPING' ? 'bg-primary' : 
                                          o.status == 'COMPLETED' ? 'bg-success' : 
                                          'bg-danger'}">
										${o.status} </span></td>
								<td>${o.staff != null ? o.staff.fullName : '<span class="text-muted">Chưa xử lý</span>'}</td>
								<td><c:if
										test="${o.status != 'COMPLETED' && o.status != 'CANCELLED'}">
										<button class="btn btn-sm btn-outline-primary"
											onclick="openUpdateModal(${o.orderId}, '${o.status}')">
											<i class="bi bi-pencil"></i> Cập nhật
										</button>
									</c:if> <c:if
										test="${o.status == 'COMPLETED' || o.status == 'CANCELLED'}">
										<span class="text-muted"><i class="bi bi-lock"></i> Đã
											khóa</span>
									</c:if></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="7" class="text-center text-muted py-4"><i
								class="bi bi-inbox" style="font-size: 2rem; display: block;"></i>
								Chưa có đơn hàng nào</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>

<!-- ===== MODAL CẬP NHẬT TRẠNG THÁI ===== -->
<div class="modal fade" id="updateOrderModal" tabindex="-1"
	aria-labelledby="updateOrderModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="updateOrderModalLabel">
					<i class="bi bi-arrow-repeat"></i> Cập nhật trạng thái đơn hàng
				</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>
			<form action="${pageContext.request.contextPath}/admin/orders"
				method="post" id="updateOrderForm"
				onsubmit="return validateStatus()">
				<div class="modal-body">
					<input type="hidden" name="orderId" id="modalOrderId" value="">

					<div class="mb-3">
						<label class="form-label fw-bold">Trạng thái hiện tại</label>
						<p class="form-control-plaintext" id="currentStatusDisplay">---</p>
					</div>

					<div class="mb-3">
						<label class="form-label fw-bold">Chọn trạng thái mới <span
							class="text-danger">*</span></label> <select class="form-select"
							name="status" id="newStatus" required>
							<option value="">-- Chọn trạng thái --</option>
							<option value="CONFIRMED">✅ Xác nhận</option>
							<option value="SHIPPING">🚚 Đang giao</option>
							<option value="COMPLETED">✔️ Hoàn thành</option>
							<option value="CANCELLED">❌ Hủy đơn</option>
						</select>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Hủy</button>
					<button type="submit" class="btn btn-primary" id="confirmUpdateBtn">
						<i class="bi bi-check2"></i> Xác nhận cập nhật
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<style>
.main-content {
	flex: 1;
	padding: 24px 32px;
	overflow-y: auto;
	background: #f4f6f9;
}

.main-content h1 {
	font-size: 28px;
	font-weight: 700;
	margin-bottom: 4px;
}

.badge {
	font-size: 13px;
	padding: 5px 12px;
	font-weight: 600;
}

.table th {
	font-weight: 600;
	color: #555;
}

.table td {
	vertical-align: middle;
}

.btn-outline-primary {
	border-width: 2px;
	font-weight: 600;
}

@media ( max-width : 768px) {
	.main-content {
		padding: 16px;
	}
	.table {
		font-size: 13px;
	}
	.badge {
		font-size: 11px;
		padding: 3px 8px;
	}
}
</style>

<script>
    function openUpdateModal(orderId, currentStatus) {
        document.getElementById('modalOrderId').value = orderId;
        document.getElementById('currentStatusDisplay').textContent = currentStatus;
        const select = document.getElementById('newStatus');
        select.value = '';
        updateConfirmMessage();
        select.classList.remove('is-invalid');
        new bootstrap.Modal(document.getElementById('updateOrderModal')).show();
    }

    function updateConfirmMessage() {
        const select = document.getElementById('newStatus');
        const selected = select.options[select.selectedIndex];
        const confirmText = document.getElementById('confirmText');

        if (selected && selected.value) {
            const statusMap = {
                'CONFIRMED': 'Xác nhận đơn hàng',
                'SHIPPING': 'Chuyển sang trạng thái Đang giao',
                'COMPLETED': 'Đánh dấu đơn hàng đã hoàn thành',
                'CANCELLED': 'Hủy đơn hàng này'
            };
        }
    }

    function validateStatus() {
    	const select = document.getElementById('newStatus');
        const value = select.value;
        if (!value) {
            select.classList.add('is-invalid');
            alert('Vui lòng chọn trạng thái mới trước khi cập nhật.');
            return false;
        }
        const selectedLabel = select.options[select.selectedIndex].text.trim();
        return confirm('Bạn có chắc chắn muốn cập nhật trạng thái đơn hàng thành "' + selectedLabel + '"?');
    });
</script>

<%@ include file="/WEB-INF/includes/footer.jsp"%>