package User


import io.restassured.RestAssured
import io.restassured.http.ContentType
import model.UserRequest
import model.UserResponse
import model.UsersLoginBody
import spock.lang.Specification
import spock.lang.Unroll

import static org.hamcrest.Matchers.is
import static utils.UserAuthenticatorUtils.*

class UserSpec extends Specification {

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

    def "should not return token when credentials are invalid"() {
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

        then: "should 401 - unauthorized"
            response.then()
                    .log().all()
                    .statusCode(401)
    }

    def "should logout user"() {
        given: "set up request to check if is logged"
            def loggedToken = getAuthorizationHeaderForAnyUser(USER2_EMAIL, USER2_PASSWORD)
            def request = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
                    .log().all()
            def response = request.get("/me")
            response.then()
                    .log().all()
                    .statusCode(200)
        when: "should logout user"
            request = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
                    .log().all()
            response = request.get("/logout")

        then:
            response.then()
                    .log().all()
                    .statusCode(200)
        when: "try to check information using that user authentication header"
            request = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(loggedToken)
                    .log().all()
            response = request.get("/me")
        then: "should 401 - unauthorized"
            response.then()
                    .log().all()
                    .statusCode(401)
                    .body("message", is("Unauthorized"))
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
        then: "should 201 okay - user created"
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

    def 'should delete correctly user'() {
        given: "create new user"
            def newUserBody = anyUserRequestBody()
            def authHeader = getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD)
            def newUserCreationResponse = createNewUser(newUserBody)
        when: "delete created user"
            def deleteUserRequest = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(authHeader)
                    .log().all()
            def userId = newUserCreationResponse.getId()
            def responseUserDeletion = deleteUserRequest.delete("/$userId")
        then: 'should 204 - user deleted'
            responseUserDeletion.then()
                    .log().all()
                    .statusCode(204)
    }

    def 'should not delete when user has no permission for that operation'() {
        given: "create new user"
            def newUser = anyUserRequestBody()
            def userResponse = createNewUser(newUser)
        when: "try to delete created user using not permitted authorization header"
            def notPermittedAuthHeader = getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD)
            def deleteUserRequest = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(notPermittedAuthHeader)
                    .log().all()
            def userId = userResponse.getId()
            def responseUserDeletion = deleteUserRequest.delete("/$userId")
        then: 'should 403 - Forbidden'
            responseUserDeletion.then()
                    .log().all()
                    .statusCode(403)
                    .body("message", is("Forbidden"))
    }

    def 'should update user information'() {
        given: "create new user"
            def userToUpdate = anyUserRequestBody()
            def authHeader = getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD)
            def userToUpdateResponse = createNewUser(userToUpdate)
            def userId = userToUpdateResponse.getId()
        when: "update user information"
            def updatedUser = anyUserRequestBody(userToUpdate.getEmail())
            updatedUser.setFirstName("Test User")
            updatedUser.setAddress("new Address")
            def requestUpdate = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .body(gson.toJson(updatedUser))
                    .header(authHeader)
                    .log().all()
            def responseUpdate = requestUpdate.put("$userId")
        then: "should 200 - updated"
            responseUpdate.then()
                    .log().all()
                    .statusCode(200)
        when: "get info about updated user"
            def requestInfoForUpdatedUser = RestAssured.given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(authHeader)
                    .log().all()
            def responseInfoAboutUpdatedUser = requestInfoForUpdatedUser.get("/$userId")
        then: "should 200 - with updated data"
            responseInfoAboutUpdatedUser.then()
                    .log().all()
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
