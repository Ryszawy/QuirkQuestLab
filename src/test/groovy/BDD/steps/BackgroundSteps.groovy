package BDD.steps

import BDD.context.ScenarioContext
import io.cucumber.groovy.EN
import io.cucumber.groovy.Hooks
import io.github.bonigarcia.wdm.WebDriverManager
import io.qameta.allure.Allure
import org.openqa.selenium.chrome.ChromeDriver

import java.util.concurrent.TimeUnit

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

World {
    new ScenarioContext()
}

Before {
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

After {
    driver.close()
}