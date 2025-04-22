package com.iBai.ecommerce.dao;



import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * Implementación abstracta de la interfaz GenericDAO que proporciona
 * operaciones CRUD básicas para cualquier entidad.
 * 
 * @param <T> El tipo de entidad
 * @param <ID> El tipo de identificador único de la entidad
 */
public abstract class AbstractDAO<T, ID> implements GenericDAO<T, ID> {
    protected PersistenceManager pm;
    protected Class<T> entityClass;
    
    /**
     * Constructor que inicializa el PersistenceManager y la clase de entidad
     * @param entityClass la clase de la entidad a manipular
     */
    public AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.pm = PMF.getInstance().getPersistenceManager();
    }
    
    @Override
    public T findById(ID id) {
        try {
            return pm.getObjectById(entityClass, id);
        } catch (javax.jdo.JDOObjectNotFoundException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        Query<T> query = pm.newQuery(entityClass);
        try {
            return (List<T>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    @Override
    public T save(T entity) {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            T persistedEntity = pm.makePersistent(entity);
            tx.commit();
            return persistedEntity;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
    
    @Override
    public void update(T entity) {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.makePersistent(entity);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
    
    @Override
    public void delete(T entity) {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.deletePersistent(entity);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
    
    @Override
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }
    
    @Override
    public void close() {
        if (pm != null && !pm.isClosed()) {
            pm.close();
        }
    }
    
    /**
     * Ejecuta una operación dentro de una transacción
     * @param operation la operación a ejecutar
     */
    protected void executeWithTransaction(Runnable operation) {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            operation.run();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
}