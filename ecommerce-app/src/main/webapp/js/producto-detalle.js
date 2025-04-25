/**
 * Script mejorado para la página de detalle de producto
 */

let productoId = null;
let categoriaId = null;

// Elementos del DOM
const productoBreadcrumb = document.getElementById('producto-breadcrumb');
const productoContent = document.getElementById('producto-content');
const productosRelacionados = document.getElementById('productos-relacionados');

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Obtener ID de producto de la URL
    const params = new URLSearchParams(window.location.search);
    productoId = params.get('id');
    
    if (!productoId) {
        showErrorMessage('No se ha especificado un producto. Regrese a la lista de productos.');
        return;
    }
    
    // Cargar detalle del producto
    loadProductoDetalle(productoId);
});

// Cargar detalle del producto
async function loadProductoDetalle(id) {
    try {
        showLoadingIndicator();
        
        const producto = await fetchAPI(`/productos/${id}`);
        
        // Guardar ID de categoría para productos relacionados
        if (producto.categoria) {
            categoriaId = producto.categoria.id;
        }
        
        // Actualizar título de la página
        document.title = `${producto.nombre} - iBai`;
        
        // Actualizar breadcrumb
        updateBreadcrumb(producto);
        
        // Renderizar detalle
        renderProductoDetalle(producto);
        
        // Cargar productos relacionados
        loadProductosRelacionados();
        
        // Cargar imágenes del producto
        loadProductImages(id);
    } catch (error) {
        console.error('Error al cargar detalle del producto:', error);
        showErrorMessage('No se pudo cargar la información del producto. Intente nuevamente más tarde.');
    }
}

// Cargar productos relacionados
async function loadProductosRelacionados() {
    try {
        // Si no hay categoría, no mostrar productos relacionados
        if (!categoriaId) {
            productosRelacionados.innerHTML = '';
            return;
        }
        
        // Mostrar indicador de carga
        productosRelacionados.innerHTML = `
            <div class="text-center py-4 w-100">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-2">Cargando productos relacionados...</p>
            </div>
        `;
        
        // Cargar productos de la misma categoría (excluir el producto actual)
        const productos = await fetchAPI(`/productos/categoria/${categoriaId}?size=4`);
        
        // Filtrar producto actual y limitar a 4
        const relacionados = (productos.data || productos)
            .filter(p => p.id !== productoId)
            .slice(0, 4);
        
        // Si no hay productos relacionados, ocultar sección
        if (relacionados.length === 0) {
            productosRelacionados.closest('.mt-5').style.display = 'none';
            return;
        }
        
        // Renderizar productos relacionados
        renderProductosRelacionados(relacionados);
    } catch (error) {
        console.error('Error al cargar productos relacionados:', error);
        productosRelacionados.innerHTML = '';
    }
}

// Renderizar detalle del producto con el diseño mejorado
function renderProductoDetalle(producto) {
    const disponible = producto.stock > 0;
    const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
    
    productoContent.innerHTML = `
        <div class="row">
            <!-- Imagen del producto -->
            <div class="col-md-6 mb-4 mb-md-0">
                <div class="card">
                    <div id="product-images-gallery" class="card-body p-0">
                        <!-- Las imágenes se cargarán dinámicamente -->
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Cargando imágenes...</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Información del producto -->
            <div class="col-md-6">
                <h1 class="mb-3">${producto.nombre}</h1>
                
                <div class="mb-3">
                    ${producto.categoria ? `
                        <a href="categorias.html?id=${producto.categoria.id}" class="badge bg-primary text-decoration-none">
                            <i class="bi bi-tag"></i> ${producto.categoria.nombre}
                        </a>
                    ` : ''}
                    ${producto.destacado ? `
                        <span class="badge bg-warning text-dark">
                            <i class="bi bi-star-fill"></i> Destacado
                        </span>
                    ` : ''}
                    <span class="badge ${disponible ? 'bg-success' : 'bg-danger'}">
                        <i class="bi ${disponible ? 'bi-check-circle' : 'bi-x-circle'}"></i>
                        ${disponible ? 'En stock' : 'Sin stock'}
                    </span>
                </div>
                
                <div class="product-price-detail mb-4">
                    ${precioOferta ? `
                        <p class="text-decoration-line-through text-muted mb-1">${formatPrice(producto.precio)}</p>
                        <p class="fs-2 fw-bold text-danger">${formatPrice(producto.precioOferta)}</p>
                        <p class="badge bg-danger">Ahorra ${formatPrice(producto.precio - producto.precioOferta)}</p>
                    ` : `
                        <p class="fs-2 fw-bold">${formatPrice(producto.precio)}</p>
                    `}
                </div>
                
                <hr>
                
                <!-- Información adicional -->
                <div class="mb-4">
                    <h5>Detalles del producto</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>SKU:</strong> ${producto.codigoSku || 'N/A'}</p>
                            <p><strong>Stock:</strong> ${producto.stock || 0} unidades</p>
                        </div>
                        <div class="col-md-6">
                            ${producto.marca ? `<p><strong>Marca:</strong> ${producto.marca}</p>` : ''}
                            ${producto.peso ? `<p><strong>Peso:</strong> ${producto.peso} kg</p>` : ''}
                            ${producto.dimensiones ? `<p><strong>Dimensiones:</strong> ${producto.dimensiones}</p>` : ''}
                        </div>
                    </div>
                </div>
                
                <div class="mb-4">
                    <h5>Descripción</h5>
                    <div class="product-description">
                        ${producto.descripcion || 'No hay descripción disponible para este producto.'}
                    </div>
                </div>
                
                <!-- Acciones -->
                <div class="d-grid gap-2 d-md-flex">
                    <button class="btn btn-primary btn-lg flex-grow-1" ${!disponible ? 'disabled' : ''}>
                        <i class="bi bi-cart-plus"></i> Añadir al carrito
                    </button>
                    <button class="btn btn-outline-primary">
                        <i class="bi bi-heart"></i> Añadir a favoritos
                    </button>
                </div>
                
                <!-- Última actualización -->
                ${producto.ultimaActualizacion ? `
                    <p class="text-muted small mt-3">
                        <i class="bi bi-clock"></i> Última actualización: 
                        ${new Date(producto.ultimaActualizacion).toLocaleDateString('es-ES')}
                    </p>
                ` : ''}
            </div>
        </div>
    `;
    
    // Añadir eventos a los botones
    setupButtons(disponible);
}

