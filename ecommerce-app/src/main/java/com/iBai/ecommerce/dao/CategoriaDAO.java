package com.iBai.ecommerce.dao;

import com.iBai.ecommerce.model.Categoria;
import java.util.List;
import javax.jdo.Query;

/**
 * DAO para operaciones relacionadas con categorías.
 */
public class CategoriaDAO extends AbstractDAO<Categoria, Long> {
    
    public CategoriaDAO() {
        super(Categoria.class);
    }
    
    /**
     * Busca categorías por nombre
     * @param nombre el nombre a buscar
     * @return la categoría encontrada o null si no existe
     */
    public Categoria findByNombre(String nombre) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("nombre == nombreParam");
        query.declareParameters("String nombreParam");
        query.setUnique(true);
        
        try {
            return (Categoria) query.execute(nombre);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca categorías activas
     * @return lista de categorías activas
     */
    @SuppressWarnings("unchecked")
    public List<Categoria> findActivas() {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("activa == true");
        
        try {
            return (List<Categoria>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca subcategorías de una categoría padre
     * @param categoriaId ID de la categoría padre
     * @return lista de subcategorías
     */
    @SuppressWarnings("unchecked")
    public List<Categoria> findSubcategorias(Long categoriaId) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("categoriaPadre.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            return (List<Categoria>) query.execute(categoriaId);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca categorías principales (sin categoría padre)
     * @return lista de categorías principales
     */
    @SuppressWarnings("unchecked")
    public List<Categoria> findCategoriasPrincipales() {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("categoriaPadre == null");
        
        try {
            return (List<Categoria>) query.execute();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Busca categorías por término (búsqueda parcial en nombre o descripción)
     * @param termino el término a buscar
     * @return lista de categorías que coinciden con el criterio
     */
    @SuppressWarnings("unchecked")
    public List<Categoria> buscarPorTermino(String termino) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("nombre.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0 || " +
                       "(descripcion != null && descripcion.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0)");
        query.declareParameters("String terminoParam");
        
        try {
            return (List<Categoria>) query.execute(termino);
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Cambia el estado activo/inactivo de una categoría
     * @param categoriaId el ID de la categoría
     * @param activa el nuevo estado
     */
    public void cambiarEstadoActiva(Long categoriaId, boolean activa) {
        Categoria categoria = findById(categoriaId);
        if (categoria != null) {
            executeWithTransaction(() -> {
                categoria.setActiva(activa);
            });
        }
    }
}