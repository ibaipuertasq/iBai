package com.iBai.ecommerce.rest.exception;

import javax.ws.rs.core.Response.Status;

/**
 * Excepción para errores de autenticación
 */
public class AuthenticationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Convierte la excepción a un objeto ApiError para la respuesta
     * @return el objeto ApiError con la información de error
     */
    public ApiError toApiError() {
        ApiError error = new ApiError();
        error.setStatus(Status.UNAUTHORIZED.getStatusCode());
        error.setError("Error de autenticación");
        error.setMessage(getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return error;
    }
}
