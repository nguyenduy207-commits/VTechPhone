<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>VTechPhone - Cửa hàng</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
<style>
/* Reset & base */
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
	max-width: 1280px;
	margin: 0 auto;
	padding: 20px 16px;
}

/* Filter bar */
.filter-bar {
	background: #fff;
	border-radius: 16px;
	padding: 20px 24px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
	margin-bottom: 28px;
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 16px 20px;
}

.filter-bar .search-box {
	flex: 2;
	min-width: 200px;
	display: flex;
	align-items: center;
	background: #f0f2f5;
	border-radius: 10px;
	padding: 0 14px;
	transition: 0.3s;
}

.filter-bar .search-box:focus-within {
	background: #fff;
	box-shadow: 0 0 0 3px rgba(166, 243, 107, 0.3);
}

.filter-bar .search-box input {
	border: none;
	background: transparent;
	padding: 10px 0;
	width: 100%;
	font-size: 14px;
	outline: none;
}

.filter-bar .search-box i {
	color: #aaa;
	margin-right: 10px;
	font-size: 18px;
}

.filter-bar .filter-group {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
	align-items: center;
}

.filter-bar .filter-group select {
	padding: 8px 14px;
	border: 2px solid #e8ecf1;
	border-radius: 8px;
	background: #fafbfc;
	font-size: 13px;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	outline: none;
}

.filter-bar .filter-group select:focus {
	border-color: #a6f36b;
	box-shadow: 0 0 0 3px rgba(166, 243, 107, 0.2);
}

.filter-bar .filter-group .price-input {
	display: flex;
	align-items: center;
	gap: 6px;
}

.filter-bar .filter-group .price-input input {
	width: 90px;
	padding: 8px 10px;
	border: 2px solid #e8ecf1;
	border-radius: 8px;
	font-size: 13px;
	background: #fafbfc;
	transition: 0.3s;
	outline: none;
}

.filter-bar .filter-group .price-input input:focus {
	border-color: #a6f36b;
	box-shadow: 0 0 0 3px rgba(166, 243, 107, 0.2);
}

.filter-bar .btn-filter {
	padding: 8px 24px;
	background: #a6f36b;
	border: none;
	border-radius: 8px;
	font-weight: 600;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	font-size: 14px;
}

.filter-bar .btn-filter:hover {
	background: #93e857;
	transform: translateY(-2px);
	box-shadow: 0 6px 20px rgba(166, 243, 107, 0.3);
}

/* Product grid */
.product-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
	gap: 24px;
}

.product-card {
	background: #fff;
	border-radius: 16px;
	overflow: hidden;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
	transition: 0.3s;
	cursor: pointer;
	display: flex;
	flex-direction: column;
}

