/**
 * PW2 by Rodrigo Prestes Machado
 *
 * PW2 is licensed under a
 * Creative Commons Attribution 4.0 International License.
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
*/
package dev.rpmhub.web;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import dev.rpmhub.model.Book;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Represents a web service for managing a catalog of books.
 */
@Path("/books")
public class CatalogWS {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Adds a new book to the catalog.
     *
     * @param book the book to add (id and loaned are ignored/overridden)
     * @return the created book, status 201
     */
    @POST //aqui usamos POST porque estamos criando um novo recurso (o livro) no catálogo. O verbo POST é apropriado para operações de criação, onde o servidor gera um novo identificador para o recurso criado.
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    public Response addBook(Book book) {
        Book created = new Book(nextId.getAndIncrement(), book.title(), book.author(), false);
        books.put(created.id(), created);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Retrieves the catalog of books.
     *
     * @return the list of books, status 200
     */
    @GET //aqui usamos GET porque estamos apenas recuperando informações sobre o recurso (os livros) sem modificar seu estado. O verbo GET é apropriado para operações de leitura, onde não há efeitos colaterais no servidor.
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> listBooks() {
        return List.copyOf(books.values());
    }

    /**
     * Retrieves a specific book.
     *
     * @param id the book identifier
     * @return the book, status 200, or 404 if it does not exist
     */
    @GET //aqui usamos GET porque estamos apenas recuperando informações sobre o recurso (o livro) sem modificar seu estado. O verbo GET é apropriado para operações de leitura, onde não há efeitos colaterais no servidor.
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(book).build();
    }

    /**
     * Updates a book's title and author.
     *
     * @param id the book identifier
     * @param book the updated book data (id and loaned are ignored)
     * @return the updated book, status 200, or 404 if it does not exist
     */
    @PUT //aqui usamos PUT porque estamos atualizando o recurso (o livro) existente. O verbo PUT é apropriado para operações idempotentes, onde a mesma solicitação pode ser feita várias vezes sem alterar o resultado além da primeira aplicação.
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    public Response updateBook(@PathParam("id") Long id, Book book) {
        Book existing = books.get(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Book updated = new Book(id, book.title(), book.author(), existing.loaned());
        books.put(id, updated);
        return Response.ok(updated).build();
    }

    /**
     * Removes a book from the catalog.
     *
     * @param id the book identifier
     * @return status 204, or 404 if the book does not exist
     */
    @DELETE //aqui usamos DELETE porque estamos removendo o recurso (o livro) do catálogo. O verbo DELETE é apropriado para operações que resultam na exclusão de um recurso.
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"})
    public Response deleteBook(@PathParam("id") Long id) {
        if (books.remove(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    /**
     * Marks a book as loaned.
     *
     * @param id the book identifier
     * @return the updated book, status 200, 404 if it does not exist, or 409
     *         if it is already loaned
     */
    @PUT //aqui usa-se PUT porque estamos atualizando o estado do livro
    @Path("/{id}/loan")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loanBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (book.loaned()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        Book updated = new Book(book.id(), book.title(), book.author(), true);
        books.put(updated.id(), updated);
        return Response.ok(updated).build();
    }

    /**
     * Marks a book as returned.
     *
     * @param id the book identifier
     * @return the updated book, status 200, or 404 if it does not exist
     */
    @PUT //aqui usamos PUT para indicar que estamos atualizando o estado do recurso (o livro) para "devolvido". O verbo PUT é apropriado para operações idempotentes, onde a mesma solicitação pode ser feita várias vezes sem alterar o resultado além da primeira aplicação.
    @Path("/{id}/return")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@PathParam("id") Long id) {
        Book book = books.get(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Book updated = new Book(book.id(), book.title(), book.author(), false);
        books.put(updated.id(), updated);
        return Response.ok(updated).build();
    }
}
