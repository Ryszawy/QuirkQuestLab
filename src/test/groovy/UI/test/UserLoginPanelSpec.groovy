package UI.test

import UI.utils.UiUtils
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

class UserLoginPanelSpec extends UiUtils {


    public static final By emailXpath = By.xpath("//*[@id='email']")
    public static final By passwordXpath = By.xpath("//*[@id='password']")
    public static final By submitBtnXpath = By.xpath("//input[@type='submit']")

    def 'T1 - should not be able to register new user when user with such email is already registered'() {
        given:
            registerRandomUser()
            def existingEmail = this.userEmail
            def alertXpath = By.xpath("//div[contains(@class, 'alert')]")
        when: 'fill register panel with data'
            driver.get("https://practicesoftwaretesting.com/#/auth/register")
            def firstNameXpath = By.xpath("//*[@id='first_name']")
            driver.findElement(firstNameXpath).sendKeys("firstName")
            driver.findElement(By.xpath("//*[@id='last_name']")).sendKeys("lastName")

            def date = driver.findElement(By.id("dob"))
            date.sendKeys("12121200");
            date.submit()

            driver.findElement(By.xpath("//*[@id='address']")).sendKeys("address")
            driver.findElement(By.xpath("//*[@id='postcode']")).sendKeys("21-370")
            driver.findElement(By.xpath("//*[@id='city']")).sendKeys("Lodz")
            driver.findElement(By.xpath("//*[@id='state']")).sendKeys("state")
            def country = driver.findElement(By.cssSelector("select[id='country']"))

            def selectCountry = new Select(country)
            selectCountry.selectByVisibleText("Poland")
            driver.findElement(By.xpath("//*[@id='phone']")).sendKeys("123123123")
        and: "use existing email address"
            driver.findElement(emailXpath).sendKeys(existingEmail)
            driver.findElement(passwordXpath).sendKeys("password")
            driver.findElement(By.xpath("//button[@type='submit']")).click()
        then:
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(alertXpath))
            def alert = driver.findElement(alertXpath)
            alert.getText() == "A customer with this email address already exists."
    }

    def 'T2 - should not log in with incorrect password'() {
        given:
            registerRandomUser()
            driver.get(LOGIN_URL)
            def existingUserEmail = this.userEmail
            def incorrectPassword = "incorrect"
            def alertXpath = By.xpath("//div[@data-test='login-error']")
        when: 'fill login panel with existing user email and incorrect password'
            driver.findElement(emailXpath).sendKeys(existingUserEmail)
            driver.findElement(passwordXpath).sendKeys(incorrectPassword)
            driver.findElement(submitBtnXpath).click()
        then: 'alert message should be correct'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(alertXpath))
            def alert = driver.findElement(alertXpath)
            alert.getText() == "Invalid email or password"
    }

    def 'T3 - should register new user correctly'() {
        given:
            def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
            def xpathRegister = By.xpath("//a[@href='#/auth/register']")
            def email = "${UUID.randomUUID().toString()}@email.com"
            def password = "password"
        when:
            driver.findElement(xpathSignInButton).click()
        then: 'should open registration panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))
        when: 'fill brackets with data'
            driver.findElement(xpathRegister).click()
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-8 auth-form']")))
            driver.findElement(By.xpath("//*[@id='first_name']")).sendKeys("fName")
            driver.findElement(By.xpath("//*[@id='last_name']")).sendKeys("lName")
            def date = driver.findElement(By.id("dob"))
            date.sendKeys("01012000");
            date.submit()

            driver.findElement(By.xpath("//*[@id='address']")).sendKeys("random address")
            driver.findElement(By.xpath("//*[@id='postcode']")).sendKeys("02-137")
            driver.findElement(By.xpath("//*[@id='city']")).sendKeys("city")
            driver.findElement(By.xpath("//*[@id='state']")).sendKeys("state")
            def country = driver.findElement(By.cssSelector("select[id='country']"))

            def selectCountry = new Select(country)
            selectCountry.selectByVisibleText("Poland")
            driver.findElement(By.xpath("//*[@id='phone']")).sendKeys("987654321")

            driver.findElement(emailXpath).sendKeys(email)
            driver.findElement(passwordXpath).sendKeys(password)
        and: 'submit registration'
            driver.findElement(By.xpath("//button[@type='submit']")).click()
        then: 'should register user and back to login panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(LOGIN_URL))
        when: 'login as newly registered user'
            driver.findElement(emailXpath).sendKeys(email)
            driver.findElement(passwordXpath).sendKeys(password)
            driver.findElement(submitBtnXpath).click()
        then: 'should be logged in and move us to user account panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlToBe(USER_ACCOUNT_URL))
    }

    def 'T4 - should change user password'() {
        given:
            getHomePageAsLoggedUser()
            def registeredUserEmail = this.userEmail
            def registeredUserPassword = userPassword
            def newPassword = "newPassword"
        when:
            driver.get("https://practicesoftwaretesting.com/#/account")
            driver.findElement(By.xpath("//a[@routerlink='profile']")).click()
            driver.findElement(By.xpath("//*[@id='current-password']")).sendKeys(registeredUserPassword)
            driver.findElement(By.xpath("//*[@id='new-password']")).sendKeys(newPassword)
            driver.findElement(By.xpath("//*[@id='new-password-confirm']")).sendKeys(newPassword)
            driver.findElement(By.xpath("//button[@data-test='change-password-submit']")).click()
        then:
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='alert']")))
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/auth/login"))
        when:
            driver.findElement(emailXpath).sendKeys(registeredUserEmail)
            driver.findElement(passwordXpath).sendKeys(newPassword)
            driver.findElement(submitBtnXpath).click()
        then:
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlToBe(USER_ACCOUNT_URL))
    }

    def 'should login user'() {
        given:
            registerRandomUser()
        when:
            driver.findElement(emailXpath).sendKeys(userEmail)
            driver.findElement(passwordXpath).sendKeys(userPassword)
            driver.findElement(submitBtnXpath).click()
        then: 'should be logged in and move us to user account panel'
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlToBe(USER_ACCOUNT_URL))
    }


}
