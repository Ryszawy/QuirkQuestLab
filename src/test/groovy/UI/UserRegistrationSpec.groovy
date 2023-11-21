package UI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class UserRegistrationSpec extends Specification {
    private static String URL = "https://practicesoftwaretesting.com/#/"
    private WebDriver driver;

    def setup() {
        driver = new SafariDriver()
        driver.manage().window().maximize()
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS)
    }

    def cleanup() {
        driver.quit()
    }


    def 'should login user'() {
        given:
            driver.get(URL)
            def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
            def xpathRegister = By.xpath("//a[@href='#/auth/register']")
        when:
            driver.findElement(xpathSignInButton).click()
        then:
            new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
        when:
            driver.findElement(xpathRegister).click()
            new WebDriverWait(driver, 3).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-8 auth-form']")))
            driver.findElement(By.xpath("//*[@id='first_name']")).sendKeys("firstName")
            driver.findElement(By.xpath("//*[@id='last_name']")).sendKeys("lastName")

            def element1 = driver.findElement(By.id("dob"))
            element1.sendKeys("12/12/1200");
            element1.submit()

            driver.findElement(By.xpath("//*[@id='address']")).sendKeys("address")
            driver.findElement(By.xpath("//*[@id='postcode']")).sendKeys("21-370")
            driver.findElement(By.xpath("//*[@id='city']")).sendKeys("Lodz")
            driver.findElement(By.xpath("//*[@id='state']")).sendKeys("state")

            def element = driver.findElement(By.cssSelector("select[id='country']"))

            def selectCountry = new Select(element)
            selectCountry.selectByVisibleText("Poland")
            driver.findElement(By.xpath("//*[@id='phone']")).sendKeys("123123123")
            driver.findElement(By.xpath("//*[@id='email']")).sendKeys("email@email.com")
            driver.findElement(By.xpath("//*[@id='password']")).sendKeys("password")
        then:
            driver.findElement(By.xpath("//button[@type='submit']")).click()
            driver

    }
}
