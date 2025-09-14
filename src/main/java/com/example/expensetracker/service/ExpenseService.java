package com.example.expensetracker.service;

import com.example.expensetracker.dto.*;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ExpenseValidationException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.exception.UnauthorizedException;
import com.example.expensetracker.repo.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private UserService userService;

    public ExpenseResponse addExpense(String username, ExpenseRequest req) {
        User user = userService.findByUsernameOrThrow(username);
        if (req.getAmount() <= 0) {
            throw new ExpenseValidationException("Expense amount must be greater than 0");
        }
        if (req.getExpenseDate().isAfter(LocalDate.now())) {
            throw new ExpenseValidationException("Expense date cannot be in the future");
        }
        Expense e = Expense.builder()
                .title(req.getTitle())
                .amount(req.getAmount())
                .category(req.getCategory())
                .expenseDate(req.getExpenseDate())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
        Expense saved = expenseRepo.save(e);
        return toDto(saved);
    }

    public List<ExpenseResponse> getExpenses(String username, String category, LocalDate start, LocalDate end) {
        User user = userService.findByUsernameOrThrow(username);
        List<Expense> list;
        if (category != null) {
            list = expenseRepo.findByUserAndCategory(user, category);
        } else if (start != null && end != null) {
            list = expenseRepo.findByUserAndExpenseDateBetween(user, start, end);
        } else {
            list = expenseRepo.findByUser(user);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(String username, Long id) {
        Expense e = expenseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense with id=" + id + " not found"));
        if (!e.getUser().getUsername().equals(username)) throw new UnauthorizedException("You are not allowed to access this expense");
        return toDto(e);
    }

    public ExpenseResponse updateExpense(String username, Long id, ExpenseRequest req) {
        Expense e = expenseRepo.findById(id).orElseThrow(() -> new  ResourceNotFoundException("Expense with id=" + id + " not found"));
        if (!e.getUser().getUsername().equals(username)) throw new UnauthorizedException("You are not allowed to access this expense");
        e.setTitle(req.getTitle());
        e.setAmount(req.getAmount());
        e.setCategory(req.getCategory());
        e.setExpenseDate(req.getExpenseDate());
        Expense updated = expenseRepo.save(e);
        return toDto(updated);
    }

    public void deleteExpense(String username, Long id) {
        Expense e = expenseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense with id=" + id + " not found"));
        if (!e.getUser().getUsername().equals(username)) throw new UnauthorizedException("You are not allowed to access this expense");
        expenseRepo.delete(e);
    }

    public List<CategorySummary> getExpenseSummary(String username) {
        User user = userService.findByUsernameOrThrow(username);
        List<Object[]> rows = expenseRepo.summarizeByCategory(user);
        return rows.stream()
                .map(r -> new CategorySummary((String) r[0], ((Number) r[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    private ExpenseResponse toDto(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .amount(e.getAmount())
                .category(e.getCategory())
                .expenseDate(e.getExpenseDate())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
