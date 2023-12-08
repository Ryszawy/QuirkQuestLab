package API.test.Contact

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import model.ContactRequest
import model.ContactResponse
import model.MessageIdStatusBody
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.ContactUtils.messages
import static API.utils.ContactUtils.status
import static API.utils.ProductDetailsUtils.logout
import static API.utils.ProductDetailsUtils.unauthorized
import static API.utils.RequestUtils.*
import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given
import static io.restassured.http.ContentType.JSON

@Epic("REST API TESTS")
@Story("Verify CRUID Operations on Contact module")
class ContactSpec extends Specification {
    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    ContactResponse addNewMessage() {
        def requestBody = new ContactRequest()
        def name = UUID.randomUUID().toString()
        def lastName = UUID.randomUUID().toString()
        def email = UUID.randomUUID().toString()
        def subject = UUID.randomUUID().toString()
        def message = UUID.randomUUID().toString()
        requestBody.setFirstName(name)
        requestBody.setLastName(lastName)
        requestBody.setEmail(email)
        requestBody.setSubject(subject)
        requestBody.setMessage(message)
        def response = given().filter(new AllureRestAssured())
                .body(gson.toJson(requestBody))
                .contentType(JSON).post(messages)
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(contentType, applicationJsonContentTypeWithCharset)

        def jsonBody = response.body().prettyPrint()
        return gson.fromJson(jsonBody, ContactResponse.class)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not retrieve messages where not logged in")
    def "should not retrieve messages where not logged in"() {
        when: "send get to retrieve all message products"
            given().filter(new AllureRestAssured()).get(logout)
            def response = given().filter(new AllureRestAssured()).get(messages)
        then: "should return 401 Unauthorized"
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                    .body("message", Matchers.equalTo(unauthorized))
                    .header(contentType, applicationJsonContentType)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve messages where logged as user")
    def "should retrieve messages where logged as user"() {
        when: "send get to retrieve all message products"
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD))
                    .get(messages)
        then: "should return user messages"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body(Matchers.not(Matchers.isEmptyOrNullString()))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve messages where logged as admin")
    def "should retrieve messages where logged as admin"() {
        when: "send get to retrieve all message products"
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(
                            ADMIN_EMAIL, ADMIN_PASSWORD))
                    .get(messages)
        then: "should return user messages"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body(Matchers.not(Matchers.isEmptyOrNullString()))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should send new message")
    def "should send new message"() {
        when: "send post request"
            def requestBody = new ContactRequest()
            def name = UUID.randomUUID().toString()
            def lastName = UUID.randomUUID().toString()
            def email = UUID.randomUUID().toString()
            def subject = UUID.randomUUID().toString()
            def message = UUID.randomUUID().toString()
            requestBody.setFirstName(name)
            requestBody.setLastName(lastName)
            requestBody.setEmail(email)
            requestBody.setSubject(subject)
            requestBody.setMessage(message)

            def response = given().filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(JSON).post(messages)
        then: "should send message "
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def contactResponse = gson.fromJson(jsonBody, ContactResponse.class)
            with(contactResponse) {
                assert getEmail() == email
                assert getSubject() == subject
                assert getMessage() == message
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not retrieve message by wrong id")
    def "should not retrieve message by wrong id"() {
        when: "send get message by id"
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .get(messages + "/A")
        then: "should return empty body "
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("", Matchers.hasSize(0))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve message by proper id ")
    def "should retrieve message by proper id"() {
        when: "send get message by id"
            def message = addNewMessage()
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER2_EMAIL, USER2_PASSWORD))
                    .get(messages + "/" + addNewMessage().getId())
        then: "should not return empty body "
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def contactResponse = gson.fromJson(jsonBody, ContactResponse.class)
            with(contactResponse) {
                assert getId() == contactResponse.getId()
                assert getName() == contactResponse.getName()
                assert getEmail() == contactResponse.getEmail()
                assert getSubject() == contactResponse.getSubject()
                assert getMessage() == contactResponse.getMessage()
                assert getCreatedAt() == contactResponse.getCreatedAt()
                assert getStatus() == contactResponse.getStatus()
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not retrieve message by id test as non logged user")
    def "should not retrieve message by id test as non logged user"() {
        when: "send get message by id"
            given().filter(new AllureRestAssured()).get(logout)
            def newMessage = addNewMessage()
            def response = given().filter(new AllureRestAssured()).get(messages + "/" + newMessage.getId())
        then: "should return unauthorized  "
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                    .header(contentType, applicationJsonContentType)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not set message status as non logged user")
    def "should not set message status as non logged user "() {
        when: "try to put new message"
            given().filter(new AllureRestAssured()).get(logout)
            def newMessage = addNewMessage()
            def response = given().filter(new AllureRestAssured()).put(messages + "/" + newMessage.getId() + status)
        then: "should return unauthorized "
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                    .header(contentType, applicationJsonContentType)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should set message status as admin ")
    def "should set message status as admin "() {
        when: "try to put new message status"
            def newMessage = addNewMessage()
            def statusBody = new MessageIdStatusBody()
            statusBody.setStatus("RESOLVED")
            def response = given().filter(new AllureRestAssured())
                    .body(gson.toJson(statusBody))
                    .contentType(JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .put(messages + "/" + newMessage.getId() + status)
        then: "should return ok "
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
    }
}
