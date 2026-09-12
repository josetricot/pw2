package dev.ifrs.model;

// Representa um livro no catálogo
public record Book(
    Long id,
    String title,
    String author,
    boolean loaned) {}