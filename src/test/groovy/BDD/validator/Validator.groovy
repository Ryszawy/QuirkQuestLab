package BDD.validator


import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class Validator {
    static By productsXpath = By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table")
    static By allertXpath = By.xpath("//div[contains(@class, 'alert')]")

    static void validateCurrentUrl(WebDriver driver, int timeout, String url) {
        new WebDriverWait(driver, timeout).until(urlContains(url))
    }

    static void validateUserBeingOnPageOfProduct(WebDriver driver, int timeout, String productUrl) {
        new WebDriverWait(driver, timeout).until(urlContains(productUrl))
    }

    static void validateUserSeeAddedProduct(WebDriver driver, String productName) {
        def products = driver.findElements(productsXpath)
        products.stream().anyMatch { it.getText().contains(productName) }
    }

    static void valideUserDoesNotSeeProduct(WebDriver driver, String productName) {
        def products = driver.findElements(productsXpath)
        !products.stream().anyMatch { it.getText().contains(productName) }
    }

    static void validateUserSeeUpdatedAmountInCart(WebDriver driver, String productName, double previousPrice) {
        def products = driver.findElements(productsXpath)
        def selectedProduct = products.stream().filter { it.getText().contains(productName) }.findFirst()
        def updatedPrice = selectedProduct.get().findElement(By.cssSelector("body > app-root > div > app-checkout > aw-wizard > div > aw-wizard-step:nth-child(1) > div > table > tbody > tr > td:nth-child(5) > span"))
        updatedPrice = updatedPrice.getText().toString().replace('$', "")
        updatedPrice = updatedPrice as double
        updatedPrice != previousPrice
    }

    static void validateAlertAboutSentMessage(WebDriver driver, int timeout) {
        new WebDriverWait(driver, timeout).until(visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-contact/div/div/div/div")))
    }

    static void validateUserRegistrationError(WebDriver driver, int timeout) {
        def alertXpath = allertXpath
        new WebDriverWait(driver, timeout).until(visibilityOfElementLocated(alertXpath))
        def alert = driver.findElement(alertXpath)
        alert.getText() == "A customer with this email address already exists."
    }

    static void validateUserSuccessfulRegistration(WebDriver driver, int timeout) {
        new WebDriverWait(driver, timeout).until(visibilityOfElementLocated(By.xpath("//div[@class='col-lg-6 auth-form']")))

    }

    static void validateUserLoginError(WebDriver driver, int timeout) {
        new WebDriverWait(driver, timeout).until(visibilityOfElementLocated(allertXpath))
        def alert = driver.findElement(allertXpath)
        alert.getText() == "Invalid email or password"
    }

    static void validateUserChangePassword(WebDriver driver, int timeout) {
        new WebDriverWait(driver, timeout).until(visibilityOfElementLocated(By.xpath("//div[@role='alert']")))
        new WebDriverWait(driver, timeout).until(urlContains("https://practicesoftwaretesting.com/#/auth/login"))
    }
}
