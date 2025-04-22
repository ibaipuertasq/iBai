package com.iBai.ecommerce.model;


import java.util.List;
import javax.jdo.annotations.*;

/**
 * Entidad que representa una categoría de productos en el e-commerce.
 */
@PersistenceCapable(table = "categoria")
public class Categoria {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombre;
    
    @Persistent
    private String descripcion;
    
    @Column(name = "categoria_padre_id")
    @Persistent
    private Categoria categoriaPadre;
    
    @Persistent
    private String imagen;
    
    @Persistent
    private Boolean activa = true;
    
    @Persistent(mappedBy = "categoriaPadre")
    private List<Categoria> subcategorias;
    
    @Persistent(mappedBy = "categoria")
    private List<Producto> productos;
    
    public Categoria() {
        this.activa = true;
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

    public Categoria getCategoriaPadre() {
        return categoriaPadre;
    }

    public void setCategoriaPadre(Categoria categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
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

    public List<Categoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<Categoria> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id != null && id.equals(categoria.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}