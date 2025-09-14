package com.example.expensetracker.dto;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;


    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;

    }



}
