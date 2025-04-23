package com.iBai.ecommerce.rest.controllers;

import com.iBai.ecommerce.dao.ImagenProductoDAO;
import com.iBai.ecommerce.dto.mapper.ImagenProductoMapper;
import com.iBai.ecommerce.dto.request.ProductoRequest;
import com.iBai.ecommerce.dto.response.ImagenProductoResponse;
import com.iBai.ecommerce.dto.response.ProductoResponse;
import com.iBai.ecommerce.model.ImagenProducto;
import com.iBai.ecommerce.rest.exception.ApiError;
import com.iBai.ecommerce.security.Secured;
import com.iBai.ecommerce.service.ProductoService;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controlador REST para operaciones de productos
 */
@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoController {
    
    private final ProductoService productoService;
    
    public ProductoController() {
        this.productoService = new ProductoService();
    }
    
    /**
     * Obtiene todos los productos
     * @return lista de productos
     */
    @GET
    public Response getAllProductos() {
        List<ProductoResponse> productos = productoService.getAllProductos();
        return Response.ok(productos).build();
    }
    
    /**
     * Obtiene un producto por su ID
     * @param id identificador del producto
     * @return el producto
     */
    @GET
    @Path("/{id}")
    public Response getProductoById(@PathParam("id") Long id) {
        ProductoResponse producto = productoService.getProductoById(id);
        return Response.ok(producto).build();
    }
    
    /**
     * Busca un producto por su código SKU
     * @param sku código único del producto
     * @return el producto
     */
    @GET
    @Path("/sku/{sku}")
    public Response getProductoBySku(@PathParam("sku") String sku) {
        ProductoResponse producto = productoService.getProductoBySku(sku);
        return Response.ok(producto).build();
    }
    
    /**
     * Busca productos por categoría
     * @param categoriaId ID de la categoría
     * @return lista de productos de la categoría
     */
    @GET
    @Path("/categoria/{categoriaId}")
    public Response getProductosByCategoria(@PathParam("categoriaId") Long categoriaId) {
        List<ProductoResponse> productos = productoService.getProductosByCategoria(categoriaId);
        return Response.ok(productos).build();
    }
    
    /**
     * Busca productos con stock disponible
     * @return lista de productos con stock
     */
    @GET
    @Path("/disponibles")
    public Response getProductosConStock() {
        List<ProductoResponse> productos = productoService.getProductosConStock();
        return Response.ok(productos).build();
    }
    
    /**
     * Busca productos por un término (en nombre o descripción)
     * @param termino texto a buscar
     * @return lista de productos que coinciden
     */
    @GET
    @Path("/buscar")
    public Response buscarProductos(@QueryParam("q") String termino) {
        List<ProductoResponse> productos = productoService.buscarProductos(termino);
        return Response.ok(productos).build();
    }
    
    /**
     * Busca productos por rango de precios
     * @param min precio mínimo
     * @param max precio máximo
     * @return lista de productos en el rango de precios
     */
    @GET
    @Path("/precio")
    public Response getProductosByRangoPrecio(
            @QueryParam("min") BigDecimal min,
            @QueryParam("max") BigDecimal max) {
        if (min == null) {
            min = BigDecimal.ZERO;
        }
        if (max == null) {
            max = new BigDecimal("999999.99"); // Un valor muy alto por defecto
        }
        List<ProductoResponse> productos = productoService.getProductosByRangoPrecio(min, max);
        return Response.ok(productos).build();
    }
    
    /**
     * Crea un nuevo producto
     * @param productoRequest datos del producto
     * @return el producto creado
     */
    @POST
    @Secured
    public Response createProducto(ProductoRequest productoRequest) {
        ProductoResponse producto = productoService.createProducto(productoRequest);
        return Response.status(Response.Status.CREATED).entity(producto).build();
    }
    
    /**
     * Actualiza un producto existente
     * @param id identificador del producto
     * @param productoRequest datos actualizados
     * @return el producto actualizado
     */
    @PUT
    @Path("/{id}")
    @Secured
    public Response updateProducto(
            @PathParam("id") Long id,
            ProductoRequest productoRequest) {
        ProductoResponse producto = productoService.updateProducto(id, productoRequest);
        return Response.ok(producto).build();
    }
    
    /**
     * Cambia el estado activo/inactivo de un producto
     * @param id identificador del producto
     * @param activo nuevo estado
     * @return el producto actualizado
     */
    @PUT
    @Path("/{id}/activo")
    @Secured
    public Response cambiarEstadoActivo(
            @PathParam("id") Long id,
            @QueryParam("estado") boolean activo) {
        ProductoResponse producto = productoService.cambiarEstadoActivo(id, activo);
        return Response.ok(producto).build();
    }
    
    /**
     * Actualiza el stock de un producto
     * @param id identificador del producto
     * @param cantidad la cantidad a añadir (negativa para restar)
     * @return el producto actualizado
     */
    @PUT
    @Path("/{id}/stock")
    @Secured
    public Response actualizarStock(
            @PathParam("id") Long id,
            @QueryParam("cantidad") int cantidad) {
        ProductoResponse producto = productoService.actualizarStock(id, cantidad);
        return Response.ok(producto).build();
    }
    
    /**
     * Actualiza el precio de un producto
     * @param id identificador del producto
     * @param precio el nuevo precio
     * @return el producto actualizado
     */
    @PUT
    @Path("/{id}/precio")
    @Secured
    public Response actualizarPrecio(
            @PathParam("id") Long id,
            @QueryParam("precio") BigDecimal precio) {
        ProductoResponse producto = productoService.actualizarPrecio(id, precio);
        return Response.ok(producto).build();
    }
    
    /**
     * Elimina un producto
     * @param id identificador del producto
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteProducto(@PathParam("id") Long id) {
        productoService.deleteProducto(id);
        return Response.noContent().build();
    }

    // Añadir en ProductoController.java
    @GET
    @Path("/{id}/imagenes")
    public Response getProductoImagenes(@PathParam("id") Long id) {
        try {
            // Crear instancias necesarias
            ImagenProductoDAO imagenProductoDAO = new ImagenProductoDAO();
            ImagenProductoMapper imagenMapper = new ImagenProductoMapper();
            
            // Obtener imágenes del producto
            List<ImagenProducto> imagenes = imagenProductoDAO.findByProductoId(id);
            List<ImagenProductoResponse> response = imagenMapper.toResponseList(imagenes);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            error.setError("Error al obtener imágenes");
            error.setMessage("No se pudieron recuperar las imágenes del producto");
            error.setTimestamp(System.currentTimeMillis());
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    /**
     * Busca productos por categoría incluyendo subcategorías
     * @param categoriaId ID de la categoría
     * @return lista de productos de la categoría y sus subcategorías
     */
    @GET
    @Path("/categoria/{categoriaId}/recursivo")
    public Response getProductosByCategoriaRecursivo(@PathParam("categoriaId") Long categoriaId) {
        List<ProductoResponse> productos = productoService.getProductosByCategoria(categoriaId);
        return Response.ok(productos).build();
    }
}