package com.iBai.ecommerce.dto.request;

import java.math.BigDecimal;

/**
 * DTO para recibir datos de productos en las peticiones
 */
public class ProductoRequest {
    
    private String nombre;
    private String descripcion;
    private String codigoSku;
    private BigDecimal precio;
    private Integer stock;
    private Long categoriaId;
    private String imagen;
    private Boolean activo;
    
    // Constructores
    
    public ProductoRequest() {
    }

    // Getters y setters
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getCodigoSku() {
        return codigoSku;
    }
    
    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}