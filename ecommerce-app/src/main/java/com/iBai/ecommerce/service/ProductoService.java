package com.iBai.ecommerce.service;

import com.iBai.ecommerce.dao.ProductoDAO;
import com.iBai.ecommerce.dao.CategoriaDAO;
import com.iBai.ecommerce.model.Producto;
import com.iBai.ecommerce.model.Categoria;
import com.iBai.ecommerce.dto.mapper.ProductoMapper;
import com.iBai.ecommerce.dto.request.ProductoRequest;
import com.iBai.ecommerce.dto.response.ProductoResponse;
import com.iBai.ecommerce.rest.exception.ResourceNotFoundException;
import com.iBai.ecommerce.rest.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Servicio para la gestión de productos
 */
public class ProductoService {
    
    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProductoMapper productoMapper;
    
    public ProductoService() {
        this.productoDAO = new ProductoDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.productoMapper = new ProductoMapper();
    }
    
    /**
     * Constructor para pruebas
     */
    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO, ProductoMapper productoMapper) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
        this.productoMapper = productoMapper;
    }
    
    /**
     * Obtiene todos los productos activos
     * @return lista de productos
     */
    public List<ProductoResponse> getAllProductos() {
        List<Producto> productos = productoDAO.findAll();
        return productoMapper.toResponseList(productos);
    }
    
    /**
     * Obtiene un producto por su ID
     * @param id identificador del producto
     * @return el producto
     * @throws ResourceNotFoundException si no existe
     */
    public ProductoResponse getProductoById(Long id) {
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Busca un producto por su código SKU
     * @param codigoSku código único del producto
     * @return el producto
     * @throws ResourceNotFoundException si no existe
     */
    public ProductoResponse getProductoBySku(String codigoSku) {
        Producto producto = productoDAO.findByCodigoSku(codigoSku);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con SKU: " + codigoSku);
        }
        return productoMapper.toResponse(producto);
    }
    
    /**
 * Busca productos por categoría incluidas subcategorías
 * @param categoriaId ID de la categoría
 * @return lista de productos de la categoría y subcategorías
 */
    /**
 * Busca productos por categoría (incluye subcategorías)
 * @param categoriaId ID de la categoría
 * @return lista de productos de la categoría y sus subcategorías
 */
