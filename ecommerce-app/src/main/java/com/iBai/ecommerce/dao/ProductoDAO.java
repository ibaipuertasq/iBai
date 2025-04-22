package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.Categoria;
import com.iBai.ecommerce.model.Producto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con productos.
 */
public class ProductoDAO extends AbstractDAO<Producto, Long> {
    
    public ProductoDAO() {
        super(Producto.class);
    }
    
    /**
     * Busca un producto por su código SKU
     * @param codigoSku el código SKU a buscar
     * @return el producto encontrado o null si no existe
     */
    public Producto findByCodigoSku(String codigoSku) {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("codigoSku == codigoParam");
        query.declareParameters("String codigoParam");
        query.setUnique(true);
        
        try {
            return (Producto) query.execute(codigoSku);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos por categoría
     * @param categoriaId ID de la categoría
     * @return lista de productos de la categoría
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findByCategoria(Long categoriaId) {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("categoria.id == idParam && activo == true");
        query.declareParameters("Long idParam");
        
        try {
            return (List<Producto>) query.execute(categoriaId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos por rango de precios
     * @param precioMin precio mínimo
     * @param precioMax precio máximo
     * @return lista de productos en el rango de precios
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findByRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("(precioOferta != null && precioOferta >= precioMinParam && precioOferta <= precioMaxParam) || " +
                       "(precioOferta == null && precio >= precioMinParam && precio <= precioMaxParam)");
        query.declareParameters("java.math.BigDecimal precioMinParam, java.math.BigDecimal precioMaxParam");
        
        try {
            return (List<Producto>) query.execute(precioMin, precioMax);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos destacados
     * @return lista de productos destacados
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findDestacados() {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("destacado == true && activo == true");
        
        try {
            return (List<Producto>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos con stock disponible
     * @return lista de productos con stock
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findConStock() {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("stock > 0 && activo == true");
        
        try {
            return (List<Producto>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos sin stock
     * @return lista de productos sin stock
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findSinStock() {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("stock <= 0 && activo == true");
        
        try {
            return (List<Producto>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos por término (búsqueda parcial en nombre o descripción)
     * @param termino el término a buscar
     * @return lista de productos que coinciden con el criterio
     */
    @SuppressWarnings("unchecked")
    public List<Producto> buscarPorTermino(String termino) {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("(nombre.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0 || " +
                       "descripcion.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0) && activo == true");
        query.declareParameters("String terminoParam");
        
        try {
            return (List<Producto>) query.execute(termino);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca productos actualizados en un rango de fechas
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return lista de productos actualizados en el rango
     */
    @SuppressWarnings("unchecked")
    public List<Producto> findByFechaActualizacion(Date fechaInicio, Date fechaFin) {
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("ultimaActualizacion >= fechaInicioParam && ultimaActualizacion <= fechaFinParam");
        query.declareParameters("java.util.Date fechaInicioParam, java.util.Date fechaFinParam");
        
        try {
            return (List<Producto>) query.execute(fechaInicio, fechaFin);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Actualiza el stock de un producto
     * @param productoId ID del producto
     * @param cantidad la cantidad a añadir (negativa para restar)
     */
    public void actualizarStock(Long productoId, int cantidad) {
        Producto producto = findById(productoId);
        if (producto != null) {
            executeWithTransaction(() -> {
                int nuevoStock = producto.getStock() + cantidad;
                producto.setStock(Math.max(0, nuevoStock)); // Evitar stock negativo
                producto.setUltimaActualizacion(new Date());
            });
        }
    }
    
    /**
     * Cambia el estado activo/inactivo de un producto
     * @param productoId ID del producto
     * @param activo el nuevo estado
     */
    public void cambiarEstadoActivo(Long productoId, boolean activo) {
        Producto producto = findById(productoId);
        if (producto != null) {
            executeWithTransaction(() -> {
                producto.setActivo(activo);
                producto.setUltimaActualizacion(new Date());
            });
        }
    }
    
    /**
     * Cambia el estado destacado de un producto
     * @param productoId ID del producto
     * @param destacado el nuevo estado destacado
     */
    public void cambiarEstadoDestacado(Long productoId, boolean destacado) {
        Producto producto = findById(productoId);
        if (producto != null) {
            executeWithTransaction(() -> {
                producto.setDestacado(destacado);
                producto.setUltimaActualizacion(new Date());
            });
        }
    }
    
    /**
     * Actualiza el precio de un producto
     * @param productoId ID del producto
     * @param nuevoPrecio el nuevo precio
     */
    public void actualizarPrecio(Long productoId, BigDecimal nuevoPrecio) {
        Producto producto = findById(productoId);
        if (producto != null && nuevoPrecio.compareTo(BigDecimal.ZERO) > 0) {
            executeWithTransaction(() -> {
                producto.setPrecio(nuevoPrecio);
                producto.setUltimaActualizacion(new Date());
            });
        }
    }
    
    /**
     * Actualiza el precio de oferta de un producto
     * @param productoId ID del producto
     * @param precioOferta el nuevo precio de oferta (null para quitar oferta)
     */
    public void actualizarPrecioOferta(Long productoId, BigDecimal precioOferta) {
        Producto producto = findById(productoId);
        if (producto != null) {
            executeWithTransaction(() -> {
                producto.setPrecioOferta(precioOferta);
                producto.setUltimaActualizacion(new Date());
            });
        }
    }
}
