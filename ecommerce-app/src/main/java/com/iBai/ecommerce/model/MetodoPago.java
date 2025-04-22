package com.iBai.ecommerce.model;


import javax.jdo.annotations.*;

/**
 * Entidad que representa un método de pago asociado a un usuario.
 */
@PersistenceCapable(table = "metodo_pago")
public class MetodoPago {
    
    public enum Tipo {
        TARJETA, PAYPAL, TRANSFERENCIA
    }
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Usuario usuario;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Column(jdbcType="VARCHAR", length=15)
    private Tipo tipo;
    
    @Persistent
    private String titular;
    
    @Column(name = "numero_tarjeta")
    @Persistent
    private String numeroTarjeta;
    
    @Column(name = "fecha_expiracion")
    @Persistent
    private String fechaExpiracion;
    
    @Persistent
    private Boolean predeterminado = false;
    
    public MetodoPago() {
        this.predeterminado = false;
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

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getPredeterminado() {
        return predeterminado;
    }

    public void setPredeterminado(Boolean predeterminado) {
        this.predeterminado = predeterminado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetodoPago metodoPago = (MetodoPago) o;
        return id != null && id.equals(metodoPago.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}