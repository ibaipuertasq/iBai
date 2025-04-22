package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.Direccion;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con direcciones.
 */
public class DireccionDAO extends AbstractDAO<Direccion, Long> {
    
    public DireccionDAO() {
        super(Direccion.class);
    }
    
    /**
     * Busca direcciones por ID de usuario
     * @param usuarioId ID del usuario
     * @return lista de direcciones del usuario
     */
    @SuppressWarnings("unchecked")
    public List<Direccion> findByUsuario(Long usuarioId) {
        Query<Direccion> query = pm.newQuery(Direccion.class);
        query.setFilter("usuario.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<Direccion>) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca direcciones por tipo
     * @param usuarioId ID del usuario
     * @param tipo tipo de dirección (ENVIO o FACTURACION)
     * @return lista de direcciones del tipo específico
     */
    @SuppressWarnings("unchecked")
    public List<Direccion> findByTipo(Long usuarioId, Direccion.Tipo tipo) {
        Query<Direccion> query = pm.newQuery(Direccion.class);
        query.setFilter("usuario.id == usuarioIdParam && tipo == tipoParam");
        query.declareParameters("Long usuarioIdParam, Direccion.Tipo tipoParam");
        
        try {
            return (List<Direccion>) query.execute(usuarioId, tipo);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca la dirección predeterminada de un usuario por tipo
     * @param usuarioId ID del usuario
     * @param tipo tipo de dirección
     * @return dirección predeterminada o null si no existe
     */
    public Direccion findPredeterminada(Long usuarioId, Direccion.Tipo tipo) {
        Query<Direccion> query = pm.newQuery(Direccion.class);
        query.setFilter("usuario.id == usuarioIdParam && tipo == tipoParam && predeterminada == true");
        query.declareParameters("Long usuarioIdParam, Direccion.Tipo tipoParam");
        query.setUnique(true);
        
        try {
            return (Direccion) query.execute(usuarioId, tipo);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Establece una dirección como predeterminada y quita esta marca de las demás
     * @param direccionId ID de la dirección
     */
    public void establecerPredeterminada(Long direccionId) {
        Direccion direccion = findById(direccionId);
        if (direccion != null) {
            executeWithTransaction(() -> {
                // Primero quitamos la marca predeterminada de todas las direcciones del mismo tipo
                Query<Direccion> query = pm.newQuery(Direccion.class);
                query.setFilter("usuario.id == usuarioIdParam && tipo == tipoParam && predeterminada == true");
                query.declareParameters("Long usuarioIdParam, Direccion.Tipo tipoParam");
                
                @SuppressWarnings("unchecked")
                List<Direccion> direccionesPredeterminadas = (List<Direccion>) query.execute(
                    direccion.getUsuario().getId(), direccion.getTipo());
                
                for (Direccion dir : direccionesPredeterminadas) {
                    dir.setPredeterminada(false);
                }
                
                // Luego marcamos la nueva predeterminada
                direccion.setPredeterminada(true);
            });
        }
    }
    
    /**
     * Elimina todas las direcciones de un usuario
     * @param usuarioId ID del usuario
     * @return número de direcciones eliminadas
     */
    public int eliminarDireccionesDeUsuario(Long usuarioId) {
        List<Direccion> direcciones = findByUsuario(usuarioId);
        int count = 0;
        
        for (Direccion direccion : direcciones) {
            delete(direccion);
            count++;
        }
        
        return count;
    }
}