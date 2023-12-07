package API.test.Account

import API.utils.AccountUtils
import API.utils.ProductDetailsUtils
import API.utils.RequestUtils
import io.qameta.allure.*
import io.qameta.allure.restassured.AllureRestAssured
import io.restassured.RestAssured
import model.FavoriteResponse
import org.apache.http.HttpStatus
import org.hamcrest.Matchers
import spock.lang.Specification

import static API.utils.UserAuthenticatorUtils.*
import static io.restassured.RestAssured.given

@Epic("REST API Tests")
@Story("Verify CRUID Operations on Account module")
class FavouritesSpec extends Specification {

    def setup() {
        RestAssured.baseURI = "https://api.practicesoftwaretesting.com"
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should retrieve favourites")
    def "should retrieve favourites"() {
        when: "send get to retrieve all favourite products"
            def response = given()
                    .filter(new AllureRestAssured())
                    .header(getAuthorizationHeaderForAnyUser(USER1_EMAIL, USER1_PASSWORD))
                    .get(AccountUtils.favourites)
        then: "should return favourites"
            response.then()
                    .statusCode(HttpStatus.SC_OK.intValue())
                    .header(RequestUtils.contentType, RequestUtils.applicationJsonContentTypeWithCharset)
            def jsonBody = response.body().prettyPrint()
            def favouriteResponse = (ArrayList<FavoriteResponse>) gson.fromJson(jsonBody, List.class)
            favouriteResponse.size() > 0
    }

    @Severity(SeverityLevel.MINOR)
    @Description("Test Description: should not retrieve favourites because of no credentials")
    def "should not retrieve favourites where user not logged in "() {
        when: "send get to retrieve all favourite products"
            given().filter(new AllureRestAssured())
                    .get(ProductDetailsUtils.logout)
            def response = given().filter(new AllureRestAssured()).get(AccountUtils.favourites)
        then: "should return 401 Unauthorized"
            response.then()
                    .statusCode(HttpStatus.SC_UNAUTHORIZED.intValue())
                    .body("message", Matchers.equalTo(ProductDetailsUtils.unauthorized))
                    .header(RequestUtils.contentType, RequestUtils.applicationJsonContentType)
    }

}
