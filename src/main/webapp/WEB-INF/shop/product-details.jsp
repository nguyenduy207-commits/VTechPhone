<%@ page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp"%>
<%@ include file="/WEB-INF/includes/sidebar.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>VTechPhone - Chi tiết sản phẩm</title>
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
	max-width: 1200px;
	margin: 0 auto;
	padding: 20px 16px;
}

.detail-wrapper {
	display: flex;
	gap: 40px;
	background: #fff;
	border-radius: 20px;
	padding: 32px;
	box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
	margin-top: 20px;
}

.detail-image {
	flex: 1;
	background: #f0f2f5;
	border-radius: 16px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 24px;
	min-height: 360px;
}

.detail-image img {
	max-width: 100%;
	max-height: 400px;
	object-fit: contain;
}

.detail-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.detail-info .name {
	font-size: 28px;
	font-weight: 700;
	margin-bottom: 6px;
}

.detail-info .brand-cat {
	color: #888;
	font-size: 15px;
	margin-bottom: 12px;
}

.detail-info .price {
	font-size: 32px;
	font-weight: 700;
	color: #1a1a2e;
	margin-bottom: 8px;
}

.detail-info .price .old {
	font-size: 20px;
	font-weight: 400;
	color: #999;
	text-decoration: line-through;
	margin-left: 12px;
}

.detail-info .stock {
	font-size: 15px;
	font-weight: 600;
	margin-bottom: 16px;
}

.detail-info .stock.in {
	color: #22c55e;
}

.detail-info .stock.out {
	color: #ef4444;
}

.detail-info .description {
	color: #444;
	line-height: 1.7;
	margin: 12px 0 20px;
	border-top: 1px solid #eee;
	padding-top: 16px;
}

.detail-info .actions {
	display: flex;
	gap: 16px;
	flex-wrap: wrap;
	margin-top: auto;
}

.detail-info .actions .btn-primary {
	padding: 12px 32px;
	background: #a6f36b;
	border: none;
	border-radius: 12px;
	font-weight: 700;
	font-size: 16px;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	display: flex;
	align-items: center;
	gap: 8px;
	flex: 1;
	justify-content: center;
}

.detail-info .actions .btn-primary:hover {
	background: #93e857;
	transform: translateY(-2px);
	box-shadow: 0 8px 24px rgba(166, 243, 107, 0.3);
}

.detail-info .actions .btn-primary:disabled {
	opacity: 0.5;
	cursor: not-allowed;
	transform: none;
}

.detail-info .actions .btn-secondary {
	padding: 12px 24px;
	border: 2px solid #e8ecf1;
	border-radius: 12px;
	background: #fff;
	font-weight: 600;
	color: #1a1a2e;
	cursor: pointer;
	transition: 0.3s;
	display: flex;
	align-items: center;
	gap: 6px;
}

.detail-info .actions .btn-secondary:hover {
	border-color: #a6f36b;
	background: #f8fff5;
}

.alert {
	padding: 12px 20px;
	border-radius: 12px;
	margin-bottom: 16px;
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

@media ( max-width : 768px) {
	.detail-wrapper {
		flex-direction: column;
		padding: 20px;
	}
	.detail-image {
		min-height: 200px;
	}
	.detail-info .name {
		font-size: 22px;
	}
	.detail-info .price {
		font-size: 26px;
	}
}
</style>
</head>
<body>
	<div class="container">
		<c:if test="${not empty sessionScope.cartError}">
			<div class="alert alert-danger">
				<i class="bi bi-exclamation-circle"></i> ${sessionScope.cartError}
			</div>
			<c:remove var="cartError" scope="session" />
		</c:if>

		<c:choose>
			<c:when test="${not empty product}">
				<div class="detail-wrapper">
					<div class="detail-image">
						<c:choose>
							<c:when test="${not empty product.imageUrl}">
								<img src="${product.imageUrl}" alt="${product.name}">
							</c:when>
							<c:otherwise>
								<img
									src="${pageContext.request.contextPath}/assets/images/no-image.png"
									alt="No image">
							</c:otherwise>
						</c:choose>
					</div>
					<div class="detail-info">
						<div class="name">${product.name}</div>
						<div class="brand-cat">${product.brand.name}·
							${product.category.name}</div>
						<div class="price">
							<fmt:formatNumber value="${product.price}" type="currency"
								currencySymbol="₫" maxFractionDigits="0" />
							<span class="old"><fmt:formatNumber
									value="${product.price * 1.1}" type="currency"
									currencySymbol="₫" maxFractionDigits="0" /></span>
						</div>
						<div class="stock ${product.stock > 0 ? 'in' : 'out'}">
							<c:choose>
								<c:when test="${product.stock > 0}">✅ Còn hàng (${product.stock} sản phẩm)</c:when>
								<c:otherwise>❌ Hết hàng</c:otherwise>
							</c:choose>
						</div>
						<div class="description">
							<strong>Mô tả:</strong>
							<p>${not empty product.description ? product.description : 'Chưa có mô tả chi tiết cho sản phẩm này.'}</p>
						</div>
						<div class="actions">
							<form action="${pageContext.request.contextPath}/shop/cart"
								method="post">
								<input type="hidden" name="productId"
									value="${product.productId}">
								<button type="submit" class="btn-primary"
									${product.stock <= 0 ? 'disabled' : ''}>
									<i class="bi bi-cart-plus"></i> Thêm vào giỏ hàng
								</button>
							</form>
							<a href="${pageContext.request.contextPath}/shop/products"
								class="btn-secondary"> <i class="bi bi-arrow-left"></i> Quay
								lại
							</a>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="alert alert-danger">
					<i class="bi bi-exclamation-circle"></i> Sản phẩm không tồn tại.
				</div>
				<a href="${pageContext.request.contextPath}/shop/products"
					class="btn-secondary">Quay lại cửa hàng</a>
			</c:otherwise>
		</c:choose>
	</div>

	<%@ include file="/WEB-INF/includes/footer.jsp"%>

	<script>
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