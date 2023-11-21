package API.Details


import model.BrandRequest
import model.BrandResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import API.utils.UserAuthenticatorUtils

import static API.utils.UserAuthenticatorUtils.ADMIN_EMAIL
import static API.utils.UserAuthenticatorUtils.ADMIN_PASSWORD
import static API.utils.UserAuthenticatorUtils.USER1_EMAIL
import static API.utils.UserAuthenticatorUtils.USER1_PASSWORD
import static API.utils.UserAuthenticatorUtils.gson

class BrandSpec extends Specification {

    BrandResponse addNewBrand() {
        def requestBody = new BrandResponse()
        requestBody.setSlug(UUID.randomUUID().toString())
        requestBody.setName(UUID.randomUUID().toString())
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.post(API.utils.ProductDetailsUtils.brands)
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        gson.fromJson(jsonBody, BrandResponse.class)
    }

    def "should retrieve all brands "() {
        when: "send get to retrieve all exist brands"
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.brands)
        then: "should return brands"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = (ArrayList<BrandResponse>) gson.fromJson(jsonBody, List.class)
        brandResponse.size() > 0
    }

    def "should add new brand "() {
        when: "send post to add new brand"
        def requestBody = new BrandResponse()
        def name = UUID.randomUUID().toString()
        def slug = UUID.randomUUID().toString()
        requestBody.setSlug(slug)
        requestBody.setName(name)
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.post(API.utils.ProductDetailsUtils.brands)
        then: "should add new brand"
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
        with(brandResponse) {
            assert getName() == name
            assert getSlug() == slug
        }
    }

    def "should get brand by existing ID "() {
        when: "send get to retrieve selected brand"
        def addedBrand = addNewBrand()
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.brands + "/"
                + addedBrand.getId())
        then: "should return brand"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def brandResponse = gson.fromJson(jsonBody, BrandResponse.class)
        with(brandResponse) {
            assert getId() == addedBrand.getId()
            assert getName() == addedBrand.getName()
            assert getSlug() == addedBrand.getSlug()
        }
    }

    def "should not get brand by non existing ID "() {
        when: "send get to retrieve non existing brand"
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.brands + "/"
                + "F")
        then: "should return 404"
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should update brand with selected wrong ID "() {
        when: "send update for selected brand"
        def response = API.utils.ProductDetailsUtils.request.put(API.utils.ProductDetailsUtils.brands + "/"
                + "F")
        then: "operation should be ended with false"
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(false))
                .log().all()
    }

    def "should update brand with selected ID"() {
        when: "send update for selected brand"
        def addedBrand = addNewBrand()
        def requestBody = new BrandRequest()
        requestBody.setName(UUID.randomUUID().toString())
        requestBody.setSlug(UUID.randomUUID().toString())
        API.utils.ProductDetailsUtils.request
                .body(gson.toJson(requestBody))
                .contentType(io.restassured.http.ContentType.JSON)
                .log().all()
        def response = API.utils.ProductDetailsUtils.request.
                put(API.utils.ProductDetailsUtils.brands + "/"
                        + addedBrand.getId())
        then: "operation should be ended with true"
            response.then()
                .statusCode(HttpStatus.SC_OK)
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body("success", Matchers.equalTo(true))
                .log().all()
    }


    def "should delete brand with selected and existing id while not logged in "() {
        when: "send delete request"
            API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
            def addedResponse = addNewBrand()
            def response = API.utils.ProductDetailsUtils.request.delete(API.utils.ProductDetailsUtils.brands + "/"
                + addedResponse.getId())
        then: "should return 401 Unauthorized"
            response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                    .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.unauthorized))
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

    def "should not delete brand with selected and existing id while logged in as normal user "() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = API.utils.ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD)).
                delete(API.utils.ProductDetailsUtils.brands + "/"
                        + addedResponse.getId())
        then: "should return 403 Forbidden"
            response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                 .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.forbidden))
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }


    def "should delete brand with selected and existing id while logged in as admin"() {
        when: "send delete request"
            def addedResponse = addNewBrand()
            def response = API.utils.ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        ADMIN_EMAIL, ADMIN_PASSWORD)).delete(API.utils.ProductDetailsUtils.brands + "/"
                + addedResponse.getId())
        then: "should delete brand"
            response.then()
                .statusCode(HttpStatus.SC_NO_CONTENT)
                .log().all()
    }

}