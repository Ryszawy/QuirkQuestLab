package API.test.Details

import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import model.CategoryRequest
import model.CategoryResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.ProductDetailsUtils.*
import static API.utils.RequestUtils.*
import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given
import static io.restassured.http.ContentType.JSON

@Epic("REST API Tests")
@Story("Verify CRUID Operations on Details module")
class CategoriesSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    @Step("Add new Category")
    CategoryResponse addNewCategory() {
        def requestBody = new CategoryResponse()
        requestBody.setSlug(UUID.randomUUID().toString())
        requestBody.setName(UUID.randomUUID().toString())
        def response = given().filter(new AllureRestAssured())
                .body(gson.toJson(requestBody))
                .contentType(JSON).post(categories)
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(contentType, applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        gson.fromJson(jsonBody, CategoryResponse.class)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should retrieve all categories ")
    def "should retrieve all categories "() {
        when: "send get to retrieve all exist categories"
            def response = given().filter(new AllureRestAssured()).get(categories)
        then: "should return categories"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)

            def jsonBody = response.body().prettyPrint()
            def categoryResponse = (ArrayList<CategoryResponse>) gson.fromJson(jsonBody, List.class)
            categoryResponse.size() > 0
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should add new Category ")
    def "should add new Category "() {
        when: "send post to add new Category"
            def requestBody = new CategoryResponse()
            def name = UUID.randomUUID().toString()
            def slug = UUID.randomUUID().toString()
            requestBody.setSlug(slug)
            requestBody.setName(name)
            def response = given().filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(JSON).post(categories)
        then: "should add new Category"
            response.then()
                    .statusCode(HttpStatus.SC_CREATED.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)

            def jsonBody = response.body().prettyPrint()
            def CategoryResponse = gson.fromJson(jsonBody, CategoryResponse.class)
            with(CategoryResponse) {
                assert getName() == name
                assert getSlug() == slug
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should get Category by existing ID  ")
    def "should get Category by existing ID "() {
        when: "send get to retrieve selected Category"
            def addedCategory = addNewCategory()
            def response = given().filter(new AllureRestAssured()).get(categories + "/" + addedCategory.getId())
        then: "should return Category"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(contentType, applicationJsonContentTypeWithCharset)

            def jsonBody = response.body().prettyPrint()
            def CategoryResponse = gson.fromJson(jsonBody, CategoryResponse.class)
            with(CategoryResponse) {
                assert getId() == addedCategory.getId()
                assert getName() == addedCategory.getName()
                assert getSlug() == addedCategory.getSlug()
            }
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not get Category by non existing ID test ")
    def "should not get Category by non existing ID test"() {
        when: "send get to retrieve non existing Category"
            def response = given().filter(new AllureRestAssured()).get(categories + "/" + "F")
        then: "should return 404"
            response.then()
                    .statusCode(HttpStatus.SC_NOT_FOUND)
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not update Category with selected wrong ID ")
    def "should not update Category with selected wrong ID"() {
        when: "send update for selected Category"
            def response = given().filter(new AllureRestAssured()).put(categories + "/" + "F")
        then: "operation should be ended with false"
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("success", Matchers.equalTo(false))

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should update Category with selected ID ")
    def "should update Category with selected ID"() {
        when: "send update for selected Category"
            def addedCategory = addNewCategory()
            def requestBody = new CategoryRequest()
            requestBody.setName(UUID.randomUUID().toString())
            requestBody.setSlug(UUID.randomUUID().toString())

            def response = given().filter(new AllureRestAssured())
                    .body(gson.toJson(requestBody))
                    .contentType(JSON)
                    .put(categories + "/" + addedCategory.getId())
        then: "operation should be ended with true"
            response.then()
                    .statusCode(HttpStatus.SC_OK)
                    .header(contentType, applicationJsonContentTypeWithCharset)
                    .body("success", Matchers.equalTo(true))

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not delete category with selected and existing id while not logged in  ")
    def "should not delete category with selected and existing id while not logged in "() {
        when: "send delete request"
            given().filter(new AllureRestAssured()).get(logout)
            def addedResponse = addNewCategory()
            def response = given().filter(new AllureRestAssured()).delete(categories + "/" + addedResponse.getId())
        then: "should return 401 Unauthorized"
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body("message", Matchers.equalTo(unauthorized))
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should not delete category with selected and existing id while logged in as normal user")
    def "should not delete category with selected and existing id while logged in as normal user "() {
        when: "send delete request"
            def addedResponse = addNewCategory()
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD))
                    .delete(categories + "/" + addedResponse.getId())
        then: "should return 403 Forbidden"
            response.then()
                    .statusCode(HttpStatus.SC_FORBIDDEN)
                    .body("message", Matchers.equalTo(forbidden))
                    .header(contentType, applicationJsonContentType)

    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should delete Category with selected and existing id while logged in as admin")
    def "should delete Category with selected and existing id while logged in as admin"() {
        when: "send delete request"
            def addedResponse = addNewCategory()
            def response = given().filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(ADMIN_EMAIL, ADMIN_PASSWORD))
                    .delete(categories + "/" + addedResponse.getId())
        then: "should delete Category"
            response.then()
                    .statusCode(HttpStatus.SC_NO_CONTENT)

    }
}
