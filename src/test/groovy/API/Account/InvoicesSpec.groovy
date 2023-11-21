package API.Account


import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import API.utils.UserAuthenticatorUtils

import static API.utils.UserAuthenticatorUtils.ADMIN_EMAIL
import static API.utils.UserAuthenticatorUtils.ADMIN_PASSWORD
import static API.utils.UserAuthenticatorUtils.USER1_EMAIL
import static API.utils.UserAuthenticatorUtils.USER1_PASSWORD

class InvoicesSpec extends Specification {
    def "should not retrieve invoices where user not logged in"() {
        when: "send get to retrieve all invoice products"
        API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.AccountUtils.invoices)
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.unauthorized))
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }
    def "should retrieve only related invoices where logged as user"() {
        when: "send get to retrieve all invoice products"
        def response = API.utils.ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                USER1_EMAIL, USER1_PASSWORD))
                .get(API.utils.AccountUtils.invoices)
        then: "should return user invoices"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }

    def "should retrieve all invoices where logged as admin"() {
        when: "send get to retrieve all invoice products"
        def response = API.utils.ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD))
                .get(API.utils.AccountUtils.invoices)
        then: "should return user invoices"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }
}
