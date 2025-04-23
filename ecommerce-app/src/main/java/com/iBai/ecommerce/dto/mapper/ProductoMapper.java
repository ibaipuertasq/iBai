package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dao.ImagenProductoDAO;
import com.iBai.ecommerce.dto.request.ProductoRequest;
import com.iBai.ecommerce.dto.response.ProductoResponse;
import com.iBai.ecommerce.model.Producto;
import com.iBai.ecommerce.model.Categoria;
import com.iBai.ecommerce.model.ImagenProducto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase para convertir entre entidades Producto y DTOs
 */
public class ProductoMapper {
    
    private final CategoriaMapper categoriaMapper;
    
    public ProductoMapper() {
        this.categoriaMapper = new CategoriaMapper();
    }
    
    /**
 * Convierte una entidad Producto a un DTO ProductoResponse
 * @param producto la entidad a convertir
 * @return el DTO resultante
 */
    public ProductoResponse toResponse(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setCodigoSku(producto.getCodigoSku());
        response.setPrecio(producto.getPrecio());
        //response.setPrecioOferta(producto.getPrecioOferta());
        response.setStock(producto.getStock());
        response.setActivo(producto.getActivo());
        response.setUltimaActualizacion(producto.getUltimaActualizacion());
        
        // En lugar de acceder directamente a la colección imagenes, usamos el DAO
        try {
            ImagenProductoDAO imagenDAO = new ImagenProductoDAO();
            List<ImagenProducto> imagenes = imagenDAO.findByProductoId(producto.getId());
            
            if (imagenes != null && !imagenes.isEmpty()) {
                // Buscar la imagen principal
                for (ImagenProducto img : imagenes) {
                    if (img.getEsPrincipal()) {
                        response.setImagen(img.getUrl());
                        break;
                    }
                }
                
                // Si no hay imagen principal, usar la primera
                if (response.getImagen() == null && !imagenes.isEmpty()) {
                    response.setImagen(imagenes.get(0).getUrl());
                }
            }
        } catch (Exception e) {
            // Log error but continue
            System.err.println("Error al cargar imágenes para producto ID: " + producto.getId() + " - " + e.getMessage());
        }

        // Mapear la categoría si existe
        if (producto.getCategoria() != null) {
            response.setCategoria(categoriaMapper.toResponseSimple(producto.getCategoria()));
        }
        
        return response;
    }
        
    /**
     * Convierte una lista de entidades Producto a una lista de DTOs ProductoResponse
     * @param productos las entidades a convertir
     * @return la lista de DTOs resultante
     */
    public List<ProductoResponse> toResponseList(List<Producto> productos) {
        if (productos == null) {
            return null;
        }
        
        return productos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte un DTO ProductoRequest a una entidad Producto (para creación)
     * @param request el DTO a convertir
     * @return la entidad resultante
     */
    public Producto toEntity(ProductoRequest request) {
        if (request == null) {
            return null;
        }
        
        Producto producto = new Producto();
        updateEntityFromRequest(producto, request);
        
        return producto;
    }
    
    /**
     * Actualiza una entidad Producto con los datos de un DTO ProductoRequest
     * @param producto la entidad a actualizar
     * @param request el DTO con los datos nuevos
     */
    public void updateEntityFromRequest(Producto producto, ProductoRequest request) {
        if (producto == null || request == null) {
            return;
        }
        
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCodigoSku(request.getCodigoSku());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        
        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }
    }
    
    /**
     * Establece la categoría de un producto
     * @param producto el producto a actualizar
     * @param categoria la categoría a establecer
     */
    public void setCategoria(Producto producto, Categoria categoria) {
        if (producto != null) {
            producto.setCategoria(categoria);
        }
    }
}