public List<ProductoResponse> getProductosByCategoria(Long categoriaId) {
    Categoria categoria = categoriaDAO.findById(categoriaId);
    if (categoria == null) {
        throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId);
    }
    
    List<Producto> productos = productoDAO.findByCategoriaRecursivo(categoriaId);
    return productoMapper.toResponseList(productos);
}
    
    /**
     * Busca productos por rango de precios
     * @param min precio mínimo
     * @param max precio máximo
     * @return lista de productos en el rango de precios
     */
    public List<ProductoResponse> getProductosByRangoPrecio(BigDecimal min, BigDecimal max) {
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(min) < 0) {
            throw new BusinessException("Rango de precios inválido");
        }
        
        List<Producto> productos = productoDAO.findByRangoPrecio(min, max);
        return productoMapper.toResponseList(productos);
    }
    
    /**
     * Busca productos con stock disponible
     * @return lista de productos con stock
     */
    public List<ProductoResponse> getProductosConStock() {
        List<Producto> productos = productoDAO.findConStock();
        return productoMapper.toResponseList(productos);
    }
    
    /**
     * Busca productos por un término (en nombre o descripción)
     * @param termino texto a buscar
     * @return lista de productos que coinciden
     */
    public List<ProductoResponse> buscarProductos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            throw new BusinessException("El término de búsqueda no puede estar vacío");
        }
        
        List<Producto> productos = productoDAO.buscarPorTermino(termino);
        return productoMapper.toResponseList(productos);
    }
    
    /**
     * Crea un nuevo producto
     * @param productoRequest datos del producto
     * @return el producto creado
     */
    public ProductoResponse createProducto(ProductoRequest productoRequest) {
        validarProductoRequest(productoRequest);
        
        // Verificar que el SKU no exista si se proporciona
        if (productoRequest.getCodigoSku() != null) {
            Producto existente = productoDAO.findByCodigoSku(productoRequest.getCodigoSku());
            if (existente != null) {
                throw new BusinessException("Ya existe un producto con el SKU: " + productoRequest.getCodigoSku());
            }
        }
        
        // Obtener la categoría
        Categoria categoria = null;
        if (productoRequest.getCategoriaId() != null) {
            categoria = categoriaDAO.findById(productoRequest.getCategoriaId());
            if (categoria == null) {
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + productoRequest.getCategoriaId());
            }
        }
        
        // Crear el producto
        Producto producto = productoMapper.toEntity(productoRequest);
        producto.setCategoria(categoria);
        producto.setUltimaActualizacion(new Date());
        
        // Persistir
        producto = productoDAO.save(producto);
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Actualiza un producto existente
     * @param id identificador del producto
     * @param productoRequest datos actualizados
     * @return el producto actualizado
     */
    public ProductoResponse updateProducto(Long id, ProductoRequest productoRequest) {
        validarProductoRequest(productoRequest);
        
        // Verificar que el producto exista
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        
        // Verificar que el SKU no exista en otro producto si se cambia
        if (productoRequest.getCodigoSku() != null && 
            !productoRequest.getCodigoSku().equals(producto.getCodigoSku())) {
            Producto existente = productoDAO.findByCodigoSku(productoRequest.getCodigoSku());
            if (existente != null && !existente.getId().equals(id)) {
                throw new BusinessException("Ya existe otro producto con el SKU: " + productoRequest.getCodigoSku());
            }
        }
        
        // Obtener la categoría si cambió
        if (productoRequest.getCategoriaId() != null && 
            (producto.getCategoria() == null || !productoRequest.getCategoriaId().equals(producto.getCategoria().getId()))) {
            Categoria categoria = categoriaDAO.findById(productoRequest.getCategoriaId());
            if (categoria == null) {
                throw new ResourceNotFoundException("Categoría no encontrada con ID: " + productoRequest.getCategoriaId());
            }
            producto.setCategoria(categoria);
        }
        
        // Actualizar los datos
        productoMapper.updateEntityFromRequest(producto, productoRequest);
        producto.setUltimaActualizacion(new Date());
        
        // Persistir
        productoDAO.update(producto);
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Cambia el estado activo/inactivo de un producto
     * @param id identificador del producto
     * @param activo nuevo estado
     * @return el producto actualizado
     */
    public ProductoResponse cambiarEstadoActivo(Long id, boolean activo) {
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        
        productoDAO.cambiarEstadoActivo(id, activo);
        producto.setActivo(activo);
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Actualiza el stock de un producto
     * @param id identificador del producto
     * @param cantidad la cantidad a añadir (negativa para restar)
     * @return el producto actualizado
     */
    public ProductoResponse actualizarStock(Long id, int cantidad) {
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        
        productoDAO.actualizarStock(id, cantidad);
        producto.setStock(Math.max(0, producto.getStock() + cantidad));
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Actualiza el precio de un producto
     * @param id identificador del producto
     * @param nuevoPrecio el nuevo precio
     * @return el producto actualizado
     */
    public ProductoResponse actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que cero");
        }
        
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        
        productoDAO.actualizarPrecio(id, nuevoPrecio);
        producto.setPrecio(nuevoPrecio);
        return productoMapper.toResponse(producto);
    }
    
    /**
     * Elimina un producto
     * @param id identificador del producto
     */
    public void deleteProducto(Long id) {
        Producto producto = productoDAO.findById(id);
        if (producto == null) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        
        productoDAO.deleteById(id);
    }
    
    /**
     * Valida los datos del producto
     * @param productoRequest datos a validar
     * @throws BusinessException si hay datos inválidos
     */
    private void validarProductoRequest(ProductoRequest productoRequest) {
        if (productoRequest.getNombre() == null || productoRequest.getNombre().trim().isEmpty()) {
            throw new BusinessException("El nombre del producto es obligatorio");
        }
        
        if (productoRequest.getPrecio() == null || productoRequest.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que cero");
        }
        
        if (productoRequest.getCategoriaId() == null) {
            throw new BusinessException("La categoría es obligatoria");
        }
    }
    
    /**
     * Cierra los recursos del servicio
     */
    public void close() {
        if (productoDAO != null) {
            productoDAO.close();
        }
        if (categoriaDAO != null) {
            categoriaDAO.close();
        }
    }
}