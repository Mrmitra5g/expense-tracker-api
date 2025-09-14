package com.example.expensetracker.exception;

public class ExpenseValidationException extends RuntimeException {
    public ExpenseValidationException(String message) {
        super(message);
    }
}
