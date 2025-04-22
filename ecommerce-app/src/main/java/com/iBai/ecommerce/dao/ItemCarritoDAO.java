package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.ItemCarrito;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con ítems de carrito.
 */
public class ItemCarritoDAO extends AbstractDAO<ItemCarrito, Long> {
    
    public ItemCarritoDAO() {
        super(ItemCarrito.class);
    }
    
    /**
     * Busca ítems por ID de carrito
     * @param carritoId ID del carrito
     * @return lista de ítems del carrito
     */
    @SuppressWarnings("unchecked")
    public List<ItemCarrito> findByCarrito(Long carritoId) {
        Query<ItemCarrito> query = pm.newQuery(ItemCarrito.class);
        query.setFilter("carrito.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<ItemCarrito>) query.execute(carritoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca un ítem específico en un carrito
     * @param carritoId ID del carrito
     * @param productoId ID del producto
     * @return el ítem encontrado o null si no existe
     */
    public ItemCarrito findByCarritoYProducto(Long carritoId, Long productoId) {
        Query<ItemCarrito> query = pm.newQuery(ItemCarrito.class);
        query.setFilter("carrito.id == carritoIdParam && producto.id == productoIdParam");
        query.declareParameters("Long carritoIdParam, Long productoIdParam");
        query.setUnique(true);
        
        try {
            return (ItemCarrito) query.execute(carritoId, productoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Actualiza la cantidad de un ítem
     * @param itemId ID del ítem
     * @param cantidad la nueva cantidad
     */
    public void actualizarCantidad(Long itemId, int cantidad) {
        if (cantidad <= 0) {
            deleteById(itemId);
            return;
        }
        
        ItemCarrito item = findById(itemId);
        if (item != null) {
            executeWithTransaction(() -> {
                item.setCantidad(cantidad);
            });
        }
    }
    
    /**
     * Elimina todos los ítems de un carrito
     * @param carritoId ID del carrito
     * @return número de ítems eliminados
     */
    public int eliminarItemsDeCarrito(Long carritoId) {
        List<ItemCarrito> items = findByCarrito(carritoId);
        int count = 0;
        
        for (ItemCarrito item : items) {
            delete(item);
            count++;
        }
        
        return count;
    }
    
    
    public ItemCarrito addOrUpdateItem(Long carritoId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        ItemCarrito item = findByCarritoYProducto(carritoId, productoId);
        
        if (item == null) {
            // Crear nuevo ítem
            item = new ItemCarrito();
            item.setCarrito(pm.getObjectById(com.iBai.ecommerce.model.Carrito.class, carritoId));
            item.setProducto(pm.getObjectById(com.iBai.ecommerce.model.Producto.class, productoId));
            item.setCantidad(cantidad);
            return save(item);
        } else {
            // Actualizar ítem existente
            return actualizarCantidadItem(item, cantidad);
        }
    }
    
    private ItemCarrito actualizarCantidadItem(ItemCarrito item, int cantidad) {
        executeWithTransaction(() -> {
            item.setCantidad(item.getCantidad() + cantidad);
        });
        return item;
    }
}