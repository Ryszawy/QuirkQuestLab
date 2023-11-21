package UI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class UiUtils extends Specification {
    public static String HOME_URL = "https://practicesoftwaretesting.com/#/"
    public static String USER_ACCOUNT_URL = "https://practicesoftwaretesting.com/#/account"

    @Shared
    public static String userEmail

    @Shared
    public static String userPassword

    @Shared
    public WebDriver driver

    def setup() {
        driver = new SafariDriver()
        driver.manage().window().maximize()
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS)
        driver.get(HOME_URL)
        userEmail = "${UUID.randomUUID().toString()}@mail.com"
        userPassword = "${UUID.randomUUID().toString()}"
    }

    def cleanup() {
        driver.quit()
    }

    void registerRandomUser() {
        registerUser(userEmail, userPassword)
    }

    void getHomePageAsLoggedUser() {
        registerUser(userEmail, userPassword)
        loginUser(userEmail, userPassword)
    }

    private void loginUser(String userEmail, String userPassword) {
        registerRandomUser()
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(userEmail)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(userPassword)
        driver.findElement(By.xpath("//input[@type='submit']")).click()
        new WebDriverWait(driver, 3).until(ExpectedConditions.urlToBe(USER_ACCOUNT_URL))
        driver.get(HOME_URL)
    }

    private void registerUser(String email, String password) {
        def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
        def xpathRegister = By.xpath("//a[@href='#/auth/register']")

        driver.findElement(xpathSignInButton).click()
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))

        driver.findElement(xpathRegister).click()
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-8 auth-form']")))
        driver.findElement(By.xpath("//*[@id='first_name']")).sendKeys("firstName")
        driver.findElement(By.xpath("//*[@id='last_name']")).sendKeys("lastName")

        def element1 = driver.findElement(By.id("dob"))
        element1.sendKeys("12121200");
        element1.submit()

        driver.findElement(By.xpath("//*[@id='address']")).sendKeys("address")
        driver.findElement(By.xpath("//*[@id='postcode']")).sendKeys("21-370")
        driver.findElement(By.xpath("//*[@id='city']")).sendKeys("Lodz")
        driver.findElement(By.xpath("//*[@id='state']")).sendKeys("state")

        def element = driver.findElement(By.cssSelector("select[id='country']"))

        def selectCountry = new Select(element)
        selectCountry.selectByVisibleText("Poland")
        driver.findElement(By.xpath("//*[@id='phone']")).sendKeys("123123123")
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(email)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(password)

        driver.findElement(By.xpath("//button[@type='submit']")).click()
        new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
    }
}
