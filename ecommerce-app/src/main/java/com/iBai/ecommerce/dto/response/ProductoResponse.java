package com.iBai.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para enviar datos de productos en las respuestas
 */
public class ProductoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String codigoSku;
    private BigDecimal precio;
    private Integer stock;
    private CategoriaResponse categoria;
    private String imagen;
    private Boolean activo;
    private Date ultimaActualizacion;
    
    // Constructores
    
    public ProductoResponse() {
    }
    
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public CategoriaResponse getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaResponse categoria) {
        this.categoria = categoria;
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
    
    public Date getUltimaActualizacion() {
        return ultimaActualizacion;
    }
    
    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    /**
     * Verifica si el producto tiene stock disponible
     * @return true si hay stock disponible, false en caso contrario
     */
    public boolean isDisponible() {
        return stock != null && stock > 0 && activo;
    }
}