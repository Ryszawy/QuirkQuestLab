package Details

import model.BrandRequest
import model.BrandResponse
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

class BrandSpec extends Specification {

    BrandResponse addNewBrand() {
        def requestBody = new BrandResponse()
        requestBody.setSlug(UUID.randomUUID().toString())
        requestBody.setName(UUID.randomUUID().toString())
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.brands)
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        gson.fromJson(jsonBody, BrandResponse.class)
    }

    def "retrieve all brands test"() {
        when: "send get to retrieve all exist brands"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.brands)
        then: "should return brands"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = (ArrayList<BrandResponse>) gson.fromJson(jsonBody, List.class)
        brandResponse.size() > 0
    }

    def "add new brand test"() {
        when: "send post to add new brand"
        def requestBody = new BrandResponse()
        def name = UUID.randomUUID().toString()
        def slug = UUID.randomUUID().toString()
        requestBody.setSlug(slug)
        requestBody.setName(name)
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.brands)
        then: "should add new brand"
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
        with(brandResponse) {
            assert getName() == name
            assert getSlug() == slug
        }
    }

    def "get brand by existing ID test"() {
        when: "send get to retrieve selected brand"
        def addedBrand = addNewBrand()
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.brands + "/"
                + addedBrand.getId())
        then: "should return brand"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
        with(brandResponse) {
            assert getId() == addedBrand.getId()
            assert getName() == addedBrand.getName()
            assert getSlug() == addedBrand.getSlug()
        }
    }

    def "get brand by non existing ID test"() {
        when: "send get to retrieve non existing brand"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.brands + "/"
                + "F")
        then: "should return 404"
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "update brand with selected wrong ID test"() {
        when: "send update for selected brand"
        def response = ProductDetailsUtils.request.put(ProductDetailsUtils.brands + "/"
                + "F")
        then: "operation should be ended with false"
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(false))
                .log().all()
    }

    def "update brand with selected ID test"() {
        when: "send update for selected brand"
        def addedBrand = addNewBrand()
        def requestBody = new BrandRequest()
        requestBody.setName(UUID.randomUUID().toString())
        requestBody.setSlug(UUID.randomUUID().toString())
        ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = ProductDetailsUtils.request.
                put(ProductDetailsUtils.brands + "/"
                        + addedBrand.getId())
        then: "operation should be ended with true"
            response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(true))
                .log().all()
    }


    def "delete brand with selected and existing id while not logged in test "() {
        when: "send delete request"
            ProductDetailsUtils.request.get(ProductDetailsUtils.logout)
            def addedResponse = addNewBrand()
            def response = ProductDetailsUtils.request.delete(ProductDetailsUtils.brands + "/"
                + addedResponse.getId())
        then: "should return 401 Unauthorized"
            response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "delete brand with selected and existing id while logged in as normal user test "() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD)).
                delete(ProductDetailsUtils.brands + "/"
                        + addedResponse.getId())
        then: "should return 403 Forbidden"
            response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }


    def "delete brand with selected and existing id while logged in as admin test"() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD)).delete(ProductDetailsUtils.brands + "/"
                + addedResponse.getId())
        then: "should delete brand"
            response.then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .log().all()
    }

}