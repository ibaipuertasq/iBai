package com.iBai.ecommerce.rest.controllers;

import com.iBai.ecommerce.dto.request.LoginRequest;
import com.iBai.ecommerce.dto.request.RegistroRequest;
import com.iBai.ecommerce.dto.response.AuthResponse;
import com.iBai.ecommerce.service.AuthService;
import com.iBai.ecommerce.rest.exception.ApiError;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controlador REST para autenticación de usuarios
 */
@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController() {
        this.authService = getAuthService();
    }
    
    /**
     * Método protegido para permitir la inyección de mocks en las pruebas
     * @return instancia de AuthService
     */
    protected AuthService getAuthService() {
        return new AuthService();
    }
    
    /**
     * Endpoint para registrar un nuevo usuario
     * @param request los datos de registro
     * @return respuesta con el token y datos del usuario
     */
    @POST
    @Path("/register")
    public Response registrar(@Valid RegistroRequest request) {
        try {
            AuthResponse response = authService.registrar(request);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error de registro");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Endpoint para iniciar sesión
     * @param request los datos de login
     * @return respuesta con el token y datos del usuario
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            error.setError("Error de autenticación");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }
    
    /**
     * Endpoint para validar un token JWT
     * @param token el token a validar
     * @return respuesta indicando si el token es válido
     */
    @GET
    @Path("/validate")
    public Response validarToken(@HeaderParam("Authorization") String token) {
        // Extraer el token del header (eliminar "Bearer ")
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        boolean esValido = authService.validarToken(token);
        
        if (esValido) {
            return Response.ok().entity("{\"valid\": true}").build();
        } else {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            error.setError("Token inválido");
            error.setMessage("El token proporcionado no es válido o ha expirado");
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }
}