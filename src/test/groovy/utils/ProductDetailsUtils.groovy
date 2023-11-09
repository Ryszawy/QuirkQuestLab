package utils

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder

class ProductDetailsUtils {
    public static final String brands = "/brands"
    public static final String categories = "/categories"
    public static final String products = "/products"
    public static final String images = "/images"
    public static final String cart = "/cart"
    public static final String logout = "/users/logout"
    public static final String baseURL = "https://api.practicesoftwaretesting.com"
    public static final request = RestAssured.given(new RequestSpecBuilder()
            .setBaseUri(baseURL)
            .build())
}
