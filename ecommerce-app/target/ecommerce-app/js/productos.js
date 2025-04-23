/**
 * Script para la página de listado de productos
 */

// Variables de estado
let currentPage = 1;
const PAGE_SIZE = 12;
let totalProducts = 0;
let totalPages = 0;
let currentFilters = {
    categoriaId: null,
    precioMin: null,
    precioMax: null,
    soloDisponibles: false,
    termino: null,
    ordenarPor: 'nombre-asc'
};

// Elementos del DOM
const productosContainer = document.getElementById('productos-container');
const paginationContainer = document.getElementById('pagination');
const totalProductosElement = document.getElementById('total-productos');
const categoriaFilterElement = document.getElementById('categoria-filter');
const precioMinElement = document.getElementById('precio-min');
const precioMaxElement = document.getElementById('precio-max');
const soloDisponiblesElement = document.getElementById('solo-disponibles');
const ordenarPorElement = document.getElementById('ordenar-por');
const searchInputElement = document.getElementById('search-input');
const searchButtonElement = document.getElementById('search-button');
const applyFiltersButton = document.getElementById('apply-filters');
const clearFiltersButton = document.getElementById('clear-filters');

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Cargar categorías para el filtro
    loadCategorias();
    
    // Cargar productos
    loadProductos();
    
    // Configurar eventos
    setupEventListeners();
    
    // Verificar si hay parámetros en la URL para filtros
    parseURLParameters();
});

// Configuración de eventos
function setupEventListeners() {
    // Evento de búsqueda
    searchButtonElement.addEventListener('click', function() {
        currentFilters.termino = searchInputElement.value.trim() || null;
        currentPage = 1;
        loadProductos();
        updateURL();
    });
    
    // Evento de Enter en búsqueda
    searchInputElement.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            currentFilters.termino = searchInputElement.value.trim() || null;
            currentPage = 1;
            loadProductos();
            updateURL();
        }
    });
    
    // Evento de filtros
    applyFiltersButton.addEventListener('click', function() {
        currentFilters.categoriaId = categoriaFilterElement.value || null;
        currentFilters.precioMin = precioMinElement.value || null;
        currentFilters.precioMax = precioMaxElement.value || null;
        currentFilters.soloDisponibles = soloDisponiblesElement.checked;
        currentPage = 1;
        loadProductos();
        updateURL();
    });
    
    // Evento de limpiar filtros
    clearFiltersButton.addEventListener('click', function() {
        categoriaFilterElement.value = '';
        precioMinElement.value = '';
        precioMaxElement.value = '';
        soloDisponiblesElement.checked = false;
        searchInputElement.value = '';
        
        currentFilters = {
            categoriaId: null,
            precioMin: null,
            precioMax: null,
            soloDisponibles: false,
            termino: null,
            ordenarPor: 'nombre-asc'
        };
        
        ordenarPorElement.value = 'nombre-asc';
        currentPage = 1;
        loadProductos();
        updateURL();
    });
    
    // Evento de ordenación
    ordenarPorElement.addEventListener('change', function() {
        currentFilters.ordenarPor = this.value;
        loadProductos();
        updateURL();
    });
}

// Cargar categorías para el filtro
async function loadCategorias() {
    try {
        const categorias = await fetchAPI('/categorias/activas');
        
        // Limpiar opciones existentes
        categoriaFilterElement.innerHTML = '<option value="">Todas las categorías</option>';
        
        // Agregar categorías
        categorias.forEach(categoria => {
            const option = document.createElement('option');
            option.value = categoria.id;
            option.textContent = categoria.nombre;
            categoriaFilterElement.appendChild(option);
        });
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        // No mostrar error al usuario para este caso
    }
}

// Cargar productos con filtros
async function loadProductos() {
    try {
        showLoadingIndicator();
        
        // Construir URL con filtros
        let endpoint = '/productos';
        const queryParams = [];
        
        if (currentFilters.categoriaId) {
            endpoint = `/productos/categoria/${currentFilters.categoriaId}`;
        } else {
            if (currentFilters.termino) {
                queryParams.push(`q=${encodeURIComponent(currentFilters.termino)}`);
            }
            
            if (currentFilters.precioMin && currentFilters.precioMax) {
                queryParams.push(`min=${currentFilters.precioMin}&max=${currentFilters.precioMax}`);
            } else if (currentFilters.precioMin) {
                queryParams.push(`min=${currentFilters.precioMin}`);
            } else if (currentFilters.precioMax) {
                queryParams.push(`max=${currentFilters.precioMax}`);
            }
            
            if (currentFilters.soloDisponibles) {
                endpoint = '/productos/disponibles';
            }
        }
        
        // Añadir paginación
        queryParams.push(`page=${currentPage}&size=${PAGE_SIZE}`);
        
        // Añadir parámetro de ordenación
        if (currentFilters.ordenarPor) {
            const [campo, orden] = currentFilters.ordenarPor.split('-');
            queryParams.push(`sort=${campo}&order=${orden}`);
        }
        
        const url = `${endpoint}${queryParams.length ? '?' + queryParams.join('&') : ''}`;
        
        const result = await fetchAPI(url);
        
        totalProducts = result.total || result.length;
        totalPages = Math.ceil(totalProducts / PAGE_SIZE);
        
        updateTotalCounter();
        renderProductos(result.data || result);
        renderPagination();
    } catch (error) {
        console.error('Error al cargar productos:', error);
        showErrorMessage('No se pudieron cargar los productos. Intente nuevamente más tarde.');
    }
}

