<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Container hiển thị thông báo lỗi -->
<div id="error-message-container">
    <c:if test="${not empty sessionScope.cartError}">
        <div id="server-alert" class="alert alert-danger alert-dismissible fade show mt-3">
            <i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.cartError}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="cartError" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.selectionError}">
        <div class="alert alert-warning alert-dismissible fade show mt-3">
            <i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.selectionError}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="selectionError" scope="session" />
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger alert-dismissible fade show mt-3">
            <i class="bi bi-exclamation-triangle-fill"></i> ${requestScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
</div>

<c:set var="grandTotal" value="0" />
<c:set var="shippingFee" value="0" />

<h2 class="mb-4">Giỏ hàng của bạn</h2>

<c:if test="${empty cartItems}">
    <div class="text-center py-5 my-5 bg-white rounded shadow-sm border-0 p-5">
        <i class="bi bi-cart-x" style="font-size: 4rem; color: #ccc;"></i>
        <h4 class="mt-3 text-muted fw-bold">Giỏ hàng của bạn đang trống</h4>
        <p class="text-muted mb-4">Hãy thêm sản phẩm yêu thích vào giỏ hàng nhé!</p>
        <a href="${pageContext.request.contextPath}/shop/products" class="btn btn-primary px-4 py-2 fw-bold rounded-pill">
            <i class="bi bi-arrow-left me-2"></i> Tiếp tục mua sắm
        </a>
    </div>
</c:if>

<c:if test="${not empty cartItems}">
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="card shadow-sm p-3 border-0">
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead class="table-light">
                            <tr>
                                <th scope="col" style="width: 40px;">
                                    <input type="checkbox" id="selectAll" onchange="toggleAll()" />
                                </th>
                                <th scope="col" style="width: 50%;">Sản phẩm</th>
                                <th scope="col" class="text-end">Đơn giá</th>
                                <th scope="col" class="text-center">Số lượng</th>
                                <th scope="col" class="text-end">Thành tiền</th>
                                <th scope="col" class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${cartItems}" var="item" varStatus="status">
                                <c:set var="grandTotal" value="${grandTotal + (item.product.price * item.quantity)}" />
                                <tr>
                                    <td>
                                        <input type="checkbox" class="product-checkbox" value="${item.product.productId}" onchange="updateTotal()" />
                                    </td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <div class="bg-light rounded me-3 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px; overflow: hidden;">
                                                <c:choose>
                                                    <c:when test="${not empty item.product.imageUrl}">
                                                        <c:choose>
                                                            <c:when test="${item.product.imageUrl.contains('http')}">
                                                                <img src="${item.product.imageUrl}" alt="${item.product.name}" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}${item.product.imageUrl}" alt="${item.product.name}" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/assets/images/no-image.png" alt="No Image" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div>
                                                <h6 class="mb-0 fw-bold">${item.product.name}</h6>
                                                <small class="text-muted">Thương hiệu: ${item.product.brand.name}</small>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="text-end fw-bold text-primary">
                                        <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                    </td>
                                    <td>
                                        <div class="d-flex justify-content-center align-items-center">
                                            <a href="${pageContext.request.contextPath}/shop/cart?action=update&productId=${item.product.productId}&quantity=${item.quantity - 1}" class="btn btn-sm btn-outline-secondary rounded-circle fw-bold px-2">-</a>
                                            
                                            <!-- 🔥 Đã thêm max="${item.product.stock}" và gọi hàm JS xử lý vượt quá -->
                                            <input type="number" id="qty-${item.product.productId}" 
                                                   class="form-control form-control-sm text-center fw-bold" 
                                                   style="width: 55px; margin: 0 5px;" 
                                                   min="1" max="${item.product.stock}" 
                                                   value="${item.quantity}" 
                                                   onkeypress="return (event.charCode != 45)" 
                                                   oninput="if(this.value === '') this.value = 0" 
                                                   onkeydown="if(event.key === 'Enter') { event.preventDefault(); this.onchange(); }" 
                                                   onchange="handleQuantityChange(this, ${item.product.productId}, ${item.product.stock})" />
                                            
                                            <a href="${pageContext.request.contextPath}/shop/cart?action=update&productId=${item.product.productId}&quantity=${item.quantity + 1}" class="btn btn-sm btn-outline-secondary rounded-circle fw-bold px-2">+</a>
                                        </div>
                                    </td>
                                    <td class="text-end fw-bold text-danger">
                                        <fmt:formatNumber value="${item.product.price * item.quantity}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/shop/cart?action=remove&productId=${item.product.productId}" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                            <i class="bi bi-trash3" style="font-size: 1.2rem;"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card shadow-sm p-4 border-0 sticky-top" style="top: 20px;">
                <h5 class="fw-bold border-bottom pb-3 mb-3">Thông tin đơn hàng</h5>
                
                <div class="d-flex justify-content-between mb-2 text-secondary">
                    <span>Tạm tính (<span id="selectedCount">0</span> sản phẩm)</span>
                    <span class="fw-bold" id="subTotalDisplay">0đ</span>
                </div>
                
                <c:set var="shippingFee" value="${grandTotal * 0.01}" />
                <div class="d-flex justify-content-between mb-2 text-primary">
                    <span>Phí vận chuyển (1%)</span>
                    <span class="fw-bold" id="shippingFeeDisplay">0đ</span>
                </div>
                
                <div class="border-top pt-3 mt-3">
                    <div class="d-flex justify-content-between fw-bold fs-5">
                        <span>Tổng cộng</span>
                        <span class="text-primary" id="totalDisplay">0đ</span>
                    </div>
                    <p class="text-muted small text-end mb-0">(Đã bao gồm VAT)</p>
                </div>

                <div class="mt-4 d-grid gap-2">
                    <a href="${pageContext.request.contextPath}/shop/products" class="btn btn-outline-primary py-2">
                        <i class="bi bi-arrow-left me-2"></i> Tiếp tục mua sắm
                    </a>
                    
                    <button type="button" class="btn btn-primary py-2 fw-bold" id="checkoutBtn" data-bs-toggle="modal" data-bs-target="#checkoutModal">
                        <i class="bi bi-credit-card me-2"></i> Tiến hành thanh toán
                    </button>
                    <p class="text-center text-muted small mt-2 mb-0">
                        <i class="bi bi-lock"></i> Thanh toán an toàn và bảo mật
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- MODAL XÁC NHẬN THANH TOÁN 2 CỘT -->
    <div class="modal fade" id="checkoutModal" tabindex="-1" aria-labelledby="checkoutModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="checkoutModalLabel">Xác nhận thông tin giao hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/shop/checkout" method="post" id="checkoutForm">
                    <div class="modal-body">
                        <div class="row">
                            <!-- Cột TRÁI: Danh sách sản phẩm đã chọn -->
                            <div class="col-md-6 border-end">
                                <h6 class="fw-bold mb-3 border-bottom pb-2">Sản phẩm đã chọn</h6>
                                <div class="product-list" style="max-height: 300px; overflow-y: auto;">
                                    <c:forEach items="${cartItems}" var="item">
                                        <div class="d-flex align-items-center mb-3 pb-2 border-bottom">
                                            <!-- Ảnh sản phẩm -->
                                            <div class="bg-light rounded me-3" style="width: 50px; height: 50px; overflow: hidden;">
                                                <c:choose>
                                                    <c:when test="${not empty item.product.imageUrl}">
                                                        <c:choose>
                                                            <c:when test="${item.product.imageUrl.contains('http')}">
                                                                <img src="${item.product.imageUrl}" alt="${item.product.name}" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}${item.product.imageUrl}" alt="${item.product.name}" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/assets/images/no-image.png" alt="No Image" class="img-fluid" style="width: 100%; height: 100%; object-fit: cover;">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <!-- Tên và Số lượng -->
                                            <div>
                                                <div class="fw-bold">${item.product.name}</div>
                                                <div class="text-muted small">Số lượng: ${item.quantity}</div>
                                                <div class="text-primary small fw-bold"><fmt:formatNumber value="${item.product.price * item.quantity}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ</div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <!-- Cột PHẢI: Thông tin giao hàng -->
                            <div class="col-md-6">
                                <h6 class="fw-bold mb-3 border-bottom pb-2">Thông tin giao hàng</h6>
                                <div class="mb-3">
                                    <label for="receiverName" class="form-label">Người nhận *</label>
                                    <input type="text" class="form-control" id="receiverName" name="receiverName" value="${sessionScope.user.fullName}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="receiverPhone" class="form-label">Số điện thoại *</label>
                                    <input type="text" class="form-control" id="receiverPhone" name="receiverPhone" value="${sessionScope.user.phone}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="shippingAddress" class="form-label">Địa chỉ nhận hàng *</label>
                                    <input type="text" class="form-control" id="shippingAddress" name="shippingAddress" value="${sessionScope.user.address}" required>
                                </div>

                                <!-- Tổng tiền cuối cùng -->
                                <div class="border-top pt-3 mt-3 bg-light p-2 rounded">
                                    <div class="d-flex justify-content-between fw-bold fs-6">
                                        <span>Tổng tiền thanh toán:</span>
                                        <span class="text-primary" id="modalTotalDisplay">0đ</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary fw-bold" onclick="submitCheckout()">Xác nhận thanh toán</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</c:if>

