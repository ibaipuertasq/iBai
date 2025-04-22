package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.DetallePedido;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con detalles de pedido.
 */
public class DetallePedidoDAO extends AbstractDAO<DetallePedido, Long> {
    
    public DetallePedidoDAO() {
        super(DetallePedido.class);
    }
    
    /**
     * Busca detalles por ID de pedido
     * @param pedidoId ID del pedido
     * @return lista de detalles del pedido
     */
    @SuppressWarnings("unchecked")
    public List<DetallePedido> findByPedido(Long pedidoId) {
        Query<DetallePedido> query = pm.newQuery(DetallePedido.class);
        query.setFilter("pedido.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<DetallePedido>) query.execute(pedidoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca detalles por ID de producto
     * @param productoId ID del producto
     * @return lista de detalles que contienen el producto
     */
    @SuppressWarnings("unchecked")
    public List<DetallePedido> findByProducto(Long productoId) {
        Query<DetallePedido> query = pm.newQuery(DetallePedido.class);
        query.setFilter("producto.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<DetallePedido>) query.execute(productoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Encuentra los productos más vendidos
     * @param limite número máximo de resultados
     * @return lista de detalles ordenados por cantidad vendida
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findProductosMasVendidos(int limite) {
        String jpql = "SELECT d.producto.id, d.nombreProducto, SUM(d.cantidad) " +
                      "FROM " + DetallePedido.class.getName() + " d " +
                      "GROUP BY d.producto.id, d.nombreProducto " +
                      "ORDER BY SUM(d.cantidad) DESC";
        
        Query query = pm.newQuery("javax.jdo.query.JPQL", jpql);
        query.setRange(0, limite);
        
        try {
            return (List<Object[]>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Actualiza la cantidad de un detalle de pedido
     * @param detalleId ID del detalle
     * @param cantidad nueva cantidad
     */
    public void actualizarCantidad(Long detalleId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        DetallePedido detalle = findById(detalleId);
        if (detalle != null) {
            executeWithTransaction(() -> {
                detalle.setCantidad(cantidad);
                detalle.calcularSubtotal();
            });
        }
    }
    
    /**
     * Calcula el total de un pedido sumando sus detalles
     * @param pedidoId ID del pedido
     * @return subtotal calculado
     */
    public double calcularSubtotalPedido(Long pedidoId) {
        List<DetallePedido> detalles = findByPedido(pedidoId);
        double subtotal = 0.0;
        
        for (DetallePedido detalle : detalles) {
            subtotal += detalle.getSubtotal().doubleValue();
        }
        
        return subtotal;
    }
}