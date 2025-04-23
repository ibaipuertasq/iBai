package com.iBai.ecommerce.security;

/**
 * Roles disponibles en el sistema
 */
public enum Role {
    ADMIN,
    CLIENTE,
    GESTOR; // Este rol no está en la BD pero lo incluimos para gestión interna
    
    /**
     * Verifica si el rol actual tiene permisos superiores o iguales al rol especificado
     * @param role el rol a comparar
     * @return true si tiene permisos suficientes, false en caso contrario
     */
    public boolean hasPermission(Role role) {
        if (this == ADMIN) return true;
        if (this == GESTOR && role == CLIENTE) return true;
        return this == role;
    }

    /**
     * Convierte un String a un Role
     * @param roleStr el string del rol
     * @return el enum Role correspondiente
     * @throws IllegalArgumentException si el string no corresponde a ningún rol
     */
    public static Role fromString(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + roleStr);
        }
    }
}