<!-- JavaScript tính toán và xử lý Submit -->
<script>
    function toggleAll() {
        const selectAll = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.product-checkbox');
        checkboxes.forEach(cb => cb.checked = selectAll.checked);
        updateTotal();
    }

    function updateTotal() {
        const checkboxes = document.querySelectorAll('.product-checkbox:checked');
        let subTotal = 0;
        let count = 0;
        
        checkboxes.forEach(cb => {
            const row = cb.closest('tr');
            const priceText = row.querySelector('td:nth-child(3)').textContent.trim();
            const priceValue = parseInt(priceText.replace(/[^0-9]/g, ''), 10);
            const qty = parseInt(row.querySelector('input[type="number"]').value, 10);
            if (!isNaN(priceValue) && !isNaN(qty)) {
                subTotal += priceValue * qty;
                count++;
            }
        });
        const shippingFee = Math.round(subTotal * 0.01);
        const total = subTotal + shippingFee;

        const btn = document.getElementById('checkoutBtn');
        if (count === 0) {
            btn.disabled = true;
            btn.classList.remove('btn-primary');
            btn.classList.add('btn-secondary');
            btn.style.cursor = 'not-allowed';
        } else {
            btn.disabled = false;
            btn.classList.remove('btn-secondary');
            btn.classList.add('btn-primary');
            btn.style.cursor = 'pointer';
        }
        document.getElementById('selectedCount').textContent = count;
        document.getElementById('subTotalDisplay').textContent = formatCurrency(subTotal);
        document.getElementById('shippingFeeDisplay').textContent = formatCurrency(shippingFee);
        document.getElementById('totalDisplay').textContent = formatCurrency(total);
        document.getElementById('modalTotalDisplay').textContent = formatCurrency(total);
    }

    function formatCurrency(value) {
        if (!value && value !== 0) return '0đ';
        return value.toLocaleString('vi-VN') + 'đ';
    }

    // 🔥 Hàm xử lý khi người dùng nhập tay vào ô số lượng
    function handleQuantityChange(inputElement, productId, maxStock) {
        let val = parseInt(inputElement.value, 10);
        if (isNaN(val) || val < 1) {
            val = 1;
            inputElement.value = 1;
        }
        if (val > maxStock) {
            val = maxStock;
            inputElement.value = maxStock; // Nếu vượt quá, tự động set về số lượng tối đa
        }
        window.location.href = '${pageContext.request.contextPath}/shop/cart?action=update&productId=' + productId + '&quantity=' + val;
    }

    function submitCheckout() {
        const selectedCheckboxes = document.querySelectorAll('.product-checkbox:checked');
        if (selectedCheckboxes.length === 0) {
            alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán.');
            return;
        }

        const form = document.getElementById('checkoutForm');
        form.querySelectorAll('input[name="selectedProductIds"]').forEach(el => el.remove());

        selectedCheckboxes.forEach(cb => {
            const hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'selectedProductIds';
            hiddenInput.value = cb.value;
            form.appendChild(hiddenInput);
        });

        form.submit();
    }

    document.addEventListener('DOMContentLoaded', function() {
        setTimeout(updateTotal, 50);
    });
</script>

<script>
    document.querySelectorAll('input[type="number"]').forEach(input => {
        input.addEventListener('keypress', function (e) {
            if (e.key === '-' || e.key === 'e') { e.preventDefault(); }
        });
    });
</script>

<%@ include file="/WEB-INF/includes/footer.jsp" %>