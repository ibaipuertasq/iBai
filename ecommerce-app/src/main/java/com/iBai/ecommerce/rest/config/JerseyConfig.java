package com.iBai.ecommerce.rest.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;

/**
 * Configuración de la aplicación REST con Jersey
 */
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Registrar paquetes para escanear controladores REST
        packages("com.iBai.ecommerce.rest.controllers");
        
        // Registrar filtros
        packages("com.iBai.ecommerce.rest.filter");
        
        // Registrar manejador de excepciones
        packages("com.iBai.ecommerce.rest.exception");
        
        // Habilitar validación de bean
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }
}