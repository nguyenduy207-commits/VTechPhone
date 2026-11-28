package vtechphone.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vtechphone.entity.User;
import vtechphone.service.UserService;
import vtechphone.service.UserServiceImpl;
import vtechphone.util.OTPUtil;
import vtechphone.util.PasswordUtil;
import vtechphone.service.EmailService;

@WebServlet({"/forgot-password", "/reset-password"})
public class ForgotPasswordServlet extends HttpServlet {
    UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.contains("reset-password")) {
            req.getRequestDispatcher("/auth/reset-password.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String error = null;

        // ============ BƯỚC 1: QUÊN MẬT KHẨU (Nhập Email) ============
        if (path.contains("forgot-password")) {
            String email = req.getParameter("email");
            if (email == null || email.isBlank()) {
                error = "Vui lòng nhập email!";
            } else {
                // 🔥 Kiểm tra email có tồn tại trong hệ thống không
                User user = userService.findByEmail(email.trim());
                if (user == null) {
                    error = "Email không tồn tại trong hệ thống!";
                } else {
                    String otp = OTPUtil.generateOTP(6);
                    req.getSession().setAttribute("reset_otp_" + email, otp);
                    req.getSession().setAttribute("reset_otp_expiry_" + email, System.currentTimeMillis() + 5 * 60 * 1000);
                    req.getSession().setAttribute("reset_email", email);

                    // 🔥 Gửi email reset mật khẩu
                    String html = "<h2>Đặt lại mật khẩu VTechPhone</h2>"
                            + "<p>Mã OTP để đặt lại mật khẩu của bạn là: <b>" + otp + "</b></p>"
                            + "<p>Mã có hiệu lực trong 5 phút.</p>";
                    EmailService.sendEmail(email, "Đặt lại mật khẩu VTechPhone", html);

                    req.setAttribute("message", "Mã OTP đã được gửi đến email của bạn.");
                    req.setAttribute("email", email);
                    req.getRequestDispatcher("/auth/reset-password.jsp").forward(req, resp);
                    return;
                }
            }
            req.setAttribute("error", error);
            req.getRequestDispatcher("/auth/forgot-password.jsp").forward(req, resp);

        // ============ BƯỚC 2: ĐẶT LẠI MẬT KHẨU (Nhập OTP + Pass mới) ============
        } else if (path.contains("reset-password")) {
            String email = req.getParameter("email");
            String otp = req.getParameter("otp");
            String newPassword = req.getParameter("newPassword");
            String confirm = req.getParameter("confirmPassword");

            if (email == null || email.isBlank()) {
                error = "Email không được để trống!";
            } else if (otp == null || otp.isBlank()) {
                error = "Vui lòng nhập mã OTP!";
            } else if (newPassword == null || newPassword.isBlank()) {
                error = "Mật khẩu mới không được để trống!";
            } else if (newPassword.length() < 6) {
                error = "Mật khẩu mới phải có ít nhất 6 ký tự!";
            } else if (!newPassword.equals(confirm)) {
                error = "Xác nhận mật khẩu không khớp!";
            } else {
                String sessionOtp = (String) req.getSession().getAttribute("reset_otp_" + email);
                Long expiry = (Long) req.getSession().getAttribute("reset_otp_expiry_" + email);

                if (sessionOtp == null || expiry == null) {
                    error = "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu gửi lại!";
                } else if (System.currentTimeMillis() > expiry) {
                    error = "Mã OTP đã hết hạn! Vui lòng yêu cầu gửi lại.";
                    req.getSession().removeAttribute("reset_otp_" + email);
                    req.getSession().removeAttribute("reset_otp_expiry_" + email);
                } else if (!sessionOtp.equals(otp)) {
                    error = "Mã OTP không đúng!";
                } else {
                    User user = userService.findByEmail(email.trim());
                    if (user != null) {
                        try {
                            user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                            userService.update(user);
                            
                            req.getSession().removeAttribute("reset_otp_" + email);
                            req.getSession().removeAttribute("reset_otp_expiry_" + email);
                            req.getSession().setAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                            resp.sendRedirect(req.getContextPath() + "/login");
                            return;
                        } catch (Exception e) {
                            error = "Lỗi hệ thống khi cập nhật mật khẩu: " + e.getMessage();
                            e.printStackTrace();
                        }
                    } else {
                        error = "Không tìm thấy người dùng với email này.";
                    }
                }
            }
            req.setAttribute("error", error);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/auth/reset-password.jsp").forward(req, resp);
        }
    }
}