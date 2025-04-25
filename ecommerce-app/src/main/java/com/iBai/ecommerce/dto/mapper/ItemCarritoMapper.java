package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dto.request.ItemCarritoRequest;
import com.iBai.ecommerce.dto.response.ItemCarritoResponse;
import com.iBai.ecommerce.model.Carrito;
import com.iBai.ecommerce.model.ItemCarrito;
import com.iBai.ecommerce.model.Producto;
import com.iBai.ecommerce.model.ImagenProducto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mapper para convertir entre entidades ItemCarrito y sus DTOs
 */
public class ItemCarritoMapper {
    
    /**
     * Convierte un DTO de petición a una entidad ItemCarrito
     * @param request el DTO de petición
     * @param carrito el carrito asociado
     * @param producto el producto asociado
     * @return la entidad ItemCarrito
     */
    public ItemCarrito toEntity(ItemCarritoRequest request, Carrito carrito, Producto producto) {
        if (request == null) {
            return null;
        }
        
        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(request.getCantidad());
        item.setFechaAgregado(new Date());
        
        return item;
    }
    
    /**
     * Convierte una entidad ItemCarrito a un DTO de respuesta
     * @param item la entidad ItemCarrito
     * @return el DTO de respuesta
     */
    public ItemCarritoResponse toResponse(ItemCarrito item) {
        if (item == null) {
            return null;
        }
        
        ItemCarritoResponse response = new ItemCarritoResponse();
        response.setId(item.getId());
        response.setCarritoId(item.getCarrito().getId());
        response.setProductoId(item.getProducto().getId());
        response.setNombreProducto(item.getProducto().getNombre());
        
        // Obtener URL de imagen principal si existe
        if (item.getProducto().getImagenes() != null && !item.getProducto().getImagenes().isEmpty()) {
            for (ImagenProducto imagen : item.getProducto().getImagenes()) {
                if (imagen.getEsPrincipal()) {
                    response.setImagenUrl(imagen.getUrl());
                    break;
                }
            }
            // Si no hay imagen principal, usar la primera disponible
            if (response.getImagenUrl() == null && !item.getProducto().getImagenes().isEmpty()) {
                response.setImagenUrl(item.getProducto().getImagenes().get(0).getUrl());
            }
        }
        
        // Usar precio de oferta si existe
        BigDecimal precioUnitario = item.getProducto().getPrecioOferta() != null ? 
            item.getProducto().getPrecioOferta() : item.getProducto().getPrecio();
        response.setPrecioUnitario(precioUnitario);
        
        response.setCantidad(item.getCantidad());
        response.setSubtotal(precioUnitario.multiply(new BigDecimal(item.getCantidad())));
        response.setFechaAgregado(item.getFechaAgregado());
        
        return response;
    }
    
    /**
     * Convierte una lista de entidades ItemCarrito a DTOs de respuesta
     * @param items la lista de entidades
     * @return la lista de DTOs de respuesta
     */
    public List<ItemCarritoResponse> toResponseList(List<ItemCarrito> items) {
        if (items == null) {
            return null;
        }
        
        List<ItemCarritoResponse> responses = new ArrayList<>();
        for (ItemCarrito item : items) {
            responses.add(toResponse(item));
        }
        
        return responses;
    }
}