// Renderizar productos relacionados con el nuevo formato estandarizado
// Reemplaza la función renderProductosRelacionados en producto-detalle.js con esta versión
function renderProductosRelacionados(productos) {
    productosRelacionados.innerHTML = '';
    
    // Crear el grid container para los productos relacionados
    const productsGrid = document.createElement('div');
    productsGrid.className = 'products-grid';
    
    productos.forEach(producto => {
        const disponible = producto.stock > 0;
        const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
        
        // Crear la tarjeta de producto con el formato estandarizado
        const productCard = document.createElement('div');
        productCard.className = `product-card ${!disponible ? 'opacity-75' : ''}`;
        
        // Estructura HTML estandarizada para los productos
        productCard.innerHTML = `
            <div class="product-img-container">
                <img src="${producto.imagen || 'images/producto-default.jpg'}" 
                     class="product-img" alt="${producto.nombre}">
                ${precioOferta ? `
                    <div class="product-badge product-badge-offer">Oferta</div>
                ` : ''}
                ${!disponible ? `
                    <div class="product-badge product-badge-stock">Sin stock</div>
                ` : ''}
            </div>
            <div class="product-body">
                <h5 class="product-title">${producto.nombre}</h5>
                ${producto.categoria ? `
                    <div class="product-category">
                        <a href="categorias.html?id=${producto.categoria.id}" class="text-decoration-none">
                            <i class="bi bi-tag"></i> ${producto.categoria.nombre}
                        </a>
                    </div>
                ` : ''}
                <div class="product-price-container">
                    ${precioOferta ? `
                        <div class="product-price-original">${formatPrice(producto.precio)}</div>
                        <div class="product-price product-price-offer">${formatPrice(producto.precioOferta)}</div>
                    ` : `
                        <div class="product-price">${formatPrice(producto.precio)}</div>
                    `}
                </div>
                <div class="product-footer">
                    <a href="producto-detalle.html?id=${producto.id}" class="btn btn-primary w-100">
                        <i class="bi bi-eye me-1"></i> Ver detalles
                    </a>
                </div>
            </div>
        `;
        
        productsGrid.appendChild(productCard);
    });
    
    productosRelacionados.appendChild(productsGrid);
}

// Configurar eventos de botones
function setupButtons(disponible) {
    const addToCartBtn = productoContent.querySelector('.btn-primary');
    const addToFavBtn = productoContent.querySelector('.btn-outline-primary');
    
    // Botón de añadir al carrito
    if (disponible) {
        addToCartBtn.addEventListener('click', function() {
            // Aquí iría la lógica para añadir al carrito
            // Por ahora solo mostramos una alerta
            alert('Producto añadido al carrito. Esta funcionalidad aún no está implementada.');
        });
    }
    
    // Botón de añadir a favoritos
    addToFavBtn.addEventListener('click', function() {
        // Aquí iría la lógica para añadir a favoritos
        // Por ahora solo mostramos una alerta
        alert('Producto añadido a favoritos. Esta funcionalidad aún no está implementada.');
    });
}

