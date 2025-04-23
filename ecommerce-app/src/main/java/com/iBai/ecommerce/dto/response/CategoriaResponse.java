package com.iBai.ecommerce.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para enviar datos de categorías en las respuestas
 */
public class CategoriaResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Boolean activa;
    private CategoriaResponse categoriaPadre;
    private List<CategoriaResponse> subcategorias = new ArrayList<>();
    
    // Constructores
    
    public CategoriaResponse() {
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
    
    public CategoriaResponse getCategoriaPadre() {
        return categoriaPadre;
    }
    
    public void setCategoriaPadre(CategoriaResponse categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
    }
    
    public List<CategoriaResponse> getSubcategorias() {
        return subcategorias;
    }
    
    public void setSubcategorias(List<CategoriaResponse> subcategorias) {
        this.subcategorias = subcategorias;
    }
    
    /**
     * Añade una subcategoría a la lista de subcategorías
     * @param subcategoria la subcategoría a añadir
     */
    public void addSubcategoria(CategoriaResponse subcategoria) {
        if (this.subcategorias == null) {
            this.subcategorias = new ArrayList<>();
        }
        this.subcategorias.add(subcategoria);
    }
}