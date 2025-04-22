package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dto.request.RegistroRequest;
import com.iBai.ecommerce.dto.response.UsuarioResponse;
import com.iBai.ecommerce.model.Usuario;

/**
 * Clase para mapear entre la entidad Usuario y sus DTOs
 */
public class UsuarioMapper {
    
    /**
     * Convierte una entidad Usuario a un DTO UsuarioResponse
     * @param usuario la entidad a convertir
     * @return el DTO con la información del usuario
     */
    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setEmail(usuario.getEmail());
        response.setNombre(usuario.getNombre());
        response.setApellidos(usuario.getApellidos());
        response.setTelefono(usuario.getTelefono());
        response.setFechaRegistro(usuario.getFechaRegistro());
        response.setUltimaConexion(usuario.getUltimaConexion());
        response.setActivo(usuario.getActivo());
        response.setRol(usuario.getRol().name());
        
        return response;
    }
    
    /**
     * Convierte un DTO RegistroRequest a una entidad Usuario
     * @param request el DTO con la información de registro
     * @param passwordHash el hash de la contraseña
     * @return la entidad Usuario creada
     */
    public static Usuario toUsuario(RegistroRequest request, String passwordHash) {
        if (request == null) {
            return null;
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordHash);
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefono(request.getTelefono());
        
        return usuario;
    }
}