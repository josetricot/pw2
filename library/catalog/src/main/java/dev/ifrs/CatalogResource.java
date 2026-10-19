package dev.ifrs;

import java.util.ArrayList;
import java.util.List;

import dev.ifrs.model.Book;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/books")
public class CatalogResource {

    List<Book> books = new ArrayList<>();

    //Adicionar um livro ao catálogo
    @POST
    @Consumes(MediaType.APPLICATION_JSON) //eu endpoint /books consumo/recebo um JSON
    @Produces(MediaType.APPLICATION_JSON) //eu endpoint /books produzo/devolvo um JSON
    //aqui mudei Book para Response, pois o retorno do método é um 
    // http completo com obejto + status code
    
    public Response create(Book book) {

        book.setId((long) (books.size() + 1)); //gerando id automatico
        book.setLoaned(false);
        books.add(book);
        return Response.status(201).entity(book).build();

    }
    //ainda existe o problema de guardar o livro em algum lugar
    //como nao temos banco de dados vou guardar na memoria do programa
    //criado no topo...

    //Consultar o catálogo de livros disponíveis
    @GET 
    @Produces(MediaType.APPLICATION_JSON)

    //public List<Book> getBooks() {
    public Response getBooks() {
        return Response.status(200).entity(books).build();
    }
    
    //Consultar um livro específico pelo seu ID
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Response getBookById(@jakarta.ws.rs.PathParam("id") Long id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                return Response.status(200).entity(book).build();
            }
        }
        return Response.status(404).build();
    }

    //Marcar um livro como emprestado
    @Path("{id}/loan")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response loanBook(@jakarta.ws.rs.PathParam("id") Long id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                if (!book.isLoaned()) {
                    book.setLoaned(true);
                    return Response.status(200).entity(book).build();
                } else {
                    return Response.status(409).entity("Livro já emprestado").build();
                }    
            }   
        }
        return Response.status(404).entity("Livro não encontrado").build();
    }

    //Marcar um livro como devolvido
    @Path("{id}/return")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@jakarta.ws.rs.PathParam("id") Long id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                    book.setLoaned(false);
                    return Response.status(200).entity(book).build();
            }
        }
        return Response.status(404).entity("Livro não encontrado").build();    
    }    
}