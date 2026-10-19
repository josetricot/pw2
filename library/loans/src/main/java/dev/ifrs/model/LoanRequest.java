package dev.ifrs.model;

// Corpo enviado para solicitar um novo empréstimo
public record LoanRequest(
    Long bookId,
    String borrower) {}