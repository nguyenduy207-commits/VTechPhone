package vtechphone.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vtechphone.entity.*;
import vtechphone.service.*;
import vtechphone.util.AuthUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/shop/*")
public class ShopServlet extends HttpServlet {
	ProductService productService = new ProductServiceImpl();
	OrderService orderService = new OrderServiceImpl();
	CategoryService categoryService = new CategoryServiceImpl();
	BrandService brandService = new BrandServiceImpl();
	CartService cartService = new CartServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getPathInfo();
		System.out.println(">>> ShopServlet bắt đầu chạy. Action: " + action);

		if (action == null || action.equals("/products")) {
			// ... (Giữ nguyên code lấy sản phẩm) ...
			String pageStr = req.getParameter("page");
			int currentPage = (pageStr == null || pageStr.isBlank()) ? 1 : Integer.parseInt(pageStr);
			int pageSize = 8;
			String keyword = req.getParameter("keyword");
			String catIdStr = req.getParameter("categoryId");
			String brandIdStr = req.getParameter("brandId");
			String minStr = req.getParameter("minPrice");
			String maxStr = req.getParameter("maxPrice");

			Integer catId = (catIdStr != null && !catIdStr.isBlank()) ? Integer.parseInt(catIdStr) : null;
			Integer brandId = (brandIdStr != null && !brandIdStr.isBlank()) ? Integer.parseInt(brandIdStr) : null;
			Double min = (minStr != null && !minStr.isBlank()) ? Double.parseDouble(minStr) : null;
			Double max = (maxStr != null && !maxStr.isBlank()) ? Double.parseDouble(maxStr) : null;

			List<Product> products;
			long totalItems = 0;
			boolean isFiltering = (keyword != null && !keyword.isBlank()) || catId != null || brandId != null
					|| min != null || max != null;
			if (isFiltering) {
				products = productService.searchProducts(keyword, catId, brandId, min, max, currentPage, pageSize);
				totalItems = productService.countSearchProducts(keyword, catId, brandId, min, max);
			} else {
				products = productService.getAllProducts(currentPage, pageSize);
				totalItems = productService.countAllProducts();
			}
			int totalPages = (int) Math.ceil((double) totalItems / pageSize);
			req.setAttribute("products", products);
			req.setAttribute("categories", categoryService.getAllCategories());
			req.setAttribute("brands", brandService.getAllBrands());
			req.setAttribute("currentPage", currentPage);
			req.setAttribute("totalPages", totalPages);
			req.getRequestDispatcher("/WEB-INF/shop/products.jsp").forward(req, resp);

		} else if (action.equals("/product-details")) {
			String idStr = req.getParameter("id");
			if (idStr == null || idStr.isBlank()) {
				resp.sendRedirect(req.getContextPath() + "/shop/products");
				return;
			}
			try {
				int id = Integer.parseInt(idStr);
				Product product = productService.getProductById(id);
				if (product == null) {
					resp.sendRedirect(req.getContextPath() + "/shop/products");
					return;
				}
				req.setAttribute("product", product);
				req.getRequestDispatcher("/WEB-INF/shop/product-details.jsp").forward(req, resp);
			} catch (Exception e) {
				resp.sendRedirect(req.getContextPath() + "/shop/products");
			}

			// ================= GIỎ HÀNG DÙNG DATABASE =================
		} else if (action.equals("/cart")) {
			if (!AuthUtil.isAuthenticated(req)) {
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			User user = AuthUtil.getUser(req);
			req.getSession().removeAttribute("selectionError");
			req.getSession().removeAttribute("cartError");

			String op = req.getParameter("action");

			// 1. Xóa sản phẩm khỏi giỏ hàng
			if ("remove".equals(op)) {
				int pId = Integer.parseInt(req.getParameter("productId"));
				cartService.removeFromCart(user.getUserId(), pId);
				resp.sendRedirect(req.getContextPath() + "/shop/cart");
				return;
			}

			// 🔥 2. Cập nhật số lượng sản phẩm (Đã bổ sung kiểm tra tồn kho và tự động set
			// max)
			if ("update".equals(op)) {
				int pId = Integer.parseInt(req.getParameter("productId"));
				String qtyStr = req.getParameter("quantity");
				int qty = 0;
				if (qtyStr != null && !qtyStr.isBlank()) {
					try {
						qty = Integer.parseInt(qtyStr);
					} catch (NumberFormatException e) {
						qty = 0;
					}
				}

				// 🔥 QUAN TRỌNG: Lấy sản phẩm để kiểm tra tồn kho
				Product product = productService.getProductById(pId);

				if (qty <= 0) {
					cartService.removeFromCart(user.getUserId(), pId);
				} else if (qty > product.getStock()) {
					// Nếu người dùng nhập vượt quá tồn kho, tự động gán về số lượng lớn nhất hiện
					// có
					qty = product.getStock();
					cartService.updateQuantity(user.getUserId(), pId, qty);
					req.getSession().setAttribute("cartError", "Sản phẩm \"" + product.getName() + "\" chỉ còn " + qty
							+ " sản phẩm. Đã tự động điều chỉnh số lượng.");
				} else {
					cartService.updateQuantity(user.getUserId(), pId, qty);
				}
				resp.sendRedirect(req.getContextPath() + "/shop/cart");
				return;
			}

			// 3. Lấy danh sách giỏ hàng từ Database gửi sang JSP
			List<CartItem> cartItems = cartService.getCartItemsByUserId(user.getUserId());
			req.setAttribute("cartItems", cartItems);
			req.getRequestDispatcher("/WEB-INF/shop/cart.jsp").forward(req, resp);

		} else if (action.equals("/my-orders")) {
			if (!AuthUtil.isAuthenticated(req)) {
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}
			User currentUser = AuthUtil.getUser(req);
			List<Order> orders = orderService.getOrdersByUserId(currentUser.getUserId());
			req.setAttribute("myOrders", orders);
			req.getRequestDispatcher("/WEB-INF/shop/my-orders.jsp").forward(req, resp);
		} else {
			resp.sendRedirect(req.getContextPath() + "/shop/products");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getPathInfo();

		if (action.equals("/cart")) {
			if (!AuthUtil.isAuthenticated(req)) {
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}
			int productId = Integer.parseInt(req.getParameter("productId"));
			Product p = productService.getProductById(productId);
			if (p.getStock() <= 0) {
				req.getSession().setAttribute("cartError", "Sản phẩm \"" + p.getName() + "\" đã bán hết!");
				resp.sendRedirect(req.getContextPath() + "/shop/products");
				return;
			}
			User user = AuthUtil.getUser(req);
			cartService.addToCart(user.getUserId(), productId, 1);
			resp.sendRedirect(req.getContextPath() + "/shop/cart");
			return;

		} else if (action.equals("/checkout")) {
			if (!AuthUtil.isAuthenticated(req)) {
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			User user = AuthUtil.getUser(req);
			List<CartItem> cartItems = cartService.getCartItemsByUserId(user.getUserId());
			String[] selectedIds = req.getParameterValues("selectedProductIds");
			if (selectedIds == null || selectedIds.length == 0) {
				req.getSession().setAttribute("selectionError", "Vui lòng chọn ít nhất một sản phẩm để thanh toán.");
				resp.sendRedirect(req.getContextPath() + "/shop/cart");
				return;
			}

			List<CartItem> selectedItems = new ArrayList<>();
			for (CartItem item : cartItems) {
				for (String idStr : selectedIds) {
					if (item.getProduct().getProductId().equals(Integer.parseInt(idStr))) {
						selectedItems.add(item);
						break;
					}
				}
			}

			if (selectedItems.isEmpty()) {
				req.getSession().setAttribute("selectionError", "Không tìm thấy sản phẩm nào được chọn.");
				resp.sendRedirect(req.getContextPath() + "/shop/cart");
				return;
			}

			BigDecimal goodsTotal = BigDecimal.ZERO;
			for (CartItem item : selectedItems) {
				goodsTotal = goodsTotal
						.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			}

			BigDecimal shippingFee = goodsTotal.multiply(new BigDecimal("0.01")).setScale(0, RoundingMode.HALF_UP);
			BigDecimal finalTotal = goodsTotal.add(shippingFee);

			String receiverName = req.getParameter("receiverName");
			String receiverPhone = req.getParameter("receiverPhone");
			String address = req.getParameter("shippingAddress");

			Order order = new Order();
			order.setCustomer(user);
			order.setStatus("PENDING");
			order.setReceiverName(receiverName);
			order.setReceiverPhone(receiverPhone);
			order.setShippingAddress(address);
			order.setTotalAmount(finalTotal);

			for (CartItem item : selectedItems) {
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setProduct(item.getProduct());
				orderItem.setQuantity(item.getQuantity());
				orderItem.setUnitPrice(item.getProduct().getPrice());
				order.getItems().add(orderItem);
			}

			try {
				orderService.createOrder(order);
				for (CartItem item : selectedItems) {
					cartService.removeFromCart(user.getUserId(), item.getProduct().getProductId());
				}
				req.getSession().setAttribute("message", "Đặt hàng thành công! Vui lòng chờ xử lý.");
				resp.sendRedirect(req.getContextPath() + "/shop/my-orders");
			} catch (Exception e) {
				e.printStackTrace();
				req.setAttribute("error", "Lỗi thanh toán: " + e.getMessage());
				req.getRequestDispatcher("/WEB-INF/shop/cart.jsp").forward(req, resp);
			}
		}
	}
}