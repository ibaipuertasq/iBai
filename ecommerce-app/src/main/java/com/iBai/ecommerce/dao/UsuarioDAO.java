package com.iBai.ecommerce.dao;

import com.iBai.ecommerce.model.Usuario;
import java.util.Date;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con usuarios.
 */
public class UsuarioDAO extends AbstractDAO<Usuario, Long> {
    
    public UsuarioDAO() {
        super(Usuario.class);
    }
    
    /**
     * Busca un usuario por su email
     * @param email el email a buscar
     * @return el usuario encontrado o null si no existe
     */
    public Usuario findByEmail(String email) {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("email == emailParam");
        query.declareParameters("String emailParam");
        query.setUnique(true);
        
        try {
            return (Usuario) query.execute(email);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca usuarios por su rol
     * @param rol el rol a buscar
     * @return lista de usuarios con el rol especificado
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> findByRol(Usuario.Rol rol) {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("rol == rolParam");
        query.declareParameters("Usuario.Rol rolParam");
        
        try {
            return (List<Usuario>) query.execute(rol);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca usuarios activos
     * @return lista de usuarios activos
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> findActivos() {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("activo == true");
        
        try {
            return (List<Usuario>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca usuarios inactivos
     * @return lista de usuarios inactivos
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> findInactivos() {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("activo == false");
        
        try {
            return (List<Usuario>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca usuarios que no han iniciado sesión desde una fecha determinada
     * @param fecha la fecha de referencia
     * @return lista de usuarios sin actividad desde la fecha
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> findInactivosDesdeFecha(Date fecha) {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("ultimaConexion < fechaParam || ultimaConexion == null");
        query.declareParameters("java.util.Date fechaParam");
        
        try {
            return (List<Usuario>) query.execute(fecha);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca usuarios registrados en un rango de fechas
     * @param fechaInicio la fecha de inicio del rango
     * @param fechaFin la fecha de fin del rango
     * @return lista de usuarios registrados en el rango de fechas
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> findByRangoFechaRegistro(Date fechaInicio, Date fechaFin) {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("fechaRegistro >= fechaInicioParam && fechaRegistro <= fechaFinParam");
        query.declareParameters("java.util.Date fechaInicioParam, java.util.Date fechaFinParam");
        
        try {
            return (List<Usuario>) query.execute(fechaInicio, fechaFin);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Actualiza la fecha de última conexión de un usuario
     * @param usuarioId el ID del usuario
     * @param fecha la nueva fecha de última conexión
     */
    public void actualizarUltimaConexion(Long usuarioId, Date fecha) {
        Usuario usuario = findById(usuarioId);
        if (usuario != null) {
            executeWithTransaction(() -> {
                usuario.setUltimaConexion(fecha);
            });
        }
    }
    
    /**
     * Cambia el estado activo/inactivo de un usuario
     * @param usuarioId el ID del usuario
     * @param activo el nuevo estado
     */
    public void cambiarEstadoActivo(Long usuarioId, boolean activo) {
        Usuario usuario = findById(usuarioId);
        if (usuario != null) {
            executeWithTransaction(() -> {
                usuario.setActivo(activo);
            });
        }
    }
    
    /**
     * Busca usuarios por nombre o apellidos (búsqueda parcial)
     * @param termino el término de búsqueda
     * @return lista de usuarios que coinciden con el criterio
     */
    @SuppressWarnings("unchecked")
    public List<Usuario> buscarPorNombreOApellidos(String termino) {
        Query<Usuario> query = pm.newQuery(Usuario.class);
        query.setFilter("nombre.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0 || " +
                       "apellidos.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0");
        query.declareParameters("String terminoParam");
        
        try {
            return (List<Usuario>) query.execute(termino);
        } finally {
            query.closeAll();
        }
    }
}