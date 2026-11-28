package vtechphone.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Test giao diện thật bằng Chrome. Cần Tomcat + SQL Server đang chạy.
 * Gắn tag "ui" nên mặc định bị loại khỏi `mvn test` (xem pom). Chạy riêng:
 *   mvn test -Dtest.groups=ui -Dtest.excludedGroups= -DbaseUrl=http://localhost:8080/VTechPhone
 * Tài khoản thật truyền qua tham số, KHÔNG viết cứng trong code:
 *   -Dtest.email=... -Dtest.password=...
 */
@Tag("ui")
@Tag("smoke")
@DisplayName("Giao diện đăng nhập (Selenium)")
class LoginUiTest {

    private static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:8080/VTechPhone");

    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver, BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Sai mật khẩu => hiện thông báo lỗi")
    void wrongPassword_showsError() {
        loginPage.open().loginAs("khongtontai@gmail.com", "saimatkhau");

        assertEquals("Email hoặc mật khẩu không đúng!", loginPage.errorMessage());
    }

    @Test
    @DisplayName("Mật khẩu dưới 6 ký tự => hiện thông báo lỗi")
    void shortPassword_showsError() {
        loginPage.open().loginAs("khongtontai@gmail.com", "123");

        assertEquals("Mật khẩu phải có ít nhất 6 ký tự!", loginPage.errorMessage());
    }

    @Test
    @DisplayName("Tài khoản thật đăng nhập thành công => rời khỏi trang /login")
    void validAccount_leavesLoginPage() {
        String email = System.getProperty("test.email");
        String password = System.getProperty("test.password");
        assumeTrue(email != null && password != null, "Bỏ qua: chưa truyền -Dtest.email / -Dtest.password");

        loginPage.open().loginAs(email, password);

        assertTrue(loginPage.leftLoginPage());
    }
}
