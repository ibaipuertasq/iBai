package com.iBai.ecommerce.model;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un pedido realizado por un usuario.
 */
@PersistenceCapable(table = "pedido")
public class Pedido {
    
    public enum Estado {
        PENDIENTE, PAGADO, ENVIADO, ENTREGADO, CANCELADO
    }
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Usuario usuario;
    
    @Column(name = "codigo_referencia")
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Unique
    private String codigoReferencia;
    
    @Column(name = "fecha_pedido")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fechaPedido;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Column(jdbcType="VARCHAR", length=10)
    private Estado estado;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal subtotal;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal impuestos;
    
    @Column(name = "gastos_envio")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal gastosEnvio;
    
    @Persistent
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private BigDecimal total;
    
    @Column(name = "direccion_envio_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Direccion direccionEnvio;
    
    @Column(name = "direccion_facturacion_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Direccion direccionFacturacion;
    
    @Column(name = "metodo_pago_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private MetodoPago metodoPago;
    
    @Persistent
    private String notas;
    
    @Persistent(mappedBy = "pedido")
    private List<DetallePedido> detalles;
    
    public Pedido() {
        this.fechaPedido = new Date();
        this.estado = Estado.PENDIENTE;
        this.descuento = BigDecimal.ZERO;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getGastosEnvio() {
        return gastosEnvio;
    }

    public void setGastosEnvio(BigDecimal gastosEnvio) {
        this.gastosEnvio = gastosEnvio;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Direccion getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(Direccion direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public Direccion getDireccionFacturacion() {
        return direccionFacturacion;
    }

    public void setDireccionFacturacion(Direccion direccionFacturacion) {
        this.direccionFacturacion = direccionFacturacion;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id != null && id.equals(pedido.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}