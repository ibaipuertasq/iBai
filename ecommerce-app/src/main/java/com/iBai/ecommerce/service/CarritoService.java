package com.iBai.ecommerce.service;

import com.iBai.ecommerce.dao.CarritoDAO;
import com.iBai.ecommerce.dao.ItemCarritoDAO;
import com.iBai.ecommerce.dao.ProductoDAO;
import com.iBai.ecommerce.dao.UsuarioDAO;
import com.iBai.ecommerce.dto.mapper.CarritoMapper;
import com.iBai.ecommerce.dto.mapper.ItemCarritoMapper;
import com.iBai.ecommerce.dto.request.CarritoRequest;
import com.iBai.ecommerce.dto.request.ItemCarritoRequest;
import com.iBai.ecommerce.dto.response.CarritoResponse;
import com.iBai.ecommerce.dto.response.ItemCarritoResponse;
import com.iBai.ecommerce.model.Carrito;
import com.iBai.ecommerce.model.ItemCarrito;
import com.iBai.ecommerce.model.Producto;
import com.iBai.ecommerce.model.Usuario;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar operaciones del carrito de compra
 */
public class CarritoService {
    
    private final CarritoDAO carritoDAO;
    private final ItemCarritoDAO itemCarritoDAO;
    private final ProductoDAO productoDAO;
    private final UsuarioDAO usuarioDAO;
    
    private final CarritoMapper carritoMapper;
    private final ItemCarritoMapper itemCarritoMapper;
    
    public CarritoService() {
        this.carritoDAO = new CarritoDAO();
        this.itemCarritoDAO = new ItemCarritoDAO();
        this.productoDAO = new ProductoDAO();
        this.usuarioDAO = new UsuarioDAO();
        
        this.carritoMapper = new CarritoMapper();
        this.itemCarritoMapper = new ItemCarritoMapper();
    }
    
    /**
     * Obtiene el carrito de un usuario por su ID
     * @param usuarioId ID del usuario
     * @return el carrito del usuario
     * @throws RuntimeException si el usuario no existe o no tiene carrito
     */
    public CarritoResponse getCarritoByUsuario(Long usuarioId) {
        Usuario usuario = usuarioDAO.findById(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Carrito carrito = carritoDAO.findByUsuario(usuarioId);
        if (carrito == null) {
            // Crear un nuevo carrito para el usuario
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito.setFechaCreacion(new Date());
            carrito = carritoDAO.save(carrito);
        }
        
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carrito.getId());
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Obtiene el carrito por ID de sesión (para usuarios no autenticados)
     * @param sessionId ID de la sesión
     * @return el carrito de la sesión
     */
    public CarritoResponse getCarritoBySessionId(String sessionId) {
        Carrito carrito = carritoDAO.findBySessionId(sessionId);
        if (carrito == null) {
            // Crear un nuevo carrito para la sesión
            carrito = new Carrito();
            carrito.setSessionId(sessionId);
            carrito.setFechaCreacion(new Date());
            carrito = carritoDAO.save(carrito);
        }
        
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carrito.getId());
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Obtiene o crea un carrito para un usuario o sesión
     * @param request los datos del carrito
     * @return el carrito
     */
    public CarritoResponse getOrCreateCarrito(CarritoRequest request) {
        if (request.getUsuarioId() != null) {
            return getCarritoByUsuario(request.getUsuarioId());
        } else if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            return getCarritoBySessionId(request.getSessionId());
        } else {
            // Generar un nuevo ID de sesión
            String sessionId = UUID.randomUUID().toString();
            CarritoRequest newRequest = new CarritoRequest();
            newRequest.setSessionId(sessionId);
            return getCarritoBySessionId(sessionId);
        }
    }
    
    /**
     * Añade un producto al carrito
     * @param carritoId ID del carrito
     * @param request datos del ítem a añadir
     * @return el carrito actualizado
     * @throws RuntimeException si el carrito o producto no existe
     */
    public CarritoResponse addItemToCarrito(Long carritoId, ItemCarritoRequest request) {
        Carrito carrito = carritoDAO.findById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }
        
