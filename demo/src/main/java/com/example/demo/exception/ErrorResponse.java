package com.example.demo.exception;

import com.example.demo.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private Map<String,String> errors;

    public ErrorResponse(boolean suc,String message, int status) {
        this.success=suc;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
    public ErrorResponse(boolean suc,String message, int status,Map<String,String> err) {
        this.errors=err;
        this.success=suc;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }


}
