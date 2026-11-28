<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!--
    SIDEBAR QUẢN TRỊ
    Chỉ hiển thị khi user có role ADMIN (1) hoặc STAFF (2)
    Được include vào các trang admin (sau header, trước main-content)
-->

<c:if
	test="${sessionScope.user.roleId == 1 or sessionScope.user.roleId == 2}">
	<style>
/* ===== SIDEBAR STYLES ===== */
.admin-sidebar {
	width: 260px;
	background: #1a1a2e;
	color: #fff;
	padding: 20px 12px;
	display: flex;
	flex-direction: column;
	gap: 4px;
	position: sticky;
	top: 70px; /* vừa dưới header */
	height: calc(100vh - 70px);
	overflow-y: auto;
	flex-shrink: 0;
	border-right: 1px solid rgba(255, 255, 255, 0.05);
	z-index: 100;
}

.admin-sidebar .sidebar-logo {
	font-size: 20px;
	font-weight: 700;
	color: #a6f36b;
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 4px 12px 16px 12px;
	border-bottom: 1px solid rgba(255, 255, 255, 0.06);
	margin-bottom: 8px;
}

.admin-sidebar .sidebar-logo img {
	height: 32px;
	width: auto;
}

.admin-sidebar .sidebar-logo i {
	font-size: 24px;
}

.admin-sidebar .sidebar-link {
	color: #aaa;
	text-decoration: none;
	padding: 10px 14px;
	border-radius: 10px;
	font-weight: 500;
	transition: 0.3s;
	display: flex;
	align-items: center;
	gap: 12px;
	font-size: 14px;
}

.admin-sidebar .sidebar-link:hover {
	background: rgba(166, 243, 107, 0.12);
	color: #a6f36b;
}

.admin-sidebar .sidebar-link.active {
	background: rgba(166, 243, 107, 0.15);
	color: #a6f36b;
	font-weight: 600;
}

.admin-sidebar .sidebar-link i {
	font-size: 20px;
	width: 24px;
	text-align: center;
}

.admin-sidebar .sidebar-divider {
	border-top: 1px solid rgba(255, 255, 255, 0.06);
	margin: 8px 12px;
}

.admin-sidebar .sidebar-user {
	margin-top: auto;
	padding-top: 12px;
	border-top: 1px solid rgba(255, 255, 255, 0.06);
	display: flex;
	align-items: center;
	gap: 12px;
}

.admin-sidebar .sidebar-user .avatar-sm {
	width: 36px;
	height: 36px;
	border-radius: 50%;
	background: #a6f36b;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #1a1a2e;
	font-weight: 700;
	overflow: hidden;
	flex-shrink: 0;
}

.admin-sidebar .sidebar-user .avatar-sm img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.admin-sidebar .sidebar-user .user-info {
	flex: 1;
	min-width: 0;
}

.admin-sidebar .sidebar-user .user-info .name {
	font-weight: 600;
	font-size: 14px;
	color: #eee;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.admin-sidebar .sidebar-user .user-info .role {
	font-size: 12px;
	color: #888;
}

.admin-sidebar .sidebar-user .logout-link {
	color: #f87171;
	font-size: 13px;
	text-decoration: none;
	padding: 4px 10px;
	border-radius: 20px;
	border: 1px solid #f87171;
	transition: 0.3s;
}

.admin-sidebar .sidebar-user .logout-link:hover {
	background: #f87171;
	color: #1a1a2e;
}

/* Scrollbar đẹp */
.admin-sidebar::-webkit-scrollbar {
	width: 4px;
}

.admin-sidebar::-webkit-scrollbar-thumb {
	background: #a6f36b;
	border-radius: 4px;
}

.admin-sidebar::-webkit-scrollbar-track {
	background: transparent;
}

/* Responsive: sidebar thu gọn trên mobile */
@media ( max-width : 768px) {
	.admin-sidebar {
		width: 100%;
		height: auto;
		position: static;
		flex-direction: row;
		flex-wrap: wrap;
		gap: 2px;
		padding: 12px 10px;
		border-right: none;
		border-bottom: 1px solid rgba(255, 255, 255, 0.05);
	}
	.admin-sidebar .sidebar-logo {
		display: none;
	}
	.admin-sidebar .sidebar-link {
		padding: 6px 12px;
		font-size: 13px;
		white-space: nowrap;
	}
	.admin-sidebar .sidebar-link i {
		font-size: 16px;
		width: 20px;
	}
	.admin-sidebar .sidebar-divider {
		display: none;
	}
	.admin-sidebar .sidebar-user {
		display: none;
	}
}

