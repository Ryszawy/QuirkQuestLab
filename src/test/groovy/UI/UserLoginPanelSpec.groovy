package UI

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

class UserLoginPanelSpec extends UiUtils {

    def 'should register user'() {
        given:
            def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
            def xpathRegister = By.xpath("//a[@href='#/auth/register']")
        when:
            driver.findElement(xpathSignInButton).click()
        then: 'should open registration panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
        when: 'fill brackets with data'
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
            driver.findElement(By.xpath("//*[@id='email']")).sendKeys("${UUID.randomUUID().toString()}@email.com")
            driver.findElement(By.xpath("//*[@id='password']")).sendKeys("password")
        and: 'submit registration'
            driver.findElement(By.xpath("//button[@type='submit']")).click()
        then: 'should register user and back to login panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
    }

    def 'should login user'() {
        given:
            registerRandomUser()
        when:
            driver.findElement(By.xpath("//*[@id='email']")).sendKeys(userEmail)
            driver.findElement(By.xpath("//*[@id='password']")).sendKeys(userPassword)
            driver.findElement(By.xpath("//input[@type='submit']")).click()
        then: 'should be logged in and move us to user account panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlToBe(USER_ACCOUNT_URL))
    }
}
