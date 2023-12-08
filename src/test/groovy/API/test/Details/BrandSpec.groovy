package API.test.Details

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import model.BrandRequest
import model.BrandResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.ProductDetailsUtils.*
import static API.utils.RequestUtils.*
import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given
import static io.restassured.http.ContentType.JSON

@Epic("REST API TESTS")
@Story("Verify CRUID Operations on Details module")
class BrandSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    BrandResponse addNewBrand() {
        def requestBody = new BrandResponse()
        requestBody.setSlug(UUID.randomUUID().toString())
        requestBody.setName(UUID.randomUUID().toString())
        def response = given().filter(new AllureRestAssured())
                .body(gson.toJson(requestBody))
                .contentType(JSON)
                .post(brands)
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(contentType, applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        gson.fromJson(jsonBody, BrandResponse.class)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve all brands ")
    def "should retrieve all brands "() {
        when: "send get to retrieve all exist brands"
            def response = given().filter(new AllureRestAssured()).get(brands)
        then: "should return brands"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def brandResponse = (ArrayList<BrandResponse>) gson.fromJson(jsonBody, List.class)
            brandResponse.size() > 0
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should add new brand")
    def "should add new brand "() {
        when: "send post to add new brand"
            def requestBody = new BrandResponse()
            def name = UUID.randomUUID().toString()
            def slug = UUID.randomUUID().toString()
            requestBody.setSlug(slug)
            requestBody.setName(name)
            def response = given().filter(new AllureRestAssured()).body(gson.toJson(requestBody))
                    .contentType(JSON).post(brands)
        then: "should add new brand"
            response.then()
                    .statusCode(HttpStatus.SC_CREATED.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)

            def jsonBody = response.body().prettyPrint()
            def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
            with(brandResponse) {
                assert getName() == name
                assert getSlug() == slug
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should get brand by existing ID  ")
    def "should get brand by existing ID "() {
        when: "send get to retrieve selected brand"
            def addedBrand = addNewBrand()
            def response = given().filter(new AllureRestAssured()).get(brands + "/" + addedBrand.getId())
        then: "should return brand"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)

            def jsonBody = response.body().prettyPrint()
            def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
            with(brandResponse) {
                assert getId() == addedBrand.getId()
                assert getName() == addedBrand.getName()
                assert getSlug() == addedBrand.getSlug()
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not get brand by non existing ID ")
    def "should not get brand by non existing ID "() {
        when: "send get to retrieve non existing brand"
            def response = given().filter(new AllureRestAssured()).get(brands + "/" + "F")
        then: "should return 404"
            response.then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should update brand with selected wrong ID ")
    def "should update brand with selected wrong ID "() {
        when: "send update for selected brand"
            def response = given().filter(new AllureRestAssured()).put(brands + "/" + "F")
        then: "operation should be ended with false"
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("success", Matchers.equalTo(false))

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should update brand with selected ID ")
    def "should update brand with selected ID"() {
        when: "send update for selected brand"
            def addedBrand = addNewBrand()
            def requestBody = new BrandRequest()
            requestBody.setName(UUID.randomUUID().toString())
            requestBody.setSlug(UUID.randomUUID().toString())


            def response = given().filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(JSON)
                    .put(brands + "/" + addedBrand.getId())
        then: "operation should be ended with true"
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("success", Matchers.equalTo(true))

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should delete brand with selected and existing id while not logged in")
    def "should delete brand with selected and existing id while not logged in "() {
        when: "send delete request"
            given().filter(new AllureRestAssured()).get(logout)
            def addedResponse = addNewBrand()
            def response = given().filter(new AllureRestAssured())
                    .delete(brands + "/" + addedResponse.getId())
        then: "should return 401 Unauthorized"
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body("message", Matchers.equalTo(unauthorized))
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not delete brand with selected and existing id while logged in as normal user ")
    def "should not delete brand with selected and existing id while logged in as normal user "() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD))
                    .delete(brands + "/" + addedResponse.getId())
        then: "should return 403 Forbidden"
            response.then()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("message", Matchers.equalTo(forbidden))
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should delete brand with selected and existing id while logged in as admin ")
    def "should delete brand with selected and existing id while logged in as admin"() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .delete(brands + "/" + addedResponse.getId())
        then: "should delete brand"
            response.then()
                    .statusCode(HttpStatus.SC_NO_CONTENT)

    }

}