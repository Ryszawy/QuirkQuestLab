package BDD.action

import BDD.context.ScenarioContext
import com.codeborne.selenide.WebDriverRunner
import com.github.javafaker.Faker
import io.qameta.allure.Allure
import io.qameta.allure.Attachment
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

import java.util.NoSuchElementException

import static BDD.validator.Validator.validateUserSuccessfulRegistration

class Action {
    static By productsXpath = By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table")

    static String getAvailableProductFromHomePage(WebDriver driver, String homePageUrl, int timeout) {
        driver.get(homePageUrl);
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
            Thread.sleep(timeout / 5 * 1000 as long)
            getAvailableProductFromHomePage(driver, homePageUrl, timeout)
        }

        def randomElementName = driver.findElement(product)

        randomElementName.click()
        new WebDriverWait(driver, timeout).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/product"))
        driver.findElement(By.xpath("/html/body/app-root/div/app-detail/div[1]/div[2]/h1")).getText()
    }

    @Attachment(value = "Web page screenshot", type = "image/png")
    static byte[] takeScreenshot() {
        ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
    }

    static void clickOnButton(WebDriver driver, String s, int timeout, String productName = null) {
        if (s == "Add to cart") {
            driver.findElement(By.xpath("//*[@id=\"btn-add-to-cart\"]")).click()
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))

        } else if (s == "send") {
            driver.findElement(By.xpath("/html/body/app-root/div/app-contact/div/div/div/form/div/div[2]/div[4]/input"))
                    .click()
        } else if (s == "register") {
            clickRegisterButton(driver)
        } else if (s == "login") {
            clickLoginButton(driver)
        } else if (s == "profile") {
            driver.findElement(By.xpath("//a[@routerlink='profile']")).click()
        } else if (s == "change password") {
            driver.findElement(By.xpath("//button[@data-test='change-password-submit']")).click()
        } else if (s == "Add to favorites") {
            driver.findElement(By.xpath("//*[@id=\"btn-add-to-favorites\"]")).click()
            Thread.sleep((timeout / 2 as long) * 1000)
        } else if (s == "X") {
            def products = driver.findElements(By.xpath("//div[@data-test]"))
            products.stream().anyMatch { it.getText().contains(productName) }
            def favouriteProductElement = products.stream().filter { it.getText().contains(productName) }.findFirst()
            favouriteProductElement.get().findElement(By.cssSelector("button.btn-danger.btn.mb-3")).click()
            Thread.sleep((timeout / 2 as long) * 1000)
        } else if (s == "delete") {
            deleteFromAdminProductList(driver, timeout)
        } else {
            throw new IllegalAccessException()
        }
    }

    private static void deleteFromAdminProductList(WebDriver driver, int timeout) {
        def productsList = By.xpath("/html/body/app-root/div/app-products-list/table/tbody")
        def products = driver.findElement(productsList).findElements(By.tagName("tr"))
                .stream().filter {
            it.findElements(By.tagName("td"))
                    .get(2).getText().toInteger() > 0
        }.toList()
        boolean shouldBreak = false
        while (shouldBreak) {
            products.each {
                def passed = false
                try {
                    it.findElement(By.cssSelector("button.btn.btn-sm.btn-danger")).click()
                    passed = new WebDriverWait(driver, timeout).until(ExpectedConditions.invisibilityOf(it))
                } catch (TimeoutException e) {

                }
                if (passed) {
                    shouldBreak = true
                }
            }
        }
    }

    static void deleteProductFromCart(WebDriver driver, int timeout, String productName) {
        def products = driver.findElements(productsXpath)
        def selectedProduct = products.stream().filter { it.getText().contains(productName) }.findFirst()
        def removeBtn = selectedProduct.get().findElement(By.xpath("//i[@class='fa fa-remove']"))
        removeBtn.click()
        Thread.sleep((timeout / 2 as long) * 1000)
    }

    static double changeAmountOfSelectedProductInCart(WebDriver driver, String productName) {
        def products = driver.findElements(productsXpath)
        def selectedProduct = products.stream().filter { it.getText().contains(productName) }.findFirst()
        def price = selectedProduct.get().findElement(By.cssSelector("body > app-root > div > app-checkout > aw-wizard > div > aw-wizard-step:nth-child(1) > div > table > tbody > tr > td:nth-child(5) > span"))
        price = price.getText().toString().replace('$', "")
        def number = 3
        def quantity = selectedProduct.get().findElement((By.cssSelector("input.form-control.quantity")))
        quantity.clear()
        quantity.sendKeys(number.toString())
        price as double
    }

    static void unclickField(WebDriver driver, int timeout) {
        def products = driver.findElement(By.xpath("//aw-wizard-step[@steptitle='Cart']"))
        products.click()
        Thread.sleep((timeout / 5 as long) * 1000)
    }


    static void registerUser(WebDriver driver, int timeout, String email, String password) {
        fillRegisterForm(driver, timeout, email, password)
        clickRegisterButton(driver)
        validateUserSuccessfulRegistration(driver, timeout)
    }

    static void clickRegisterButton(WebDriver driver) {
        driver.findElement(By.xpath("//button[@type='submit']")).click()
    }

    static void clickLoginButton(WebDriver driver) {
        driver.findElement(By.xpath("//input[@type='submit']")).click()
    }

    static void fillRegisterForm(WebDriver driver, int timeout, String email, String password) {
        def xpathSignInButton = By.xpath("//*[@id=\"navbarSupportedContent\"]/ul/li[4]/a")
        def xpathRegister = By.xpath("//a[@href='#/auth/register']")

        driver.findElement(xpathSignInButton).click()
        new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))

        driver.findElement(xpathRegister).click()
        new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col-lg-8 auth-form']")))
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
    }

    static void loginUser(WebDriver driver, int timeout, String userEmail, String userPassword) {
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(userEmail)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(userPassword)
        driver.findElement(By.xpath("//input[@type='submit']")).click()
        Thread.sleep((timeout / 5) * 1000 as long)
    }

    static void fillMessageForm(WebDriver driver) {
        def firstNameInput = By.xpath("//*[@id=\"first_name\"]")
        def lastNameInput = By.xpath("//*[@id=\"last_name\"]")
        def emailInput = By.xpath("//*[@id=\"email\"]")
        def subjectSelect = By.xpath("//*[@id=\"subject\"]")
        def message = By.xpath("//*[@id=\"message\"]")
        driver.findElement(firstNameInput).sendKeys("firstName")
        driver.findElement(lastNameInput).sendKeys("lastName")
        driver.findElement(emailInput).sendKeys("userEmail@email.com")
        def submitElement = driver.findElement(subjectSelect)
        def selectedSubject = new Select(submitElement)
        selectedSubject.selectByVisibleText("Return")
        driver.findElement(message).sendKeys(new Faker().lorem().characters(51, 249))
    }

    static void fillLoginForm(WebDriver driver, String userEmail, String userPassword) {
        driver.findElement(By.xpath("//*[@id='email']")).sendKeys(userEmail)
        driver.findElement(By.xpath("//*[@id='password']")).sendKeys(userPassword)
    }

    static String fillPasswordChangeForm(WebDriver driver, String userPassword, String newPassword) {
        driver.findElement(By.xpath("//*[@id='current-password']")).sendKeys(userPassword)
        driver.findElement(By.xpath("//*[@id='new-password']")).sendKeys(newPassword)
        driver.findElement(By.xpath("//*[@id='new-password-confirm']")).sendKeys(newPassword)
        newPassword
    }
}
