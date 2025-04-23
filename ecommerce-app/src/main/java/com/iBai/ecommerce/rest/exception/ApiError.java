package com.iBai.ecommerce.rest.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para respuestas de error en la API
 */
public class ApiError {
    
    private int status;
    private String error;
    private String message;
    private long timestamp;
    private List<String> details = new ArrayList<>();
    
    public ApiError() {
    }
    
    public ApiError(int status, String error, String message, long timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getDetails() {
        return details;
    }
    
    public void setDetails(List<String> details) {
        this.details = details;
    }
    
    public void addDetail(String detail) {
        this.details.add(detail);
    }
}