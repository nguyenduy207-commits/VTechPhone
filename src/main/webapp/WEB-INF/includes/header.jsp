<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>VTechPhone</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<style>
:root {
	--primary-dark: #021024;
	--primary-gradient: linear-gradient(90deg, #021024 0%, #2d1b4e 100%);
	--accent-green: #a6f36b;
}

* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	 background-color: #ffffff;
	font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
}

/* ===== HEADER ===== */
.custom-navbar {
	background: var(--primary-gradient);
	height: 70px;
	border-bottom: 3px solid var(--accent-green);
	padding: 0 1.5rem;
	position: sticky;
	top: 0;
	z-index: 1050;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

/* Logo - Tăng kích thước */
.custom-navbar .navbar-brand {
	font-weight: 700;
	color: var(--accent-green) !important;
	letter-spacing: 1px; /* Tăng giãn chữ */
	font-size: 2rem; /* To hơn (trước 1.5rem) */
	display: flex;
	align-items: center;
	gap: 10px;
	margin-right: 24px; /* Khoảng cách sang phải */
	padding: 4px 0;
}

.custom-navbar .navbar-brand img {
	align-items: center;
	height: 50px; /* To hơn (trước 38px) */
	width: auto;
	display: block;
}

.custom-navbar .navbar-brand .brand-text {
	display: inline-block;
}

/* ===== PHẦN BÊN TRÁI (Logo + Nav + Search) ===== */
.navbar-left {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}

/* Navigation links */
.nav-link-custom {
	color: #b0b0b0 !important;
	text-decoration: none;
	transition: 0.3s;
	font-weight: 500;
	padding: 6px 12px;
	border-radius: 8px;
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 14px;
}

.nav-link-custom:hover {
	color: var(--accent-green) !important;
	background: rgba(166, 243, 107, 0.08);
}

.nav-link-custom i {
	font-size: 18px;
}

/* ===== THANH TÌM KIẾM ===== */
.search-wrapper {
	display: flex;
	align-items: center;
	background: rgba(255, 255, 255, 0.12);
	border-radius: 30px;
	padding: 2px 2px 2px 16px;
	transition: all 0.3s ease;
	border: 2px solid transparent;
	min-width: 320px;
}

.search-wrapper:focus-within {
	background: rgba(255, 255, 255, 0.2);
	border-color: var(--accent-green);
	box-shadow: 0 0 0 4px rgba(166, 243, 107, 0.15);
}

.search-wrapper .search-input {
	border: none;
	background: transparent;
	padding: 7px 0;
	font-size: 14px;
	color: #fff;
	outline: none;
	width: 100%;
	min-width: 120px;
}

.search-wrapper .search-input::placeholder {
	color: rgba(255, 255, 255, 0.5);
	font-weight: 300;
}

.search-wrapper .search-btn {
	background: var(--accent-green);
	color: #021024;
	border: none;
	border-radius: 30px;
	padding: 6px 18px;
	font-weight: 600;
	font-size: 14px;
	transition: 0.3s;
	display: flex;
	align-items: center;
	gap: 4px;
	cursor: pointer;
	white-space: nowrap;
}

.search-wrapper .search-btn:hover {
	background: #93e857;
	transform: scale(1.02);
}

/* ===== PHẦN BÊN PHẢI ===== */
.navbar-right {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-left: auto;
	flex-wrap: wrap;
}

/* Cart badge */
.cart-badge-wrapper {
	position: relative;
	display: inline-flex;
	align-items: center;
}

.cart-badge-wrapper .badge-count {
	position: absolute;
	top: -6px;
	right: -8px;
	background: var(--accent-green);
	color: #1a1a2e;
	font-size: 11px;
	font-weight: 700;
	border-radius: 50%;
	padding: 2px 6px;
	min-width: 20px;
	text-align: center;
	line-height: 1.2;
	border: 2px solid #1a1a2e;
}

/* ====== AVATAR – LUÔN TRÒN ====== */
.avatar-link {
	display: flex;
	align-items: center;
	gap: 8px;
	color: white !important;
	text-decoration: none;
	transition: 0.3s;
	padding: 4px 8px;
	border-radius: 30px;
}

.avatar-link:hover {
	background: rgba(166, 243, 107, 0.1);
	color: var(--accent-green) !important;
}

.avatar-img {
	width: 36px !important;
	height: 36px !important;
	min-width: 36px !important;
	min-height: 36px !important;
	border-radius: 50% !important;
	object-fit: cover !important;
	border: 2px solid var(--accent-green) !important;
	background: #2d1b4e !important;
	flex-shrink: 0 !important;
	display: inline-block !important;
}

.avatar-img-placeholder {
	width: 36px !important;
	height: 36px !important;
	min-width: 36px !important;
	min-height: 36px !important;
	border-radius: 50% !important;
	background: var(--accent-green) !important;
	display: flex !important;
	align-items: center !important;
	justify-content: center !important;
	color: #1a1a2e !important;
	font-weight: 700 !important;
	font-size: 16px !important;
	border: 2px solid var(--accent-green) !important;
	flex-shrink: 0 !important;
}

.user-name {
	color: #eee;
	font-size: 14px;
	font-weight: 500;
	white-space: nowrap;
}

.btn-logout {
	color: #f87171 !important;
	font-size: 13px;
	padding: 4px 12px;
	border-radius: 20px;
	border: 1px solid #f87171;
	transition: 0.3s;
	background: transparent;
	text-decoration: none;
}

.btn-logout:hover {
	background: #f87171;
	color: #1a1a2e !important;
	text-decoration: none;
}

/* Dropdown Admin */
.dropdown-menu-admin {
	background-color: #1a1a2e;
	border: 1px solid var(--accent-green);
	border-radius: 12px;
	box-shadow: 0 8px 20px rgba(0, 0, 0, 0.5);
	padding: 6px 0;
	min-width: 200px;
}

.dropdown-menu-admin .dropdown-item {
	color: #ccc;
	padding: 8px 18px;
	transition: 0.2s;
	font-size: 14px;
	display: flex;
	align-items: center;
	gap: 10px;
}

.dropdown-menu-admin .dropdown-item:hover {
	background-color: var(--accent-green);
	color: #1a1a2e;
}

.dropdown-menu-admin .dropdown-divider {
	border-color: rgba(255, 255, 255, 0.08);
}

/* Auth buttons */
.btn-outline-light-custom {
	color: #fff;
	border: 1px solid rgba(255, 255, 255, 0.3);
	transition: 0.3s;
	border-radius: 20px;
	padding: 6px 18px;
	font-weight: 500;
	font-size: 14px;
	text-decoration: none;
}

.btn-outline-light-custom:hover {
	background: var(--accent-green);
	border-color: var(--accent-green);
	color: #1a1a2e;
}

.btn-register-custom {
	background: var(--accent-green);
	color: #021024;
	font-weight: 700;
	border-radius: 20px;
	padding: 6px 20px;
	border: none;
	transition: 0.3s;
	font-size: 14px;
	text-decoration: none;
}

.btn-register-custom:hover {
	background: #93e857;
	transform: translateY(-1px);
	box-shadow: 0 4px 16px rgba(166, 243, 107, 0.3);
	color: #021024;
}

/* Navbar toggler */
.navbar-toggler {
	border-color: rgba(255, 255, 255, 0.3);
}

.navbar-toggler-icon {
	background-image:
		url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba(255,255,255,0.8)' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}

/* Responsive */
@media ( max-width : 992px) {
	.custom-navbar {
		height: auto;
		padding: 10px 1rem;
	}
	.navbar-left {
		flex-direction: column;
		align-items: stretch;
		width: 100%;
		gap: 8px;
	}
	.navbar-right {
		flex-direction: column;
		align-items: stretch;
		width: 100%;
		gap: 8px;
		margin-left: 0;
		border-top: 1px solid rgba(255, 255, 255, 0.05);
		padding-top: 12px;
		margin-top: 12px;
	}
	.search-wrapper {
		width: 100%;
		min-width: 0;
	}
	.search-wrapper .search-input {
		min-width: 0;
	}
	.user-name {
		font-size: 13px;
	}
}

@media ( max-width : 576px) {
	.custom-navbar .navbar-brand img {
		height: 30px;
	}
	.custom-navbar .navbar-brand .brand-text {
		font-size: 18px;
	}
	.btn-register-custom {
		padding: 4px 14px;
		font-size: 13px;
	}
	.search-wrapper .search-btn span {
		display: none;
	}
}
</style>
</head>
<body>

	<nav class="navbar navbar-expand-lg custom-navbar">
		<div
			class="container-fluid px-0 d-flex align-items-center justify-content-between">
			<!-- Logo + Trang chủ + Tìm kiếm (bên trái) -->
			<div class="navbar-left">
				<a class="navbar-brand"
					href="${pageContext.request.contextPath}/shop/products"> <img
					src="${pageContext.request.contextPath}/assets/images/logo2.png"
					alt="VTechPhone" style="height: 100px; width: auto;"
					onerror="this.style.display='none'; this.nextElementSibling.style.display='inline-block';">
					<span class="brand-text" style="display: none;"> <i
						class="bi bi-phone me-1"></i>VTechPhone
				</span>
				</a>

				<!-- Trang chủ (luôn hiển thị trên desktop, ẩn trên mobile sẽ ở trong collapse) -->
				<a class="nav-link-custom d-none d-lg-inline-flex"
					href="${pageContext.request.contextPath}/shop/products"> <i
					class="bi bi-house"></i> <span>Trang chủ</span>
				</a>

				<!-- Thanh tìm kiếm (hiển thị trên desktop) -->
				<form class="search-wrapper d-none d-lg-flex"
					action="${pageContext.request.contextPath}/shop/products"
					method="get">
					<input class="search-input" type="search" name="keyword"
						placeholder="Bạn muốn mua gì hôm nay?" aria-label="Search"
						value="${param.keyword}">
					<button class="search-btn" type="submit">
						<i class="bi bi-search"></i> <span>Tìm</span>
					</button>
				</form>
			</div>

			<!-- Toggler cho mobile -->
			<button class="navbar-toggler" type="button"
				data-bs-toggle="collapse" data-bs-target="#navbarContent">
				<span class="navbar-toggler-icon"></span>
			</button>

			<!-- Menu bên phải + Collapse trên mobile -->
			<div class="collapse navbar-collapse" id="navbarContent">
				<!-- Hiển thị các item trên mobile (vì đã ẩn trên desktop) -->
				<ul class="navbar-nav ms-auto align-items-lg-center gap-1 gap-lg-3">
					<!-- Trang chủ (chỉ hiện trên mobile) -->
					<li class="nav-item d-lg-none"><a class="nav-link-custom"
						href="${pageContext.request.contextPath}/shop/products"> <i
							class="bi bi-house"></i> <span>Trang chủ</span>
					</a></li>

					<!-- Tìm kiếm (chỉ hiện trên mobile) -->
					<li class="nav-item d-lg-none" style="width: 100%;">
						<form class="search-wrapper"
							action="${pageContext.request.contextPath}/shop/products"
							method="get" style="width: 100%;">
							<input class="search-input" type="search" name="keyword"
								placeholder="Bạn muốn mua gì hôm nay?" aria-label="Search"
								value="${param.keyword}">
							<button class="search-btn" type="submit">
								<i class="bi bi-search"></i> <span>Tìm</span>
							</button>
						</form>
					</li>

					<!-- Giỏ hàng -->
					<li class="nav-item"><a class="nav-link-custom"
						href="${pageContext.request.contextPath}/shop/cart"> <span
							class="cart-badge-wrapper"> <i class="bi bi-bag"></i>
								<c:set var="cartSize" value="${sessionScope.cartItems.size()}" />
								<!-- Chỉ hiển thị badge nếu > 0 --> 
								<c:if test="${cartSize > 0}">
									<span class="badge-count">${cartSize}</span>
								</c:if>
						</span> <span class="d-none d-sm-inline">Giỏ hàng</span>
					</a></li>

					<!-- Đơn hàng -->
					<li class="nav-item"><a class="nav-link-custom"
						href="${pageContext.request.contextPath}/shop/my-orders"> <i
							class="bi bi-clipboard"></i> <span class="d-none d-sm-inline">Đơn
								hàng</span>
					</a></li>

					<!-- User / Auth -->
					<c:choose>
						<c:when test="${not empty sessionScope.user}">
							<li class="nav-item"><a class="avatar-link"
								href="${pageContext.request.contextPath}/user/profile"> <c:choose>
										<c:when test="${not empty sessionScope.user.avatarUrl}">
											<img src="${sessionScope.user.avatarUrl}" alt="Avatar"
												class="avatar-img"
												onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/no-image.png';">
										</c:when>
										<c:otherwise>
											<span class="avatar-img-placeholder"> <c:choose>
													<c:when test="${not empty sessionScope.user.fullName}">
                                                    ${fn:substring(sessionScope.user.fullName, 0, 1)}
                                                </c:when>
													<c:otherwise>
														<i class="bi bi-person"></i>
													</c:otherwise>
												</c:choose>
											</span>
										</c:otherwise>
									</c:choose> <span class="user-name d-none d-md-inline">${sessionScope.user.fullName}</span>
							</a></li>
							<li class="nav-item"><a
								href="${pageContext.request.contextPath}/logout"
								class="btn-logout"
								onclick="return confirm('Bạn có chắc muốn đăng xuất?')"> <i
									class="bi bi-box-arrow-right"></i> <span
									class="d-none d-sm-inline">Đăng xuất</span>
							</a></li>
						</c:when>
						<c:otherwise>
							<li class="nav-item"><a
								href="${pageContext.request.contextPath}/login"
								class="btn-outline-light-custom">Đăng nhập</a></li>
							<li class="nav-item"><a
								href="${pageContext.request.contextPath}/register"
								class="btn-register-custom">Đăng ký</a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div>
	</nav>

	<!-- Cập nhật số lượng giỏ hàng -->
	<script>
		document.addEventListener('DOMContentLoaded', function() {
			<c:if test="${not empty sessionScope.cartItems}">
			const count = $
			{
				sessionScope.cartItems.size()
			}
			;
			const badge = document.getElementById('cartCount');
			if (badge)
				badge.textContent = count;
			</c:if>
		});
	</script>

	<!-- Mở container chính -->
	<div class="container-fluid p-0">
		<div class="row g-0 main-row" style="min-height: calc(100vh - 70px);">