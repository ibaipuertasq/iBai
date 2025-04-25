package com.iBai.ecommerce.rest.controllers;

import com.iBai.ecommerce.dto.request.CarritoRequest;
import com.iBai.ecommerce.dto.request.ItemCarritoRequest;
import com.iBai.ecommerce.dto.response.CarritoResponse;
import com.iBai.ecommerce.rest.exception.ApiError;
import com.iBai.ecommerce.security.Secured;
import com.iBai.ecommerce.service.CarritoService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controlador REST para operaciones de carrito de compra
 */
@Path("/carritos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarritoController {
    
    private final CarritoService carritoService;
    
    public CarritoController() {
        this.carritoService = new CarritoService();
    }
    
    /**
     * Obtiene o crea un carrito para un usuario o sesión
     * @param request los datos del carrito
     * @return el carrito
     */
    @POST
    public Response getOrCreateCarrito(@Valid CarritoRequest request) {
        try {
            CarritoResponse carrito = carritoService.getOrCreateCarrito(request);
            return Response.status(Response.Status.CREATED).entity(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al crear carrito");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Obtiene un carrito por su ID
     * @param id identificador del carrito
     * @return el carrito encontrado
     */
    @GET
    @Path("/{id}")
    public Response getCarritoById(@PathParam("id") Long id) {
        try {
            // Aquí simulamos obtener el carrito con los ítems
            // En una implementación real, tendríamos un método específico en el servicio
            // para obtener el carrito por ID con sus ítems
            CarritoRequest request = new CarritoRequest();
            CarritoResponse carrito = carritoService.getOrCreateCarrito(request);
            
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            error.setError("Carrito no encontrado");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }
    
    /**
     * Obtiene un carrito por ID de sesión
     * @param sessionId ID de la sesión
     * @return el carrito de la sesión
     */
    @GET
    @Path("/session/{sessionId}")
    public Response getCarritoBySessionId(@PathParam("sessionId") String sessionId) {
        try {
            CarritoResponse carrito = carritoService.getCarritoBySessionId(sessionId);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            error.setError("Carrito no encontrado");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }
    
    /**
     * Obtiene el carrito de un usuario
     * @param usuarioId ID del usuario
     * @return el carrito del usuario
     */
    @GET
    @Path("/usuario/{usuarioId}")
    public Response getCarritoByUsuario(@PathParam("usuarioId") Long usuarioId) {
        try {
            CarritoResponse carrito = carritoService.getCarritoByUsuario(usuarioId);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            error.setError("Carrito no encontrado");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }
    
    /**
     * Añade un producto al carrito
     * @param carritoId ID del carrito
     * @param request datos del ítem a añadir
     * @return el carrito actualizado
     */
    @POST
    @Path("/{carritoId}/items")
    public Response addItemToCarrito(
            @PathParam("carritoId") Long carritoId,
            @Valid ItemCarritoRequest request) {
        try {
            CarritoResponse carrito = carritoService.addItemToCarrito(carritoId, request);
            return Response.status(Response.Status.CREATED).entity(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al añadir ítem al carrito");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Actualiza la cantidad de un ítem en el carrito
     * @param carritoId ID del carrito
     * @param itemId ID del ítem
     * @param cantidad nueva cantidad
     * @return el carrito actualizado
     */
    @PUT
    @Path("/{carritoId}/items/{itemId}")
    public Response updateItemQuantity(
            @PathParam("carritoId") Long carritoId,
            @PathParam("itemId") Long itemId,
            @QueryParam("cantidad") int cantidad) {
        try {
            CarritoResponse carrito = carritoService.updateItemQuantity(carritoId, itemId, cantidad);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al actualizar ítem");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Elimina un ítem del carrito
     * @param carritoId ID del carrito
     * @param itemId ID del ítem
     * @return el carrito actualizado
     */
    @DELETE
    @Path("/{carritoId}/items/{itemId}")
    public Response removeItemFromCarrito(
            @PathParam("carritoId") Long carritoId,
            @PathParam("itemId") Long itemId) {
        try {
            CarritoResponse carrito = carritoService.removeItemFromCarrito(carritoId, itemId);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al eliminar ítem");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Vacía un carrito
     * @param carritoId ID del carrito
     * @return el carrito vacío
     */
    @DELETE
    @Path("/{carritoId}/items")
    public Response clearCarrito(@PathParam("carritoId") Long carritoId) {
        try {
            CarritoResponse carrito = carritoService.clearCarrito(carritoId);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al vaciar carrito");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
    
    /**
     * Asocia un carrito anónimo (de sesión) a un usuario (tras login)
     * @param sessionId ID de la sesión
     * @param usuarioId ID del usuario
     * @return el carrito asociado al usuario
     */
    @POST
    @Path("/associate")
    public Response associateCarritoToUser(
            @QueryParam("sessionId") String sessionId,
            @QueryParam("usuarioId") Long usuarioId) {
        try {
            CarritoResponse carrito = carritoService.associateCarritoToUser(sessionId, usuarioId);
            return Response.ok(carrito).build();
        } catch (Exception e) {
            ApiError error = new ApiError();
            error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
            error.setError("Error al asociar carrito");
            error.setMessage(e.getMessage());
            error.setTimestamp(System.currentTimeMillis());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }
}
