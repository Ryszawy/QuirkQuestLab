package API.utils

import com.google.gson.Gson
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.http.Header
import io.restassured.specification.RequestSpecification
import model.InlineResponse2005
import model.UserRequest
import model.UserResponse
import model.UsersLoginBody

class UserAuthenticatorUtils {
    public static String ADMIN_EMAIL = "admin@practicesoftwaretesting.com"
    public static String ADMIN_PASSWORD = "welcome01"
    public static String USER1_EMAIL = "customer@practicesoftwaretesting.com"
    public static String USER1_PASSWORD = "welcome01"
    public static String USER2_EMAIL = "customer2@practicesoftwaretesting.com"
    public static String USER2_PASSWORD = "welcome01"
    public static final Gson gson = new Gson()
    public static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.practicesoftwaretesting.com/users")
            .build()

    static Header getAuthorizationHeaderForAnyUser(String email, String password) {
        def response = getAnyUserAuthResponse(email, password)
        new Header("Authorization", response.getTokenType() + response.getAccessToken())
    }

    static UserResponse createNewUser(UserRequest userRequest) {
        def authHeader = getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD)
        def addNewUserRequest = RestAssured.given(requestSpec)
                .contentType(ContentType.JSON)
                .body(gson.toJson(userRequest))
                .header(authHeader)
                .log().all()
        def responseUserCreation = addNewUserRequest.post("/register")
        responseUserCreation.then()
                .log().all()
                .statusCode(201)
        gson.fromJson(responseUserCreation.getBody().prettyPrint(), UserResponse.class)
    }

    static UserRequest anyUserRequestBody(String email = UUID.randomUUID().toString() + "@email.com", String password = "password") {
        def newUser = new UserRequest()
        newUser.setFirstName("firstName")
        newUser.lastName("lastName")
        newUser.address("address")
        newUser.city("city")
        newUser.state("state")
        newUser.country("country")
        newUser.postcode("1234AA")
        newUser.phone("0987654321")
        newUser.dob("1970-01-10")
        newUser.email(email)
        newUser.password(password)
        newUser
    }

    private static InlineResponse2005 getAnyUserAuthResponse(String email, String password) {
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

