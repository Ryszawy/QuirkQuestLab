package UI

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ProductTestSpec extends UiUtils{

    def setup() {
        getHomePageAsLoggedUser()
    }

    def 'should add random product to favourite' () {
        given:
            def randomProductOnFirstPage = By.cssSelector("body > app-root > div > app-overview > div:nth-child(3) > div.col-md-9 > div.container > a:nth-child(1)")
            def addToFavouriteBtn = By.xpath("//*[@id=\"btn-add-to-favorites\"]")
            def favouriteURL = "https://practicesoftwaretesting.com/#/account/favorites"
        when:
            def randomElementName = driver.findElement(randomProductOnFirstPage)
            randomElementName.click()
        then:
            new WebDriverWait(driver, 3).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/product"))
        when:
            def productName = driver.findElement(By.xpath("/html/body/app-root/div/app-detail/div[1]/div[2]/h1")).getText()
            driver.findElement(addToFavouriteBtn).click()
        then:
            new WebDriverWait(driver, 3).until(ExpectedConditions.urlContains("https://practicesoftwaretesting.com/#/product"))


    }
}
