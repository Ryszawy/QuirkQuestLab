package UI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ProductTestSpec extends UiUtils{

    def setup() {
        getHomePageAsLoggedUser()
    }

    def 'should add random product to favorites' () {
        given:
            def productName = getProductFromHomePage()
            def addToFavouriteBtn = By.xpath("//*[@id=\"btn-add-to-favorites\"]")
            def favouriteURL = "https://practicesoftwaretesting.com/#/account/favorites"
        when: "add product to favorites"
            driver.findElement(addToFavouriteBtn).click()
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))
            driver.get(favouriteURL)
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(favouriteURL))
        then: "product is located in favorites list on user profile"
            def products =  driver.findElements(By.xpath("//div[@data-test]"))
            products.stream().anyMatch{it.getText().contains(productName)}

    }

    def 'should remove product from favorites' () {
        given:
            def productName = getProductFromHomePage()
            def addToFavouriteBtn = By.xpath("//*[@id=\"btn-add-to-favorites\"]")
            def favouriteURL = "https://practicesoftwaretesting.com/#/account/favorites"
        when: "add product to favorites"
            driver.findElement(addToFavouriteBtn).click()
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='toast-body']")))
            driver.get(favouriteURL)
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.urlContains(favouriteURL))
        then: "locate added product in favorites list on user profile"
            def products =  driver.findElements(By.xpath("//div[@data-test]"))
            products.stream().anyMatch{it.getText().contains(productName)}
            def favouriteProductElement = products.stream().filter{it.getText().contains(productName)}.findFirst()
        when: "remove added product from favorites list"
            favouriteProductElement.get().findElement(By.cssSelector("button.btn-danger.btn.mb-3")).click()
        then: "product is delated"
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.invisibilityOf(favouriteProductElement.get()))
    }
}
