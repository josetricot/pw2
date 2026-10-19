package dev.ifrs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import dev.ifrs.client.IBookCatalog;
import dev.ifrs.model.Book;
import dev.ifrs.model.Loan;
import dev.ifrs.model.LoanRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/loans")
public class LoansResource {

    private List<Loan> loans = new ArrayList<>();

    @Inject
    @RestClient
    IBookCatalog catalog;

    @POST
    @Consumes (MediaType.APPLICATION_JSON)
    @Produces (MediaType.APPLICATION_JSON)
    //public Response LoanRequest(LoanRequest request) {
    public Response loanRequest(LoanRequest request) { //recebe os dados que o cliente enviou
        //verifica se o livro está disponível no catálogo
        //avisa o catalogo que o livro foi emprestado
        try {
            catalog.markAsLoaned(request.bookId());
            Long loanId = (long) loans.size() + 1;
            Loan loan = new Loan(loanId, request.bookId(), request.borrower());
            loans.add(loan);
            return Response.status(201).entity(loan).build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch ( Exception e) {
            return Response.status(409).entity("Conflict: Book not available").build();
        }
    }
    @Path("/books")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Response listBooks() {

        //pega todos os livros do catálogo
        List<Book> allBooks = catalog.listBooks();

        //cria uma lista vazia apenas para os disponíveis
        List<Book> availableBooks = new ArrayList<>();
        
        //percorre todos os livros e adiciona apenas os disponíveis na lista
        for (Book book : allBooks) {
            if (book.loaned() == false) {
                availableBooks.add(book);
            }
        }
        //retorna a lista filtrada de livros disponíveis
        return Response.status(200).entity(availableBooks).build();

    }

}