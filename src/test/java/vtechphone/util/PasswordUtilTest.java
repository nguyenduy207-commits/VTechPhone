package vtechphone.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PasswordUtil - băm và kiểm tra mật khẩu BCrypt")
class PasswordUtilTest {

    @Test
    @DisplayName("Hash không được trùng mật khẩu gốc, và checkPassword(đúng) = true")
    void hashThenCheck_returnsTrue() {
        String hash = PasswordUtil.hashPassword("123456");

        assertNotEquals("123456", hash);
        assertTrue(PasswordUtil.checkPassword("123456", hash));
    }

    @Test
    @DisplayName("Sai mật khẩu thì checkPassword = false")
    void wrongPassword_returnsFalse() {
        String hash = PasswordUtil.hashPassword("123456");

        assertFalse(PasswordUtil.checkPassword("654321", hash));
    }

    @Test
    @DisplayName("Cùng một mật khẩu băm 2 lần phải ra 2 hash khác nhau (do salt ngẫu nhiên)")
    void samePasswordTwice_givesDifferentHashes() {
        String h1 = PasswordUtil.hashPassword("123456");
        String h2 = PasswordUtil.hashPassword("123456");

        assertNotEquals(h1, h2);
        assertTrue(PasswordUtil.checkPassword("123456", h1));
        assertTrue(PasswordUtil.checkPassword("123456", h2));
    }
}
