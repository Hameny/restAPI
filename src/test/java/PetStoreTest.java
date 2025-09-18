import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

public class PetStoreTest {

  @Test
  public void checkCreatePetWithoutBody() {
    given()
        .header("Content-Type", "application/json")
        .body("")
        .when()
        .post("https://petstore.swagger.io/v2/pet")
        .then()
        .statusCode(405) // Method Not Allowed или 400 Bad Request
        .log().all();
  }

  @Test
  public void checkCreatePet() {
    String petJson = """
            {
              "id": 12345,
              "category": {
                "id": 1,
                "name": "Cat"
              },
              "name": "Leo",
              "photoUrls": [
                "https://example.com/leo.jpg"
              ],
              "tags": [
                {
                  "id": 1,
                  "name": "British"
                }
              ],
              "status": "available"
            }
            """;
    String petJson2 = """
            {
              "id": 12345,
              "category": {
                "id": 1,
                "name": "Cat"
              },
              "name": "LeoPard",
              "photoUrls": [
                "https://example.com/leo.jpg"
              ],
              "tags": [
                {
                  "id": 1,
                  "name": "British"
                }
              ],
              "status": "available"
            }
            """;

    int id = given()
        .contentType(ContentType.JSON)
        .body(petJson)
        .when()
        .post("https://petstore.swagger.io/v2/pet")
        .then()
        .statusCode(200) // Успешное создание
        .body("name", equalTo("Leo"))
        .body("category.name", equalTo("Cat"))
        .body("status", equalTo("available"))
        .body("photoUrls",hasItem("https://example.com/leo.jpg"))
        .log().all()
        .extract().path("id");

    when()
        .get("https://petstore.swagger.io/v2/pet/" + id)
        .then()
        .statusCode(200)
        .log().all()
        .body("name", equalTo("Leo"))
        .body("category.name", equalTo("Cat"))
        .body("status", equalTo("available"))
        .body("photoUrls",hasItem("https://example.com/leo.jpg"));

    given()
        .contentType(ContentType.JSON)
        .body(petJson2)
        .when()
        .put("https://petstore.swagger.io/v2/pet")
        .then()
        .statusCode(200)
        .log().all()
        .body("name", equalTo("LeoPard"))
        .body("category.name", equalTo("Cat"))
        .body("status", equalTo("available"))
        .body("photoUrls",hasItem("https://example.com/leo.jpg"));

    when()
        .delete("https://petstore.swagger.io/v2/pet/" + id)
        .then()
        .log().all()
        .statusCode(200);
  }
}
