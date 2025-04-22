package com.iBai.ecommerce.persistence;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Singleton que proporciona acceso al PersistenceManagerFactory.
 * Garantiza que solo exista una instancia en toda la aplicación.
 */
public class PMF {
    private static final PersistenceManagerFactory pmf;
    
    static {
        try {
            // Inicializa el PersistenceManagerFactory usando la unidad de persistencia definida en persistence.xml
            pmf = JDOHelper.getPersistenceManagerFactory("ecommerce-pu");
        } catch (Exception e) {
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
