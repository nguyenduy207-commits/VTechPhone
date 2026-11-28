package vtechphone.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// 🔥 QUAN TRỌNG: Bắt đường dẫn gốc (http://localhost:8080/VTechPhone/)
@WebServlet("")
public class RootRedirectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lập tức chuyển hướng sang trang mua sắm dành cho khách hàng
        resp.sendRedirect(req.getContextPath() + "/shop/products");
    }
}