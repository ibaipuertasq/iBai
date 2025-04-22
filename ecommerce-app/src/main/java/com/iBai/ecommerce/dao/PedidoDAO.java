package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.Pedido;
import com.iBai.ecommerce.model.Usuario;
import java.util.Date;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con pedidos.
 */
public class PedidoDAO extends AbstractDAO<Pedido, Long> {
    
    public PedidoDAO() {
        super(Pedido.class);
    }
    
    /**
     * Busca un pedido por su código de referencia
     * @param codigoReferencia el código a buscar
     * @return el pedido encontrado o null si no existe
     */
    public Pedido findByCodigoReferencia(String codigoReferencia) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("codigoReferencia == codigoParam");
        query.declareParameters("String codigoParam");
        query.setUnique(true);
        
        try {
            return (Pedido) query.execute(codigoReferencia);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca pedidos por usuario
     * @param usuarioId ID del usuario
     * @return lista de pedidos del usuario
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findByUsuario(Long usuarioId) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("usuario.id == idParam");
        query.declareParameters("Long idParam");
        query.setOrdering("fechaPedido DESC");
        
        try {
            return (List<Pedido>) query.execute(usuarioId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca pedidos por estado
     * @param estado el estado a buscar
     * @return lista de pedidos con el estado especificado
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findByEstado(Pedido.Estado estado) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("estado == estadoParam");
        query.declareParameters("Pedido.Estado estadoParam");
        query.setOrdering("fechaPedido DESC");
        
        try {
            return (List<Pedido>) query.execute(estado);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca pedidos realizados en un rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de pedidos en el rango de fechas
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findByRangoFechas(Date fechaInicio, Date fechaFin) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("fechaPedido >= fechaInicioParam && fechaPedido <= fechaFinParam");
        query.declareParameters("java.util.Date fechaInicioParam, java.util.Date fechaFinParam");
        query.setOrdering("fechaPedido DESC");
        
        try {
            return (List<Pedido>) query.execute(fechaInicio, fechaFin);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca pedidos por estado y rango de fechas
     * @param estado el estado a buscar
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de pedidos que coinciden con los criterios
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findByEstadoYRangoFechas(Pedido.Estado estado, Date fechaInicio, Date fechaFin) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("estado == estadoParam && fechaPedido >= fechaInicioParam && fechaPedido <= fechaFinParam");
        query.declareParameters("Pedido.Estado estadoParam, java.util.Date fechaInicioParam, java.util.Date fechaFinParam");
        query.setOrdering("fechaPedido DESC");
        
        try {
            return (List<Pedido>) query.execute(estado, fechaInicio, fechaFin);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Actualiza el estado de un pedido
     * @param pedidoId ID del pedido
     * @param nuevoEstado el nuevo estado
     */
    public void actualizarEstado(Long pedidoId, Pedido.Estado nuevoEstado) {
        Pedido pedido = findById(pedidoId);
        if (pedido != null) {
            executeWithTransaction(() -> {
                pedido.setEstado(nuevoEstado);
            });
        }
    }
    
    /**
     * Busca los últimos pedidos realizados (limitado)
     * @param limite número máximo de resultados
     * @return lista de los últimos pedidos
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findUltimosPedidos(int limite) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setOrdering("fechaPedido DESC");
        query.setRange(0, limite);
        
        try {
            return (List<Pedido>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca pedidos con un importe total superior a un valor
     * @param importeMinimo el importe mínimo
     * @return lista de pedidos con importe superior
     */
    @SuppressWarnings("unchecked")
    public List<Pedido> findByImporteSuperior(double importeMinimo) {
        Query<Pedido> query = pm.newQuery(Pedido.class);
        query.setFilter("total.doubleValue() >= importeParam");
        query.declareParameters("double importeParam");
        query.setOrdering("total DESC");
        
        try {
            return (List<Pedido>) query.execute(importeMinimo);
        } finally {
            query.closeAll();
        }
    }
}