// Actualizar breadcrumb
function updateBreadcrumb(producto) {
    // Limpiar breadcrumb actual
    const lastItem = productoBreadcrumb.querySelector('li:last-child');
    
    // Actualizar último item con nombre del producto
    lastItem.textContent = producto.nombre;
    
    // Si hay categoría, agregar enlace
    if (producto.categoria) {
        const categoriaItem = document.createElement('li');
        categoriaItem.className = 'breadcrumb-item';
        categoriaItem.innerHTML = `<a href="categorias.html?id=${producto.categoria.id}">${producto.categoria.nombre}</a>`;
        
        // Insertar antes del último item
        productoBreadcrumb.insertBefore(categoriaItem, lastItem);
    }
}

// Cargar imágenes del producto
async function loadProductImages(productoId) {
    try {
        const imagenes = await fetchAPI(`/productos/${productoId}/imagenes`);
        renderProductImages(imagenes);
    } catch (error) {
        console.error('Error al cargar imágenes del producto:', error);
        renderDefaultImage();
    }
}

// Renderizar imágenes del producto
function renderProductImages(imagenes) {
    const imagesContainer = document.getElementById('product-images-gallery');
    
    if (!imagesContainer) {
        console.error('El contenedor de imágenes no existe');
        return;
    }
    
    // Limpiar contenedor
    imagesContainer.innerHTML = '';
    
    // Si no hay imágenes
    if (!imagenes || imagenes.length === 0) {
        renderDefaultImage();
        return;
    }
    
    // Buscar imagen principal o tomar la primera
    const mainImage = imagenes.find(img => img.esPrincipal) || imagenes[0];
    
    // Crear contenedor para imagen principal con diseño mejorado
    const mainImageContainer = document.createElement('div');
    mainImageContainer.className = 'main-image-container position-relative';
    mainImageContainer.innerHTML = `
        <img src="${mainImage.url}" class="img-fluid w-100" 
            alt="Imagen principal del producto" id="main-product-image"
            style="height: 400px; object-fit: contain;">
    `;
    
    // Añadir imagen principal al contenedor
    imagesContainer.appendChild(mainImageContainer);
    
    // Si hay más de una imagen, crear galería de miniaturas
    if (imagenes.length > 1) {
        const thumbnailsContainer = document.createElement('div');
        thumbnailsContainer.className = 'd-flex overflow-auto mt-3 p-2';
        
        // Crear miniaturas para todas las imágenes
        imagenes.forEach(imagen => {
            const thumbnail = document.createElement('div');
            thumbnail.className = `thumbnail-item me-2 ${imagen.id === mainImage.id ? 'border border-primary' : ''}`;
            thumbnail.style.width = '70px';
            thumbnail.style.height = '70px';
            thumbnail.style.cursor = 'pointer';
            
            thumbnail.innerHTML = `
                <img src="${imagen.url}" class="img-fluid" alt="Miniatura del producto"
                    style="width: 100%; height: 100%; object-fit: contain;">
            `;
            
            // Evento para cambiar la imagen principal al hacer clic en una miniatura
            thumbnail.addEventListener('click', function() {
                document.getElementById('main-product-image').src = imagen.url;
                // Quitar clase active de todas las miniaturas
                document.querySelectorAll('.thumbnail-item').forEach(item => {
                    item.classList.remove('border', 'border-primary');
                });
                // Añadir clase active a la miniatura seleccionada
                thumbnail.classList.add('border', 'border-primary');
            });
            
            thumbnailsContainer.appendChild(thumbnail);
        });
        
        // Añadir contenedor de miniaturas
        imagesContainer.appendChild(thumbnailsContainer);
    }
}

// Mostrar imagen por defecto
function renderDefaultImage() {
    const imagesContainer = document.getElementById('product-images-gallery');
    if (imagesContainer) {
        imagesContainer.innerHTML = `
            <div class="main-image-container">
                <img src="images/producto-default.jpg" class="img-fluid w-100" 
                    alt="Imagen no disponible"
                    style="height: 400px; object-fit: contain;">
            </div>
        `;
    }
}

// Mostrar indicador de carga
function showLoadingIndicator() {
    productoContent.innerHTML = `
        <div class="text-center py-5 w-100">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
            <p class="mt-2">Cargando información del producto...</p>
        </div>
    `;
}

// Mostrar mensaje de error
function showErrorMessage(message) {
    productoContent.innerHTML = `
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            ${message}
        </div>
        <div class="text-center mt-3">
            <a href="productos.html" class="btn btn-primary">
                <i class="bi bi-box-arrow-left me-2"></i>
                Ver todos los productos
            </a>
        </div>
    `;
}