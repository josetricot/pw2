package dev.rpmhub;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;

@QuarkusTest
class CatalogWSTest {

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
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
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
    void testUpdateBook() {
        int id = createBook("Livro antigo", "Autor");

        given()
            .contentType(ContentType.JSON)
            .body("{\"id\":999,\"title\":\"Livro atualizado\","
                + "\"author\":\"Novo autor\",\"loaned\":true}")
        .when()
            .put("/books/" + id)
        .then()
            .statusCode(200)
            .body("id", is(id))
            .body("title", is("Livro atualizado"))
            .body("author", is("Novo autor"))
            .body("loaned", is(false));
    }

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
    void testUpdateNonExistentBookReturnsNotFound() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"title\":\"Livro\",\"author\":\"Autor\"}")
        .when()
            .put("/books/999999")
        .then()
            .statusCode(404);
    }

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
    void testDeleteBook() {
        int id = createBook("Livro para excluir", "Autor");

        given()
            .when().delete("/books/" + id)
            .then().statusCode(204);

        given()
            .when().get("/books/" + id)
            .then().statusCode(404);
    }

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
    void testDeleteNonExistentBookReturnsNotFound() {
        given()
            .when().delete("/books/999999")
            .then().statusCode(404);
    }

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
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
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
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

    @Test
    @TestSecurity(user = "catalog-test-user", roles = {"User", "Admin"})
    void testUpdateBookPreservesLoanedStatus() {
        int id = createBook("Livro emprestado", "Autor");
        given().when().put("/books/" + id + "/loan").then().statusCode(200);

        given()
            .contentType(ContentType.JSON)
            .body("{\"title\":\"Titulo alterado\",\"author\":\"Autor alterado\"}")
        .when()
            .put("/books/" + id)
        .then()
            .statusCode(200)
            .body("loaned", is(true));
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
