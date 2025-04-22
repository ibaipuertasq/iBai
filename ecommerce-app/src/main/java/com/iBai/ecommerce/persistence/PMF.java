package com.iBai.ecommerce.persistence;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.util.Properties;

/**
 * Singleton que proporciona acceso al PersistenceManagerFactory.
 * Garantiza que solo exista una instancia en toda la aplicación.
 */
public class PMF {
    private static final PersistenceManagerFactory pmf;
    
    static {
        try {
            System.out.println("Inicializando PMF...");
            
            // Comprobar si el driver de MySQL está disponible
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Driver MySQL encontrado");
            } catch (ClassNotFoundException e) {
                System.err.println("Error: Driver MySQL no encontrado");
                e.printStackTrace();
            }
            
            // Usar propiedades explícitas para depuración
            Properties props = new Properties();
            props.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
            props.setProperty("javax.jdo.option.ConnectionDriverName", "com.mysql.cj.jdbc.Driver");
            props.setProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql://localhost:3306/ecommercedb?useSSL=false&serverTimezone=UTC");
            props.setProperty("javax.jdo.option.ConnectionUserName", "root");
            props.setProperty("javax.jdo.option.ConnectionPassword", "root");
            props.setProperty("datanucleus.schema.autoCreateAll", "false");
            
            System.out.println("Intentando crear PMF con propiedades explícitas...");
            pmf = JDOHelper.getPersistenceManagerFactory(props);
            System.out.println("PMF inicializado correctamente: " + pmf);
            
        } catch (Exception e) {
            System.err.println("Error inicializando PersistenceManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error inicializando PersistenceManagerFactory", e);
        }
    }
    
    // Constructor privado para prevenir instanciación
    private PMF() {}
    
    /**
     * Obtiene la instancia única del PersistenceManagerFactory
     * @return instancia de PersistenceManagerFactory
     */
    public static PersistenceManagerFactory getInstance() {
        return pmf;
    }
}