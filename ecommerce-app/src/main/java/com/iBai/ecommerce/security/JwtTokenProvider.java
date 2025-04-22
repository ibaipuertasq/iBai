package com.iBai.ecommerce.security;

import com.iBai.ecommerce.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de tokens JWT para la autenticación
 */
public class JwtTokenProvider {
    
    // Clave secreta para firmar el token (en producción, usar una configuración externa)
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    // Tiempo de expiración del token en milisegundos (24 horas)
    private static final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    
    /**
     * Genera un token JWT para un usuario
     * @param usuario el usuario para el que se genera el token
     * @return el token JWT generado
     */
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_VALIDITY);
        
        return Jwts.builder()
                .setSubject(Long.toString(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(KEY)
                .compact();
    }
    
    /**
     * Extrae el ID del usuario del token JWT
     * @param token el token JWT
     * @return el ID del usuario contenido en el token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * Extrae el rol del usuario del token JWT
     * @param token el token JWT
     * @return el rol del usuario contenido en el token
     */
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("rol", String.class);
    }
    
    /**
     * Valida un token JWT
     * @param token el token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}