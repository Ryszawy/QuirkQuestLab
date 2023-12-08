Feature: BDD - As a User, I want to UI functionalities

  Scenario: A1 - As a User i want to add product to cart

    When User opens https://practicesoftwaretesting.com/#/
    And User clicks on random product
    Then User see page of a product
    When User clicks on "Add to cart"
    And User opens https://practicesoftwaretesting.com/#/checkout
    Then User see added product

  Scenario: A2 - As a User i want to remove product from cart

    Given User opens https://practicesoftwaretesting.com/#/
    And User clicks on random product
    Then User see page of a product
    When User clicks on "Add to cart"
    And User opens https://practicesoftwaretesting.com/#/checkout
    Then User see added product

    When User clicks on X button to remove product from cart
    Then User does not see the product in the cart

  Scenario: A3 - As a User i want to change amount in cart

    Given User opens https://practicesoftwaretesting.com/#/
    And User clicks on random product
    Then User see page of a product
    When User clicks on "Add to cart"
    And User opens https://practicesoftwaretesting.com/#/checkout
    Then User see added product

    When User change amount of selected product in cart
    And User unclick field amount
    Then User see updated amount in cart


  Scenario: C1 - As a User i want to add product to favourites
    Given User already has an account
    And User is already logged in
    And User opens https://practicesoftwaretesting.com/#/
    When User clicks on random product
    Then User see page of a product

    When User clicks on "Add to favorites"
    And User opens https://practicesoftwaretesting.com/#/account/favorites
    Then User see added product

  Scenario: C2 - As a User i want to remove product from favourities
    Given User already has an account
    And User is already logged in
    And User opens https://practicesoftwaretesting.com/#/
    When User clicks on random product
    Then User see page of a product

    When User clicks on "Add to favorites"
    And User opens https://practicesoftwaretesting.com/#/account/favorites
    Then User see added product

    When User clicks on "X"
    Then User does not see the product in favorites

  Scenario: C3 - As a User i want to send a message
    Given User already has an account
    And User is already logged in
    When User opens https://practicesoftwaretesting.com/#/contact
    And User fills message form
    And User clicks on "send"
    Then User sees alert that message was sent

  Scenario: C4 - As A Admin User i want to delete product
    Given User is already logged on admin account
    When User opens https://practicesoftwaretesting.com/#/admin/products
    Then User clicks on "delete"


  Scenario: T1 - As a User, I should not be able to create a new account with an existing email
    Given User already has an account
    And User opens https://practicesoftwaretesting.com/#/auth/register
    When User fills in the register form with email of his existing account
    Then User clicks on "register"
    And User should see an error registartion message

  Scenario: T2 - As a User, I should not be able to login with incorrect password
    Given User already has an account
    And User opens https://practicesoftwaretesting.com/#/auth/login
    When User fills in the login form with email of his existing account and incorrect password
    Then User clicks on "login"
    And User should see an login error message

  Scenario: T3 - As a User, I should be able to create a new account
    Given User opens https://practicesoftwaretesting.com/#/auth/register
    When User fills registration form
    Then User clicks on "register"
    And User is on https://practicesoftwaretesting.com/#/auth/login
    When User fills in the login form
    And User clicks on "login"
    Then User is on https://practicesoftwaretesting.com/#/account

  Scenario: T4 - As a User, I should be able to change my password
    Given User already has an account
    And User is already logged in
    And User is on https://practicesoftwaretesting.com/#/account
    When User clicks on "profile"
    And User fill form with his current and new password
    Then User clicks on "change password"
    And User see an alert of change password
    And User is on https://practicesoftwaretesting.com/#/auth/login

    When User fills in the login form
    Then User clicks on "login"
    And User is on https://practicesoftwaretesting.com/#/account