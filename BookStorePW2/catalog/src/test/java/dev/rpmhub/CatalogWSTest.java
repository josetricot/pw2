package dev.rpmhub;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class CatalogWSTest {

    @Test
    void testAddBook() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"title\":\"Dom Casmurro\",\"author\":\"Machado de Assis\"}")
        .when()
            .post("/books")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", is("Dom Casmurro"))
            .body("author", is("Machado de Assis"))
            .body("loaned", is(false));
    }

    @Test
    void testGetNonExistentBookReturnsNotFound() {
        given()
            .when().get("/books/999999")
            .then().statusCode(404);
    }

    @Test
    void testListBooksIncludesCreatedBook() {
        int id = createBook("O Cortico", "Aluisio Azevedo");

        given()
            .when().get("/books")
            .then()
                .statusCode(200)
                .body("id", hasItem(id));
    }

    @Test
    void testListBooksIsPublic() {
        given()
            .when().get("/books")
            .then().statusCode(200);
    }

    @Test
    void testLoanAndReturnFlow() {
        int id = createBook("Grande Sertao Veredas", "Guimaraes Rosa");

        given()
            .when().put("/books/" + id + "/loan")
            .then().statusCode(200).body("loaned", is(true));

        given()
            .when().put("/books/" + id + "/loan")
            .then().statusCode(409);

        given()
            .when().put("/books/" + id + "/return")
            .then().statusCode(200).body("loaned", is(false));
    }

    @Test
    void testLoanNonExistentBookReturnsNotFound() {
        given()
            .when().put("/books/999999/loan")
            .then().statusCode(404);
    }

    @Test
    void testReturnNonExistentBookReturnsNotFound() {
        given()
            .when().put("/books/999999/return")
            .then().statusCode(404);
    }

    private int createBook(String title, String author) {
        return given()
            .contentType(ContentType.JSON)
            .body("{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}")
        .when()
            .post("/books")
        .then()
            .statusCode(201)
            .extract().path("id");
    }
}
