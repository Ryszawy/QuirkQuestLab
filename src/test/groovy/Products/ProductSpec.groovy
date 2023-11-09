package Products

import com.google.gson.JsonObject
import model.ImageResponse
import model.ProductResponse
import org.apache.http.HttpStatus
import spock.lang.Specification
import utils.ProductDetailsUtils
import utils.RequestUtils

import static utils.UserAuthenticatorUtils.gson

class ProductSpec extends Specification {

    def "retrieve all products test"() {
        when: "send get to retrieve all exist products"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.products)
        then: "should return products"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        def jsonObjects = (JsonObject) gson.fromJson(jsonBody, JsonObject.class)
        def jsonArray = jsonObjects.get("data").toString()
        def productResponse = (ArrayList<ProductResponse>) gson.fromJson(jsonArray, List.class)
        productResponse.size() > 0
    }

    def "retrieve all products images test" () {
        when: "send get to retrieve all exist products"
        def response = ProductDetailsUtils.request.get(ProductDetailsUtils.images)
        then: "should return products"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
        def jsonBody = response.body().prettyPrint()
        def productImageResponse = (ArrayList<ImageResponse>) gson.fromJson(jsonBody, List.class)
        productImageResponse.size() > 0

    }
}
