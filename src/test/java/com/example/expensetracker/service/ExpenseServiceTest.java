package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.repo.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addExpense_success() {
        // arrange
        String username = "alice";
        User user = User.builder().id(1L).username(username).password("hashed").build();
        ExpenseRequest req = new ExpenseRequest();
        req.setTitle("Coffee");
        req.setAmount(3.5);
        req.setCategory("Food");
        req.setExpenseDate(LocalDate.of(2025, 9, 12));

        when(userService.findByUsernameOrThrow(username)).thenReturn(user);

        Expense saved = Expense.builder()
                .id(100L)
                .title(req.getTitle())
                .amount(req.getAmount())
                .category(req.getCategory())
                .expenseDate(req.getExpenseDate())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        when(expenseRepo.save(any(Expense.class))).thenReturn(saved);

        // act
        ExpenseResponse resp = expenseService.addExpense(username, req);

        // assert
        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals("Coffee", resp.getTitle());
        verify(expenseRepo, times(1)).save(any(Expense.class));
    }

    @Test
    void getExpenseById_notOwner_throwsSecurityException() {
        // arrange
        String username = "alice";
        User owner = User.builder().id(2L).username("bob").password("hashed").build();
        Expense e = Expense.builder()
                .id(200L)
                .title("Taxi")
                .amount(10.0)
                .category("Travel")
                .expenseDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .user(owner)
                .build();

        when(expenseRepo.findById(200L)).thenReturn(Optional.of(e));

        // act & assert
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            expenseService.getExpenseById(username, 200L);
        });
        assertEquals("Access denied", ex.getMessage());
    }
}
