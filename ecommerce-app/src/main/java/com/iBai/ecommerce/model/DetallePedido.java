package com.iBai.ecommerce.model;


import java.math.BigDecimal;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un ítem dentro de un pedido.
 */
@PersistenceCapable(table = "detalle_pedido")
public class DetallePedido {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Pedido pedido;
    
    @Column(name = "producto_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Producto producto;
    
    @Column(name = "nombre_producto")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombreProducto;
    
    @Column(name = "precio_unitario")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal precioUnitario;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Integer cantidad;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal subtotal;
    
    public DetallePedido() {
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    /**
     * Calcula el subtotal basado en el precio unitario y la cantidad
     */
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedido detalle = (DetallePedido) o;
        return id != null && id.equals(detalle.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}