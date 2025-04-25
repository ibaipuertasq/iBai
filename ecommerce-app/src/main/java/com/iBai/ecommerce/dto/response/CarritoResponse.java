package com.iBai.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CarritoResponse {
    private Long id;
    private Long usuarioId;
    private String sessionId;
    private Date fechaCreacion;
    private List<ItemCarritoResponse> items;
    private BigDecimal subtotal;
    private Integer cantidadItems;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public List<ItemCarritoResponse> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCarritoResponse> items) {
        this.items = items;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public Integer getCantidadItems() {
        return cantidadItems;
    }
    
    public void setCantidadItems(Integer cantidadItems) {
        this.cantidadItems = cantidadItems;
    }
}