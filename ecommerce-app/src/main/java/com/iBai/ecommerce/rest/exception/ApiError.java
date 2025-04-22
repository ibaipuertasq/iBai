package com.iBai.ecommerce.rest.exception;

import java.util.Date;

/**
 * Modelo para respuestas de error de la API
 */
public class ApiError {
    
    private int status;
    private String error;
    private String message;
    private Date timestamp;
    
    public ApiError() {
        this.timestamp = new Date();
    }
    
    public ApiError(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }
    
    // Getters y setters
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
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}