package Account

import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import utils.AccountUtils
import utils.ProductDetailsUtils
import utils.RequestUtils
import utils.UserAuthenticatorUtils

import static utils.UserAuthenticatorUtils.ADMIN_EMAIL
import static utils.UserAuthenticatorUtils.ADMIN_PASSWORD
import static utils.UserAuthenticatorUtils.USER1_EMAIL
import static utils.UserAuthenticatorUtils.USER1_PASSWORD

class InvoicesSpec extends Specification {
    def "should not retrieve invoices where user not logged in"() {
        when: "send get to retrieve all invoice products"
        ProductDetailsUtils.request.get(ProductDetailsUtils.logout)
        def response = ProductDetailsUtils.request.get(AccountUtils.invoices)
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .body("message", Matchers.equalTo(ProductDetailsUtils.unauthorized))
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }
    def "should retrieve only related invoices where logged as user"() {
        when: "send get to retrieve all invoice products"
        def response = ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                USER1_EMAIL, USER1_PASSWORD))
                .get(AccountUtils.invoices)
        then: "should return user invoices"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }

    def "should retrieve all invoices where logged as admin"() {
        when: "send get to retrieve all invoice products"
        def response = ProductDetailsUtils.request
                .header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD))
                .get(AccountUtils.invoices)
        then: "should return user invoices"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body(Matchers.not(Matchers.isEmptyOrNullString()))
                .log().all()
    }
}
