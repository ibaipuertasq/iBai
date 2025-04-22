package com.iBai.ecommerce.model;


import java.util.Date;
import java.util.List;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un usuario en el sistema de e-commerce.
 */
@PersistenceCapable(table = "usuario")
public class Usuario {
    
    public enum Rol {
        CLIENTE, ADMIN
    }
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    @Unique
    private String email;
    
    @Column(name = "password_hash")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String passwordHash;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String nombre;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private String apellidos;
    
    @Persistent
    private String telefono;
    
    @Column(name = "fecha_registro")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fechaRegistro;
    
    @Persistent
    @Column(name = "ultima_conexion")
    private Date ultimaConexion;
    
    @Persistent
    private Boolean activo = true;
    
    @Persistent
    @Column(jdbcType="VARCHAR", length=8)
    private Rol rol = Rol.CLIENTE;
    
    @Persistent(mappedBy = "usuario")
    private List<Direccion> direcciones;
    
    @Persistent(mappedBy = "usuario")
    private List<MetodoPago> metodosPago;
    
    @Persistent(mappedBy = "usuario")
    private List<Pedido> pedidos;
    
    @Persistent(mappedBy = "usuario")
    private List<Valoracion> valoraciones;
    
    public Usuario() {
        this.fechaRegistro = new Date();
        this.activo = true;
        this.rol = Rol.CLIENTE;
    }
    
    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Date getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(Date ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    public List<MetodoPago> getMetodosPago() {
        return metodosPago;
    }

    public void setMetodosPago(List<MetodoPago> metodosPago) {
        this.metodosPago = metodosPago;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
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
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}