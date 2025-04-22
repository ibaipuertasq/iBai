package com.iBai.ecommerce.model;




import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un producto en el sistema de e-commerce.
 */
@PersistenceCapable(table = "producto")
public class Producto {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_sku")
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Unique
    private String codigoSku;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombre;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String descripcion;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal precio;
    
    @Column(name = "precio_oferta")
    @Persistent
    private BigDecimal precioOferta;
    
    @Persistent
    private Integer stock = 0;
    
    @Column(name = "categoria_id")
    @Persistent
    private Categoria categoria;
    
    @Column(name = "fecha_creacion")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fechaCreacion;
    
    @Column(name = "ultima_actualizacion")
    @Persistent
    private Date ultimaActualizacion;
    
    @Persistent
    private Boolean activo = true;
    
    @Persistent
    private Boolean destacado = false;
    
    @Persistent
    private BigDecimal peso;
    
    @Persistent
    private String dimensiones;
    
    @Persistent(mappedBy = "producto")
    private List<ImagenProducto> imagenes;
    
    @Persistent(mappedBy = "producto")
    private List<Valoracion> valoraciones;
    
    public Producto() {
        this.fechaCreacion = new Date();
        this.activo = true;
        this.destacado = false;
        this.stock = 0;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoSku() {
        return codigoSku;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
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

    public List<Valoracion> getValoraciones() {
        return valoraciones;
    }

    public void setValoraciones(List<Valoracion> valoraciones) {
        this.valoraciones = valoraciones;
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