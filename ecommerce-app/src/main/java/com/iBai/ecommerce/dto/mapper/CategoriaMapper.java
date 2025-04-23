package com.iBai.ecommerce.dto.mapper;

import com.iBai.ecommerce.dto.request.CategoriaRequest;
import com.iBai.ecommerce.dto.response.CategoriaResponse;
import com.iBai.ecommerce.model.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase para convertir entre entidades Categoria y DTOs
 */
public class CategoriaMapper {
    
    /**
     * Convierte una entidad Categoria a un DTO CategoriaResponse completo
     * incluyendo referencias a subcategorías
     * @param categoria la entidad a convertir
     * @return el DTO resultante
     */
    public CategoriaResponse toResponse(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setImagen(categoria.getImagen());
        response.setActiva(categoria.getActiva());
        
        // Establecer la categoría padre si existe
        if (categoria.getCategoriaPadre() != null) {
            response.setCategoriaPadre(toResponseSimple(categoria.getCategoriaPadre()));
        }
        
        // Convertir subcategorías si existen
        if (categoria.getSubcategorias() != null && !categoria.getSubcategorias().isEmpty()) {
            List<CategoriaResponse> subcategorias = categoria.getSubcategorias().stream()
                    .filter(c -> c.getActiva())
                    .map(this::toResponseSimple) // Usamos toResponseSimple para evitar recursión infinita
                    .collect(Collectors.toList());
            response.setSubcategorias(subcategorias);
        } else {
            response.setSubcategorias(new ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * Convierte una entidad Categoria a un DTO CategoriaResponse simple
     * sin incluir subcategorías para evitar recursión infinita
     * @param categoria la entidad a convertir
     * @return el DTO resultante
     */
    public CategoriaResponse toResponseSimple(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        
        CategoriaResponse response = new CategoriaResponse();
        response.setId(categoria.getId());
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());
        response.setImagen(categoria.getImagen());
        response.setActiva(categoria.getActiva());
        
        // No incluimos subcategorías ni categoría padre para evitar recursión
        response.setSubcategorias(new ArrayList<>());
        
        return response;
    }
    
    /**
     * Convierte una lista de entidades Categoria a una lista de DTOs CategoriaResponse
     * @param categorias las entidades a convertir
     * @return la lista de DTOs resultante
     */
    public List<CategoriaResponse> toResponseList(List<Categoria> categorias) {
        if (categorias == null) {
            return null;
        }
        
        return categorias.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte un DTO CategoriaRequest a una entidad Categoria (para creación)
     * @param request el DTO a convertir
     * @return la entidad resultante
     */
    public Categoria toEntity(CategoriaRequest request) {
        if (request == null) {
            return null;
        }
        
        Categoria categoria = new Categoria();
        updateEntityFromRequest(categoria, request);
        
        return categoria;
    }
    
    /**
     * Actualiza una entidad Categoria con los datos de un DTO CategoriaRequest
     * @param categoria la entidad a actualizar
     * @param request el DTO con los datos nuevos
     */
    public void updateEntityFromRequest(Categoria categoria, CategoriaRequest request) {
        if (categoria == null || request == null) {
            return;
        }
        
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setImagen(request.getImagen());
        
        if (request.getActiva() != null) {
            categoria.setActiva(request.getActiva());
        }
    }
    
    /**
     * Establece la categoría padre de una categoría
     * @param categoria la categoría a actualizar
     * @param categoriaPadre la categoría padre a establecer
     */
    public void setCategoriaPadre(Categoria categoria, Categoria categoriaPadre) {
        if (categoria != null) {
            categoria.setCategoriaPadre(categoriaPadre);
        }
    }
}