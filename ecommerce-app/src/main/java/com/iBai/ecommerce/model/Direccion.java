package com.iBai.ecommerce.model;


import javax.jdo.annotations.*;

/**
 * Entidad que representa una dirección asociada a un usuario.
 */
@PersistenceCapable(table = "direccion")
public class Direccion {
    
    public enum Tipo {
        ENVIO, FACTURACION
    }
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Usuario usuario;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Column(jdbcType="VARCHAR", length=10)
    private Tipo tipo;
    
    @Column(name = "nombre_completo")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombreCompleto;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String calle;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String ciudad;
    
    @Persistent
    private String estado;
    
    @Column(name = "codigo_postal")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String codigoPostal;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String pais;
    
    @Persistent
    private String telefono;
    
    @Persistent
    private Boolean predeterminada = false;
    
    public Direccion() {
        this.predeterminada = false;
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

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getPredeterminada() {
        return predeterminada;
    }

    public void setPredeterminada(Boolean predeterminada) {
        this.predeterminada = predeterminada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direccion direccion = (Direccion) o;
        return id != null && id.equals(direccion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}