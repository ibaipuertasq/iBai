package com.iBai.ecommerce.dao;


import com.iBai.ecommerce.model.ImagenProducto;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con imágenes de productos.
 */
public class ImagenProductoDAO extends AbstractDAO<ImagenProducto, Long> {
    
    public ImagenProductoDAO() {
        super(ImagenProducto.class);
    }
    
    /**
     * Busca imágenes por ID de producto
     * @param productoId ID del producto
     * @return lista de imágenes del producto
     */
    @SuppressWarnings("unchecked")
    public List<ImagenProducto> findByProducto(Long productoId) {
        Query<ImagenProducto> query = pm.newQuery(ImagenProducto.class);
        query.setFilter("producto.id == idParam");
        query.declareParameters("Long idParam");
        query.setOrdering("orden ASC");
        
        try {
            return (List<ImagenProducto>) query.execute(productoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca la imagen principal de un producto
     * @param productoId ID del producto
     * @return imagen principal o null si no existe
     */
    public ImagenProducto findImagenPrincipal(Long productoId) {
        Query<ImagenProducto> query = pm.newQuery(ImagenProducto.class);
        query.setFilter("producto.id == idParam && esPrincipal == true");
        query.declareParameters("Long idParam");
        query.setUnique(true);
        
        try {
            return (ImagenProducto) query.execute(productoId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Establece una imagen como principal y quita esta marca de las demás
     * @param imagenId ID de la imagen
     */
    public void establecerImagenPrincipal(Long imagenId) {
        ImagenProducto imagen = findById(imagenId);
        if (imagen != null) {
            executeWithTransaction(() -> {
                // Primero quitamos la marca principal de todas las imágenes del producto
                Query<ImagenProducto> query = pm.newQuery(ImagenProducto.class);
                query.setFilter("producto.id == productoIdParam && esPrincipal == true");
                query.declareParameters("Long productoIdParam");
                
                @SuppressWarnings("unchecked")
                List<ImagenProducto> imagenesPrincipales = (List<ImagenProducto>) query.execute(
                    imagen.getProducto().getId());
                
                for (ImagenProducto img : imagenesPrincipales) {
                    img.setEsPrincipal(false);
                }
                
                // Luego marcamos la nueva principal
                imagen.setEsPrincipal(true);
            });
        }
    }
    
    /**
     * Actualiza el orden de una imagen
     * @param imagenId ID de la imagen
     * @param nuevoOrden nuevo orden
     */
    public void actualizarOrden(Long imagenId, Integer nuevoOrden) {
        ImagenProducto imagen = findById(imagenId);
        if (imagen != null) {
            executeWithTransaction(() -> {
                imagen.setOrden(nuevoOrden);
            });
        }
    }
    
    /**
     * Elimina todas las imágenes de un producto
     * @param productoId ID del producto
     * @return número de imágenes eliminadas
     */
    public int eliminarImagenesDeProducto(Long productoId) {
        List<ImagenProducto> imagenes = findByProducto(productoId);
        int count = 0;
        
        for (ImagenProducto imagen : imagenes) {
            delete(imagen);
            count++;
        }
        
        return count;
    }
    
    /**
     * Reordena las imágenes de un producto para asegurar una secuencia consecutiva
     * @param productoId ID del producto
     */
    public void reordenarImagenes(Long productoId) {
        List<ImagenProducto> imagenes = findByProducto(productoId);
        if (imagenes != null && !imagenes.isEmpty()) {
            executeWithTransaction(() -> {
                int orden = 0;
                for (ImagenProducto imagen : imagenes) {
                    imagen.setOrden(orden++);
                }
            });
        }
    }
}