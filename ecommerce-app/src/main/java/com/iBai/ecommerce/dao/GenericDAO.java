package com.iBai.ecommerce.dao;

import java.util.List;

/**
 * Interfaz genérica para operaciones básicas de acceso a datos.
 * @param <T> El tipo de entidad
 * @param <ID> El tipo de identificador único de la entidad
 */
public interface GenericDAO<T, ID> {
    /**
     * Busca una entidad por su ID
     * @param id identificador único
     * @return la entidad encontrada o null si no existe
     */
    T findById(ID id);
    
    /**
     * Recupera todas las entidades
     * @return lista de todas las entidades
     */
    List<T> findAll();
    
    /**
     * Persiste una nueva entidad
     * @param entity la entidad a guardar
     * @return la entidad persistida con su ID generado
     */
    T save(T entity);
    
    /**
     * Actualiza una entidad existente
     * @param entity la entidad con los cambios
     */
    void update(T entity);
    
    /**
     * Elimina una entidad
     * @param entity la entidad a eliminar
     */
    void delete(T entity);
    
    /**
     * Elimina una entidad por su ID
     * @param id el identificador único de la entidad a eliminar
     */
    void deleteById(ID id);
    
    /**
     * Cierra los recursos utilizados por el DAO
     */
    void close();
}