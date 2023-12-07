package UI.test

import UI.utils.UiUtils
import io.qameta.allure.*
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

@Epic("UI Tests")
@Story("Test cases for Product")
class ProductSpec extends UiUtils {
    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should fill form and send it")
    def 'C1 - should add random product to favorites'() {
        given:
            getHomePageAsLoggedUser()
            takeScreenshot()
            def productName = getProductFromHomePage()
            def addToFavouriteBtn = By.xpath("//*[@id=\"btn-add-to-favorites\"]")
            def favouriteURL = "https://practicesoftwaretesting.com/#/account/favorites"
        when: "add product to favorites"
            takeScreenshot()
            driver.findElement(addToFavouriteBtn).click()
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))
            takeScreenshot()
            driver.get(favouriteURL)
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(favouriteURL))
        then: "product is located in favorites list on user profile"
            def products = driver.findElements(By.xpath("//div[@data-test]"))
            products.stream().anyMatch { it.getText().contains(productName) }
            takeScreenshot()
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should fill form and send it")
    def 'C2 - should remove product from favorites'() {
        given:
            getHomePageAsLoggedUser()
            takeScreenshot()
            def productName = getProductFromHomePage()
            def addToFavouriteBtn = By.xpath("//*[@id=\"btn-add-to-favorites\"]")
            def favouriteURL = "https://practicesoftwaretesting.com/#/account/favorites"
        when: "add product to favorites"
            driver.findElement(addToFavouriteBtn).click()
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))
            takeScreenshot()
            driver.get(favouriteURL)
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(favouriteURL))
        then: "locate added product in favorites list on user profile"
            def products = driver.findElements(By.xpath("//div[@data-test]"))
            products.stream().anyMatch { it.getText().contains(productName) }
            def favouriteProductElement = products.stream().filter { it.getText().contains(productName) }.findFirst()
        when: "remove added product from favorites list"
            takeScreenshot()
            favouriteProductElement.get().findElement(By.cssSelector("button.btn-danger.btn.mb-3")).click()
        then: "product is delated"
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOf(favouriteProductElement.get()))
            takeScreenshot()
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should fill form and send it")
    def 'C4 - should remove product'() {
        given:
            getHomePageAsAdmin()
            synchronized (driver) {
                driver.wait(1000)
            }
            takeScreenshot()
            def productsList = By.xpath("/html/body/app-root/div/app-products-list/table/tbody")
        when:
            driver.get("https://practicesoftwaretesting.com/#/admin/products")
            def products = driver.findElement(productsList).findElements(By.tagName("tr"))
                    .stream().filter {
                it.findElements(By.tagName("td"))
                        .get(2).getText().toInteger() > 0
            }.toList()
        then:
            boolean shouldBreak = false
            while (shouldBreak) {
                products.each {
                    def passed = false
                    try {
                        it.findElement(By.cssSelector("button.btn.btn-sm.btn-danger")).click()
                        passed = new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOf(it))
                    } catch (TimeoutException e) {

                    }
                    if (passed) {
                        shouldBreak = true
                    }
                }
            }
            takeScreenshot()
    }
}
