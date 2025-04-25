package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dto.request.CarritoRequest;
import com.iBai.ecommerce.dto.response.CarritoResponse;
import com.iBai.ecommerce.model.Carrito;
import com.iBai.ecommerce.model.ItemCarrito;
import com.iBai.ecommerce.model.Usuario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Carrito y sus DTOs
 */
public class CarritoMapper {
    
    private final ItemCarritoMapper itemCarritoMapper;
    
    public CarritoMapper() {
        this.itemCarritoMapper = new ItemCarritoMapper();
    }
    
    /**
     * Convierte un DTO de petición a una entidad Carrito
     * @param request el DTO de petición
     * @param usuario el usuario asociado al carrito (puede ser null)
     * @return la entidad Carrito
     */
    public Carrito toEntity(CarritoRequest request, Usuario usuario) {
        if (request == null) {
            return null;
        }
        
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setSessionId(request.getSessionId());
        carrito.setFechaCreacion(new Date());
        
        return carrito;
    }
    
    /**
     * Convierte una entidad Carrito a un DTO de respuesta
     * @param carrito la entidad Carrito
     * @param items los ítems del carrito
     * @return el DTO de respuesta
     */
    public CarritoResponse toResponse(Carrito carrito, List<ItemCarrito> items) {
        if (carrito == null) {
            return null;
        }
        
        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioId(carrito.getUsuario() != null ? carrito.getUsuario().getId() : null);
        response.setSessionId(carrito.getSessionId());
        response.setFechaCreacion(carrito.getFechaCreacion());
        
        // Convertir items
        List<com.iBai.ecommerce.dto.response.ItemCarritoResponse> itemsResponse = 
            items.stream().map(itemCarritoMapper::toResponse).collect(Collectors.toList());
        response.setItems(itemsResponse);
        
        // Calcular totales
        response.setCantidadItems(items.size());
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCarrito item : items) {
            BigDecimal precio = item.getProducto().getPrecioOferta() != null ? 
                item.getProducto().getPrecioOferta() : item.getProducto().getPrecio();
            subtotal = subtotal.add(precio.multiply(new BigDecimal(item.getCantidad())));
        }
        response.setSubtotal(subtotal);
        
        return response;
    }
    
    /**
     * Convierte una lista de entidades Carrito a DTOs de respuesta
     * @param carritos la lista de entidades
     * @return la lista de DTOs de respuesta
     */
    public List<CarritoResponse> toResponseList(List<Carrito> carritos) {
        if (carritos == null) {
            return null;
        }
        
        List<CarritoResponse> responses = new ArrayList<>();
        for (Carrito carrito : carritos) {
            // Aquí necesitaríamos los ítems del carrito, que normalmente vendrían de un DAO
            // Para simplificar, asumimos que los ítems están cargados en la entidad
            responses.add(toResponse(carrito, carrito.getItems()));
        }
        
        return responses;
    }
}
