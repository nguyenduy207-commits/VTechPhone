package vtechphone.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vtechphone.dao.UserDAO;
import vtechphone.entity.User;
import vtechphone.util.PasswordUtil;

/**
 * UNIT TEST thuần: chỉ kiểm tra UserServiceImpl, DAO bị thay bằng mock
 * => không cần SQL Server, chạy trong vài mili-giây.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    UserDAO dao;

    UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl();
        service.dao = dao; // field package-private nên test cùng package gán được mock vào
    }

    // =====================================================================
    @Nested
    @DisplayName("loginByEmail")
    class Login {

        @Test
        @DisplayName("Email hoặc mật khẩu rỗng/null => null và KHÔNG chạm DB")
        void blankInput_returnsNull_withoutHittingDao() {
            assertNull(service.loginByEmail(null, "123456"));
            assertNull(service.loginByEmail("   ", "123456"));
            assertNull(service.loginByEmail("a@gmail.com", null));
            assertNull(service.loginByEmail("a@gmail.com", "  "));

            verifyNoInteractions(dao);
        }

        @Test
        @DisplayName("Email không tồn tại => null")
        void unknownEmail_returnsNull() {
            when(dao.findByEmail("a@gmail.com")).thenReturn(null);

            assertNull(service.loginByEmail("a@gmail.com", "123456"));
        }

        @Test
        @DisplayName("Sai mật khẩu => null")
        void wrongPassword_returnsNull() {
            User u = User.builder().email("a@gmail.com")
                    .passwordHash(PasswordUtil.hashPassword("dungmatkhau")).build();
            when(dao.findByEmail("a@gmail.com")).thenReturn(u);

            assertNull(service.loginByEmail("a@gmail.com", "saimatkhau"));
        }

        @Test
        @Tag("smoke")
        @DisplayName("Đúng mật khẩu => trả về user; email được trim khoảng trắng")
        void correctPassword_returnsUser_andTrimsEmail() {
            User u = User.builder().email("a@gmail.com")
                    .passwordHash(PasswordUtil.hashPassword("dungmatkhau")).build();
            when(dao.findByEmail("a@gmail.com")).thenReturn(u);

            assertSame(u, service.loginByEmail("  a@gmail.com  ", "dungmatkhau"));
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("register / registerUser")
    class Register {

        @Test
        @Tag("smoke")
        @DisplayName("Dữ liệu hợp lệ => tạo user: email viết thường, mật khẩu được băm, chưa kích hoạt")
        void validData_createsInactiveCustomer() {
            when(dao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User u = service.register("  Nguyễn An  ", "An@Gmail.com", "0912345678", "HCM", "secret1");

            assertEquals("Nguyễn An", u.getFullName());      // đã trim
            assertEquals("an@gmail.com", u.getEmail());      // đã toLowerCase
            assertEquals(3, u.getRoleId());                  // ROLE_CUSTOMER
            assertFalse(u.isActive());                       // chờ xác thực OTP
            assertEquals(0, u.getCustomerPoint());
            assertEquals("MEMBER", u.getCustomerRank());
            assertNotEquals("secret1", u.getPasswordHash()); // không lưu mật khẩu thô
            assertTrue(PasswordUtil.checkPassword("secret1", u.getPasswordHash()));
            assertNotNull(u.getHireDate());                  // hireDate null => lấy ngày hiện tại
        }

        @Test
        @DisplayName("registerUser giữ nguyên roleId và chức vụ khi tạo nhân viên")
        void registerStaff_keepsRoleAndPosition() {
            when(dao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User u = service.registerUser("Trần Bình", "binh@gmail.com", null, null,
                    "secret1", 2, "Thu ngân", null);

            assertEquals(2, u.getRoleId());
            assertEquals("Thu ngân", u.getStaffPosition());
            assertFalse(u.isActive());
        }

        @Test
        @DisplayName("SĐT để trống là hợp lệ (không bắt buộc) và được lưu null")
        void blankPhone_isOptional() {
            when(dao.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User u = service.register("Nguyễn An", "an@gmail.com", "   ", "HCM", "secret1");

            assertNull(u.getPhone());
        }

        // ---- Họ tên: biên 1 (sai) | 2 (đúng) ----
        @ParameterizedTest(name = "họ tên \"{0}\" bị từ chối")
        @ValueSource(strings = {"A", "  A  ", ""})
        void invalidName_throws(String name) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.register(name, "an@gmail.com", "0912345678", "HCM", "secret1"));

            assertEquals("Họ tên không hợp lệ", ex.getMessage());
            verify(dao, never()).create(any());
        }

        @Test
        @DisplayName("Họ tên null bị từ chối")
        void nullName_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register(null, "an@gmail.com", "0912345678", "HCM", "secret1"));
        }

        @Test
        @DisplayName("Họ tên đúng 2 ký tự (biên dưới) được chấp nhận")
        void nameWithTwoChars_isAccepted() {
            assertDoesNotThrow(() -> service.register("An", "an@gmail.com", null, null, "secret1"));
        }

        // ---- Email: chỉ chấp nhận @gmail.com ----
        @ParameterizedTest(name = "email \"{0}\" bị từ chối")
        @ValueSource(strings = {"abc", "an@yahoo.com", "an@gmail.com.vn", "@gmail.com", "an gmail.com"})
        void invalidEmail_throws(String email) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.register("Nguyễn An", email, "0912345678", "HCM", "secret1"));

            assertEquals("Email không hợp lệ", ex.getMessage());
            verify(dao, never()).create(any());
        }

        @Test
        @DisplayName("Email null bị từ chối")
        void nullEmail_throws() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.register("Nguyễn An", null, null, null, "secret1"));

            assertEquals("Email không hợp lệ", ex.getMessage());
        }

        // ---- SĐT: bắt đầu bằng 0, tổng 9..15 chữ số ----
        @ParameterizedTest(name = "SĐT {0} => hợp lệ = {1}")
        @CsvSource({
            "01234567,         false",   // 8 chữ số  : dưới biên
            "012345678,        true",    // 9 chữ số  : biên dưới
            "012345678901234,  true",    // 15 chữ số : biên trên
            "0123456789012345, false",   // 16 chữ số : trên biên
            "912345678,        false"    // không bắt đầu bằng 0
        })
        void phoneLengthBoundaries(String phone, boolean valid) {
            if (valid) {
                assertDoesNotThrow(() -> service.register("Nguyễn An", "an@gmail.com", phone, null, "secret1"));
            } else {
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                        () -> service.register("Nguyễn An", "an@gmail.com", phone, null, "secret1"));
                assertEquals("SĐT không hợp lệ", ex.getMessage());
            }
        }

        // ---- Mật khẩu: biên 5 (sai) | 6 (đúng) ----
        @ParameterizedTest(name = "mật khẩu \"{0}\" => hợp lệ = {1}")
        @CsvSource({"12345, false", "123456, true"})
        void passwordLengthBoundary(String password, boolean valid) {
            if (valid) {
                assertDoesNotThrow(() -> service.register("Nguyễn An", "an@gmail.com", null, null, password));
            } else {
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                        () -> service.register("Nguyễn An", "an@gmail.com", null, null, password));
                assertEquals("Mật khẩu phải có ít nhất 6 ký tự", ex.getMessage());
            }
        }

        @Test
        @DisplayName("Mật khẩu null bị từ chối")
        void nullPassword_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.register("Nguyễn An", "an@gmail.com", null, null, null));
        }

        @Test
        @DisplayName("Email đã tồn tại => ném lỗi và không tạo user")
        void duplicateEmail_throws() {
            when(dao.findByEmail("an@gmail.com")).thenReturn(new User());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.register("Nguyễn An", "An@gmail.com", null, null, "secret1"));

            assertEquals("Email đã được đăng ký", ex.getMessage());
            verify(dao, never()).create(any());
        }
    }

    // =====================================================================
    @Nested
    @DisplayName("deleteUser / existsByEmail")
    class DeleteAndExists {

        @Test
        @DisplayName("Xóa user tồn tại => gọi dao.delete đúng đối tượng")
        void existingUser_isDeleted() {
            User u = User.builder().userId(9).build();
            when(dao.findById(9)).thenReturn(u);

            service.deleteUser(9);

            verify(dao).delete(u);
        }

        @Test
        @DisplayName("Xóa user không tồn tại => không làm gì, không lỗi")
        void missingUser_isIgnored() {
            when(dao.findById(9)).thenReturn(null);

            service.deleteUser(9);

            verify(dao, never()).delete(any());
        }

        @Test
        @DisplayName("existsByEmail phản ánh đúng kết quả tìm kiếm")
        void existsByEmail() {
            when(dao.findByEmail("co@gmail.com")).thenReturn(new User());
            when(dao.findByEmail("khong@gmail.com")).thenReturn(null);

            assertTrue(service.existsByEmail("co@gmail.com"));
            assertFalse(service.existsByEmail("khong@gmail.com"));
        }
    }
}
