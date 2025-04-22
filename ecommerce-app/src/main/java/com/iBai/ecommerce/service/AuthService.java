package com.iBai.ecommerce.service;

import com.iBai.ecommerce.dao.UsuarioDAO;
import com.iBai.ecommerce.dto.mapper.UsuarioMapper;
import com.iBai.ecommerce.dto.request.LoginRequest;
import com.iBai.ecommerce.dto.request.RegistroRequest;
import com.iBai.ecommerce.dto.response.AuthResponse;
import com.iBai.ecommerce.model.Usuario;
import com.iBai.ecommerce.security.JwtTokenProvider;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

/**
 * Servicio para la autenticación de usuarios
 */
public class AuthService {
    
    private final UsuarioDAO usuarioDAO;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * Constructor del servicio de autenticación
     */
    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
        this.tokenProvider = new JwtTokenProvider();
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     * @param request datos de registro del usuario
     * @return respuesta con el token y datos del usuario
     * @throws Exception si el email ya está registrado
     */
    public AuthResponse registrar(RegistroRequest request) throws Exception {
        // Verificar si el email ya está registrado
        if (usuarioDAO.findByEmail(request.getEmail()) != null) {
            throw new Exception("El email ya está registrado");
        }
        
        // Generar hash de la contraseña
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));
        
        // Crear y guardar el usuario
        Usuario usuario = UsuarioMapper.toUsuario(request, passwordHash);
        usuario = usuarioDAO.save(usuario);
        
        // Generar token JWT
        String token = tokenProvider.generateToken(usuario);
        
        // Crear y devolver respuesta
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), 
                usuario.getNombre(), usuario.getRol().name());
    }
    
    /**
     * Autentica un usuario con sus credenciales
     * @param request datos de login del usuario
     * @return respuesta con el token y datos del usuario
     * @throws Exception si las credenciales son inválidas o el usuario está inactivo
     */
    public AuthResponse login(LoginRequest request) throws Exception {
        // Buscar el usuario por email
        Usuario usuario = usuarioDAO.findByEmail(request.getEmail());
        
        // Verificar si el usuario existe y está activo
        if (usuario == null) {
            throw new Exception("Email o contraseña incorrectos");
        }
        
        if (!usuario.getActivo()) {
            throw new Exception("La cuenta está desactivada");
        }
        
        // Verificar la contraseña
        if (!BCrypt.checkpw(request.getPassword(), usuario.getPasswordHash())) {
            throw new Exception("Email o contraseña incorrectos");
        }
        
        // Actualizar la fecha de última conexión
        usuario.setUltimaConexion(new Date());
        usuarioDAO.update(usuario);
        
        // Generar token JWT
        String token = tokenProvider.generateToken(usuario);
        
        // Crear y devolver respuesta
        return new AuthResponse(token, usuario.getId(), usuario.getEmail(), 
                usuario.getNombre(), usuario.getRol().name());
    }
    
    /**
     * Valida un token JWT
     * @param token el token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validarToken(String token) {
        return tokenProvider.validateToken(token);
    }
    
    /**
     * Obtiene el ID del usuario a partir de un token JWT
     * @param token el token JWT
     * @return el ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        return tokenProvider.getUserIdFromToken(token);
    }
    
    /**
     * Obtiene el rol del usuario a partir de un token JWT
     * @param token el token JWT
     * @return el rol del usuario
     */
    public String getRoleFromToken(String token) {
        return tokenProvider.getRoleFromToken(token);
    }
}