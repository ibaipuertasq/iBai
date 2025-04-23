package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dto.response.ImagenProductoResponse;
import com.iBai.ecommerce.model.ImagenProducto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase para convertir entre entidades ImagenProducto y DTOs
 */
public class ImagenProductoMapper {
    
    /**
     * Convierte una entidad ImagenProducto a un DTO ImagenProductoResponse
     * @param imagen la entidad a convertir
     * @return el DTO resultante
     */
    public ImagenProductoResponse toResponse(ImagenProducto imagen) {
        if (imagen == null) {
            return null;
        }
        
        ImagenProductoResponse response = new ImagenProductoResponse();
        response.setId(imagen.getId());
        response.setUrl(imagen.getUrl());
        response.setEsPrincipal(imagen.getEsPrincipal());
        response.setOrden(imagen.getOrden());
        
        return response;
    }
    
    /**
     * Convierte una lista de entidades ImagenProducto a una lista de DTOs ImagenProductoResponse
     * @param imagenes las entidades a convertir
     * @return la lista de DTOs resultante
     */
    public List<ImagenProductoResponse> toResponseList(List<ImagenProducto> imagenes) {
        if (imagenes == null) {
            return null;
        }
        
        return imagenes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}