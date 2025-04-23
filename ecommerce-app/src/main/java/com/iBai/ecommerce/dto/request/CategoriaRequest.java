package com.iBai.ecommerce.dto.request;

/**
 * DTO para recibir datos de categorías en las peticiones
 */
public class CategoriaRequest {
    
    private String nombre;
    private String descripcion;
    private Long categoriaPadreId;
    private String imagen;
    private Boolean activa;
    
    // Constructores
    
    public CategoriaRequest() {
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
    
    public Long getCategoriaPadreId() {
        return categoriaPadreId;
    }
    
    public void setCategoriaPadreId(Long categoriaPadreId) {
        this.categoriaPadreId = categoriaPadreId;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    public Boolean getActiva() {
        return activa;
    }
    
    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}