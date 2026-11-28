package vtechphone.servlet;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vtechphone.entity.User;
import vtechphone.service.UserService;
import vtechphone.service.UserServiceImpl;
import vtechphone.util.AuthUtil;
import vtechphone.util.OTPUtil;
import vtechphone.service.EmailService;

@WebServlet({"/login", "/register", "/logout", "/verify-email", "/resend-otp"})
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    UserService userService = new UserServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        String error = null;

        // ============ ĐĂNG NHẬP (Giữ nguyên) ============
        if (path.contains("login")) {
            if ("POST".equalsIgnoreCase(req.getMethod())) {
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String remember = req.getParameter("remember");

                if (email == null || email.isBlank()) {
                    error = "Vui lòng nhập email!";
                } else if (password == null || password.isBlank()) {
                    error = "Vui lòng nhập mật khẩu!";
                } else if (password.length() < 6) {
                    error = "Mật khẩu phải có ít nhất 6 ký tự!";
                } else {
                    try {
                        User user = userService.loginByEmail(email.trim(), password);
                        if (user == null) {
                            error = "Email hoặc mật khẩu không đúng!";
                        } else if (!user.isActive()) {
                            error = "Tài khoản chưa được xác thực. Vui lòng kiểm tra email!";
                            req.getSession().setAttribute("pendingEmail", email);
                            req.getRequestDispatcher("/auth/verify-email.jsp").forward(req, resp);
                            return;
                        } else {
                            AuthUtil.setUser(req, user);
                            if ("true".equalsIgnoreCase(remember)) {
                                Cookie cookie = new Cookie("userEmail", email.trim());
                                cookie.setMaxAge(7 * 24 * 60 * 60);
                                cookie.setPath(req.getContextPath() + "/");
                                cookie.setHttpOnly(true);
                                resp.addCookie(cookie);
                            } else {
                                Cookie cookie = new Cookie("userEmail", "");
                                cookie.setMaxAge(0);
                                cookie.setPath(req.getContextPath() + "/");
                                resp.addCookie(cookie);
                            }
                            resp.sendRedirect(req.getContextPath() + "/home");
                            return;
                        }
                    } catch (Exception e) {
                        error = "Lỗi hệ thống: " + e.getMessage();
                    }
                }
            }
            req.setAttribute("error", error);
            req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);

        // ============ ĐĂNG KÝ (Giữ nguyên) ============
        } else if (path.contains("register")) {
            // ... Giữ nguyên logic đăng ký ...
            if ("POST".equalsIgnoreCase(req.getMethod())) {
                String fullName = req.getParameter("fullname");
                String email = req.getParameter("email");
                String phone = req.getParameter("phone");
                String address = req.getParameter("address");
                String password = req.getParameter("password");
                String confirm = req.getParameter("confirmPassword");
                String agree = req.getParameter("agree");

                if (fullName == null || fullName.isBlank()) error = "Họ tên không được để trống!";
                else if (fullName.trim().length() < 2) error = "Họ tên phải có ít nhất 2 ký tự!";
                else if (email == null || email.isBlank()) error = "Email không được để trống!";
                else if (!email.trim().matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) error = "Email phải là Gmail hợp lệ (ví dụ: ten@gmail.com)!";
                else if (phone != null && !phone.isBlank() && !phone.trim().matches("^0[0-9]{8,14}$")) error = "Số điện thoại không hợp lệ!";
                else if (password == null || password.isBlank()) error = "Mật khẩu không được để trống!";
                else if (password.length() < 6) error = "Mật khẩu phải có ít nhất 6 ký tự!";
                else if (confirm == null || confirm.isBlank()) error = "Vui lòng xác nhận mật khẩu!";
                else if (!password.equals(confirm)) error = "Xác nhận mật khẩu không khớp!";
                else if (agree == null || !agree.equals("true")) error = "Bạn phải đồng ý với điều khoản để đăng ký!";
                else if (userService.existsByEmail(email.trim())) error = "Email đã được đăng ký!";
                else {
                    try {
                        User user = userService.register(fullName.trim(), email.trim(), phone.trim(), address.trim(), password);
                        String otp = OTPUtil.generateOTP(6);
                        req.getSession().setAttribute("otp_" + email, otp);
                        req.getSession().setAttribute("otp_expiry_" + email, System.currentTimeMillis() + 5 * 60 * 1000);
                        req.getSession().setAttribute("pendingEmail", email);

                        String html = "<h2>Xác thực tài khoản VTechPhone</h2><p>Mã OTP của bạn là: <b>" + otp + "</b></p>";
                        EmailService.sendEmail(email, "Xác thực tài khoản VTechPhone", html);

                        req.setAttribute("message", "Đăng ký thành công! Vui lòng nhập mã OTP được gửi đến email của bạn.");
                        req.getRequestDispatcher("/auth/verify-email.jsp").forward(req, resp);
                        return;
                    } catch (Exception e) {
                        error = "Lỗi hệ thống: " + e.getMessage();
                        e.printStackTrace();
                    }
                }
                req.setAttribute("fullname", fullName);
                req.setAttribute("email", email);
                req.setAttribute("phone", phone);
                req.setAttribute("address", address);
            }
            req.setAttribute("error", error);
            req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);

        // ============ XÁC THỰC EMAIL ============
        } else if (path.contains("verify-email")) {
            if ("POST".equalsIgnoreCase(req.getMethod())) {
                String email = req.getParameter("email");
                String otp = req.getParameter("otp");
                String sessionOtp = (String) req.getSession().getAttribute("otp_" + email);
                Long expiry = (Long) req.getSession().getAttribute("otp_expiry_" + email);

                if (sessionOtp == null || expiry == null) {
                    error = "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu gửi lại!";
                } else if (System.currentTimeMillis() > expiry) {
                    error = "Mã OTP đã hết hạn! Vui lòng yêu cầu gửi lại.";
                    req.getSession().removeAttribute("otp_" + email);
                    req.getSession().removeAttribute("otp_expiry_" + email);
                } else if (!sessionOtp.equals(otp)) {
                    error = "Mã OTP không đúng!";
                } else {
                    User user = userService.findByEmail(email);
                    if (user != null) {
                        user.setActive(true);
                        userService.update(user);
                    }
                    req.getSession().removeAttribute("otp_" + email);
                    req.getSession().removeAttribute("otp_expiry_" + email);
                    req.getSession().removeAttribute("pendingEmail");
                    req.getSession().setAttribute("message", "Xác thực email thành công! Vui lòng đăng nhập.");
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }
            }
            req.setAttribute("error", error);
            String email = (String) req.getSession().getAttribute("pendingEmail");
            req.setAttribute("email", email);
            req.getRequestDispatcher("/auth/verify-email.jsp").forward(req, resp);

        // ============ GỬI LẠI OTP ============
        } else if (path.contains("resend-otp")) {
            String email = req.getParameter("email");
            if (email != null && !email.isBlank()) {
                String otp = OTPUtil.generateOTP(6);
                req.getSession().setAttribute("otp_" + email, otp);
                req.getSession().setAttribute("otp_expiry_" + email, System.currentTimeMillis() + 5 * 60 * 1000);
                String html = "<h2>Xác thực tài khoản VTechPhone (OTP mới)</h2><p>Mã OTP mới của bạn là: <b>" + otp + "</b></p>";
                EmailService.sendEmail(email, "Xác thực tài khoản VTechPhone (OTP mới)", html);
                req.getSession().setAttribute("message", "Đã gửi lại mã OTP mới. Kiểm tra email.");
            }
            resp.sendRedirect(req.getContextPath() + "/verify-email");

        // ============ ĐĂNG XUẤT ============
        } else if (path.contains("logout")) {
            req.getSession().invalidate();
            Cookie cookie = new Cookie("userEmail", "");
            cookie.setMaxAge(0);
            cookie.setPath(req.getContextPath() + "/");
            resp.addCookie(cookie);
            // 🔥 Chuyển hướng về trang chủ sản phẩm
            resp.sendRedirect(req.getContextPath() + "/shop/products");
        }
    }
}