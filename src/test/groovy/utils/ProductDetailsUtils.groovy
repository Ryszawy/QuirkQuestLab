package utils

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder

class ProductDetailsUtils {
    public static final String brands = "/brands"
    public static final String categories = "/categories"
    public static final String products = "/products"
    public static final String images = "/images"
    public static final String cart = "/carts"
    public static final String logout = "/users/logout"
    public static final String baseURL = "https://api.practicesoftwaretesting.com"
    public static final String rfs = "Resource not found"
    public static final String unauthorized = "Unauthorized"
    public static final String forbidden = "Forbidden"
    public static final request = RestAssured.given(new RequestSpecBuilder()
            .setBaseUri(baseURL)
            .build())
}
