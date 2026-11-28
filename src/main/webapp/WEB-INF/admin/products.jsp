<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ include file="/WEB-INF/includes/header.jsp" %>
<%@ include file="/WEB-INF/includes/sidebar.jsp" %>

<div class="col-12 p-3 bg-light">
    <h1>📦 Quản lý sản phẩm</h1>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> ${error}</div>
    </c:if>
    <c:if test="${not empty param.success}">
        <div class="alert alert-success"><i class="bi bi-check-circle"></i> ${param.success}</div>
    </c:if>

    <button class="btn btn-primary mb-3" onclick="openModal('create')">
        <i class="bi bi-plus-circle"></i> Thêm sản phẩm
    </button>

    <div class="table-responsive bg-white p-3 rounded shadow-sm">
        <table class="table table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Hình ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th>Thương hiệu</th>
                    <th>Danh mục</th>
                    <th>Giá</th>
                    <th>Tồn kho</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty products}">
                        <c:forEach var="p" items="${products}">
                            <tr>
                                <td>${p.productId}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.imageUrl}">
                                            <img src="${p.imageUrl}" alt="${p.name}" class="rounded" style="width:50px;height:50px;object-fit:cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/images/no-image.png" alt="No image" style="width:50px;height:50px;object-fit:cover;">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${p.name}</td>
                                <td>${p.brand.name}</td>
                                <td>${p.category.name}</td>
                                <td><fmt:formatNumber value="${p.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                <td>${p.stock}</td>
                                <td>
                                    <button class="btn btn-sm btn-outline-primary" onclick="openEditModal(${p.productId})"><i class="bi bi-pencil"></i></button>
                                    <a href="${pageContext.request.contextPath}/admin/products?delete=${p.productId}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Xóa sản phẩm này?')"><i class="bi bi-trash3"></i></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr><td colspan="8" class="text-center text-muted">Chưa có sản phẩm</td></tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Modal Thêm/Sửa -->
<div class="modal fade" id="productModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">Thêm sản phẩm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data" id="productForm" onsubmit="return validateProductForm()">
                <div class="modal-body">
                    <input type="hidden" name="formAction" id="formAction" value="create">
                    <input type="hidden" name="id" id="productId" value="">

                    <div class="mb-3">
                        <label class="form-label fw-bold">Tên sản phẩm <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="name" id="pName" required>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Thương hiệu <span class="text-danger">*</span></label>
                                <select class="form-select" name="brandId" id="pBrand" required>
                                    <c:forEach var="b" items="${brands}">
                                        <option value="${b.brandId}">${b.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Danh mục <span class="text-danger">*</span></label>
                                <select class="form-select" name="categoryId" id="pCategory" required>
                                    <c:forEach var="c" items="${categories}">
                                        <option value="${c.categoryId}">${c.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Giá (VND) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="price" id="pPrice" step="1000" min="0" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Tồn kho <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="stock" id="pStock" min="0" required>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Mô tả</label>
                        <textarea class="form-control" name="description" id="pDescription" rows="3"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Ảnh (URL hoặc tải lên)</label>
                        <input type="text" class="form-control" name="imageUrlInput" id="pImageUrl" placeholder="Nhập URL ảnh">
                        <div class="mt-2">
                            <label class="form-label fw-normal">Hoặc tải lên:</label>
                            <input type="file" class="form-control" name="imageFile" accept="image/*">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Lưu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Validation client
    function validateProductForm() {
        var name = document.getElementById('pName').value.trim();
        var price = document.getElementById('pPrice').value;
        var stock = document.getElementById('pStock').value;

        if (name.length < 2) {
            alert('Tên sản phẩm phải có ít nhất 2 ký tự.');
            return false;
        }
        if (parseFloat(price) <= 0) {
            alert('Giá sản phẩm phải lớn hơn 0.');
            return false;
        }
        if (parseInt(stock) < 0) {
            alert('Tồn kho không được âm.');
            return false;
        }
        return true;
    }

    // Dữ liệu products từ server (để edit)
    <c:if test="${not empty products}">
        const productsData = [
            <c:forEach var="p" items="${products}" varStatus="status">
            {
                id: ${p.productId},
                name: '${p.name}',
                brandId: ${p.brand.brandId},
                categoryId: ${p.category.categoryId},
                price: ${p.price},
                stock: ${p.stock},
                description: '${p.description.replace("'", "\\'")}',
                imageUrl: '${p.imageUrl}'
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
        function openEditModal(id) {
            const p = productsData.find(item => item.id === id);
            if (!p) return;
            document.getElementById('modalTitle').textContent = 'Sửa sản phẩm';
            document.getElementById('formAction').value = 'update';
            document.getElementById('productId').value = p.id;
            document.getElementById('pName').value = p.name;
            document.getElementById('pBrand').value = p.brandId;
            document.getElementById('pCategory').value = p.categoryId;
            document.getElementById('pPrice').value = p.price;
            document.getElementById('pStock').value = p.stock;
            document.getElementById('pDescription').value = p.description;
            document.getElementById('pImageUrl').value = p.imageUrl || '';
            new bootstrap.Modal(document.getElementById('productModal')).show();
        }
    </c:if>

    function openModal(action) {
        if (action === 'create') {
            document.getElementById('modalTitle').textContent = 'Thêm sản phẩm mới';
            document.getElementById('formAction').value = 'create';
            document.getElementById('productId').value = '';
            document.getElementById('productForm').reset();
            new bootstrap.Modal(document.getElementById('productModal')).show();
        }
    }
</script>

<%@ include file="/WEB-INF/includes/footer.jsp" %>