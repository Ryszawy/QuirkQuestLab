package BDD.context

import io.github.bonigarcia.wdm.WebDriverManager
import io.qameta.allure.Allure
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import java.util.concurrent.TimeUnit

class ScenarioContext {
    String homeUrl
    String userAccountUrl
    String cartUrl
    String productUrl
    double productPrice
    int timeout
    String loginUrl
    String userEmail
    String userPassword
    String adminEmail
    String adminPassword
    WebDriver driver
    String productName


    def setUp() {
        Allure.epic("BDD")
        homeUrl = "https://practicesoftwaretesting.com/#/"
        userAccountUrl = "https://practicesoftwaretesting.com/#/account"
        cartUrl = "https://practicesoftwaretesting.com/#/checkout"
        timeout = 10
        productUrl = "https://practicesoftwaretesting.com/#/product"
        loginUrl = "https://practicesoftwaretesting.com/#/auth/login"
        userEmail = "${UUID.randomUUID().toString()}@mail.com"
        userPassword = "${UUID.randomUUID().toString()}"
        adminEmail = "admin@practicesoftwaretesting.com"
        adminPassword = "welcome01"
        WebDriverManager.chromedriver().setup()
        driver = new ChromeDriver()
        driver.manage().window().maximize()
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS)
        driver.get(homeUrl)
    }

    def clean() {
        driver.close()
    }
}