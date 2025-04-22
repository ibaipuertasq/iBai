package com.iBai.ecommerce.model;

import java.util.Date;
import javax.jdo.annotations.*;

/**
 * Entidad que representa una valoración de un producto realizada por un usuario.
 */
@PersistenceCapable(table = "valoracion")
@Unique(members={"producto", "usuario"})
public class Valoracion {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    
    @Column(name = "producto_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Producto producto;
    
    @Column(name = "usuario_id")
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Usuario usuario;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Byte puntuacion;
    
    @Persistent
    private String comentario;
    
    @Persistent(nullValue = NullValue.EXCEPTION)
    private Date fecha;
    
    public Valoracion() {
        this.fecha = new Date();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Byte getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Byte puntuacion) {
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Valoracion valoracion = (Valoracion) o;
        return id != null && id.equals(valoracion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}