// Renderizar productos
function renderProductos(productos) {
    productosContainer.innerHTML = '';
    
    if (!productos || productos.length === 0) {
        productosContainer.innerHTML = `
            <div class="col-12 text-center py-5">
                <div class="alert alert-info">
                    <i class="bi bi-info-circle me-2"></i>
                    No se encontraron productos con los filtros seleccionados.
                </div>
                <button class="btn btn-outline-primary mt-3" id="clear-filters-empty">
                    <i class="bi bi-arrow-counterclockwise me-2"></i>
                    Limpiar filtros
                </button>
            </div>
        `;
        
        document.getElementById('clear-filters-empty').addEventListener('click', function() {
            clearFiltersButton.click();
        });
        return;
    }
    
    productos.forEach(producto => {
        const disponible = producto.stock > 0;
        const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
        
        const cardElement = document.createElement('div');
        cardElement.className = 'col-md-6 col-lg-4 col-xl-3 mb-4';
        cardElement.innerHTML = `
            <div class="card h-100 ${disponible ? '' : 'opacity-75'}">
                <div class="position-relative">
                    <img src="${producto.imagen || 'images/producto-default.jpg'}" class="card-img-top" 
                        alt="${producto.nombre}" style="height: 200px; object-fit: contain;">
                    ${precioOferta ? `
                        <div class="position-absolute top-0 end-0 bg-danger text-white m-2 px-2 py-1 rounded">
                            <small>Oferta</small>
                        </div>
                    ` : ''}
                    ${!disponible ? `
                        <div class="position-absolute top-0 start-0 bg-dark text-white m-2 px-2 py-1 rounded">
                            <small>Sin stock</small>
                        </div>
                    ` : ''}
                </div>
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${producto.nombre}</h5>
                    <p class="card-text small text-muted mb-2">
                        ${producto.categoria ? `<a href="categorias.html?id=${producto.categoria.id}" class="text-decoration-none">
                            <i class="bi bi-tag"></i> ${producto.categoria.nombre}
                        </a>` : ''}
                    </p>
                    <div class="mb-2">
                        ${precioOferta ? `
                            <span class="text-decoration-line-through text-muted me-2">${formatPrice(producto.precio)}</span>
                            <span class="fw-bold text-danger">${formatPrice(producto.precioOferta)}</span>
                        ` : `
                            <span class="fw-bold">${formatPrice(producto.precio)}</span>
                        `}
                    </div>
                    <p class="card-text flex-grow-1 small">${producto.descripcion ? producto.descripcion.substring(0, 100) + (producto.descripcion.length > 100 ? '...' : '') : ''}</p>
                    <div class="d-grid gap-2">
                        <a href="producto-detalle.html?id=${producto.id}" class="btn btn-primary btn-sm">
                            <i class="bi bi-eye me-1"></i> Ver detalles
                        </a>
                    </div>
                </div>
            </div>
        `;
        
        productosContainer.appendChild(cardElement);
    });
}

