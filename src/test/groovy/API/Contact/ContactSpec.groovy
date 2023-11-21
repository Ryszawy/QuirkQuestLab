package API.Contact


import model.ContactRequest
import model.ContactResponse
import model.MessageIdStatusBody
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import API.utils.UserAuthenticatorUtils

import static API.utils.UserAuthenticatorUtils.ADMIN_EMAIL
import static API.utils.UserAuthenticatorUtils.ADMIN_PASSWORD
import static API.utils.UserAuthenticatorUtils.USER1_EMAIL
import static API.utils.UserAuthenticatorUtils.USER1_PASSWORD
import static API.utils.UserAuthenticatorUtils.USER2_EMAIL
import static API.utils.UserAuthenticatorUtils.USER2_PASSWORD
import static API.utils.UserAuthenticatorUtils.gson

class ContactSpec extends Specification {

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
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.post(API.utils.ContactUtils.messages)
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        return gson.fromJson(jsonBody, ContactResponse.class)

    }

    def "should not retrieve messages where not logged in"() {
        when: "send get to retrieve all message products"
        API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ContactUtils.messages)
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.unauthorized))
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should retrieve messages where logged as user"() {
        when: "send get to retrieve all message products"
        def response = API.utils.ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD))
                .get(API.utils.ContactUtils.messages)
        then: "should return user messages"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }

    def "should retrieve messages where logged as admin "() {
        when: "send get to retrieve all message products"
        def response = API.utils.ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD))
                .get(API.utils.ContactUtils.messages)
        then: "should return user messages"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }

    def "should send new message "() {
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
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.post(API.utils.ContactUtils.messages)
        then: "should send message "
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def contactResponse = gson.fromJson(jsonBody, ContactResponse.class)
        with(contactResponse) {
            assert getEmail() == email
            assert getSubject() == subject
            assert getMessage() == message
        }
    }

    def "should not retrieve message by wrong id "() {
        when: "send get message by id"
        def response = API.utils.ProductDetailsUtils.request.header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                ADMIN_EMAIL, ADMIN_PASSWORD)).get(API.utils.ContactUtils.messages + "/A")
        then: "should return empty body "
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body("",Matchers.hasSize(0))
                .log().all()
    }

    def "should retrieve message by proper id "() {
        when: "send get message by id"
        def message = addNewMessage()
        def response = API.utils.ProductDetailsUtils.request.header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                USER2_EMAIL, USER2_PASSWORD)).get(API.utils.ContactUtils.messages + "/" + addNewMessage().getId() )
        then: "should return empty body "
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
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

    def "should not retrieve message by id test as non logged user"() {
        when: "send get message by id"
        API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
        def newMessage = addNewMessage()
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ContactUtils.messages + "/" + newMessage.getId())
        then: "should return unauthorized  "
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should not set message status as non logged user "() {
        when: "try to put new message"
        API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
        def newMessage = addNewMessage()
        def response = API.utils.ProductDetailsUtils.request.put(API.utils.ContactUtils.messages + "/"
                + newMessage.getId() + API.utils.ContactUtils.status )
        then: "should return unauthorized "
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should set message status as admin "() {
        when: "try to put new message status"
        def newMessage = addNewMessage()
        def statusBody = new MessageIdStatusBody()
        statusBody.setStatus("RESOLVED")
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(statusBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                ADMIN_EMAIL, ADMIN_PASSWORD)).put(API.utils.ContactUtils.messages + "/"
                + newMessage.getId() + API.utils.ContactUtils.status)
        then: "should return ok "
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
    }
}
