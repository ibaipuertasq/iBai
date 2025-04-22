package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.Valoracion;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con valoraciones de productos.
 */
public class ValoracionDAO extends AbstractDAO<Valoracion, Long> {
    
    public ValoracionDAO() {
        super(Valoracion.class);
    }
    
    /**
     * Busca valoraciones por ID de producto
     * @param productoId ID del producto
     * @return lista de valoraciones del producto
     */
    @SuppressWarnings("unchecked")
    public List<Valoracion> findByProducto(Long productoId) {
        Query<Valoracion> query = pm.newQuery(Valoracion.class);
        query.setFilter("producto.id == idParam");
        query.declareParameters("Long idParam");
        query.setOrdering("fecha DESC");
        
        try {
            return (List<Valoracion>) query.execute(productoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca valoraciones realizadas por un usuario
     * @param usuarioId ID del usuario
     * @return lista de valoraciones del usuario
     */
    @SuppressWarnings("unchecked")
    public List<Valoracion> findByUsuario(Long usuarioId) {
        Query<Valoracion> query = pm.newQuery(Valoracion.class);
        query.setFilter("usuario.id == idParam");
        query.declareParameters("Long idParam");
        query.setOrdering("fecha DESC");
        
        try {
            return (List<Valoracion>) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca valoraciones por rango de puntuación
     * @param productoId ID del producto
     * @param puntuacionMin puntuación mínima
     * @param puntuacionMax puntuación máxima
     * @return lista de valoraciones en el rango de puntuación
     */
    @SuppressWarnings("unchecked")
    public List<Valoracion> findByRangoPuntuacion(Long productoId, byte puntuacionMin, byte puntuacionMax) {
        Query<Valoracion> query = pm.newQuery(Valoracion.class);
        query.setFilter("producto.id == productoIdParam && puntuacion >= puntuacionMinParam && puntuacion <= puntuacionMaxParam");
        query.declareParameters("Long productoIdParam, byte puntuacionMinParam, byte puntuacionMaxParam");
        query.setOrdering("fecha DESC");
        
        try {
            return (List<Valoracion>) query.execute(productoId, puntuacionMin, puntuacionMax);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Encuentra la valoración de un usuario para un producto específico
     * @param productoId ID del producto
     * @param usuarioId ID del usuario
     * @return valoración del usuario para el producto o null si no existe
     */
    public Valoracion findByProductoYUsuario(Long productoId, Long usuarioId) {
        Query<Valoracion> query = pm.newQuery(Valoracion.class);
        query.setFilter("producto.id == productoIdParam && usuario.id == usuarioIdParam");
        query.declareParameters("Long productoIdParam, Long usuarioIdParam");
        query.setUnique(true);
        
        try {
            return (Valoracion) query.execute(productoId, usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Calcula la puntuación media de un producto
     * @param productoId ID del producto
     * @return puntuación media o 0 si no hay valoraciones
     */
    public double calcularPuntuacionMedia(Long productoId) {
        Query query = pm.newQuery("SELECT AVG(puntuacion) FROM " + Valoracion.class.getName() + 
                                 " WHERE producto.id == productoIdParam");
        query.declareParameters("Long productoIdParam");
        
        try {
            Double resultado = (Double) query.execute(productoId);
            return resultado != null ? resultado : 0.0;
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Cuenta el número de valoraciones para cada puntuación de un producto
     * @param productoId ID del producto
     * @return array con el recuento para cada puntuación (índice 0 = puntuación 1, etc.)
     */
    public int[] contarPuntuaciones(Long productoId) {
        int[] conteo = new int[5];
        
        for (byte i = 1; i <= 5; i++) {
            Query query = pm.newQuery("SELECT COUNT(id) FROM " + Valoracion.class.getName() + 
                                     " WHERE producto.id == productoIdParam && puntuacion == puntuacionParam");
            query.declareParameters("Long productoIdParam, byte puntuacionParam");
            
            try {
                Long resultado = (Long) query.execute(productoId, i);
                conteo[i-1] = resultado != null ? resultado.intValue() : 0;
            } finally {
                query.closeAll();
            }
        }
        
        return conteo;
    }
    
    /**
     * Elimina todas las valoraciones de un producto
     * @param productoId ID del producto
     * @return número de valoraciones eliminadas
     */
    public int eliminarValoracionesDeProducto(Long productoId) {
        List<Valoracion> valoraciones = findByProducto(productoId);
        int count = 0;
        
        for (Valoracion valoracion : valoraciones) {
            delete(valoracion);
            count++;
        }
        
        return count;
    }
}
