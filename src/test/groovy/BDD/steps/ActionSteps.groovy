package BDD.steps

import BDD.action.Action
import BDD.validator.Validator
import com.codeborne.selenide.WebDriverRunner
import io.cucumber.groovy.EN
import io.cucumber.groovy.Hooks
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~/User opens (.*)$/) { String url ->
    driver.get(url)
    Validator.validateCurrentUrl(driver, timeout, url)
}

And(~/^User clicks on random product$/) { ->
    productName = Action.getAvailableProductFromHomePage(driver, homeUrl, timeout)
}

When(~/^User clicks on "([^"]*)"$/) { String buttonName ->
    Action.clickOnButton(driver, buttonName, timeout, productName)
}
When(~/^User clicks on X button to remove product from cart$/) { ->
    Action.deleteProductFromCart(driver, timeout, productName)
}
When(~/^User change amount of selected product in cart$/) { ->
    productPrice = Action.changeAmountOfSelectedProductInCart(driver, productName)
}
And(~/^User unclick field amount$/) { ->
    Action.unclickField(driver, timeout)
}
Given(~/^User already has an account$/) { ->
    Action.registerUser(driver, timeout, userEmail, userPassword)
}
And(~/^User is already logged in$/) { ->
    Action.loginUser(driver, timeout, userEmail, userPassword)
}
And(~/^User fills message form$/) { ->
    Action.fillMessageForm(driver)
}
When(~/^User fills in the register form with email of his existing account$/) { ->
    Action.fillRegisterForm(driver, timeout, userEmail, userPassword)
}
When(~/^User fills registration form$/) { ->
    Action.fillRegisterForm(driver, timeout, userEmail, userPassword)
}
When(~/^User fills in the login form$/) { ->
    Action.fillLoginForm(driver, userEmail, userPassword)
}
When(~/^User fills in the login form with email of his existing account and incorrect password$/) { ->
    Action.fillLoginForm(driver, userEmail, "wrong password")
}
And(~/^User fill form with his current and new password$/) { ->
    userPassword = Action.fillPasswordChangeForm(driver, userPassword, "new password")
}
Given(~/^User is already logged on admin account$/) { ->
    driver.get(loginUrl)
    Action.loginUser(driver, timeout, adminEmail, adminPassword)
}