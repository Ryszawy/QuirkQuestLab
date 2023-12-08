package BDD.steps


import BDD.validator.Validator
import io.cucumber.groovy.EN
import io.cucumber.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~/^User see page of a product$/) { ->
    Validator.validateUserBeingOnPageOfProduct(driver, timeout, productUrl)
}
Then(~/^User see added product$/) { ->
    Validator.validateUserSeeAddedProduct(driver, productName)
}
Then(~/^User does not see the product in the cart$/) { ->
    Validator.valideUserDoesNotSeeProduct(driver, productName)
}

Then(~/^User see updated amount in cart$/) { ->
    Validator.validateUserSeeUpdatedAmountInCart(driver, productName, productPrice)
}
Then(~/^User sees alert that message was sent$/) { ->
    Validator.validateAlertAboutSentMessage(driver, timeout)
}
And(~/^User should see an error registartion message$/) { ->
    Validator.validateUserRegistrationError(driver, timeout)
}

And(~/User is on (.*)$/) { String url ->
    Validator.validateCurrentUrl(driver, timeout, url)
}

And(~/^User should see an login error message$/) { ->
    Validator.validateUserLoginError(driver, timeout)
}

And(~/^User see an alert of change password$/) { ->
    Validator.validateUserChangePassword(driver, timeout)
}

Then(~/^User does not see the product in favorites$/) { ->
    Validator.valideUserDoesNotSeeProduct(driver, productName)
}
Then(~/^User does not see the product in the product admin list$/) { ->
    Validator.valideUserDoesNotSeeProduct(driver, productName)
}