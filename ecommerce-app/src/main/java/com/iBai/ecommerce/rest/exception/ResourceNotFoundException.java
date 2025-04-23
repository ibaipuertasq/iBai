package com.iBai.ecommerce.rest.exception;

import javax.ws.rs.core.Response.Status;

/**
 * Excepción para recursos no encontrados
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Convierte la excepción a un objeto ApiError para la respuesta
     * @return el objeto ApiError con la información de error
     */
    public ApiError toApiError() {
        ApiError error = new ApiError();
        error.setStatus(Status.NOT_FOUND.getStatusCode());
        error.setError("Recurso no encontrado");
        error.setMessage(getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return error;
    }
}