.product-card:hover {
	transform: translateY(-6px);
	box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

.product-card .image {
	height: 200px;
	background-color: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 16px;
	overflow: hidden;
	position: relative;
}

.product-card .image img {
	max-width: 100%;
	max-height: 100%;
	object-fit: contain;
	transition: 0.3s;
}

.product-card:hover .image img {
	transform: scale(1.04);
}

.product-card .info {
	padding: 16px 18px 20px;
	flex: 1;
	display: flex;
	flex-direction: column;
}

.product-card .info .name {
	font-size: 16px;
	font-weight: 600;
	color: #1a1a2e;
	margin-bottom: 4px;
	line-height: 1.3;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.product-card .info .brand-cat {
	font-size: 13px;
	color: #888;
	margin-bottom: 8px;
}

.product-card .info .price {
	font-size: 20px;
	font-weight: 700;
	color: #1a1a2e;
	margin-top: auto;
}

.product-card .info .price span {
	font-size: 14px;
	font-weight: 400;
	color: #888;
	text-decoration: line-through;
	margin-left: 8px;
}

.product-card .info .stock {
	font-size: 13px;
	color: #22c55e;
	margin-top: 4px;
}

.product-card .info .stock.out {
	color: #ef4444;
}

.product-card .btn-add {
	width: 100%;
	margin-top: 12px;
	padding: 10px;
	background: #1a1a2e;
	color: #fff;
	border: none;
	border-radius: 10px;
	font-weight: 600;
	cursor: pointer;
	transition: 0.3s;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 6px;
}

.product-card .btn-add:hover {
	background: #a6f36b;
	color: #1a1a2e;
}

.product-card .btn-add:disabled {
	opacity: 0.5;
	cursor: not-allowed;
}

/* Pagination */
.pagination {
	display: flex;
	justify-content: center;
	gap: 8px;
	margin-top: 36px;
	flex-wrap: wrap;
}

.pagination a, .pagination span {
	padding: 8px 16px;
	border-radius: 8px;
	background: #fff;
	border: 2px solid #e8ecf1;
	color: #1a1a2e;
	text-decoration: none;
	font-weight: 500;
	transition: 0.3s;
}

.pagination a:hover {
	border-color: #a6f36b;
	background: #f8fff5;
}

.pagination .active {
	background: #a6f36b;
	border-color: #a6f36b;
	color: #1a1a2e;
}

/* Alert */
.alert {
	padding: 12px 20px;
	border-radius: 12px;
	font-size: 14px;
	margin-bottom: 20px;
	display: flex;
	align-items: center;
	gap: 10px;
}

.alert-success {
	background: #f0fdf4;
	color: #166534;
	border: 1px solid #bbf7d0;
}

.alert-danger {
	background: #fef2f2;
	color: #b91c1c;
	border: 1px solid #fecaca;
}

.alert-info {
	background: #eff6ff;
	color: #1e40af;
	border: 1px solid #bfdbfe;
}

/* Responsive */
@media ( max-width : 768px) {
	.filter-bar {
		flex-direction: column;
		align-items: stretch;
	}
	.filter-bar .filter-group {
		flex-wrap: wrap;
	}
	.product-grid {
		grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
		gap: 16px;
	}
	.product-card .image {
		height: 150px;
	}
	.header .container {
		flex-direction: column;
		align-items: stretch;
	}
	.header .nav-links {
		justify-content: center;
	}
}

@media ( max-width : 480px) {
	.product-grid {
		grid-template-columns: 1fr 1fr;
		gap: 12px;
	}
	.product-card .info {
		padding: 12px;
	}
	.product-card .info .name {
		font-size: 14px;
	}
	.product-card .info .price {
		font-size: 17px;
	}
}
</style>
</head>
<body>
	<!-- Main content -->
	<div class="container">
		<h1 style="font-size: 28px; font-weight: 700; margin-bottom: 8px;">
			Danh sách sản phẩm</h1>
		<p style="color: #666; margin-bottom: 20px;">Khám phá các sản phẩm
			công nghệ mới nhất</p>

		<!-- Hiển thị thông báo -->
		<c:if test="${not empty sessionScope.cartError}">
			<div class="alert alert-danger">
				<i class="bi bi-exclamation-circle"></i> ${sessionScope.cartError}
			</div>
			<c:remove var="cartError" scope="session" />
		</c:if>
		<c:if test="${not empty param.message}">
			<div class="alert alert-success">
				<i class="bi bi-check-circle"></i> ${param.message}
			</div>
		</c:if>

		<!-- Filter bar -->
		<form action="${pageContext.request.contextPath}/shop/products"
			method="get" class="filter-bar">
			<div class="search-box">
				<i class="bi bi-search"></i> <input type="text" name="keyword"
					placeholder="Tìm kiếm sản phẩm..." value="${param.keyword}">
			</div>
			<div class="filter-group">
				<select name="categoryId">
					<option value="">Tất cả danh mục</option>
					<c:forEach var="cat" items="${categories}">
						<option value="${cat.categoryId}"
							${param.categoryId == cat.categoryId ? 'selected' : ''}>${cat.name}</option>
					</c:forEach>
				</select> <select name="brandId">
					<option value="">Tất cả thương hiệu</option>
					<c:forEach var="brand" items="${brands}">
						<option value="${brand.brandId}"
							${param.brandId == brand.brandId ? 'selected' : ''}>${brand.name}</option>
					</c:forEach>
				</select>
				<div class="price-input">
					<input type="number" name="minPrice" placeholder="Từ"
						value="${param.minPrice}" min="0" step="100000"> <span>–</span>
					<input type="number" name="maxPrice" placeholder="Đến"
						value="${param.maxPrice}" min="0" step="100000">
				</div>
				<button type="submit" class="btn-filter">
					<i class="bi bi-funnel"></i> Lọc
				</button>
				<a href="${pageContext.request.contextPath}/shop/products"
					class="btn-filter"
					style="background: #e8ecf1; text-decoration: none; color: #1a1a2e;">Xóa
					lọc</a>
			</div>
		</form>

		<!-- Product grid -->
		<c:choose>
			<c:when test="${not empty products}">
				<div class="product-grid">
					<c:forEach var="p" items="${products}">
						<div class="product-card"
							onclick="location.href='${pageContext.request.contextPath}/shop/product-details?id=${p.productId}'">
							<div class="image">
								<c:choose>
									<c:when test="${not empty p.imageUrl}">
										<img src="${p.imageUrl}" alt="${p.name}" loading="lazy">
									</c:when>
									<c:otherwise>
										<img
											src="${pageContext.request.contextPath}/assets/images/no-image.png"
											alt="No image" loading="lazy">
									</c:otherwise>
								</c:choose>
							</div>
							<div class="info">
								<div class="name">${p.name}</div>
								<div class="brand-cat">${p.brand.name}· ${p.category.name}</div>

								<!-- 🔥 HIỂN THỊ GIÁ -->
								<div class="price">
									<fmt:formatNumber value="${p.price}" type="currency"
										currencySymbol="₫" maxFractionDigits="0" />
									<c:if test="${p.price > 30000000}">
										<span class="old-price"><fmt:formatNumber
												value="${p.price * 1.1}" type="currency" currencySymbol="₫"
												maxFractionDigits="0" /></span>
									</c:if>
								</div>

								<div class="stock ${p.stock <= 0 ? 'out' : ''}">
									<c:choose>
										<c:when test="${p.stock <= 0}">Hết hàng</c:when>
										<c:otherwise>Còn ${p.stock} sản phẩm</c:otherwise>
									</c:choose>
								</div>
								<form action="${pageContext.request.contextPath}/shop/cart"
									method="post" onsubmit="event.stopPropagation();">
									<input type="hidden" name="productId" value="${p.productId}">
									<button type="submit" class="btn-add"
										${p.stock <= 0 ? 'disabled' : ''}>
										<i class="bi bi-cart-plus"></i> Thêm vào giỏ
									</button>
								</form>
							</div>
						</div>
					</c:forEach>
				</div>

				<!-- Pagination -->
				<div class="pagination">
					<c:if test="${currentPage > 1}">
						<a
							href="?page=${currentPage - 1}&keyword=${param.keyword}&categoryId=${param.categoryId}&brandId=${param.brandId}&minPrice=${param.minPrice}&maxPrice=${param.maxPrice}"><i
							class="bi bi-chevron-left"></i></a>
					</c:if>
					<c:forEach begin="1" end="${totalPages}" var="i">
						<c:choose>
							<c:when test="${i == currentPage}">
								<span class="active">${i}</span>
							</c:when>
							<c:otherwise>
								<a
									href="?page=${i}&keyword=${param.keyword}&categoryId=${param.categoryId}&brandId=${param.brandId}&minPrice=${param.minPrice}&maxPrice=${param.maxPrice}">${i}</a>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${currentPage < totalPages}">
						<a
							href="?page=${currentPage + 1}&keyword=${param.keyword}&categoryId=${param.categoryId}&brandId=${param.brandId}&minPrice=${param.minPrice}&maxPrice=${param.maxPrice}"><i
							class="bi bi-chevron-right"></i></a>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-info"
					style="text-align: center; padding: 40px; font-size: 16px;">
					<i class="bi bi-inbox"
						style="font-size: 48px; display: block; margin-bottom: 12px;"></i>
					Không tìm thấy sản phẩm nào. Vui lòng thử lại với từ khóa khác.
				</div>
			</c:otherwise>
		</c:choose>
	</div>
	<script>
		// Cập nhật số lượng giỏ hàng (nếu có session cart)
		document
				.addEventListener(
						'DOMContentLoaded',
						function() {
							<c:if test="${not empty sessionScope.cartItems}">
							document.getElementById('cartCount').textContent = '${sessionScope.cartItems.size()}';
							</c:if>
						});
	</script>
	<%@ include file="/WEB-INF/includes/footer.jsp" %>
</body>
</html>