@media ( max-width : 480px) {
	.admin-sidebar .sidebar-link {
		font-size: 12px;
		padding: 4px 8px;
	}
	.admin-sidebar .sidebar-link i {
		font-size: 14px;
		width: 18px;
	}
}
</style>

	<!-- ====== SIDEBAR HTML ====== -->
	<nav class="admin-sidebar">
		<div class="sidebar-logo">
			<img src="${pageContext.request.contextPath}/assets/images/logo2.jpg"
				alt="VTechPhone" style="height: 32px; width: auto;"
				onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
			<i class="bi bi-phone" style="display: none;"></i> <span>Quản trị</span>
		</div>

		<c:set var="currentUri" value="${pageContext.request.requestURI}" />

		<!-- ===== MENU CHUNG (ADMIN + STAFF) ===== -->
		<a
			class="sidebar-link ${currentUri.contains('/admin/dashboard') ? 'active' : ''}"
			href="${pageContext.request.contextPath}/admin/dashboard"> <i
			class="bi bi-grid"></i> Tổng quan
		</a> <a
			class="sidebar-link ${currentUri.contains('/admin/products') ? 'active' : ''}"
			href="${pageContext.request.contextPath}/admin/products"> <i
			class="bi bi-box"></i> Sản phẩm
		</a> <a
			class="sidebar-link ${currentUri.contains('/admin/orders') ? 'active' : ''}"
			href="${pageContext.request.contextPath}/admin/orders"> <i
			class="bi bi-receipt"></i> Đơn hàng
		</a>

		<!-- ===== MENU CHỈ DÀNH CHO ADMIN ===== -->
		<c:if test="${sessionScope.user.roleId == 1}">
			<a
				class="sidebar-link ${currentUri.contains('/admin/categories') ? 'active' : ''}"
				href="${pageContext.request.contextPath}/admin/categories"> <i
				class="bi bi-tags"></i> Danh mục
			</a>
			<a
				class="sidebar-link ${currentUri.contains('/admin/brands') ? 'active' : ''}"
				href="${pageContext.request.contextPath}/admin/brands"> <i
				class="bi bi-building"></i> Thương hiệu
			</a>
			<a
				class="sidebar-link ${currentUri.contains('/admin/users') ? 'active' : ''}"
				href="${pageContext.request.contextPath}/admin/users"> <i
				class="bi bi-people"></i> Người dùng
			</a>
			<a
				class="sidebar-link ${currentUri.contains('/admin/revenue') ? 'active' : ''}"
				href="${pageContext.request.contextPath}/admin/revenue"> <i
				class="bi bi-graph-up"></i> Doanh thu
			</a>
		</c:if>

		<div class="sidebar-divider"></div>
		<!-- Thông tin user -->
		<div class="sidebar-user">
			<div class="avatar-sm">
				<c:choose>
					<c:when test="${not empty sessionScope.user.avatarUrl}">
						<img src="${sessionScope.user.avatarUrl}" alt="avatar">
					</c:when>
					<c:otherwise>
						<i class="bi bi-person"></i>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="user-info">
				<a
					class="sidebar-link ${currentUri.contains('/admin/users') ? 'active' : ''}"
					href="${pageContext.request.contextPath}/user/profile">
					<div class="name">${sessionScope.user.fullName}</div>
					<div class="role">${sessionScope.user.roleId == 1 ? 'Admin' : 'Staff'}</div>
				</a>
			</div>

		</div>
	</nav>

	<!-- 🔥 Mở thẻ div cho nội dung chính (bên phải sidebar) -->
	<div class="main-content"
		style="flex: 1; padding: 24px 32px; overflow-y: auto; min-height: calc(100vh - 70px);">
</c:if>

<!-- Nếu KHÔNG phải Admin/Staff, chỉ mở div chiếm toàn bộ chiều rộng -->
<c:if
	test="${sessionScope.user.roleId != 1 and sessionScope.user.roleId != 2}">
	<div class="col-12 p-3">
</c:if>