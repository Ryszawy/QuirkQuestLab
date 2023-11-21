package API.Products


import com.google.gson.JsonObject
import model.ImageResponse
import model.ProductResponse
import org.apache.http.HttpStatus
import spock.lang.Specification

import static API.utils.UserAuthenticatorUtils.gson

class ProductSpec extends Specification {

    def "should retrieve all products "() {
        when: "send get to retrieve all exist products"
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.products)
        then: "should return products"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        def jsonObjects = (JsonObject) gson.fromJson(jsonBody, JsonObject.class)
        def jsonArray = jsonObjects.get("data").toString()
        def productResponse = (ArrayList<ProductResponse>) gson.fromJson(jsonArray, List.class)
        productResponse.size() > 0
    }

    def "should retrieve all products images" () {
        when: "send get to retrieve all exist products"
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.images)
        then: "should return products"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        def productImageResponse = (ArrayList<ImageResponse>) gson.fromJson(jsonBody, List.class)
        productImageResponse.size() > 0

    }
}
