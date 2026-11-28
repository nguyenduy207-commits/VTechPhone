package vtechphone.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vtechphone.entity.User;
import vtechphone.service.UserService;
import vtechphone.service.UserServiceImpl;
import vtechphone.util.AuthUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

@WebServlet({"/login-google", "/oauth2callback"})
public class GoogleLoginServlet extends HttpServlet {

    // 🔥 THAY BẰNG CLIENT ID VÀ SECRET CỦA BẠN
    private static final String CLIENT_ID = "500854022936-o5nasjrahkr2jrhvhu56upp2b94hnkoh.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/VTechPhone/oauth2callback";

    private UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        // Bước 1: Chuyển hướng đến Google
        if ("/login-google".equals(path)) {
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?"
                    + "client_id=" + CLIENT_ID
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                    + "&response_type=code"
                    + "&scope=email%20profile"
                    + "&access_type=offline";
            resp.sendRedirect(authUrl);
            return;
        }

        // Bước 2: Xử lý callback
        if ("/oauth2callback".equals(path)) {
            String code = req.getParameter("code");
            if (code == null) {
                resp.sendRedirect(req.getContextPath() + "/login?error=google_failed");
                return;
            }

            try {
                // Đổi code lấy access token
                String tokenUrl = "https://oauth2.googleapis.com/token";
                String params = "code=" + code
                        + "&client_id=" + CLIENT_ID
                        + "&client_secret=" + CLIENT_SECRET
                        + "&redirect_uri=" + REDIRECT_URI
                        + "&grant_type=authorization_code";

                URL url = new URL(tokenUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(params.getBytes(StandardCharsets.UTF_8));
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JsonObject tokenJson = JsonParser.parseString(jsonResponse.toString()).getAsJsonObject();
                String accessToken = tokenJson.get("access_token").getAsString();

                // Lấy thông tin user
                String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
                URL userInfo = new URL(userInfoUrl);
                HttpURLConnection userConn = (HttpURLConnection) userInfo.openConnection();
                userConn.setRequestProperty("Authorization", "Bearer " + accessToken);

                BufferedReader userReader = new BufferedReader(new InputStreamReader(userConn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder userJson = new StringBuilder();
                while ((line = userReader.readLine()) != null) {
                    userJson.append(line);
                }
                userReader.close();

                JsonObject userInfoJson = JsonParser.parseString(userJson.toString()).getAsJsonObject();
                String email = userInfoJson.get("email").getAsString();
                String fullName = userInfoJson.has("name") ? userInfoJson.get("name").getAsString() : email;

                // Kiểm tra user trong DB
                User user = userService.findByEmail(email);
                if (user == null) {
                    user = userService.register(fullName, email, "", "", "");
                    user.setActive(true);
                    userService.update(user);
                }

                AuthUtil.setUser(req, user);
                resp.sendRedirect(req.getContextPath() + "/home");

            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/login?error=google_failed");
            }
        }
    }
}