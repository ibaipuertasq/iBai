package com.iBai.ecommerce.rest.exception;

import javax.ws.rs.core.Response.Status;

/**
 * Excepción para errores de reglas de negocio
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Convierte la excepción a un objeto ApiError para la respuesta
     * @return el objeto ApiError con la información de error
     */
    public ApiError toApiError() {
        ApiError error = new ApiError();
        error.setStatus(Status.BAD_REQUEST.getStatusCode());
        error.setError("Error de validación");
        error.setMessage(getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return error;
    }
}