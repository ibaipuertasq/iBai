package com.iBai.ecommerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clase de configuración principal de la aplicación.
 * Carga las propiedades desde el archivo config.properties.
 */
public class AppConfig {
    private static final Logger logger = LogManager.getLogger(AppConfig.class);
    private static final Properties properties = new Properties();
    private static final AppConfig instance = new AppConfig();
    
    private AppConfig() {
        loadProperties();
    }
    
    /**
     * Carga las propiedades desde el archivo de configuración.
     */
    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuración cargada correctamente");
            } else {
                logger.error("No se pudo encontrar el archivo config.properties");
            }
        } catch (IOException e) {
            logger.error("Error al cargar el archivo de configuración", e);
        }
    }
    
    /**
     * Obtiene una instancia de la configuración.
     * @return Instancia de AppConfig
     */
    public static AppConfig getInstance() {
        return instance;
    }
    
    /**
     * Obtiene una propiedad por su clave.
     * @param key Clave de la propiedad
     * @return Valor de la propiedad
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Obtiene una propiedad por su clave o devuelve un valor por defecto.
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto
     * @return Valor de la propiedad o valor por defecto
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}