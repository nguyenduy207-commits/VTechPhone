package vtechphone.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vtechphone.entity.User;
import vtechphone.service.EmailService;
import vtechphone.service.UserService;

/**
 * TEST HÀNH VI NGHIỆP VỤ cho luồng tài khoản: đăng nhập, đăng ký, xác thực OTP, đăng xuất.
 * EmailService.sendEmail là hàm static gửi mail thật nên phải mockStatic để KHÔNG gửi email khi test.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Hành vi tài khoản (AuthServlet)")
class AuthServletBehaviorTest {

    @Mock HttpServletRequest req;
    @Mock HttpServletResponse resp;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock UserService userService;

    AuthServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new AuthServlet();
        servlet.userService = userService;   // field package-private => gán mock
        lenient().when(req.getContextPath()).thenReturn("/VTechPhone");
        lenient().when(req.getSession()).thenReturn(session);
    }

    private void givenRequest(String servletPath, String httpMethod) {
        when(req.getServletPath()).thenReturn(servletPath);
        when(req.getMethod()).thenReturn(httpMethod);
    }

    private void givenLoginForm(String email, String password, String remember) {
        givenRequest("/login", "POST");
        when(req.getParameter("email")).thenReturn(email);
        when(req.getParameter("password")).thenReturn(password);
        when(req.getParameter("remember")).thenReturn(remember);
    }

    private void thenForwardedTo(String jsp) throws Exception {
        verify(req).getRequestDispatcher(jsp);
        verify(dispatcher).forward(req, resp);
    }

    // =====================================================================
    @Nested
    @DisplayName("Đăng nhập (POST /login)")
    class Login {

        @Test
        @DisplayName("Email trống => báo \"Vui lòng nhập email!\" và ở lại trang login")
        void blankEmail() throws Exception {
            givenLoginForm("  ", "123456", null);
            when(req.getRequestDispatcher("/auth/login.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Vui lòng nhập email!");
            thenForwardedTo("/auth/login.jsp");
        }

        @Test
        @DisplayName("Mật khẩu trống => báo \"Vui lòng nhập mật khẩu!\"")
        void blankPassword() throws Exception {
            givenLoginForm("an@gmail.com", "", null);
            when(req.getRequestDispatcher("/auth/login.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Vui lòng nhập mật khẩu!");
        }

        @Test
        @DisplayName("Mật khẩu 5 ký tự (dưới biên 6) => báo lỗi và không gọi tới service")
        void shortPassword() throws Exception {
            givenLoginForm("an@gmail.com", "12345", null);
            when(req.getRequestDispatcher("/auth/login.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            verify(userService, never()).loginByEmail(anyString(), anyString());
        }

        @Test
        @DisplayName("Sai email/mật khẩu => báo lỗi chung, không tiết lộ cái nào sai")
        void wrongCredentials() throws Exception {
            givenLoginForm("an@gmail.com", "saimatkhau", null);
            when(userService.loginByEmail("an@gmail.com", "saimatkhau")).thenReturn(null);
            when(req.getRequestDispatcher("/auth/login.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Email hoặc mật khẩu không đúng!");
            thenForwardedTo("/auth/login.jsp");
        }

        @Test
        @DisplayName("Tài khoản chưa kích hoạt => chuyển sang trang nhập OTP")
        void inactiveAccount_goesToVerifyPage() throws Exception {
            givenLoginForm("an@gmail.com", "dungmatkhau", null);
            User inactive = User.builder().email("an@gmail.com").active(false).build();
            when(userService.loginByEmail("an@gmail.com", "dungmatkhau")).thenReturn(inactive);
            when(req.getRequestDispatcher("/auth/verify-email.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(session).setAttribute("pendingEmail", "an@gmail.com");
            thenForwardedTo("/auth/verify-email.jsp");
        }

        @Test
        @Tag("smoke")
        @DisplayName("Đăng nhập đúng, KHÔNG tick \"Ghi nhớ\" => lưu session, xóa cookie, về /home")
        void success_withoutRemember() throws Exception {
            givenLoginForm("an@gmail.com", "dungmatkhau", null);
            User active = User.builder().userId(1).roleId(3).email("an@gmail.com").active(true).build();
            when(userService.loginByEmail("an@gmail.com", "dungmatkhau")).thenReturn(active);

            servlet.service(req, resp);

            verify(session).setAttribute("user", active);
            ArgumentCaptor<Cookie> cookie = ArgumentCaptor.forClass(Cookie.class);
            verify(resp).addCookie(cookie.capture());
            assertEquals("userEmail", cookie.getValue().getName());
            assertEquals(0, cookie.getValue().getMaxAge());          // xóa cookie
            verify(resp).sendRedirect("/VTechPhone/home");
        }

        @Test
        @DisplayName("Đăng nhập đúng, tick \"Ghi nhớ\" => cookie userEmail sống 7 ngày, HttpOnly")
        void success_withRemember() throws Exception {
            givenLoginForm("an@gmail.com", "dungmatkhau", "true");
            User active = User.builder().userId(1).roleId(3).email("an@gmail.com").active(true).build();
            when(userService.loginByEmail("an@gmail.com", "dungmatkhau")).thenReturn(active);

            servlet.service(req, resp);

            ArgumentCaptor<Cookie> cookie = ArgumentCaptor.forClass(Cookie.class);
            verify(resp).addCookie(cookie.capture());
            assertEquals("userEmail", cookie.getValue().getName());
            assertEquals("an@gmail.com", cookie.getValue().getValue());
            assertEquals(7 * 24 * 60 * 60, cookie.getValue().getMaxAge());
            assertTrue(cookie.getValue().isHttpOnly());
            verify(resp).sendRedirect("/VTechPhone/home");
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Đăng ký (POST /register)")
    class Register {

        private void givenRegisterForm(String fullName, String email, String phone, String address,
                                       String password, String confirm, String agree) {
            givenRequest("/register", "POST");
            when(req.getParameter("fullname")).thenReturn(fullName);
            when(req.getParameter("email")).thenReturn(email);
            when(req.getParameter("phone")).thenReturn(phone);
            when(req.getParameter("address")).thenReturn(address);
            when(req.getParameter("password")).thenReturn(password);
            when(req.getParameter("confirmPassword")).thenReturn(confirm);
            when(req.getParameter("agree")).thenReturn(agree);
        }

        @Test
        @DisplayName("Xác nhận mật khẩu không khớp => báo lỗi, không tạo tài khoản")
        void passwordMismatch() throws Exception {
            givenRegisterForm("Nguyễn An", "an@gmail.com", "0912345678", "HCM", "secret1", "secret2", "true");
            when(req.getRequestDispatcher("/auth/register.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Xác nhận mật khẩu không khớp!");
            verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Không tick đồng ý điều khoản => báo lỗi")
        void termsNotAccepted() throws Exception {
            givenRegisterForm("Nguyễn An", "an@gmail.com", "0912345678", "HCM", "secret1", "secret1", null);
            when(req.getRequestDispatcher("/auth/register.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Bạn phải đồng ý với điều khoản để đăng ký!");
        }

        @Test
        @DisplayName("Email không phải Gmail => báo lỗi và giữ lại dữ liệu đã nhập")
        void nonGmail_keepsEnteredValues() throws Exception {
            givenRegisterForm("Nguyễn An", "an@yahoo.com", "0912345678", "HCM", "secret1", "secret1", "true");
            when(req.getRequestDispatcher("/auth/register.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Email phải là Gmail hợp lệ (ví dụ: ten@gmail.com)!");
            verify(req).setAttribute("fullname", "Nguyễn An");   // form được điền lại
            verify(req).setAttribute("email", "an@yahoo.com");
        }

        @Test
        @DisplayName("Email đã được đăng ký => báo lỗi")
        void emailAlreadyRegistered() throws Exception {
            givenRegisterForm("Nguyễn An", "an@gmail.com", "0912345678", "HCM", "secret1", "secret1", "true");
            when(userService.existsByEmail("an@gmail.com")).thenReturn(true);
            when(req.getRequestDispatcher("/auth/register.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Email đã được đăng ký!");
        }

        @Test
        @Tag("smoke")
        @DisplayName("Đăng ký hợp lệ => tạo user, sinh OTP 6 số (hạn 5 phút), gửi email, sang trang xác thực")
        void success_createsUserAndSendsOtp() throws Exception {
            givenRegisterForm("Nguyễn An", "an@gmail.com", "0912345678", "HCM", "secret1", "secret1", "true");
            when(req.getRequestDispatcher("/auth/verify-email.jsp")).thenReturn(dispatcher);

            try (MockedStatic<EmailService> mail = mockStatic(EmailService.class)) {
                servlet.service(req, resp);

                verify(userService).register("Nguyễn An", "an@gmail.com", "0912345678", "HCM", "secret1");

                ArgumentCaptor<Object> otp = ArgumentCaptor.forClass(Object.class);
                verify(session).setAttribute(eq("otp_an@gmail.com"), otp.capture());
                assertTrue(otp.getValue().toString().matches("\\d{6}"), "OTP phải là 6 chữ số");

                long before = System.currentTimeMillis();
                ArgumentCaptor<Object> expiry = ArgumentCaptor.forClass(Object.class);
                verify(session).setAttribute(eq("otp_expiry_an@gmail.com"), expiry.capture());
                long ttl = (Long) expiry.getValue() - before;
                assertTrue(ttl > 4 * 60 * 1000 && ttl <= 5 * 60 * 1000, "OTP phải hết hạn sau ~5 phút");

                verify(session).setAttribute("pendingEmail", "an@gmail.com");
                mail.verify(() -> EmailService.sendEmail(eq("an@gmail.com"), anyString(), contains("OTP")));
                thenForwardedTo("/auth/verify-email.jsp");
            }
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Xác thực email bằng OTP (POST /verify-email)")
    class VerifyEmail {

        private void givenVerifyForm(String email, String otp) {
            givenRequest("/verify-email", "POST");
            when(req.getParameter("email")).thenReturn(email);
            when(req.getParameter("otp")).thenReturn(otp);
        }

        private void givenOtpInSession(String email, String otp, long expiryMillis) {
            when(session.getAttribute("otp_" + email)).thenReturn(otp);
            when(session.getAttribute("otp_expiry_" + email)).thenReturn(expiryMillis);
        }

        @Test
        @DisplayName("Không có OTP trong session => báo hết hạn/không tồn tại")
        void noOtpInSession() throws Exception {
            givenVerifyForm("an@gmail.com", "123456");
            when(req.getRequestDispatcher("/auth/verify-email.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Mã OTP đã hết hạn hoặc không tồn tại. Vui lòng yêu cầu gửi lại!");
        }

        @Test
        @DisplayName("OTP đã quá hạn => báo hết hạn và xóa OTP khỏi session")
        void expiredOtp() throws Exception {
            givenVerifyForm("an@gmail.com", "123456");
            givenOtpInSession("an@gmail.com", "123456", System.currentTimeMillis() - 1000);
            when(req.getRequestDispatcher("/auth/verify-email.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Mã OTP đã hết hạn! Vui lòng yêu cầu gửi lại.");
            verify(session).removeAttribute("otp_an@gmail.com");
            verify(session).removeAttribute("otp_expiry_an@gmail.com");
        }

        @Test
        @DisplayName("OTP sai => báo lỗi, KHÔNG kích hoạt tài khoản")
        void wrongOtp() throws Exception {
            givenVerifyForm("an@gmail.com", "000000");
            givenOtpInSession("an@gmail.com", "123456", System.currentTimeMillis() + 60_000);
            when(req.getRequestDispatcher("/auth/verify-email.jsp")).thenReturn(dispatcher);

            servlet.service(req, resp);

            verify(req).setAttribute("error", "Mã OTP không đúng!");
            verify(userService, never()).update(any(User.class));
        }

        @Test
        @Tag("smoke")
        @DisplayName("OTP đúng và còn hạn => kích hoạt tài khoản, xóa OTP, chuyển về /login")
        void correctOtp_activatesAccount() throws Exception {
            givenVerifyForm("an@gmail.com", "123456");
            givenOtpInSession("an@gmail.com", "123456", System.currentTimeMillis() + 60_000);
            User pending = User.builder().email("an@gmail.com").active(false).build();
            when(userService.findByEmail("an@gmail.com")).thenReturn(pending);

            servlet.service(req, resp);

            assertTrue(pending.isActive());
            verify(userService).update(pending);
            verify(session).removeAttribute("otp_an@gmail.com");
            verify(session).removeAttribute("otp_expiry_an@gmail.com");
            verify(session).removeAttribute("pendingEmail");
            verify(session).setAttribute("message", "Xác thực email thành công! Vui lòng đăng nhập.");
            verify(resp).sendRedirect("/VTechPhone/login");
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("Gửi lại OTP và đăng xuất")
    class ResendAndLogout {

        @Test
        @DisplayName("Gửi lại OTP: sinh mã mới, gửi mail, quay về trang xác thực")
        void resendOtp() throws Exception {
            when(req.getServletPath()).thenReturn("/resend-otp");
            when(req.getParameter("email")).thenReturn("an@gmail.com");

            try (MockedStatic<EmailService> mail = mockStatic(EmailService.class)) {
                servlet.service(req, resp);

                verify(session).setAttribute(eq("otp_an@gmail.com"), any());
                mail.verify(() -> EmailService.sendEmail(eq("an@gmail.com"), anyString(), anyString()));
                verify(resp).sendRedirect("/VTechPhone/verify-email");
            }
        }

        @Test
        @DisplayName("Đăng xuất: hủy session, xóa cookie, về trang sản phẩm")
        void logout() throws Exception {
            when(req.getServletPath()).thenReturn("/logout");

            servlet.service(req, resp);

            verify(session).invalidate();
            verify(resp).addCookie(any(Cookie.class));
            verify(resp).sendRedirect("/VTechPhone/shop/products");
        }
    }
}
