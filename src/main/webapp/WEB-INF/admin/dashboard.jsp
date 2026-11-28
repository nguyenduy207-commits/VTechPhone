<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<!-- Nội dung chính (đã được mở sẵn từ sidebar.jsp) -->
<div class="main-content">
	<h1>🏠 Bảng điều khiển</h1>
	<p class="sub">Chào mừng bạn quay lại, ${sessionScope.user.fullName}!</p>

	<div class="stats-grid">
		<!-- Tổng sản phẩm (Hiển thị cho Admin và Staff) -->
		<a href="${pageContext.request.contextPath}/admin/products"
			class="stat-card-link">
			<div class="stat-card">
				<div class="icon green">
					<i class="bi bi-box"></i>
				</div>
				<div class="info">
					<div class="label">Tổng sản phẩm</div>
					<div class="value">${totalProducts}</div>
				</div>
			</div>
		</a>

		<!-- Tổng đơn hàng (Hiển thị cho Admin và Staff) -->
		<a href="${pageContext.request.contextPath}/admin/orders"
			class="stat-card-link">
			<div class="stat-card">
				<div class="icon blue">
					<i class="bi bi-receipt"></i>
				</div>
				<div class="info">
					<div class="label">Tổng đơn hàng</div>
					<div class="value">${totalOrders}</div>
				</div>
			</div>
		</a>

		<!-- 🔥 Chỉ Admin (Role 1) mới thấy: Người dùng -->
		<c:if test="${sessionScope.user.roleId == 1}">
			<a href="${pageContext.request.contextPath}/admin/users"
				class="stat-card-link">
				<div class="stat-card">
					<div class="icon yellow">
						<i class="bi bi-people"></i>
					</div>
					<div class="info">
						<div class="label">Người dùng</div>
						<div class="value">${totalUsers}</div>
					</div>
				</div>
			</a>
		</c:if>

		<!-- 🔥 Chỉ Admin (Role 1) mới thấy: Doanh thu -->
		<c:if test="${sessionScope.user.roleId == 1}">
			<a href="${pageContext.request.contextPath}/admin/revenue"
				class="stat-card-link">
				<div class="stat-card">
					<div class="icon purple">
						<i class="bi bi-currency-dollar"></i>
					</div>
					<div class="info">
						<div class="label">Doanh thu (tháng)</div>
						<div class="value">
							<fmt:formatNumber value="${monthlyRevenue}" type="currency"
								currencySymbol="₫" maxFractionDigits="0" />
						</div>
					</div>
				</div>
			</a>
		</c:if>
	</div>

	<!-- Quick actions -->
	<div class="quick-actions">
		<a href="${pageContext.request.contextPath}/admin/products"><i
			class="bi bi-plus-circle"></i> Thêm sản phẩm</a>
		<a href="${pageContext.request.contextPath}/admin/orders"><i
			class="bi bi-eye"></i> Xem đơn hàng mới</a>
		
		<!-- 🔥 Chỉ Admin (Role 1) mới thấy: Thêm người dùng -->
		<c:if test="${sessionScope.user.roleId == 1}">
			<a href="${pageContext.request.contextPath}/admin/users"><i
				class="bi bi-person-plus"></i> Thêm người dùng</a>
		</c:if>
	</div>

	<!-- Đơn hàng gần đây (placeholder) -->
	<div
		style="background: #fff; border-radius: 16px; padding: 20px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);">
		<h3 style="margin-bottom: 12px;">📋 Đơn hàng gần đây</h3>
		<p style="color: #888;">Tính năng đang được phát triển...</p>
	</div>
</div>

<style>
/* Bổ sung style cho stat-card link */
.stat-card-link {
	display: block;
	text-decoration: none !important;
	color: inherit !important;
	transition: 0.3s;
}

.stat-card-link:hover .stat-card {
	transform: translateY(-4px);
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.stat-card {
	background: #fff;
	border-radius: 16px;
	padding: 20px 24px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
	display: flex;
	align-items: center;
	gap: 16px;
	transition: 0.3s;
	cursor: pointer;
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	gap: 20px;
	margin-bottom: 32px;
}

.stat-card .icon {
	width: 56px;
	height: 56px;
	border-radius: 14px;
	background: #f0f2f5;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28px;
	color: #1a1a2e;
	flex-shrink: 0;
}

.stat-card .icon.green {
	background: #dcfce7;
	color: #166534;
}

.stat-card .icon.blue {
	background: #dbeafe;
	color: #1e40af;
}

.stat-card .icon.yellow {
	background: #fef3c7;
	color: #92400e;
}

.stat-card .icon.purple {
	background: #e0e7ff;
	color: #3730a3;
}

.stat-card .info .label {
	font-size: 14px;
	color: #888;
}

.stat-card .info .value {
	font-size: 28px;
	font-weight: 700;
}

.quick-actions {
	display: flex;
	gap: 16px;
	flex-wrap: wrap;
	margin-bottom: 24px;
}

.quick-actions a {
	padding: 12px 24px;
	background: #fff;
	border-radius: 12px;
	text-decoration: none;
	color: #1a1a2e;
	font-weight: 600;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
	transition: 0.3s;
	display: flex;
	align-items: center;
	gap: 8px;
}

.quick-actions a:hover {
	background: #a6f36b;
	transform: translateY(-2px);
}

/* Responsive */
@media ( max-width : 768px) {
	.stats-grid {
		grid-template-columns: 1fr 1fr;
	}
	.main-content {
		padding: 16px;
	}
}

@media ( max-width : 480px) {
	.stats-grid {
		grid-template-columns: 1fr;
	}
}
</style>

<!-- Footer (đóng thẻ) -->
<%@ include file="/WEB-INF/includes/footer.jsp"%>