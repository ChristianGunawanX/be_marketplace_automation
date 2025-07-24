package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class ApiManager {
    private static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .build();
    }

    public static Response get(String path) {
        return given()
                .spec(getRequestSpec())
                .when()
                .get(path);
    }

    public static Response post(String path, Object body) {
        return given()
                .spec(getRequestSpec())
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(path);
    }
}
