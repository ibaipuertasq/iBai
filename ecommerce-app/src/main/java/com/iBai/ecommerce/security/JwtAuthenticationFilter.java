package com.iBai.ecommerce.security;

import com.iBai.ecommerce.rest.exception.ApiError;
import com.iBai.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {
    
    @Context
    private ResourceInfo resourceInfo;
    
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    
    public JwtAuthenticationFilter() {
        this.authService = new AuthService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Obtener el header Authorization de la petición
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // Verificar que el header exista y tenga el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Falta token de autenticación o formato incorrecto");
            return;
        }
        
        // Extraer el token (eliminar "Bearer ")
        String token = authorizationHeader.substring(7);
        
        try {
            // Validar el token
            if (!authService.validarToken(token)) {
                abortWithUnauthorized(requestContext, "Token inválido o expirado");
                return;
            }
            
            // Verificar roles si están especificados en la anotación @Secured
            validateRoles(token, requestContext);
            
        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Error al procesar el token: " + e.getMessage());
        }
    }
    
    private void validateRoles(String token, ContainerRequestContext requestContext) throws IOException {
        if (resourceInfo == null) {
            // Si por algún motivo resourceInfo no está disponible, permitir el acceso
            return;
        }
        
        // Obtener roles permitidos de la anotación en el método o clase
        List<String> rolesPermitidos = Collections.emptyList();
        
        Method method = resourceInfo.getResourceMethod();
        if (method != null) {
            Secured securedMethod = method.getAnnotation(Secured.class);
            if (securedMethod != null && securedMethod.value().length > 0) {
                rolesPermitidos = Arrays.asList(securedMethod.value());
            } else {
                // Si no hay roles en el método, verificar a nivel de clase
                Class<?> resourceClass = method.getDeclaringClass();
                Secured securedClass = resourceClass.getAnnotation(Secured.class);
                if (securedClass != null && securedClass.value().length > 0) {
                    rolesPermitidos = Arrays.asList(securedClass.value());
                }
            }
        }
        
        // Si no hay roles especificados, cualquier usuario autenticado puede acceder
        if (rolesPermitidos.isEmpty()) {
            return;
        }
        
        // Obtener el rol del usuario desde el token
        String userRole = authService.getRoleFromToken(token);
        
        // Verificar si el rol del usuario está en la lista de roles permitidos
        if (!rolesPermitidos.contains(userRole)) {
            abortWithForbidden(requestContext, "No tiene permiso para acceder a este recurso");
        }
    }
    
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) throws IOException {
        ApiError error = new ApiError(Response.Status.UNAUTHORIZED.getStatusCode(), 
                                     "No autenticado", message);
        
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                   .entity(objectMapper.writeValueAsString(error))
                   .type(MediaType.APPLICATION_JSON)
                   .build());
    }
    
    private void abortWithForbidden(ContainerRequestContext requestContext, String message) throws IOException {
        ApiError error = new ApiError(Response.Status.FORBIDDEN.getStatusCode(), 
                                     "Acceso denegado", message);
        
        requestContext.abortWith(
            Response.status(Response.Status.FORBIDDEN)
                   .entity(objectMapper.writeValueAsString(error))
                   .type(MediaType.APPLICATION_JSON)
                   .build());
    }
}