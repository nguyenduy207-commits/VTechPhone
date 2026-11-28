package vtechphone.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vtechphone.entity.User;

@DisplayName("AuthUtil - đăng nhập và phân quyền theo session")
class AuthUtilTest {

    private final HttpServletRequest req = mock(HttpServletRequest.class);
    private final HttpSession session = mock(HttpSession.class);

    private void givenLoggedInWithRole(int roleId) {
        User u = User.builder().userId(1).roleId(roleId).build();
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(u);
    }

    @Test
    @DisplayName("Chưa có session => chưa đăng nhập, không phải manager")
    void noSession_notAuthenticated() {
        when(req.getSession(false)).thenReturn(null);

        assertNull(AuthUtil.getUser(req));
        assertFalse(AuthUtil.isAuthenticated(req));
        assertFalse(AuthUtil.isManager(req));
    }

    @Test
    @DisplayName("Có session nhưng chưa lưu user => chưa đăng nhập")
    void sessionWithoutUser_notAuthenticated() {
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        assertFalse(AuthUtil.isAuthenticated(req));
    }

    @ParameterizedTest(name = "roleId={0} -> admin={1}, staff={2}, manager={3}")
    @CsvSource({
        "1, true,  false, true",    // ADMIN
        "2, false, true,  true",    // STAFF
        "3, false, false, false"    // CUSTOMER
    })
    @DisplayName("Phân quyền theo roleId: 1=Admin, 2=Staff, 3=Customer")
    void rolesAreMappedCorrectly(int roleId, boolean admin, boolean staff, boolean manager) {
        givenLoggedInWithRole(roleId);

        assertTrue(AuthUtil.isAuthenticated(req));
        assertEquals(admin, AuthUtil.isAdmin(req));
        assertEquals(staff, AuthUtil.isStaff(req));
        assertEquals(manager, AuthUtil.isManager(req));
    }

    @Test
    @DisplayName("setUser lưu user vào session với key \"user\"")
    void setUser_storesUserInSession() {
        when(req.getSession()).thenReturn(session);
        User u = User.builder().userId(5).build();

        AuthUtil.setUser(req, u);

        verify(session).setAttribute("user", u);
    }
}
