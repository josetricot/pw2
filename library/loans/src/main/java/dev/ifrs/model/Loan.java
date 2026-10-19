package dev.ifrs.model;

// Representa um empréstimo registrado pelo serviço de gerenciamento
public record Loan(
    Long id,
    Long bookId,
    String borrower) {}