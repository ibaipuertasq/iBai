package com.iBai.ecommerce.security;

import com.iBai.ecommerce.rest.exception.AuthenticationException;
import com.iBai.ecommerce.rest.exception.ApiError;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro para autenticación JWT en las peticiones.
 * Verifica que las peticiones que requieren autenticación (marcadas con @Secured)
 * incluyan un token JWT válido y con los permisos adecuados.
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    
    @Context
    private ResourceInfo resourceInfo;
    
    private final JwtTokenProvider tokenProvider;
    
    public JwtAuthenticationFilter() {
        this.tokenProvider = new JwtTokenProvider();
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Obtener el header de autorización de la petición
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // Verificar si el header de autorización está presente y tiene el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHENTICATION_SCHEME + " ")) {
            abortWithUnauthorized(requestContext, "El header de autorización es requerido");
            return;
        }
        
        // Extraer el token JWT
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        
        try {
            // Validar el token
            if (!tokenProvider.validateToken(token)) {
                abortWithUnauthorized(requestContext, "Token inválido o expirado");
                return;
            }
            
            // Extraer el rol del token
            String role = tokenProvider.getRoleFromToken(token);
            
            // Verificar permisos según los roles requeridos por el endpoint
            if (!checkRolePermissions(role)) {
                abortWithForbidden(requestContext, "No tiene permisos suficientes para acceder a este recurso");
                return;
            }
            
            // Si llegamos hasta aquí, la autenticación es válida
            // Establecer el ID del usuario en la petición para su uso posterior
            requestContext.setProperty("userId", tokenProvider.getUserIdFromToken(token));
            requestContext.setProperty("userRole", role);
            
        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Error en la autenticación: " + e.getMessage());
        }
    }
    
    private boolean checkRolePermissions(String tokenRole) {
        // Obtener la anotación Secured del método o clase
        Method method = resourceInfo.getResourceMethod();
        Secured securedAnnotation = method.getAnnotation(Secured.class);
        
        // Si no hay anotación en el método, buscar en la clase
        if (securedAnnotation == null) {
            securedAnnotation = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }
        
        // Si no hay roles requeridos, cualquier usuario autenticado puede acceder
        if (securedAnnotation == null || securedAnnotation.value().length == 0) {
            return true;
        }
        
        // Verificar si el rol del usuario está entre los roles permitidos
        List<String> requiredRoles = Arrays.asList(securedAnnotation.value());
        
        try {
            Role userRole = Role.valueOf(tokenRole);
            
            // Si el usuario es ADMIN, tiene acceso a todo
            if (userRole == Role.ADMIN) {
                return true;
            }
            
            // Comprobar si el rol del usuario está entre los permitidos
            for (String requiredRole : requiredRoles) {
                if (userRole.name().equals(requiredRole)) {
                    return true;
                }
            }
            
            // Si el usuario es GESTOR y alguno de los roles requeridos es CLIENTE
            if (userRole == Role.GESTOR) {
                return requiredRoles.contains(Role.CLIENTE.name());
            }
            
            return false;
        } catch (IllegalArgumentException e) {
            // Si el rol del token no es un enum Role válido
            return false;
        }
    }
    
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        apiError.setError("Error de autenticación");
        apiError.setMessage(message);
        apiError.setTimestamp(System.currentTimeMillis());
        
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(apiError)
                .build());
    }
    
    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(Response.Status.FORBIDDEN.getStatusCode());
        apiError.setError("Acceso denegado");
        apiError.setMessage(message);
        apiError.setTimestamp(System.currentTimeMillis());
        
        requestContext.abortWith(
            Response.status(Response.Status.FORBIDDEN)
                .entity(apiError)
                .build());
    }
}