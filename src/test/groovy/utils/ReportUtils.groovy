package utils

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification

class ReportUtils {
    public static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.practicesoftwaretesting.com/reports")
            .build()
}