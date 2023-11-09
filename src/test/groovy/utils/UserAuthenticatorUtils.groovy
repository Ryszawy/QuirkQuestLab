package utils

import com.google.gson.Gson
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.http.Header
import model.InlineResponse2005
import model.UsersLoginBody

class UserAuthenticatorUtils {
    public static String ADMIN_EMAIL = "admin@practicesoftwaretesting.com"
    public static String ADMIN_PASSWORD = "welcome01"
    public static String USER1_EMAIL = "customer@practicesoftwaretesting.com"
    public static String USER1_PASSWORD = "welcome01"
    public static String USER2_EMAIL = "customer2@practicesoftwaretesting.com"
    public static String USER2_PASSWORD = "welcome01"

    static Header getAuthorizationHeaderForAnyUser(String email, String password) {
        def response = getAnyUserAuthResponse(email, password)
        new Header("Authorization", response.getTokenType() + response.getAccessToken())
    }

    static InlineResponse2005 getAnyUserAuthResponse(String email, String password) {
        def requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://api.practicesoftwaretesting.com/users")
                .build()

        def requestBody = new UsersLoginBody()
        requestBody.setEmail(email)
        requestBody.setPassword(password)
        def request = RestAssured.given(requestSpec)
                .body(requestBody)
                .contentType(ContentType.JSON)
                .log().all()

        def response = request.post("/login")

        response.then()
                .log().all()
                .statusCode(200)

        def string = response.body().prettyPrint()
        new Gson().fromJson(string, InlineResponse2005.class)
    }
}
