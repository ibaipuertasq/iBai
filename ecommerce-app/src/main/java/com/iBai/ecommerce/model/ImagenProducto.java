package com.iBai.ecommerce.model;

import javax.jdo.annotations.*;

/**
 * Entidad que representa una imagen asociada a un producto.
 */
@PersistenceCapable(table = "imagen_producto")
public class ImagenProducto {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent(nullValue = NullValue.EXCEPTION, column = "producto_id")
    private Producto producto;

    @Persistent(nullValue = NullValue.EXCEPTION)
    private String url;

    @Persistent(column = "es_principal")
    private Boolean esPrincipal = false;

    @Persistent
    private Integer orden;

    public ImagenProducto() {
        this.esPrincipal = false;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImagenProducto imagen = (ImagenProducto) o;
        return id != null && id.equals(imagen.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}