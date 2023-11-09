import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import spock.lang.Shared
import spock.lang.Specification

import static org.hamcrest.Matchers.is

class RestAssuredSpec extends Specification {
    @Shared
    def requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://jsonplaceholder.typicode.com")
            .build()

    def "validate json response"() {
        given: "set up request"
            def request = RestAssured.given(requestSpec)

        when: "get todos"
            def response = request.get("/users/1")

        then: "should 200 okay, response matching expected"
            response.then()
                    .log().all()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("name", is("Leanne Graham"))
                    .body("company.name", is("Romaguera-Crona"))
    }
}
