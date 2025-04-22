package com.iBai.ecommerce.dao;



import com.iBai.ecommerce.model.MetodoPago;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con métodos de pago.
 */
public class MetodoPagoDAO extends AbstractDAO<MetodoPago, Long> {
    
    public MetodoPagoDAO() {
        super(MetodoPago.class);
    }
    
    /**
     * Busca métodos de pago por ID de usuario
     * @param usuarioId ID del usuario
     * @return lista de métodos de pago del usuario
     */
    @SuppressWarnings("unchecked")
    public List<MetodoPago> findByUsuario(Long usuarioId) {
        Query<MetodoPago> query = pm.newQuery(MetodoPago.class);
        query.setFilter("usuario.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<MetodoPago>) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca métodos de pago por tipo
     * @param usuarioId ID del usuario
     * @param tipo tipo de método de pago
     * @return lista de métodos de pago del tipo específico
     */
    @SuppressWarnings("unchecked")
    public List<MetodoPago> findByTipo(Long usuarioId, MetodoPago.Tipo tipo) {
        Query<MetodoPago> query = pm.newQuery(MetodoPago.class);
        query.setFilter("usuario.id == usuarioIdParam && tipo == tipoParam");
        query.declareParameters("Long usuarioIdParam, MetodoPago.Tipo tipoParam");
        
        try {
            return (List<MetodoPago>) query.execute(usuarioId, tipo);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca el método de pago predeterminado de un usuario
     * @param usuarioId ID del usuario
     * @return método de pago predeterminado o null si no existe
     */
    public MetodoPago findPredeterminado(Long usuarioId) {
        Query<MetodoPago> query = pm.newQuery(MetodoPago.class);
        query.setFilter("usuario.id == usuarioIdParam && predeterminado == true");
        query.declareParameters("Long usuarioIdParam");
        query.setUnique(true);
        
        try {
            return (MetodoPago) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Establece un método de pago como predeterminado y quita esta marca de los demás
     * @param metodoPagoId ID del método de pago
     */
    public void establecerPredeterminado(Long metodoPagoId) {
        MetodoPago metodoPago = findById(metodoPagoId);
        if (metodoPago != null) {
            executeWithTransaction(() -> {
                // Primero quitamos la marca predeterminada de todos los métodos de pago
                Query<MetodoPago> query = pm.newQuery(MetodoPago.class);
                query.setFilter("usuario.id == usuarioIdParam && predeterminado == true");
                query.declareParameters("Long usuarioIdParam");
                
                @SuppressWarnings("unchecked")
                List<MetodoPago> metodosPredeterminados = (List<MetodoPago>) query.execute(
                    metodoPago.getUsuario().getId());
                
                for (MetodoPago metodo : metodosPredeterminados) {
                    metodo.setPredeterminado(false);
                }
                
                // Luego marcamos el nuevo predeterminado
                metodoPago.setPredeterminado(true);
            });
        }
    }
    
    /**
     * Elimina todos los métodos de pago de un usuario
     * @param usuarioId ID del usuario
     * @return número de métodos de pago eliminados
     */
    public int eliminarMetodosPagoDeUsuario(Long usuarioId) {
        List<MetodoPago> metodosPago = findByUsuario(usuarioId);
        int count = 0;
        
        for (MetodoPago metodoPago : metodosPago) {
            delete(metodoPago);
            count++;
        }
        
        return count;
    }
}
