package com.iBai.ecommerce.dao;

import com.iBai.ecommerce.model.Categoria;
import com.iBai.ecommerce.model.Producto;

import java.util.ArrayList;
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
     * Busca categorías por nombre parcial
     * @param nombre el nombre parcial a buscar
     * @return lista de categorías que coinciden
     */
    @SuppressWarnings("unchecked")
    public List<Categoria> findByNombreParcial(String nombre) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("nombre.toLowerCase().indexOf(nombreParam.toLowerCase()) >= 0");
        query.declareParameters("String nombreParam");
        
        try {
            // Creamos una nueva lista y la poblamos con los resultados
            List<Categoria> resultados = new ArrayList<>();
            List<Categoria> queryResult = (List<Categoria>) query.execute(nombre);
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
        } finally {
            query.closeAll();
        }
    }
    @SuppressWarnings("unchecked")
    public List<Categoria> findActivas() {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("activa == true");
        
        try {
            List<Categoria> resultados = new ArrayList<>();
            List<Categoria> queryResult = (List<Categoria>) query.execute();
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
        } finally {
            query.closeAll();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Categoria> findSubcategorias(Long categoriaId) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("categoriaPadre.id == idParam");
        query.declareParameters("Long idParam");
        
        try {
            List<Categoria> resultados = new ArrayList<>();
            List<Categoria> queryResult = (List<Categoria>) query.execute(categoriaId);
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
        } finally {
            query.closeAll();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Categoria> findCategoriasPrincipales() {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("categoriaPadre == null");
        
        try {
            List<Categoria> resultados = new ArrayList<>();
            List<Categoria> queryResult = (List<Categoria>) query.execute();
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
        } finally {
            query.closeAll();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Categoria> buscarPorTermino(String termino) {
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("nombre.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0 || " +
                    "(descripcion != null && descripcion.toLowerCase().indexOf(terminoParam.toLowerCase()) >= 0)");
        query.declareParameters("String terminoParam");
        
        try {
            List<Categoria> resultados = new ArrayList<>();
            List<Categoria> queryResult = (List<Categoria>) query.execute(termino);
            if (queryResult != null) {
                resultados.addAll(queryResult);
            }
            return resultados;
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
    
    /**
     * Verifica si una categoría tiene subcategorías
     * @param categoriaId ID de la categoría
     * @return true si tiene subcategorías, false en caso contrario
     */
    public boolean tieneSubcategorias(Long categoriaId) {
        Categoria categoria = findById(categoriaId);
        if (categoria == null) {
            return false;
        }
        
        // Si las subcategorías están cargadas, verificamos directamente
        if (categoria.getSubcategorias() != null) {
            return !categoria.getSubcategorias().isEmpty();
        }
        
        // Si no están cargadas, hacemos una consulta
        Query<Categoria> query = pm.newQuery(Categoria.class);
        query.setFilter("categoriaPadre.id == idParam");
        query.declareParameters("Long idParam");
        query.setUnique(false);
        query.setRange(0, 1); // Solo necesitamos saber si hay al menos una
        
        try {
            @SuppressWarnings("unchecked")
            List<Categoria> result = (List<Categoria>) query.execute(categoriaId);
            return result != null && !result.isEmpty();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Verifica si una categoría tiene productos asociados
     * @param categoriaId ID de la categoría
     * @return true si tiene productos, false en caso contrario
     */
    public boolean tieneProductos(Long categoriaId) {
        Categoria categoria = findById(categoriaId);
        if (categoria == null) {
            return false;
        }
        
        // Si los productos están cargados, verificamos directamente
        if (categoria.getProductos() != null) {
            return !categoria.getProductos().isEmpty();
        }
        
        // Si no están cargados, hacemos una consulta
        Query<Producto> query = pm.newQuery(Producto.class);
        query.setFilter("categoria.id == idParam");
        query.declareParameters("Long idParam");
        query.setUnique(false);
        query.setRange(0, 1); // Solo necesitamos saber si hay al menos uno
        
        try {
            @SuppressWarnings("unchecked")
            List<Producto> result = (List<Producto>) query.execute(categoriaId);
            return result != null && !result.isEmpty();
        } finally {
            query.closeAll();
        }
    }
    
    /**
     * Obtiene la jerarquía completa de una categoría (ancestros)
     * @param categoriaId ID de la categoría
     * @return lista de categorías en la jerarquía, en orden de padre a hijo
     */
    public List<Categoria> getJerarquia(Long categoriaId) {
        Categoria categoria = findById(categoriaId);
        if (categoria == null) {
            return new ArrayList<>();
        }
        
        List<Categoria> jerarquia = new ArrayList<>();
        Categoria actual = categoria;
        
        while (actual != null) {
            jerarquia.add(0, actual); // Agregar al inicio para mantener orden de jerarquía
            actual = actual.getCategoriaPadre();
        }
        
        return jerarquia;
    }
}