package vtechphone.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vtechphone.entity.User;
import vtechphone.service.UserService;
import vtechphone.service.UserServiceImpl;
import vtechphone.util.AuthUtil;
import vtechphone.util.CloudinaryUtil;
import vtechphone.util.PasswordUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet("/user/profile")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class UserProfileServlet extends HttpServlet {
	UserService userService = new UserServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!AuthUtil.isAuthenticated(req)) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		req.getRequestDispatcher("/WEB-INF/user/profile.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String error = null;
		String successMsg = null;

		HttpSession session = req.getSession();
		User currentUser = AuthUtil.getUser(req);

		if (currentUser == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		try {
			String fullName = req.getParameter("fullName");
			String gender = req.getParameter("gender");
			String dobStr = req.getParameter("dob"); // yyyy-MM-dd
			String phone = req.getParameter("phone");
			String address = req.getParameter("address");

			// Cập nhật thông tin cơ bản
			currentUser.setFullName(fullName);
			currentUser.setGender(gender);
			if (dobStr != null && !dobStr.isBlank()) {
				currentUser.setDob(LocalDate.parse(dobStr));
			} else {
				currentUser.setDob(null);
			}
			currentUser.setPhone(phone);
			currentUser.setAddress(address);

			// Xử lý upload Avatar (nếu có)
			Part avatarPart = req.getPart("avatarFile");
			if (error == null && avatarPart != null && avatarPart.getSize() > 0) {
				String avatarPublicId = "avatar_" + currentUser.getUserId();
				if (currentUser.getAvatarUrl() != null && currentUser.getAvatarUrl().contains("cloudinary.com")) {
					try {
						CloudinaryUtil.deleteFile(avatarPublicId);
					} catch (Exception ignored) {
					}
				}
				String newAvatarUrl = CloudinaryUtil.uploadFile(avatarPart.getInputStream(), avatarPublicId);
				currentUser.setAvatarUrl(newAvatarUrl);
			}

			// Xử lý Đổi mật khẩu
			String oldPass = req.getParameter("oldPassword");
			String newPass = req.getParameter("newPassword");
			String confirmPass = req.getParameter("confirmPassword");

			if (oldPass != null && !oldPass.isBlank()) {
				if (!PasswordUtil.checkPassword(oldPass, currentUser.getPasswordHash())) {
					error = "Mật khẩu hiện tại không đúng!";
				} else if (newPass == null || newPass.length() < 6) {
					error = "Mật khẩu mới phải có ít nhất 6 ký tự!";
				} else if (!newPass.equals(confirmPass)) {
					error = "Xác nhận mật khẩu không khớp!";
				} else {
					currentUser.setPasswordHash(PasswordUtil.hashPassword(newPass));
				}
			}

			if (error == null) {
				userService.update(currentUser);
				// Cập nhật lại session để header và sidebar cập nhật tên/ảnh mới ngay lập tức
				session.setAttribute("user", currentUser);
				successMsg = "Cập nhật thông tin thành công!";
				req.setAttribute("success", successMsg);
			} else {
				req.setAttribute("error", error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			req.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
		}

		req.getRequestDispatcher("/WEB-INF/user/profile.jsp").forward(req, resp);
	}
}