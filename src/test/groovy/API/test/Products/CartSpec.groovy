package API.test.Products

import io.qameta.allure.Epic
import io.qameta.allure.Story
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.ProductDetailsUtils.*
import static API.utils.RequestUtils.*
import static io.restassured.RestAssured.given

@Epic("REST API TESTS")
@Story("Verify CRUID Operations on Products module")
class CartSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    def "should create cart "() {
        when: "send request to create cart"
            def response = given().filter(new AllureRestAssured())
                    .post(cart)
        then: "should return brands"
            response.then()
                    .statusCode(HttpStatus.SC_CREATED.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("id", Matchers.not(Matchers.isEmptyString()))

    }

    def "should not remove cart because of wrong id"() {
        when: "send request to remove cart"
            def response = given().filter(new AllureRestAssured()).delete(cart + "F")
        then: "should remove cart"
            response.then()
                    .statusCode(HttpStatus.SC_NOT_FOUND.intValue())
                    .header(contentType, applicationJsonContentType)
                    .body("message", Matchers.equalTo(rfs))

    }
}
