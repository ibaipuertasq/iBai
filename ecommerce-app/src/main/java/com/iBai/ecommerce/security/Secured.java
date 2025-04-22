package com.iBai.ecommerce.security;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar endpoints que requieren autenticación
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
    /**
     * Roles permitidos para acceder al recurso
     * Si no se especifica ningún rol, cualquier usuario autenticado puede acceder
     */
    String[] value() default {};
}