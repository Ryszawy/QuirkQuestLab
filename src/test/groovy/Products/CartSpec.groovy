package Products

import com.google.gson.JsonObject
import model.BrandResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import utils.ProductDetailsUtils
import utils.RequestUtils

import static utils.UserAuthenticatorUtils.gson

class CartSpec extends Specification {



    def "should create cart "() {
        when: "send request to create cart"
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.cart)
        then: "should return brands"
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .body("id", Matchers.not(Matchers.isEmptyString()))
                .log().all()
    }

    def "should not remove cart because of wrong id" () {
        when: "send request to remove cart"
        def response = ProductDetailsUtils.request.post(ProductDetailsUtils.cart + "F")
        then: "should remove cart"
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .body("message", Matchers.equalTo(ProductDetailsUtils.rfs))
                .log().all()
    }
}
