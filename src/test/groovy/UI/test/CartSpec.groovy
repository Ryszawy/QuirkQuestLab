package UI.test

import UI.utils.UiUtils
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.Select

class CartSpec extends UiUtils {
    def 'should add product to cart'() {
        given: "add product to cart"
            def name = addToCart()
        when: "go to cart"
            getCart()
        then: "product is in cart"
            def products = driver.findElements(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table"))
            products.stream().anyMatch { it.getText().contains(name) }
    }

    def 'remove item from cart'() {
        given: "add product to cart and proceed to cart"
            def name = addToCart()
            getCart()
        when: "remove selected product from cart"
            def products = driver.findElements(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table"))
            def selectedProduct = products.stream().filter { it.getText().contains(name) }.findFirst()
            def removeBtn = selectedProduct.get().findElement(By.xpath("//i[@class='fa fa-remove']"))
            removeBtn.click()
            Thread.sleep((TIMEOUT / 2 as long) * 1000)
            products = driver.findElements(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table"))
        then: "product has been removed from cart"
            !products.stream().anyMatch { it.getText().contains(name) }
    }

    def 'change amount in cart'() {
        given: "add product to cart and proceed to cart"
            def name = addToCart()
            getCart()
        when: "change amount of selected product in cart"
            def products = driver.findElements(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/table"))
            def selectedProduct = products.stream().filter { it.getText().contains(name) }.findFirst()
            def price = selectedProduct.get().findElement(By.cssSelector("body > app-root > div > app-checkout > aw-wizard > div > aw-wizard-step:nth-child(1) > div > table > tbody > tr > td:nth-child(5) > span"))
            price = price.getText().toString().replace('$', "")
            price = price as double
            def number = 3
            def quantity = selectedProduct.get().findElement((By.cssSelector("input.form-control.quantity")))
            quantity.clear()
            quantity.sendKeys(number.toString())
            quantity.sendKeys(Keys.TAB)
            Thread.sleep((TIMEOUT / 5 as long) * 1000)
            def updatedPrice = selectedProduct.get().findElement(By.cssSelector("body > app-root > div > app-checkout > aw-wizard > div > aw-wizard-step:nth-child(1) > div > table > tbody > tr > td:nth-child(5) > span"))
            updatedPrice = updatedPrice.getText().toString().replace('$', "")
            updatedPrice = updatedPrice as double
        then: "price has been updated"
            updatedPrice == price * number
    }

    def 'order product'() {
        given: "log and add products to cart"
            getHomePageAsLoggedUser()
            addToCart()
        when: "order product"
            getCart()
            def proceedBtn = driver.findElement(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[1]/div/div/button"))
            proceedBtn.click()
            Thread.sleep((TIMEOUT / 5 as long) * 1000)
            proceedBtn = driver.findElement(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[2]/div/div/div/div/button"))
            proceedBtn.click()
            Thread.sleep((TIMEOUT / 5 as long) * 1000)
            proceedBtn = driver.findElement(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-step[3]/div/div/div/div/button"))
            proceedBtn.click()
            def confirmBtn = driver.findElement(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-completion-step/div/div/div/div/button"))
            def paymentMethod = driver.findElement(By.xpath("//*[@id='payment-method']"))
            def accountName = driver.findElement(By.xpath("//*[@id='account-name']"))
            def accountNumber = driver.findElement(By.xpath("//*[@id='account-number']"))
            def selectedMethod = new Select(paymentMethod)
            selectedMethod.selectByIndex(2)
            accountName.sendKeys("aaaa")
            accountNumber.sendKeys("1234")
            confirmBtn.click()
            Thread.sleep(TIMEOUT / 5 as Long)
            def orderResult = driver.findElement(By.xpath("/html/body/app-root/div/app-checkout/aw-wizard/div/aw-wizard-completion-step/div/div/div/form/div[4]/div"))
        then: "check if product has been ordered"
            orderResult.getText() == "Payment was successful"
    }
}
