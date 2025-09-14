package com.example.expensetracker.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
}
