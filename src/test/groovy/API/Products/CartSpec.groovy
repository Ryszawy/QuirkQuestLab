package API.Products


import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

class CartSpec extends Specification {



    def "should create cart "() {
        when: "send request to create cart"
        def response = API.utils.ProductDetailsUtils.request.post(API.utils.ProductDetailsUtils.cart)
        then: "should return brands"
        response.then()
                .statusCode(HttpStatus.SC_CREATED.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .body("id", Matchers.not(Matchers.isEmptyString()))
                .log().all()
    }

    def "should not remove cart because of wrong id" () {
        when: "send request to remove cart"
        def response = API.utils.ProductDetailsUtils.request.delete(API.utils.ProductDetailsUtils.cart + "F")
        then: "should remove cart"
        response.then()
                .statusCode(HttpStatus.SC_NOT_FOUND.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.rfs))
                .log().all()
    }
}
