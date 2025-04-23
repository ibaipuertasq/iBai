package com.iBai.ecommerce.dao;

import com.iBai.ecommerce.model.ImagenProducto;
import java.util.ArrayList;
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
    public List<ImagenProducto> findByProductoId(Long productoId) {
        Query<ImagenProducto> query = pm.newQuery(ImagenProducto.class);
        query.setFilter("producto.id == idParam");
        query.declareParameters("Long idParam");
        query.setOrdering("esPrincipal desc, orden asc");
        
        try {
            List<ImagenProducto> resultados = new ArrayList<>();
            List<ImagenProducto> queryResult = (List<ImagenProducto>) query.execute(productoId);
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca la imagen principal de un producto
     * @param productoId ID del producto
     * @return la imagen principal o null si no existe
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
}