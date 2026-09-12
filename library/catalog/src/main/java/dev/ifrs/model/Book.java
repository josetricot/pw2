package dev.ifrs.model;

public class Book {
    private Long id;
    private String title;
    private String author;
    private boolean loaned;

    public Book(){}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isLoaned() {
        return loaned;
    }

    public void setLoaned(boolean loaned) {
        this.loaned = loaned;
    }

    public void setId(long l) {
        this.id = l;
    }

}
/*

//ou usamos record, que é uma forma mais simples de criar uma classe imutável com atributos e métodos de acesso automáticos
// Representa um livro no catálogo
public record Book(
    Long id,
    String title,
    String author,
    boolean loaned) {}
*/