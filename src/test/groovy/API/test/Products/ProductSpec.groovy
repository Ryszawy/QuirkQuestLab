package API.test.Products

import com.google.gson.JsonObject
import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import model.ImageResponse
import model.ProductResponse
import org.apache.http.HttpStatus
import spock.lang.Specification

import static API.utils.ProductDetailsUtils.*
import static API.utils.RequestUtils.applicationJsonContentTypeWithCharset
import static API.utils.RequestUtils.contentType
import static API.utils.UserAuthenticatorUtils.gson
import static io.restassured.RestAssured.given

@Epic("REST API Tests")
@Story("Verify CRUID Operations on Products module")
class ProductSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should retrieve all products ")
    def "should retrieve all products "() {
        when: "send get to retrieve all exist products"
            def response = given().filter(new AllureRestAssured()).get(products)
        then: "should return products"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def jsonObjects = (JsonObject) gson.fromJson(jsonBody, JsonObject.class)
            def jsonArray = jsonObjects.get("data").toString()
            def productResponse = (ArrayList<ProductResponse>) gson.fromJson(jsonArray, List.class)
            productResponse.size() > 0
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should retrieve all products images")
    def "should retrieve all products images"() {
        when: "send get to retrieve all exist products"
            def response = given().filter(new AllureRestAssured()).get(images)
        then: "should return products"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def productImageResponse = (ArrayList<ImageResponse>) gson.fromJson(jsonBody, List.class)
            productImageResponse.size() > 0

    }
}
