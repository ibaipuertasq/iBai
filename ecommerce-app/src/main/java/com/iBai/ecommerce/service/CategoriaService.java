package com.iBai.ecommerce.service;

import com.iBai.ecommerce.dao.CategoriaDAO;
import com.iBai.ecommerce.dao.ProductoDAO;
import com.iBai.ecommerce.model.Categoria;
import com.iBai.ecommerce.dto.mapper.CategoriaMapper;
import com.iBai.ecommerce.dto.request.CategoriaRequest;
import com.iBai.ecommerce.dto.response.CategoriaResponse;
import com.iBai.ecommerce.rest.exception.ResourceNotFoundException;
import com.iBai.ecommerce.rest.exception.BusinessException;

import java.util.List;

/**
 * Servicio para la gestión de categorías
 */
public class CategoriaService {
    
    private final CategoriaDAO categoriaDAO;
    private final ProductoDAO productoDAO;
    private final CategoriaMapper categoriaMapper;
    
    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
        this.productoDAO = new ProductoDAO();
        this.categoriaMapper = new CategoriaMapper();
    }
    
    /**
     * Constructor para pruebas
     */
    public CategoriaService(CategoriaDAO categoriaDAO, ProductoDAO productoDAO, CategoriaMapper categoriaMapper) {
        this.categoriaDAO = categoriaDAO;
        this.productoDAO = productoDAO;
        this.categoriaMapper = categoriaMapper;
    }
    
    /**
     * Obtiene todas las categorías
     * @return lista de categorías
     */
    public List<CategoriaResponse> getAllCategorias() {
        List<Categoria> categorias = categoriaDAO.findAll();
        return categoriaMapper.toResponseList(categorias);
    }
    
    /**
     * Obtiene todas las categorías activas
     * @return lista de categorías activas
     */
    public List<CategoriaResponse> getCategoriasActivas() {
        List<Categoria> categorias = categoriaDAO.findActivas();
        return categoriaMapper.toResponseList(categorias);
    }
    
    /**
     * Obtiene todas las categorías principales (sin categoría padre)
     * @return lista de categorías principales
     */
    public List<CategoriaResponse> getCategoriasPrincipales() {
        List<Categoria> categorias = categoriaDAO.findCategoriasPrincipales();
        return categoriaMapper.toResponseList(categorias);
    }
    
    /**
     * Obtiene una categoría por su ID
     * @param id identificador de la categoría
     * @return la categoría
     * @throws ResourceNotFoundException si no existe
     */
    public CategoriaResponse getCategoriaById(Long id) {
        Categoria categoria = categoriaDAO.findById(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        return categoriaMapper.toResponse(categoria);
    }
    
    /**
     * Obtiene las subcategorías de una categoría
     * @param categoriaId ID de la categoría padre
     * @return lista de subcategorías
     */
    public List<CategoriaResponse> getSubcategorias(Long categoriaId) {
        List<Categoria> subcategorias = categoriaDAO.findSubcategorias(categoriaId);
        return categoriaMapper.toResponseList(subcategorias);
    }
    
    /**
     * Obtiene la jerarquía completa de una categoría (ancestros)
     * @param categoriaId ID de la categoría
     * @return lista de categorías en la jerarquía, en orden de padre a hijo
     */
    public List<CategoriaResponse> getJerarquia(Long categoriaId) {
        List<Categoria> jerarquia = categoriaDAO.getJerarquia(categoriaId);
        return categoriaMapper.toResponseList(jerarquia);
    }
    
    /**
     * Busca categorías por nombre (búsqueda parcial)
     * @param nombre el nombre o parte del nombre a buscar
     * @return lista de categorías que coinciden
     */
    public List<CategoriaResponse> buscarCategoriasPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El término de búsqueda no puede estar vacío");
        }
        
        List<Categoria> categorias = categoriaDAO.findByNombreParcial(nombre);
        return categoriaMapper.toResponseList(categorias);
    }
    
    /**
     * Crea una nueva categoría
     * @param categoriaRequest datos de la categoría
     * @return la categoría creada
     */
    public CategoriaResponse createCategoria(CategoriaRequest categoriaRequest) {
        validarCategoriaRequest(categoriaRequest);
        
        // Obtener la categoría padre si se especificó
        Categoria categoriaPadre = null;
        if (categoriaRequest.getCategoriaPadreId() != null) {
            categoriaPadre = categoriaDAO.findById(categoriaRequest.getCategoriaPadreId());
            if (categoriaPadre == null) {
                throw new ResourceNotFoundException("Categoría padre no encontrada con ID: " + categoriaRequest.getCategoriaPadreId());
            }
        }
        
        // Crear la categoría
        Categoria categoria = categoriaMapper.toEntity(categoriaRequest);
        categoria.setCategoriaPadre(categoriaPadre);
        
        // Por defecto, las nuevas categorías están activas si no se especifica
        if (categoria.getActiva() == null) {
            categoria.setActiva(true);
        }
        
        // Persistir
        categoria = categoriaDAO.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }
    
    /**
     * Actualiza una categoría existente
     * @param id identificador de la categoría
     * @param categoriaRequest datos actualizados
     * @return la categoría actualizada
     */
    public CategoriaResponse updateCategoria(Long id, CategoriaRequest categoriaRequest) {
        validarCategoriaRequest(categoriaRequest);
        
        // Verificar que la categoría exista
        Categoria categoria = categoriaDAO.findById(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        
        // Verificar que no se esté creando un ciclo en la jerarquía
        if (categoriaRequest.getCategoriaPadreId() != null) {
            if (categoriaRequest.getCategoriaPadreId().equals(id)) {
                throw new BusinessException("Una categoría no puede ser su propia categoría padre");
            }
            
            // Verificar que la categoría padre existe
            Categoria categoriaPadre = categoriaDAO.findById(categoriaRequest.getCategoriaPadreId());
            if (categoriaPadre == null) {
                throw new ResourceNotFoundException("Categoría padre no encontrada con ID: " + categoriaRequest.getCategoriaPadreId());
            }
            
            // Verificar que no haya un ciclo en la jerarquía
            Categoria parent = categoriaPadre;
            while (parent != null) {
                if (parent.getId().equals(id)) {
                    throw new BusinessException("No se puede establecer una jerarquía cíclica de categorías");
                }
                parent = parent.getCategoriaPadre();
            }
            
            categoria.setCategoriaPadre(categoriaPadre);
        } else {
            categoria.setCategoriaPadre(null);
        }
        
        // Actualizar los datos
        categoriaMapper.updateEntityFromRequest(categoria, categoriaRequest);
        
        // Persistir
        categoriaDAO.update(categoria);
        return categoriaMapper.toResponse(categoria);
    }
    
    /**
     * Cambia el estado activo/inactivo de una categoría
     * @param id identificador de la categoría
     * @param activa nuevo estado
     * @return la categoría actualizada
     */
    public CategoriaResponse cambiarEstadoActivo(Long id, boolean activa) {
        Categoria categoria = categoriaDAO.findById(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        
        categoria.setActiva(activa);
        categoriaDAO.update(categoria);
        return categoriaMapper.toResponse(categoria);
    }
    
    /**
     * Elimina una categoría
     * @param id identificador de la categoría
     */
    public void deleteCategoria(Long id) {
        Categoria categoria = categoriaDAO.findById(id);
        if (categoria == null) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        
        // Verificar que no tenga subcategorías
        if (categoriaDAO.tieneSubcategorias(id)) {
            throw new BusinessException("No se puede eliminar una categoría que tiene subcategorías");
        }
        
        // Verificar que no tenga productos
        if (categoriaDAO.tieneProductos(id)) {
            throw new BusinessException("No se puede eliminar una categoría que tiene productos asociados");
        }
        
        categoriaDAO.deleteById(id);
    }
    
    /**
     * Valida los datos de la categoría
     * @param categoriaRequest datos a validar
     * @throws BusinessException si hay datos inválidos
     */
    private void validarCategoriaRequest(CategoriaRequest categoriaRequest) {
        if (categoriaRequest.getNombre() == null || categoriaRequest.getNombre().trim().isEmpty()) {
            throw new BusinessException("El nombre de la categoría es obligatorio");
        }
    }
    
    /**
     * Cierra los recursos del servicio
     */
    public void close() {
        if (categoriaDAO != null) {
            categoriaDAO.close();
        }
        if (productoDAO != null) {
            productoDAO.close();
        }
    }
}