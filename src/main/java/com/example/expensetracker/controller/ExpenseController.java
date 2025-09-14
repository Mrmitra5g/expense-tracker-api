package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.CategorySummary;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(Authentication auth, @Valid @RequestBody ExpenseRequest request) {
        String username = (String) auth.getPrincipal();
        return ResponseEntity.ok(expenseService.addExpense(username, request));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            Authentication auth,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String username = (String) auth.getPrincipal();
        return ResponseEntity.ok(expenseService.getExpenses(username, category, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(Authentication auth, @PathVariable Long id) {
        String username = (String) auth.getPrincipal();
        return ResponseEntity.ok(expenseService.getExpenseById(username, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(Authentication auth, @PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        String username = (String) auth.getPrincipal();
        return ResponseEntity.ok(expenseService.updateExpense(username, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(Authentication auth, @PathVariable Long id) {
        String username = (String) auth.getPrincipal();
        expenseService.deleteExpense(username, id);
        return ResponseEntity.ok("Expense deleted successfully");
    }

    @GetMapping("/summary")
    public ResponseEntity<List<CategorySummary>> getExpenseSummary(Authentication auth) {
        String username = (String) auth.getPrincipal();
        return ResponseEntity.ok(expenseService.getExpenseSummary(username));
    }
}
