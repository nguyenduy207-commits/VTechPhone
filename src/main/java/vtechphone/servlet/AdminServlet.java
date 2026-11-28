package vtechphone.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vtechphone.entity.*;
import vtechphone.service.*;
import vtechphone.util.AuthUtil;
import vtechphone.util.CloudinaryUtil;
import java.time.LocalDate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/admin/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class AdminServlet extends HttpServlet {

	ProductService productService = new ProductServiceImpl();
	OrderService orderService = new OrderServiceImpl();
	UserService userService = new UserServiceImpl();
	CategoryService categoryService = new CategoryServiceImpl();
	BrandService brandService = new BrandServiceImpl();

	// Kiểm tra user đã đăng nhập và có role hợp lệ (ADMIN hoặc STAFF)
	private User checkAuth(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		User user = AuthUtil.getUser(req);
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return null;
		}
		if (user.getRoleId() != 1 && user.getRoleId() != 2) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		return user;
	}

	// Kiểm tra chỉ ADMIN mới được phép
	private boolean isAdmin(User user) {
		return user != null && user.getRoleId() == 1;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getPathInfo();
		String error = null;
		User currentUser = checkAuth(req, resp);
		if (currentUser == null)
			return;

		try {
			// ===== DASHBOARD (Admin + Staff) =====
			if (action == null || action.equals("/dashboard")) {
				req.setAttribute("totalProducts", productService.getAllProducts().size());
				req.setAttribute("totalOrders", orderService.getAllOrders().size());
				req.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(req, resp);
				return;
			}
//			if (action == null || action.equals("/") || action.equals("/dashboard")) {
//			    req.setAttribute("totalProducts", productService.countAllProducts());
//			    req.setAttribute("totalOrders", orderService.getAllOrders().size());
//			    if (isAdmin(currentUser)) {
//			        req.setAttribute("totalUsers", userService.findAllUsers().size());
//			        String currentMonth = java.time.YearMonth.now().toString();
//			        BigDecimal monthlyRevenue = BigDecimal.ZERO;
//			        for (Object[] row : orderService.getMonthlyRevenue()) {
//			            if (row != null && row.length >= 2 && currentMonth.equals(String.valueOf(row[0])) && row[1] != null) {
//			                monthlyRevenue = new BigDecimal(row[1].toString());
//			                break;
//			            }
//			        }
//			        req.setAttribute("monthlyRevenue", monthlyRevenue);
//			    }
//			    req.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(req, resp);
//			    return;
//			}

			// ===== CATEGORIES (Chỉ Admin) =====
			if (action.equals("/categories")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String idStr = req.getParameter("delete");
				if (idStr != null) {
					categoryService.deleteCategory(Integer.parseInt(idStr));
					resp.sendRedirect(req.getContextPath() + "/admin/categories");
					return;
				}
				req.setAttribute("categories", categoryService.getAllCategories());
				req.getRequestDispatcher("/WEB-INF/admin/categories.jsp").forward(req, resp);
				return;
			}

			// ===== BRANDS (Chỉ Admin) =====
			if (action.equals("/brands")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String idStr = req.getParameter("delete");
				if (idStr != null) {
					brandService.deleteBrand(Integer.parseInt(idStr));
					resp.sendRedirect(req.getContextPath() + "/admin/brands");
					return;
				}
				req.setAttribute("brands", brandService.getAllBrands());
				req.getRequestDispatcher("/WEB-INF/admin/brands.jsp").forward(req, resp);
				return;
			}

			// ===== USERS (Chỉ Admin) =====
			if (action.equals("/users")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String idStr = req.getParameter("delete");
				if (idStr != null) {
					int id = Integer.parseInt(idStr);
					if (id == currentUser.getUserId()) {
						error = "Bạn không thể tự xóa tài khoản của chính mình!";
					} else {
						try {
							userService.deleteUser(id);
						} catch (Exception e) {
							error = "Không thể xóa người dùng này vì đã có dữ liệu đơn hàng liên quan!";
						}
					}
					if (error != null) {
						req.setAttribute("error", error);
						req.setAttribute("users", userService.findAllUsers());
						req.getRequestDispatcher("/WEB-INF/admin/users.jsp").forward(req, resp);
						return;
					}
					resp.sendRedirect(req.getContextPath() + "/admin/users");
					return;
				}
				req.setAttribute("users", userService.findAllUsers());
				req.getRequestDispatcher("/WEB-INF/admin/users.jsp").forward(req, resp);
				return;
			}

			// ===== PRODUCTS (Admin + Staff) =====
			if (action.equals("/products")) {
				String idStr = req.getParameter("delete");
				if (idStr != null) {
					// Staff không được phép xóa sản phẩm? (Bạn có thể cho phép hoặc không)
					// Theo use case, Staff có quyền quản lý sản phẩm (thêm/sửa/xóa) -> vẫn cho phép
					int id = Integer.parseInt(idStr);
					try {
						CloudinaryUtil.deleteFile(String.valueOf(id));
					} catch (Exception ignored) {
					}
					productService.deleteProduct(id);
					resp.sendRedirect(req.getContextPath() + "/admin/products");
					return;
				}
				req.setAttribute("products", productService.getAllProducts());
				req.setAttribute("categories", categoryService.getAllCategories());
				req.setAttribute("brands", brandService.getAllBrands());
				req.getRequestDispatcher("/WEB-INF/admin/products.jsp").forward(req, resp);
				return;
			}

			// ===== ORDERS (Admin + Staff) =====
			if (action.equals("/orders")) {
				req.setAttribute("orders", orderService.getAllOrders());
				req.getRequestDispatcher("/WEB-INF/admin/orders.jsp").forward(req, resp);
				return;
			}

			// ===== REVENUE (Chỉ Admin) =====
			if (action.equals("/revenue")) {
				if (currentUser.getRoleId() != 1) {
					resp.sendRedirect(req.getContextPath() + "/home");
					return;
				}
				req.setAttribute("totalRevenue", orderService.getTotalRevenue());
				req.setAttribute("completedOrders", orderService.getCompletedOrdersCount());
				req.setAttribute("pendingOrders",
						orderService.getAllOrders().stream().filter(o -> "PENDING".equals(o.getStatus())).count());
				// Lấy dữ liệu biểu đồ
				List<Object[]> monthlyData = orderService.getMonthlyRevenue();
				req.setAttribute("monthlyData", monthlyData);
				req.getRequestDispatcher("/WEB-INF/admin/revenue.jsp").forward(req, resp);
				return;
			}

			resp.sendError(404);
		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
			req.getRequestDispatcher("/WEB-INF/admin/products.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getPathInfo();
		User currentUser = checkAuth(req, resp);
		if (currentUser == null)
			return;

		try {
			// ===== CATEGORIES (Chỉ Admin) =====
			if (action.equals("/categories")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String formAction = req.getParameter("formAction");
				String name = req.getParameter("name");
				String description = req.getParameter("description");

				if ("create".equals(formAction)) {
					Category c = new Category();
					c.setName(name);
					c.setDescription(description != null && !description.isBlank() ? description.trim() : null);
					categoryService.createCategory(c);
				} else if ("update".equals(formAction)) {
					int id = Integer.parseInt(req.getParameter("id"));
					Category c = categoryService.getCategoryById(id);
					if (c != null) {
						c.setName(name);
						c.setDescription(description != null && !description.isBlank() ? description.trim() : null);
						categoryService.updateCategory(c);
					}
				}
				resp.sendRedirect(req.getContextPath() + "/admin/categories");
				return;
			}

			// ===== BRANDS (Chỉ Admin) =====
			if (action.equals("/brands")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String formAction = req.getParameter("formAction");
				String name = req.getParameter("name");
				String description = req.getParameter("description");

				if ("create".equals(formAction)) {
					Brand b = new Brand();
					b.setName(name);
					b.setDescription(description != null && !description.isBlank() ? description.trim() : null);
					brandService.createBrand(b);
				} else if ("update".equals(formAction)) {
					int id = Integer.parseInt(req.getParameter("id"));
					Brand b = brandService.getBrandById(id);
					if (b != null) {
						b.setName(name);
						b.setDescription(description != null && !description.isBlank() ? description.trim() : null);
						brandService.updateBrand(b);
					}
				}
				resp.sendRedirect(req.getContextPath() + "/admin/brands");
				return;
			}

			// ===== PRODUCTS (Admin + Staff) =====
			if (action.equals("/products")) {
				// Cho phép cả Admin và Staff
				String formAction = req.getParameter("formAction");
				String name = req.getParameter("name");
				BigDecimal price = new BigDecimal(req.getParameter("price"));
				int stock = Integer.parseInt(req.getParameter("stock"));
				String description = req.getParameter("description");
				int brandId = Integer.parseInt(req.getParameter("brandId"));
				int categoryId = Integer.parseInt(req.getParameter("categoryId"));

				String imageUrlInput = req.getParameter("imageUrlInput");
				Part filePart = req.getPart("imageFile");

				if ("create".equals(formAction)) {
					Product p = new Product();
					p.setName(name);
					Brand b = new Brand();
					b.setBrandId(brandId);
					p.setBrand(b);
					Category c = new Category();
					c.setCategoryId(categoryId);
					p.setCategory(c);
					p.setPrice(price);
					p.setStock(stock);
					p.setDescription(description != null && !description.isBlank() ? description.trim() : "");
					p = productService.createProduct(p);

					if (filePart != null && filePart.getSize() > 0) {
						String imageUrl = CloudinaryUtil.uploadFile(filePart.getInputStream(),
								String.valueOf(p.getProductId()));
						p.setImageUrl(imageUrl);
					} else if (imageUrlInput != null && !imageUrlInput.isBlank()) {
						p.setImageUrl(imageUrlInput.trim());
					}
					productService.updateProduct(p);
					resp.sendRedirect(req.getContextPath() + "/admin/products");
					return;

				} else if ("update".equals(formAction)) {
					int id = Integer.parseInt(req.getParameter("id"));
					Product p = productService.getProductById(id);
					if (p != null) {
						p.setName(name);
						Brand b = new Brand();
						b.setBrandId(brandId);
						p.setBrand(b);
						Category c = new Category();
						c.setCategoryId(categoryId);
						p.setCategory(c);
						p.setPrice(price);
						p.setStock(stock);
						p.setDescription(description != null && !description.isBlank() ? description.trim() : "");

						if (filePart != null && filePart.getSize() > 0) {
							if (p.getImageUrl() != null && p.getImageUrl().contains("cloudinary.com")) {
								try {
									CloudinaryUtil.deleteFile(String.valueOf(id));
								} catch (Exception ignored) {
								}
							}
							String imageUrl = CloudinaryUtil.uploadFile(filePart.getInputStream(), String.valueOf(id));
							p.setImageUrl(imageUrl);
						} else if (imageUrlInput != null && !imageUrlInput.isBlank()) {
							p.setImageUrl(imageUrlInput.trim());
						}
						productService.updateProduct(p);
					}
					resp.sendRedirect(req.getContextPath() + "/admin/products");
					return;
				}
			}

			// ===== ORDERS (Admin + Staff) =====
			if (action.equals("/orders")) {
				int orderId = Integer.parseInt(req.getParameter("orderId"));
				String status = req.getParameter("status");
				Order order = orderService.getOrderById(orderId);
				if (order != null) {
					order.setStatus(status);
					if ("CONFIRMED".equals(status) || "SHIPPING".equals(status) || "COMPLETED".equals(status)) {
						order.setStaff(AuthUtil.getUser(req));
					}
					orderService.updateOrder(order);
				}
				resp.sendRedirect(req.getContextPath() + "/admin/orders");
				return;
			}

			// ===== USERS (Chỉ Admin) =====
			if (action.equals("/users")) {
				if (!isAdmin(currentUser)) {
					resp.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				String formAction = req.getParameter("formAction");
				if ("create".equals(formAction)) {
					String fullName = req.getParameter("fullName");
					String email = req.getParameter("email");
					String phone = req.getParameter("phone");
					String address = req.getParameter("address");
					String password = req.getParameter("password");
					String confirm = req.getParameter("confirmPassword");
					String roleIdStr = req.getParameter("roleId");
					String staffPosition = req.getParameter("staffPosition");
					String hireDateStr = req.getParameter("hireDate");

					String error = null;
					if (!password.equals(confirm)) {
						error = "Mật khẩu xác nhận không khớp!";
					} else {
						try {
							int roleId = Integer.parseInt(roleIdStr);
							LocalDate hireDate = null;
							if (hireDateStr != null && !hireDateStr.isBlank()) {
								try {
									hireDate = LocalDate.parse(hireDateStr);
								} catch (Exception e) {
									// ignore
								}
							}

							userService.registerUser(fullName.trim(), email.trim(), phone.trim(), address.trim(),
									password, roleId, staffPosition, hireDate);
							resp.sendRedirect(req.getContextPath() + "/admin/users");
							return;
						} catch (Exception e) {
							error = "Lỗi tạo tài khoản: " + e.getMessage();
						}
					}
					req.setAttribute("error", error);
					req.setAttribute("users", userService.findAllUsers());
					req.getRequestDispatcher("/WEB-INF/admin/users.jsp").forward(req, resp);
					return;
				}
			}

			// Nếu không có action nào match, trả về 404
			resp.sendError(404);

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("error", "Lỗi: " + e.getMessage());
			req.setAttribute("products", productService.getAllProducts());
			req.setAttribute("categories", categoryService.getAllCategories());
			req.setAttribute("brands", brandService.getAllBrands());
			req.getRequestDispatcher("/WEB-INF/admin/products.jsp").forward(req, resp);
		}
	}
}