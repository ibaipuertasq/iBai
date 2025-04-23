package com.iBai.ecommerce.model;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidad que representa un producto en el e-commerce.
 */
@PersistenceCapable(table = "producto")
public class Producto {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombre;

    @Persistent
    private String descripcion;

    @Persistent(nullValue = NullValue.EXCEPTION, column = "codigoSku")
    private String codigoSku;

    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal precio;

    @Persistent(column = "precio_oferta")
    private BigDecimal precioOferta;

    @Persistent
    private Integer stock;

    @Persistent(column = "categoria_id")
    private Categoria categoria;

    @Persistent
    private Boolean destacado = false;

    @Persistent
    private Boolean activo = true;

    @Persistent(column = "fecha_creacion")
    private Date fechaCreacion;

    @Persistent(column = "ultima_actualizacion")
    private Date ultimaActualizacion;

    @Persistent
    private String marca;

    @Persistent
    private BigDecimal peso;

    @Persistent
    private String dimensiones;

    // Relación con ImagenProducto
    // Relación con ImagenProducto
    @Persistent(mappedBy = "producto")
    private List<ImagenProducto> imagenes = new ArrayList<>();

    public Producto() {
        // Convertir LocalDateTime a Date
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        this.ultimaActualizacion = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        this.stock = 0;
        this.activo = true;
        this.destacado = false;
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

    public BigDecimal getPrecioOferta() {
        return precioOferta;
    }

    public void setPrecioOferta(BigDecimal precioOferta) {
        this.precioOferta = precioOferta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(Date ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public List<ImagenProducto> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenProducto> imagenes) {
        this.imagenes = imagenes;
    }

    /**
     * Calcula el precio actual del producto considerando si hay oferta activa
     * @return precio actual del producto
     */
    public BigDecimal getPrecioActual() {
        return precioOferta != null ? precioOferta : precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id != null && id.equals(producto.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}