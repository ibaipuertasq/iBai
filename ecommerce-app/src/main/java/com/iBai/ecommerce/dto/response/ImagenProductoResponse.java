package com.iBai.ecommerce.dto.response;

/**
 * DTO para enviar datos de imágenes de productos en las respuestas
 */
public class ImagenProductoResponse {
    
    private Long id;
    private String url;
    private Boolean esPrincipal;
    private Integer orden;
    
    // Constructores
    
    public ImagenProductoResponse() {
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Boolean getEsPrincipal() {
        return esPrincipal;
    }
    
    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
    
    public Integer getOrden() {
        return orden;
    }
    
    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}