package Details

import model.CategoryRequest
import model.CategoryResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import utils.ProductDetailsUtils
import utils.RequestUtils
import utils.UserAuthenticatorUtils

import static utils.UserAuthenticatorUtils.ADMIN_EMAIL
import static utils.UserAuthenticatorUtils.ADMIN_PASSWORD
import static utils.UserAuthenticatorUtils.USER1_EMAIL
import static utils.UserAuthenticatorUtils.USER1_PASSWORD
import static utils.UserAuthenticatorUtils.gson

class CategoriesSpec extends Specification {
    CategoryResponse addNewCategory() {
        def requestBody = new CategoryResponse()
        requestBody.setSlug(UUID.randomUUID().toString())
        requestBody.setName(UUID.randomUUID().toString())
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.categories)
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        gson.fromJson(jsonBody, CategoryResponse.class)
    }

    def "should retrieve all categories "() {
        when: "send get to retrieve all exist categories"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.categories)
        then: "should return categories"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def categoryResponse = (ArrayList<CategoryResponse>) gson.fromJson(jsonBody, List.class)
        categoryResponse.size() > 0
    }

    def "should add new Category "() {
        when: "send post to add new Category"
        def requestBody = new CategoryResponse()
        def name = UUID.randomUUID().toString()
        def slug = UUID.randomUUID().toString()
        requestBody.setSlug(slug)
        requestBody.setName(name)
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.categories)
        then: "should add new Category"
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def CategoryResponse = gson.fromJson(jsonBody, CategoryResponse.class)
        with(CategoryResponse) {
            assert getName() == name
            assert getSlug() == slug
        }
    }

    def "should get Category by existing ID "() {
        when: "send get to retrieve selected Category"
        def addedCategory = addNewCategory()
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.categories + "/"
                + addedCategory.getId())
        then: "should return Category"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def CategoryResponse = gson.fromJson(jsonBody, CategoryResponse.class)
        with(CategoryResponse) {
            assert getId() == addedCategory.getId()
            assert getName() == addedCategory.getName()
            assert getSlug() == addedCategory.getSlug()
        }
    }

    def "should not get Category by non existing ID test"() {
        when: "send get to retrieve non existing Category"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.categories + "/"
                + "F")
        then: "should return 404"
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should not update Category with selected wrong ID"() {
        when: "send update for selected Category"
        def response = ProductDetailsUtils.request.put(ProductDetailsUtils.categories + "/"
                + "F")
        then: "operation should be ended with false"
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(false))
                .log().all()
    }

    def "should update Category with selected ID"() {
        when: "send update for selected Category"
        def addedCategory = addNewCategory()
        def requestBody = new CategoryRequest()
        requestBody.setName(UUID.randomUUID().toString())
        requestBody.setSlug(UUID.randomUUID().toString())
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.
                put(ProductDetailsUtils.categories + "/"
                        + addedCategory.getId())
        then: "operation should be ended with true"
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(true))
                .log().all()
    }


    def "should not delete category with selected and existing id while not logged in "() {
        when: "send delete request"
        ProductDetailsUtils.request.get(ProductDetailsUtils.logout)
        def addedResponse = addNewCategory()
        def response = ProductDetailsUtils.request.delete(ProductDetailsUtils.categories + "/"
                + addedResponse.getId())
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo(ProductDetailsUtils.unauthorized))
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }


    def "should not delete category with selected and existing id while logged in as normal user "() {
        when: "send delete request"
        def addedResponse = addNewCategory()
        def response = ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD)).
                delete(ProductDetailsUtils.categories + "/"
                        + addedResponse.getId())
        then: "should return 403 Forbidden"
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", Matchers.equalTo(ProductDetailsUtils.forbidden))
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }


    def "should delete Category with selected and existing id while logged in as admin"() {
        when: "send delete request"
        def addedResponse = addNewCategory()
        def response = ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD)).delete(ProductDetailsUtils.categories + "/"
                + addedResponse.getId())
        then: "should delete Category"
        response.then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .log().all()
    }
}
