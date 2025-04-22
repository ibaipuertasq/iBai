package com.iBai.ecommerce.model;


import java.util.Date;
import java.util.List;
import javax.jdo.annotations.*;

/**
 * Entidad que representa un carrito de compras, puede estar asociado a un usuario
 * o a una sesión de visitante.
 */
@PersistenceCapable(table = "carrito")
public class Carrito {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "usuario_id")
    @Persistent
    private Usuario usuario;
    
    @Column(name = "session_id")
    @Persistent
    private String sessionId;
    
    @Column(name = "fecha_creacion")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fechaCreacion;
    
    @Persistent(mappedBy = "carrito")
    private List<ItemCarrito> items;
    
    public Carrito() {
        this.fechaCreacion = new Date();
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrito carrito = (Carrito) o;
        return id != null && id.equals(carrito.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}