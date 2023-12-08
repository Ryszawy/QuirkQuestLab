package API.test.User

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import io.restassured.http.ContentType
import model.UserRequest
import model.UserResponse
import model.UsersLoginBody
import spock.lang.Specification
import spock.lang.Unroll

import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given
import static org.hamcrest.Matchers.is

@Epic("REST API TESTS")
@Story("Verify CRUID Operations on User module")
class UserSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com/users"
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should provide user token")
    @Unroll
    def "should provide user token for #email"(String email, String password) {
        given: "set up request"
            def requestBody = new UsersLoginBody()
            requestBody.setEmail(email)
            requestBody.setPassword(password)
            def request = given()
                    .filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(ContentType.JSON)
        when: "post login"
            def response = request.post("/login")
        then: "should 200 okay"
            response.then()
                    .statusCode(200)
        where:
            email                                   | password
            ADMIN_EMAIL                             | ADMIN_PASSWORD
            "customer@practicesoftwaretesting.com"  | "welcome01"
            "customer2@practicesoftwaretesting.com" | "welcome01"
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should not return token when credentials are invalid")
    def "should not return token when credentials are invalid"() {
        given: "set up request"
            def requestBody = new UsersLoginBody()
            requestBody.setEmail(ADMIN_PASSWORD)
            requestBody.setPassword("wrongpassword")
            def request = given()
                    .filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(ContentType.JSON)
        when: "post login"
            def response = request.post("/login")
        then: "should 401 - unauthorized"
            response.then()
                    .statusCode(401)
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should logout user")
    def "should logout user"() {
        given: "set up request to check if is logged"
            def loggedToken = getAuthorizationHeaderForAnyUser(USER2_EMAIL, USER2_PASSWORD)
            def request = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
            def response = request.get("/me")
            response.then()
                    .statusCode(200)
        when: "should logout user"
            request = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
            response = request.get("/logout")
        then:
            response.then()
                    .statusCode(200)
        when: "try to check information using that user authentication header"
            request = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
            response = request.get("/me")
        then: "should 401 - unauthorized"
            response.then()
                    .statusCode(401)
                    .body("message", is("Unauthorized"))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should return information about user with email")
    @Unroll
    def "should return information about user with email #email"(String email, String password) {
        given: "set up request"
            def authorizationHeader = getAuthorizationHeaderForAnyUser(email, password)
            def request = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(authorizationHeader)
        when: "get response"
            def response = request.get("/me")
        then: "should 200 okay"
            response.then()
                    .statusCode(200)
        where:
            email                                   | password
            ADMIN_EMAIL                             | ADMIN_PASSWORD
            "customer@practicesoftwaretesting.com"  | "welcome01"
            "customer2@practicesoftwaretesting.com" | "welcome01"
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should register user")
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
            def request = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .body(gson.toJson(newUser))
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
        when: "get response"
            def response = request.post("/register")
        then: "should 201 okay - user created"
            response.then()
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

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should delete correctly user")
    def 'should delete correctly user'() {
        given: "create new user"
            def newUserBody = anyUserRequestBody()
            def authHeader = getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD)
            def newUserCreationResponse = createNewUser(newUserBody, authHeader)
        when: "delete created user"
            def deleteUserRequest = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(authHeader)
            def userId = newUserCreationResponse.getId()
            def responseUserDeletion = deleteUserRequest.delete("/$userId")
        then: 'should 204 - user deleted'
            responseUserDeletion.then()
                    .statusCode(204)
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Test Description: should not delete when user has no permission for that operation")
    def 'should not delete when user has no permission for that operation'() {
        given: "create new user"
            def newUser = anyUserRequestBody()
            def userResponse = createNewUser(newUser, getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
        when: "try to delete created user using not permitted authorization header"
            def notPermittedAuthHeader = getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD)
            def deleteUserRequest = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(notPermittedAuthHeader)
            def userId = userResponse.getId()
            def responseUserDeletion = deleteUserRequest.delete("/$userId")
        then: 'should 403 - Forbidden'
            responseUserDeletion.then()
                    .statusCode(403)
                    .body("message", is("Forbidden"))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should update user information")
    def 'should update user information'() {
        given: "create new user"
            def userToUpdate = anyUserRequestBody()
            def authHeader = getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD)
            def userToUpdateResponse = createNewUser(userToUpdate, authHeader)
            def userId = userToUpdateResponse.getId()
        when: "update user information"
            def updatedUser = anyUserRequestBody(userToUpdate.getEmail())
            updatedUser.setFirstName("Test User")
            updatedUser.setAddress("new Address")
            def requestUpdate = given()
                    .contentType(ContentType.JSON)
                    .body(gson.toJson(updatedUser))
                    .header(authHeader)
                    .filter(new AllureRestAssured())
            def responseUpdate = requestUpdate.put("$userId")
        then: "should 200 - updated"
            responseUpdate.then()
                    .statusCode(200)
        when: "get info about updated user"
            def requestInfoForUpdatedUser = given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .header(authHeader)
            def responseInfoAboutUpdatedUser = requestInfoForUpdatedUser.get("/$userId")
        then: "should 200 - with updated data"
            responseInfoAboutUpdatedUser.then()
                    .statusCode(200)
            def jsonBody = responseInfoAboutUpdatedUser.body().prettyPrint()
            def updatedUserResponse = gson.fromJson(jsonBody, UserResponse.class)
            with(updatedUserResponse) {
                assert getFirstName() != userToUpdate.getFirstName()
                assert getFirstName() == "Test User"
                assert getLastName() == userToUpdate.getLastName()
                assert getAddress() != userToUpdate.getAddress()
                assert getAddress() == "new Address"
                assert getCity() == userToUpdate.getCity()
                assert getState() == userToUpdate.getState()
                assert getCountry() == userToUpdate.getCountry()
                assert getPostcode() == userToUpdate.getPostcode()
                assert getPhone() == userToUpdate.getPhone()
                assert getDob() == userToUpdate.getDob()
                assert getEmail() == userToUpdate.getEmail()
                assert !getId().isBlank()
            }

    }
}
