package API.Report


import io.restassured.http.ContentType
import spock.lang.Specification

import static io.restassured.RestAssured.given
import static API.utils.UserAuthenticatorUtils.*

class ReportSpec extends Specification {
    def "should return report about total sales per country"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/total-sales-per-country")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about top 10 purchased products"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/top10-purchased-products")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about top 10 best selling categories"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/top10-best-selling-categories")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about total sales of years"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/total-sales-of-years")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about average sales per month"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/average-sales-per-month")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about average sales per week"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/average-sales-per-week")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }

    def "should return report about customers by country"() {
        given: "set up request"
            def request = given(API.utils.ReportUtils.requestSpec)
                    .contentType(ContentType.JSON)
                    .queryParam("country", "Austria")
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .log().all()
        when: "get report"
            def response = request.get("/customers-by-country")

        then: "should 200 okay"
            response.then()
                    .log().all()
                    .statusCode(200)
    }
}
