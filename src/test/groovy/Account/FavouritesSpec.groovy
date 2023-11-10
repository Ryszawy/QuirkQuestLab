package Account

import model.BrandResponse
import model.FavoriteResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import utils.AccountUtils
import utils.ProductDetailsUtils
import utils.RequestUtils
import utils.UserAuthenticatorUtils

import static utils.UserAuthenticatorUtils.USER1_EMAIL
import static utils.UserAuthenticatorUtils.USER1_PASSWORD
import static utils.UserAuthenticatorUtils.gson

class FavouritesSpec extends Specification {
    def "retrieve favourites test"() {
        when: "send get to retrieve all favourite products"
        def response = ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD))
                .get(AccountUtils.favourites)
        then: "should return favourites"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def favouriteResponse = (ArrayList<FavoriteResponse>) gson.fromJson(jsonBody, List.class)
        favouriteResponse.size() > 0
    }
    def "retrieve favourites where not logged in test"() {
        when: "send get to retrieve all favourite products"
        ProductDetailsUtils.request.get(ProductDetailsUtils.logout)
        def response = ProductDetailsUtils.request.get(AccountUtils.favourites)
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .body("message", Matchers.equalTo(ProductDetailsUtils.unauthorized))
                .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
                .log().all()
    }

}