// Renderizar paginación
function renderPagination() {
    paginationContainer.innerHTML = '';
    
    if (totalPages <= 1) {
        return;
    }
    
    // Botón anterior
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `
        <a class="page-link" href="#" aria-label="Anterior">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
    if (currentPage > 1) {
        prevLi.addEventListener('click', () => goToPage(currentPage - 1));
    }
    paginationContainer.appendChild(prevLi);
    
    // Determinar qué páginas mostrar
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, currentPage + 2);
    
    // Ajustar para mostrar siempre 5 páginas si hay suficientes
    if (endPage - startPage < 4 && totalPages > 5) {
        if (currentPage < totalPages - 2) {
            endPage = Math.min(totalPages, startPage + 4);
        } else {
            startPage = Math.max(1, endPage - 4);
        }
    }
    
    // Primera página si no está incluida
    if (startPage > 1) {
        const firstLi = document.createElement('li');
        firstLi.className = 'page-item';
        firstLi.innerHTML = '<a class="page-link" href="#">1</a>';
        firstLi.addEventListener('click', () => goToPage(1));
        paginationContainer.appendChild(firstLi);
        
        if (startPage > 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
            paginationContainer.appendChild(ellipsisLi);
        }
    }
    
    // Páginas numeradas
    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        if (i !== currentPage) {
            pageLi.addEventListener('click', () => goToPage(i));
        }
        paginationContainer.appendChild(pageLi);
    }
    
    // Última página si no está incluida
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
            paginationContainer.appendChild(ellipsisLi);
        }
        
        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="#">${totalPages}</a>`;
        lastLi.addEventListener('click', () => goToPage(totalPages));
        paginationContainer.appendChild(lastLi);
    }
    
    // Botón siguiente
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `
        <a class="page-link" href="#" aria-label="Siguiente">
            <span aria-hidden="true">&raquo;</span>
        </a>
    `;
    if (currentPage < totalPages) {
        nextLi.addEventListener('click', () => goToPage(currentPage + 1));
    }
    paginationContainer.appendChild(nextLi);
}

// Navegar a una página específica
function goToPage(page) {
    if (page !== currentPage && page >= 1 && page <= totalPages) {
        currentPage = page;
        loadProductos();
        updateURL();
        window.scrollTo(0, 0);
    }
}

// Actualizar contador de productos
function updateTotalCounter() {
    totalProductosElement.textContent = `${totalProducts} producto${totalProducts !== 1 ? 's' : ''} encontrado${totalProducts !== 1 ? 's' : ''}`;
}

// Mostrar indicador de carga
function showLoadingIndicator() {
    productosContainer.innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
            <p class="mt-2">Cargando productos...</p>
        </div>
    `;
}

// Mostrar mensaje de error
function showErrorMessage(message) {
    productosContainer.innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                ${message}
            </div>
            <button class="btn btn-outline-primary mt-3" id="retry-button">
                <i class="bi bi-arrow-clockwise me-2"></i>
                Intentar nuevamente
            </button>
        </div>
    `;
    
    document.getElementById('retry-button').addEventListener('click', function() {
        loadProductos();
    });
}

// Actualizar URL con parámetros de filtro
function updateURL() {
    const params = new URLSearchParams();
    
    if (currentFilters.categoriaId) {
        params.set('categoria', currentFilters.categoriaId);
    }
    
    if (currentFilters.precioMin) {
        params.set('min', currentFilters.precioMin);
    }
    
    if (currentFilters.precioMax) {
        params.set('max', currentFilters.precioMax);
    }
    
    if (currentFilters.soloDisponibles) {
        params.set('disponibles', 'true');
    }
    
    if (currentFilters.termino) {
        params.set('q', currentFilters.termino);
    }
    
    if (currentFilters.ordenarPor !== 'nombre-asc') {
        params.set('sort', currentFilters.ordenarPor);
    }
    
    if (currentPage > 1) {
        params.set('page', currentPage);
    }
    
    // Actualizar URL sin recargar la página
    const newURL = params.toString() ? `?${params.toString()}` : window.location.pathname;
    window.history.pushState({}, '', newURL);
}

// Procesar parámetros de URL
function parseURLParameters() {
    const params = new URLSearchParams(window.location.search);
    
    // Categoría
    if (params.has('categoria')) {
        const categoriaId = params.get('categoria');
        currentFilters.categoriaId = categoriaId;
        categoriaFilterElement.value = categoriaId;
    }
    
    // Rango de precios
    if (params.has('min')) {
        const precioMin = params.get('min');
        currentFilters.precioMin = precioMin;
        precioMinElement.value = precioMin;
    }
    
    if (params.has('max')) {
        const precioMax = params.get('max');
        currentFilters.precioMax = precioMax;
        precioMaxElement.value = precioMax;
    }
    
    // Solo disponibles
    if (params.has('disponibles')) {
        const soloDisponibles = params.get('disponibles') === 'true';
        currentFilters.soloDisponibles = soloDisponibles;
        soloDisponiblesElement.checked = soloDisponibles;
    }
    
    // Término de búsqueda
    if (params.has('q')) {
        const termino = params.get('q');
        currentFilters.termino = termino;
        searchInputElement.value = termino;
    }
    
    // Ordenación
    if (params.has('sort')) {
        const ordenarPor = params.get('sort');
        currentFilters.ordenarPor = ordenarPor;
        ordenarPorElement.value = ordenarPor;
    }
    
    // Página actual
    if (params.has('page')) {
        currentPage = parseInt(params.get('page'), 10) || 1;
    }
    
    // Si hay algún filtro, recargar productos
    if (params.toString()) {
        loadProductos();
    }
}