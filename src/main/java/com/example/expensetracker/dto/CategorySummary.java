package com.example.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CategorySummary {
    private String category;
    private Double total;
}
