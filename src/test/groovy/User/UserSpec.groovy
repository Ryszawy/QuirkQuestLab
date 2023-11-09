package User

import com.google.gson.Gson
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import model.UserRequest
import model.UserResponse
import model.UsersLoginBody
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static utils.UserAuthenticatorUtils.*

class UserSpec extends Specification {
    static final Gson gson = new Gson()

    @Shared
    def requestSpec =
            new RequestSpecBuilder()
                    .setBaseUri("https://api.practicesoftwaretesting.com/users")
                    .build()

    @Unroll
    def "should provide user token for #email"(String email, String password) {
        given: "set up request"
            def requestBody = new UsersLoginBody()
            requestBody.setEmail(email)
            requestBody.setPassword(password)
            def request = RestAssured.given(requestSpec)
                    .body(gson.toJson(requestBody))
                    .contentType(ContentType.JSON)
                    .log().all()

        when: "post login"
            def response = request.post("/login")

        then: "should 200 okay, response matching expected"
            response.then()
                    .log().all()
                    .statusCode(200)
        where:
            email                                   | password
            ADMIN_EMAIL                             | ADMIN_PASSWORD
            "customer@practicesoftwaretesting.com"  | "welcome01"
            "customer2@practicesoftwaretesting.com" | "welcome01"
    }

    def "should return 401 when credentials are invalid"() {
        given: "set up request"
            def requestBody = new UsersLoginBody()
            requestBody.setEmail(ADMIN_PASSWORD)
            requestBody.setPassword("wrongpassword")
            def request = RestAssured.given(requestSpec)
                    .body(gson.toJson(requestBody))
                    .contentType(ContentType.JSON)
                    .log().all()

        when: "post login"
            def response = request.post("/login")

        then: "should 401"
            response.then()
                    .log().all()
                    .statusCode(401)
    }

    @Unroll
    def "should return information about user with email #email"(String email, String password) {
        given: "set up request"
            def authorizationHeader = getAuthorizationHeaderForAnyUser(email, password)
            def request = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(authorizationHeader)
                    .log().all()
        when: "get response"
            def response = request.get("/me")
        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
        where:
            email                                   | password
            ADMIN_EMAIL                             | ADMIN_PASSWORD
            "customer@practicesoftwaretesting.com"  | "welcome01"
            "customer2@practicesoftwaretesting.com" | "welcome01"
    }

    def "should register user"() {
        given: "set up request"
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
            newUser.email(UUID.randomUUID().toString() + "@email.com")
            newUser.password("password")
            def request = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .body(gson.toJson(newUser))
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get response"
            def response = request.post("/register")
        then: "should 201 okay"
            response.then()
                    .log().all()
                    .statusCode(201)
            def jsonBody = response.body().prettyPrint()
            def userResponse = gson.fromJson(jsonBody, UserResponse.class)
            with(userResponse) {
                assert getFirstName() == newUser.getFirstName()
                assert getLastName() == newUser.getLastName()
                assert getAddress() == newUser.getAddress()
                assert getCity() == newUser.getCity()
                assert getState() == newUser.getState()
                assert getCountry() == newUser.getCountry()
                assert getPostcode() == newUser.getPostcode()
                assert getPhone() == newUser.getPhone()
                assert getDob() == newUser.getDob()
                assert getEmail() == newUser.getEmail()
                assert !getId().isBlank()
            }
    }
}
