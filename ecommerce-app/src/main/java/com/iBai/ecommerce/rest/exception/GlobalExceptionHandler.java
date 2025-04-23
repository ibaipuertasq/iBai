package com.iBai.ecommerce.rest.exception;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Manejador global de excepciones para la API REST
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        // Manejar tipos específicos de excepciones
        if (exception instanceof ResourceNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(((ResourceNotFoundException)exception).toApiError())
                    .build();
        }
        
        if (exception instanceof BusinessException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(((BusinessException)exception).toApiError())
                    .build();
        }
        
        if (exception instanceof AuthenticationException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(((AuthenticationException)exception).toApiError())
                    .build();
        }
        
        // Para cualquier otra excepción, devolver un error interno del servidor
        ApiError error = new ApiError();
        error.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        error.setError("Error interno del servidor");
        error.setMessage("Se ha producido un error inesperado: " + exception.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}