        Producto producto = productoDAO.findById(request.getProductoId());
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }
        
        // Verificar stock disponible
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        // Buscar si el producto ya está en el carrito
        ItemCarrito existingItem = itemCarritoDAO.findByCarritoYProducto(carritoId, request.getProductoId());
        
        if (existingItem != null) {
            // Actualizar cantidad
            int nuevaCantidad = existingItem.getCantidad() + request.getCantidad();
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para la cantidad total");
            }
            
            itemCarritoDAO.actualizarCantidad(existingItem.getId(), nuevaCantidad);
        } else {
            // Añadir nuevo ítem
            ItemCarrito newItem = itemCarritoMapper.toEntity(request, carrito, producto);
            itemCarritoDAO.save(newItem);
        }
        
        // Obtener carrito actualizado
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carritoId);
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Actualiza la cantidad de un ítem en el carrito
     * @param carritoId ID del carrito
     * @param itemId ID del ítem
     * @param cantidad nueva cantidad
     * @return el carrito actualizado
     * @throws RuntimeException si el carrito, ítem o producto no existe
     */
    public CarritoResponse updateItemQuantity(Long carritoId, Long itemId, int cantidad) {
        Carrito carrito = carritoDAO.findById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }
        
        ItemCarrito item = itemCarritoDAO.findById(itemId);
        if (item == null || !item.getCarrito().getId().equals(carritoId)) {
            throw new RuntimeException("Ítem no encontrado en el carrito");
        }
        
        // Verificar stock disponible si aumenta la cantidad
        if (cantidad > item.getCantidad()) {
            Producto producto = item.getProducto();
            int diferencia = cantidad - item.getCantidad();
            if (producto.getStock() < diferencia) {
                throw new RuntimeException("Stock insuficiente");
            }
        }
        
        if (cantidad <= 0) {
            // Eliminar ítem si cantidad es 0 o negativa
            itemCarritoDAO.delete(item);
        } else {
            // Actualizar cantidad
            itemCarritoDAO.actualizarCantidad(itemId, cantidad);
        }
        
        // Obtener carrito actualizado
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carritoId);
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Elimina un ítem del carrito
     * @param carritoId ID del carrito
     * @param itemId ID del ítem
     * @return el carrito actualizado
     * @throws RuntimeException si el carrito o ítem no existe
     */
    public CarritoResponse removeItemFromCarrito(Long carritoId, Long itemId) {
        Carrito carrito = carritoDAO.findById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }
        
        ItemCarrito item = itemCarritoDAO.findById(itemId);
        if (item == null || !item.getCarrito().getId().equals(carritoId)) {
            throw new RuntimeException("Ítem no encontrado en el carrito");
        }
        
        // Eliminar ítem
        itemCarritoDAO.delete(item);
        
        // Obtener carrito actualizado
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carritoId);
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Vacía un carrito
     * @param carritoId ID del carrito
     * @return el carrito vacío
     * @throws RuntimeException si el carrito no existe
     */
    public CarritoResponse clearCarrito(Long carritoId) {
        Carrito carrito = carritoDAO.findById(carritoId);
        if (carrito == null) {
            throw new RuntimeException("Carrito no encontrado");
        }
        
        // Eliminar todos los ítems del carrito
        itemCarritoDAO.eliminarItemsDeCarrito(carritoId);
        
        // Obtener carrito actualizado (vacío)
        List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carritoId);
        return carritoMapper.toResponse(carrito, items);
    }
    
    /**
     * Asocia un carrito anónimo (de sesión) a un usuario (tras login)
     * @param sessionId ID de la sesión
     * @param usuarioId ID del usuario
     * @return el carrito asociado al usuario
     * @throws RuntimeException si el usuario no existe
     */
    public CarritoResponse associateCarritoToUser(String sessionId, Long usuarioId) {
        Usuario usuario = usuarioDAO.findById(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Carrito carritoSesion = carritoDAO.findBySessionId(sessionId);
        Carrito carritoUsuario = carritoDAO.findByUsuario(usuarioId);
        
        if (carritoSesion == null) {
            // No hay carrito de sesión, devolver o crear el del usuario
            if (carritoUsuario == null) {
                CarritoRequest request = new CarritoRequest();
                request.setUsuarioId(usuarioId);
                return getOrCreateCarrito(request);
            }
            return getCarritoByUsuario(usuarioId);
        }
        
        if (carritoUsuario == null) {
            // El usuario no tiene carrito, asignarle el de la sesión
            carritoDAO.asociarCarritoAUsuario(carritoSesion.getId(), usuarioId);
            List<ItemCarrito> items = itemCarritoDAO.findByCarrito(carritoSesion.getId());
            return carritoMapper.toResponse(carritoSesion, items);
        }
        
        // Ambos tienen carrito, fusionar items del carrito de sesión al del usuario
        List<ItemCarrito> itemsSesion = itemCarritoDAO.findByCarrito(carritoSesion.getId());
        
        for (ItemCarrito itemSesion : itemsSesion) {
            ItemCarrito itemUsuario = itemCarritoDAO.findByCarritoYProducto(
                carritoUsuario.getId(), itemSesion.getProducto().getId());
            
            if (itemUsuario != null) {
                // Producto ya en carrito del usuario, sumar cantidades
                int nuevaCantidad = itemUsuario.getCantidad() + itemSesion.getCantidad();
                
                // Verificar stock
                if (itemSesion.getProducto().getStock() >= nuevaCantidad) {
                    itemCarritoDAO.actualizarCantidad(itemUsuario.getId(), nuevaCantidad);
                } else {
                    // Si no hay suficiente stock, usar la cantidad máxima disponible
                    itemCarritoDAO.actualizarCantidad(itemUsuario.getId(), itemSesion.getProducto().getStock());
                }
            } else {
                // Producto no en carrito del usuario, crear nuevo ítem
                ItemCarrito nuevoItem = new ItemCarrito();
                nuevoItem.setCarrito(carritoUsuario);
                nuevoItem.setProducto(itemSesion.getProducto());
                nuevoItem.setCantidad(itemSesion.getCantidad());
                nuevoItem.setFechaAgregado(new Date());
                itemCarritoDAO.save(nuevoItem);
            }
        }
        
        // Eliminar carrito de sesión
        itemCarritoDAO.eliminarItemsDeCarrito(carritoSesion.getId());
        carritoDAO.delete(carritoSesion);
        
        // Devolver carrito del usuario actualizado
        List<ItemCarrito> itemsUsuario = itemCarritoDAO.findByCarrito(carritoUsuario.getId());
        return carritoMapper.toResponse(carritoUsuario, itemsUsuario);
    }
}