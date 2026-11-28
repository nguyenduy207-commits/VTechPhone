package vtechphone.ui;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object: gói mọi thao tác trên trang /login để test không phải lặp lại By.id(...). */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.cssSelector("button.btn-primary");
    private final By errorBox = By.cssSelector(".alert-danger");

    public LoginPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage open() {
        driver.get(baseUrl + "/login");
        return this;
    }

    public LoginPage loginAs(String email, String password) {
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(submitButton).click();
        return this;
    }

    public String errorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorBox)).getText();
    }

    public boolean leftLoginPage() {
        return wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }
}
