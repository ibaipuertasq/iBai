package com.iBai.ecommerce.rest.controllers;

import com.iBai.ecommerce.dto.request.CategoriaRequest;
import com.iBai.ecommerce.dto.response.CategoriaResponse;
import com.iBai.ecommerce.security.Secured;
import com.iBai.ecommerce.service.CategoriaService;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controlador REST para operaciones de categorías
 */
@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    public CategoriaController() {
        this.categoriaService = new CategoriaService();
    }
    
    /**
     * Obtiene todas las categorías
     * @return lista de categorías
     */
    @GET
    public Response getAllCategorias() {
        List<CategoriaResponse> categorias = categoriaService.getAllCategorias();
        return Response.ok(categorias).build();
    }
    
    /**
     * Obtiene solo las categorías activas
     * @return lista de categorías activas
     */
    @GET
    @Path("/activas")
    public Response getCategoriasActivas() {
        List<CategoriaResponse> categorias = categoriaService.getCategoriasActivas();
        return Response.ok(categorias).build();
    }
    
    /**
     * Obtiene todas las categorías principales (sin categoría padre)
     * @return lista de categorías principales
     */
    @GET
    @Path("/principales")
    public Response getCategoriasPrincipales() {
        List<CategoriaResponse> categorias = categoriaService.getCategoriasPrincipales();
        return Response.ok(categorias).build();
    }
    
    /**
     * Obtiene una categoría por su ID
     * @param id identificador de la categoría
     * @return la categoría
     */
    @GET
    @Path("/{id}")
    public Response getCategoriaById(@PathParam("id") Long id) {
        CategoriaResponse categoria = categoriaService.getCategoriaById(id);
        return Response.ok(categoria).build();
    }
    
    /**
     * Obtiene las subcategorías de una categoría
     * @param categoriaId ID de la categoría padre
     * @return lista de subcategorías
     */
    @GET
    @Path("/{id}/subcategorias")
    public Response getSubcategorias(@PathParam("id") Long categoriaId) {
        List<CategoriaResponse> subcategorias = categoriaService.getSubcategorias(categoriaId);
        return Response.ok(subcategorias).build();
    }
    
    /**
     * Obtiene la jerarquía completa de una categoría (ancestros)
     * @param categoriaId ID de la categoría
     * @return lista de categorías en la jerarquía, en orden de padre a hijo
     */
    @GET
    @Path("/{id}/jerarquia")
    public Response getJerarquia(@PathParam("id") Long categoriaId) {
        List<CategoriaResponse> jerarquia = categoriaService.getJerarquia(categoriaId);
        return Response.ok(jerarquia).build();
    }
    
    /**
     * Busca categorías por nombre (búsqueda parcial)
     * @param nombre el nombre o parte del nombre a buscar
     * @return lista de categorías que coinciden
     */
    @GET
    @Path("/buscar")
    public Response buscarCategorias(@QueryParam("nombre") String nombre) {
        List<CategoriaResponse> categorias = categoriaService.buscarCategoriasPorNombre(nombre);
        return Response.ok(categorias).build();
    }
    
    /**
     * Crea una nueva categoría
     * @param categoriaRequest datos de la categoría
     * @return la categoría creada
     */
    @POST
    @Secured
    public Response createCategoria(CategoriaRequest categoriaRequest) {
        CategoriaResponse categoria = categoriaService.createCategoria(categoriaRequest);
        return Response.status(Response.Status.CREATED).entity(categoria).build();
    }
    
    /**
     * Actualiza una categoría existente
     * @param id identificador de la categoría
     * @param categoriaRequest datos actualizados
     * @return la categoría actualizada
     */
    @PUT
    @Path("/{id}")
    @Secured
    public Response updateCategoria(
            @PathParam("id") Long id,
            CategoriaRequest categoriaRequest) {
        CategoriaResponse categoria = categoriaService.updateCategoria(id, categoriaRequest);
        return Response.ok(categoria).build();
    }
    
    /**
     * Cambia el estado activo/inactivo de una categoría
     * @param id identificador de la categoría
     * @param activa nuevo estado
     * @return la categoría actualizada
     */
    @PUT
    @Path("/{id}/activa")
    @Secured
    public Response cambiarEstadoActivo(
            @PathParam("id") Long id,
            @QueryParam("estado") boolean activa) {
        CategoriaResponse categoria = categoriaService.cambiarEstadoActivo(id, activa);
        return Response.ok(categoria).build();
    }
    
    /**
     * Elimina una categoría
     * @param id identificador de la categoría
     * @return 204 No Content
     */
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteCategoria(@PathParam("id") Long id) {
        categoriaService.deleteCategoria(id);
        return Response.noContent().build();
    }
}