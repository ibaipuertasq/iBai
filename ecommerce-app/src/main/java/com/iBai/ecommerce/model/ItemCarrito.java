package com.iBai.ecommerce.model;


import java.util.Date;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un ítem dentro de un carrito de compras.
 */
@PersistenceCapable(table = "item_carrito")
@Unique(members={"carrito", "producto"})
public class ItemCarrito {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "carrito_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Carrito carrito;
    
    @Column(name = "producto_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Producto producto;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Integer cantidad;
    
    @Column(name = "fecha_agregado")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fechaAgregado;
    
    public ItemCarrito() {
        this.fechaAgregado = new Date();
        this.cantidad = 1;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.cantidad = cantidad;
    }

    public Date getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(Date fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrito item = (ItemCarrito) o;
        return id != null && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}