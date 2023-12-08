package API.test.Account

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.AccountUtils.invoices
import static API.utils.ProductDetailsUtils.logout
import static API.utils.ProductDetailsUtils.unauthorized
import static API.utils.RequestUtils.*
import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given

@Epic("REST API TESTS")
@Story("Verify CRUID Operations on Account module")
class InvoicesSpec extends Specification {
    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve only related invoices where logged as user")
    def "should not retrieve invoices where user not logged in"() {
        when: "send get to retrieve all invoice products"
            given().filter(new AllureRestAssured()).get(logout)
            def response = given().filter(new AllureRestAssured()).get(invoices)
        then: "should return 401 Unauthorized"
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                    .body("message", Matchers.equalTo(unauthorized))
                    .header(contentType, applicationJsonContentType)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve only related invoices where logged as user")
    def "should retrieve only related invoices where logged as user"() {
        when: "send get to retrieve all invoice products"
            def response = given()
                    .filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD))
                    .get(invoices)
        then: "should return user invoices"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body(Matchers.not(Matchers.isEmptyOrNullString()))
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve all invoices where logged as admin")
    def "should retrieve all invoices where logged as admin"() {
        when: "send get to retrieve all invoice products"
            def response = given()
                    .filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .get(invoices)
        then: "should return user invoices"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body(Matchers.not(Matchers.isEmptyOrNullString()))
    }
}
