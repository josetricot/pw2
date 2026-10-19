package edu.ifrs;

//representa um livro no catálogo
public record Book(
    Long id,
    String title,
    String author,
    boolean loaned) {}