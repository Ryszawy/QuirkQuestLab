package API.Account


import model.FavoriteResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification
import API.utils.UserAuthenticatorUtils

import static API.utils.UserAuthenticatorUtils.USER1_EMAIL
import static API.utils.UserAuthenticatorUtils.USER1_PASSWORD
import static API.utils.UserAuthenticatorUtils.gson

class FavouritesSpec extends Specification {
    def "should retrieve favourites"() {
        when: "send get to retrieve all favourite products"
        def response = API.utils.ProductDetailsUtils.request.
                header(UserAuthenticatorUtils.getAuthorizationHeaderForAnyUser(
                        USER1_EMAIL, USER1_PASSWORD))
                .get(API.utils.AccountUtils.favourites)
        then: "should return favourites"
        response.then()
                .statusCode(HttpStatus.SC_OK.intValue())
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentTypeWithCharset)
                .log().all()
        def jsonBody = response.body().prettyPrint()
        def favouriteResponse = (ArrayList<FavoriteResponse>) gson.fromJson(jsonBody, List.class)
        favouriteResponse.size() > 0
    }
    def "should not retrieve favourites where user not logged in "() {
        when: "send get to retrieve all favourite products"
        API.utils.ProductDetailsUtils.request.get(API.utils.ProductDetailsUtils.logout)
        def response = API.utils.ProductDetailsUtils.request.get(API.utils.AccountUtils.favourites)
        then: "should return 401 Unauthorized"
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                .body("message", Matchers.equalTo(API.utils.ProductDetailsUtils.unauthorized))
                .header(API.utils.RequestUtils.contentType, API.utils.RequestUtils.applicationJsonContentType)
                .log().all()
    }

}
