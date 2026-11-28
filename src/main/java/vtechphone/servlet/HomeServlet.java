package vtechphone.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vtechphone.entity.User;
import vtechphone.util.AuthUtil;

import java.io.IOException;

@WebServlet({"/home", "/admin/dashboard"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if (!AuthUtil.isAuthenticated(req) && !path.equals("/login")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = AuthUtil.getUser(req);
        
        if ("/home".equals(path)) {
            // Admin/Staff cũng sẽ vào Shop Products để mua sắm
            resp.sendRedirect(req.getContextPath() + "/shop/products");
            return;
        }
        
        if ("/admin/dashboard".equals(path)) {
            // Chỉ Admin/Staff mới được vào trang Admin
            if (user.getRoleId() == 1 || user.getRoleId() == 2) {
                req.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(req, resp);
                return;
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
        }
    }
}