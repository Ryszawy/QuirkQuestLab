package API.test.Report

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.http.ContentType
import spock.lang.Specification

import static API.utils.ReportUtils.requestSpec
import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given

@Epic("REST API Tests")
@Story("Verify CRUID Operations on Report module")
class ReportSpec extends Specification {

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about total sales per country")
    def "should return report about total sales per country"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/total-sales-per-country")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about top 10 purchased products")
    def "should return report about top 10 purchased products"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/top10-purchased-products")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about top 10 best selling categories")
    def "should return report about top 10 best selling categories"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/top10-best-selling-categories")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about total sales of years")
    def "should return report about total sales of years"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/total-sales-of-years")

        then: "should 200 okay"
            response.then()

                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about average sales per month")
    def "should return report about average sales per month"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/average-sales-per-month")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about average sales per week")
    def "should return report about average sales per week"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/average-sales-per-week")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should return report about customers by country")
    def "should return report about customers by country"() {
        given: "set up request"
            def request = given(requestSpec)
                    .contentType(ContentType.JSON)
                    .queryParam("country", "Austria")
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .filter(new AllureRestAssured())
        when: "get report"
            def response = request.get("/customers-by-country")

        then: "should 200 okay"
            response.then()
                    .statusCode(200)
    }
}
