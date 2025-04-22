package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.Carrito;
import java.util.Date;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con carritos de compra.
 */
public class CarritoDAO extends AbstractDAO<Carrito, Long> {
    
    public CarritoDAO() {
        super(Carrito.class);
    }
    
    /**
     * Busca un carrito por ID de usuario
     * @param usuarioId ID del usuario
     * @return el carrito del usuario o null si no existe
     */
    public Carrito findByUsuario(Long usuarioId) {
        Query<Carrito> query = pm.newQuery(Carrito.class);
        query.setFilter("usuario.id == idParam");
        query.declareParameters("Long idParam");
        query.setUnique(true);
        
        try {
            return (Carrito) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca un carrito por ID de sesión
     * @param sessionId ID de la sesión
     * @return el carrito de la sesión o null si no existe
     */
    public Carrito findBySessionId(String sessionId) {
        Query<Carrito> query = pm.newQuery(Carrito.class);
        query.setFilter("sessionId == sessionIdParam");
        query.declareParameters("String sessionIdParam");
        query.setUnique(true);
        
        try {
            return (Carrito) query.execute(sessionId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca carritos antiguos (creados antes de una fecha)
     * @param fecha fecha límite
     * @return lista de carritos antiguos
     */
    @SuppressWarnings("unchecked")
    public List<Carrito> findCarritosAntiguos(Date fecha) {
        Query<Carrito> query = pm.newQuery(Carrito.class);
        query.setFilter("fechaCreacion < fechaParam");
        query.declareParameters("java.util.Date fechaParam");
        
        try {
            return (List<Carrito>) query.execute(fecha);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Asocia un carrito anónimo a un usuario (tras login)
     * @param carritoId ID del carrito
     * @param usuarioId ID del usuario
     */
    public void asociarCarritoAUsuario(Long carritoId, Long usuarioId) {
        Carrito carrito = findById(carritoId);
        if (carrito != null) {
            executeWithTransaction(() -> {
                carrito.setUsuario(pm.getObjectById(com.iBai.ecommerce.model.Usuario.class, usuarioId));
                // Mantenemos el sessionId para no perder referencias
            });
        }
    }
    
    /**
     * Elimina carritos antiguos
     * @param fechaLimite fecha límite para considerar un carrito antiguo
     * @return número de carritos eliminados
     */
    public int eliminarCarritosAntiguos(Date fechaLimite) {
        List<Carrito> carritosAntiguos = findCarritosAntiguos(fechaLimite);
        int count = 0;
        
        for (Carrito carrito : carritosAntiguos) {
            delete(carrito);
            count++;
        }
        
        return count;
    }
}