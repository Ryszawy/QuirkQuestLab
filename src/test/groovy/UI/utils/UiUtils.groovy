package UI.utils

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Shared
import spock.lang.Specification
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.concurrent.TimeUnit

class UiUtils extends Specification {
    public static final String HOME_URL = "https://practicesoftwaretesting.com/#/"
    public static final String USER_ACCOUNT_URL = "https://practicesoftwaretesting.com/#/account"
    public static final String CART_URL = "https://practicesoftwaretesting.com/#/checkout"
    public static final int TIMEOUT = 5
    public static String LOGIN_URL = "https://practicesoftwaretesting.com/#/auth/login"


    @Shared
    public static String userEmail

    @Shared
    public static String userPassword

    @Shared
    public WebDriver driver


    def setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver()
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
        registerRandomUser()
        loginUser(userEmail, userPassword)
    }

    void getHomePageAsAdmin() {
        driver.get(LOGIN_URL)
        loginUser("admin@practicesoftwaretesting.com", "welcome01")
    }

    void getCart() {
        driver.get(CART_URL)
    }

    String addToCart() {
        def name = getAvailableProductFromHomePage()
        def addToCartBtn = driver.findElement(By.xpath("//*[@id=\"btn-add-to-cart\"]"))
        addToCartBtn.click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))
        return name
    }

    String getProductFromHomePage() {
        driver.get(HOME_URL)
        def randomProductOnFirstPage = By.cssSelector("body > app-root > div > app-overview > div:nth-child(3) > div.col-md-9 > div.container > a:nth-child(1)")
        def randomElementName = driver.findElement(randomProductOnFirstPage)
        randomElementName.click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/product"))
        driver.findElement(By.xpath("/html/body/app-root/div/app-detail/div[1]/div[2]/h1")).getText()
    }

    String getAvailableProductFromHomePage() {
        driver.get(HOME_URL)
        def product = null
        for (int i = 1; i <= 9; i++) {
            def getProductInfo = driver.findElement(By.cssSelector("body > app-root > div > app-overview > div:nth-child(3) > div.col-md-9 > div.container > a:nth-child($i) > div.card-footer"))
            if (!getProductInfo.getText().contains("Out of stock")) {
                product = By.cssSelector("body > app-root > div > app-overview > div:nth-child(3) > div.col-md-9 > div.container > a:nth-child($i)")
                break
            }
        }
        if (product == null) {
            def nextPageBtn = driver.findElement(By.xpath("/html/body/app-root/div/app-overview/div[3]/div[2]/div[2]/app-pagination/nav/ul/li[5]/a"))
            if (!nextPageBtn.getAttribute("class").contains("disabled")) {
                throw new NoSuchElementException("There are no available products")
            }
            nextPageBtn.click()
            Thread.sleep(1000)
            getAvailableProductFromHomePage()
        }

        def randomElementName = driver.findElement(product)

        randomElementName.click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/product"))
        driver.findElement(By.xpath("/html/body/app-root/div/app-detail/div[1]/div[2]/h1")).getText()
    }


    private void loginUser(String userEmail, String userPassword) {
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(userEmail)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(userPassword)
        driver.findElement(By.xpath("//input[@type='submit']")).click()
        driver.get(HOME_URL)
    }

    private void registerUser(String email, String password) {
        def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
        def xpathRegister = By.xpath("//a[@href='#/auth/register']")

        driver.findElement(xpathSignInButton).click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))

        driver.findElement(xpathRegister).click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-8 auth-form']")))
        driver.findElement(By.xpath("//*[@id='first_name']")).sendKeys("firstName")
        driver.findElement(By.xpath("//*[@id='last_name']")).sendKeys("lastName")

        def element1 = driver.findElement(By.id("dob"))
        element1.sendKeys("12121200");
        element1.submit()

        driver.findElement(By.xpath("//*[@id='address']")).sendKeys("address")
        driver.findElement(By.xpath("//*[@id='postcode']")).sendKeys("21-370")
        driver.findElement(By.xpath("//*[@id='city']")).sendKeys("Warsaw")
        driver.findElement(By.xpath("//*[@id='state']")).sendKeys("state")

        def element = driver.findElement(By.cssSelector("select[id='country']"))

        def selectCountry = new Select(element)
        selectCountry.selectByVisibleText("Poland")
        driver.findElement(By.xpath("//*[@id='phone']")).sendKeys("123123123")
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(email)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(password)

        driver.findElement(By.xpath("//button[@type='submit']")).click()
        new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
    }
}
