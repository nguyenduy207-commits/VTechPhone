<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>



<h2>Đơn hàng của tôi</h2>
<c:if test="${not empty sessionScope.message}">
    <div class="alert alert-success">${sessionScope.message}</div>
    <% session.removeAttribute("message"); %>
</c:if>

<div class="table-responsive">
    <table class="table table-bordered bg-white mt-3">
        <thead class="table-dark">
            <tr>
                <th>Mã ĐH</th>
                <th>Sản phẩm</th>
                <th>Ngày đặt</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Địa chỉ giao</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${myOrders}" var="o">
            <tr>
                <td class="align-middle">#${o.orderId}</td>
                
                <!-- 🔥 Cột Sản phẩm: Đã sửa lại logic hiển thị ảnh (hỗ trợ cả Local và Cloudinary) -->
                <td>
                    <c:forEach items="${o.items}" var="item">
                        <div class="d-flex align-items-center mb-2 border-bottom pb-2">
                            
                            <!-- Hiển thị ảnh sản phẩm -->
                            <c:choose>
                                <c:when test="${not empty item.product.imageUrl}">
                                    <c:choose>
                                        <c:when test="${item.product.imageUrl.contains('http')}">
                                            <img src="${item.product.imageUrl}" alt="${item.product.name}" 
                                                 style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; margin-right: 10px;"
                                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}${item.product.imageUrl}" alt="${item.product.name}" 
                                                 style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; margin-right: 10px;"
                                                 onerror="this.src='${pageContext.request.contextPath}/assets/images/no-image.png'">
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/assets/images/no-image.png" alt="No Image" 
                                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #eee; margin-right: 10px;">
                                </c:otherwise>
                            </c:choose>
                            
                            <div>
                                <div class="fw-bold small">${item.product.name}</div>
                                <div class="text-muted small">Số lượng: ${item.quantity}</div>
                            </div>
                        </div>
                    </c:forEach>
                </td>

                <td class="align-middle">s
                    <small>${o.orderDate}</small>
                </td>
                <td class="align-middle fw-bold text-danger">
                    <fmt:formatNumber value="${o.totalAmount}" type="currency" currencySymbol="" maxFractionDigits="0"/> VND
                </td>
                <td class="align-middle">
                    <span class="badge ${o.status == 'PENDING' ? 'bg-warning text-dark' : o.status == 'COMPLETED' ? 'bg-success' : 'bg-primary'}">
                        ${o.status}
                    </span>
                </td>
                <td class="align-middle">
                    <small>${o.shippingAddress}</small>
                </td>
            </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="/WEB-INF/includes/footer.jsp" %>