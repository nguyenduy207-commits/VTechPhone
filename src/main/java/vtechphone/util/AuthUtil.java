package vtechphone.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vtechphone.entity.User;

public class AuthUtil {
    private static final String SESSION_USER_KEY = "user";
    private static final int ROLE_ADMIN = 1;
    private static final int ROLE_STAFF = 2;

    private AuthUtil() {}

    public static void setUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_USER_KEY, user);
    }

    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return getUser(request) != null;
    }

    public static boolean isManager(HttpServletRequest request) {
        User user = getUser(request);
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_STAFF;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        User user = getUser(request);
        return user != null && user.getRoleId() == ROLE_ADMIN;
    }

    public static boolean isStaff(HttpServletRequest request) {
        User user = getUser(request);
        return user != null && user.getRoleId() == ROLE_STAFF;
    }
}