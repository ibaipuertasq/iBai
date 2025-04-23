/**
 * Script para la página de categorías
 */

// Variables de estado
let currentCategoriaId = null;
let currentBreadcrumb = [];
let currentPage = 1;
const PAGE_SIZE = 8;
let totalProductos = 0;
let totalPages = 0;
let searchTerm = '';

// Elementos del DOM
const categoriasPrincipales = document.getElementById('categorias-principales');
const categoriaBreadcrumb = document.getElementById('categoria-breadcrumb');
const categoriaSeleccionadaContainer = document.getElementById('categoria-seleccionada-container');
const categoriaSeleccionadaTitulo = document.getElementById('categoria-seleccionada-titulo');
const productosCategoriasContainer = document.getElementById('productos-categoria-container');
const totalProductosCategoria = document.getElementById('total-productos-categoria');
const paginationCategoria = document.getElementById('pagination-categoria');
const subcategoriasContainer = document.getElementById('subcategorias-container');
const subcategoriasTitulo = document.getElementById('subcategorias-titulo');
const subcategoriasList = document.getElementById('subcategorias-list');
const searchInput = document.getElementById('search-input');
const searchButton = document.getElementById('search-button');

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Cargar categorías principales
    loadCategoriasPrincipales();
    
    // Configurar eventos
    setupEventListeners();
    
    // Verificar si hay parámetros en la URL
    parseURLParameters();
});

// Configuración de eventos
function setupEventListeners() {
    // Evento de búsqueda
    searchButton.addEventListener('click', function() {
        searchTerm = searchInput.value.trim();
        if (searchTerm) {
            buscarCategorias(searchTerm);
        }
    });
    
    // Evento de Enter en búsqueda
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchTerm = searchInput.value.trim();
            if (searchTerm) {
                buscarCategorias(searchTerm);
            }
        }
    });
}

// Cargar categorías principales
async function loadCategoriasPrincipales() {
    try {
        showLoadingIndicator(categoriasPrincipales);
        
        const categorias = await fetchAPI('/categorias/principales');
        
        if (categorias.length === 0) {
            categoriasPrincipales.innerHTML = `
                <div class="col-12 text-center py-4">
                    <div class="alert alert-info">
                        No hay categorías disponibles.
                    </div>
                </div>
            `;
            return;
        }
        
        renderCategorias(categorias, categoriasPrincipales);
    } catch (error) {
        console.error('Error al cargar categorías principales:', error);
        showErrorMessage(categoriasPrincipales, 'No se pudieron cargar las categorías. Intente nuevamente más tarde.');
    }
}

// Cargar subcategorías
async function loadSubcategorias(categoriaId) {
    try {
        showLoadingIndicator(subcategoriasList);
        
        const subcategorias = await fetchAPI(`/categorias/${categoriaId}/subcategorias`);
        
        if (subcategorias.length === 0) {
            subcategoriasContainer.style.display = 'none';
            return;
        }
        
        subcategoriasContainer.style.display = 'block';
        subcategoriasTitulo.textContent = `Subcategorías de ${currentBreadcrumb[currentBreadcrumb.length - 1].nombre}`;
        renderCategorias(subcategorias, subcategoriasList);
    } catch (error) {
        console.error('Error al cargar subcategorías:', error);
        subcategoriasContainer.style.display = 'none';
    }
}

// Cargar productos de una categoría
async function loadProductosCategoria(categoriaId, page = 1) {
    try {
        currentPage = page;
        showLoadingIndicator(productosCategoriasContainer);
        
        const url = `/productos/categoria/${categoriaId}?page=${page}&size=${PAGE_SIZE}`;
        const result = await fetchAPI(url);
        
        totalProductos = result.total || result.length;
        totalPages = Math.ceil(totalProductos / PAGE_SIZE);
        
        updateTotalCounter();
        renderProductosCategoria(result.data || result);
        renderPaginationCategoria();
        
        categoriaSeleccionadaContainer.style.display = 'block';
        categoriaSeleccionadaTitulo.textContent = `Productos en ${currentBreadcrumb[currentBreadcrumb.length - 1].nombre}`;
    } catch (error) {
        console.error('Error al cargar productos de categoría:', error);
        showErrorMessage(productosCategoriasContainer, 'No se pudieron cargar los productos. Intente nuevamente más tarde.');
    }
}

// Cargar jerarquía de categoría
async function loadCategoriaJerarquia(categoriaId) {
    try {
        const jerarquia = await fetchAPI(`/categorias/${categoriaId}/jerarquia`);
        updateBreadcrumb(jerarquia);
        return jerarquia;
    } catch (error) {
        console.error('Error al cargar jerarquía de categoría:', error);
        return [];
    }
}

// Buscar categorías
async function buscarCategorias(termino) {
    try {
        showLoadingIndicator(categoriasPrincipales);
        
        // Ocultar contenedores de productos y subcategorías
        categoriaSeleccionadaContainer.style.display = 'none';
        subcategoriasContainer.style.display = 'none';
        
        // Actualizar breadcrumb
        updateBreadcrumb([{ id: null, nombre: `Resultados para "${termino}"` }]);
        
        const categorias = await fetchAPI(`/categorias/buscar?nombre=${encodeURIComponent(termino)}`);
        
        if (categorias.length === 0) {
            categoriasPrincipales.innerHTML = `
                <div class="col-12 text-center py-4">
                    <div class="alert alert-info">
                        No se encontraron categorías para "${termino}".
                    </div>
                    <button class="btn btn-outline-primary mt-3" id="volver-categorias">
                        <i class="bi bi-arrow-left me-2"></i>
                        Volver a todas las categorías
                    </button>
                </div>
            `;
            
            document.getElementById('volver-categorias').addEventListener('click', function() {
                loadCategoriasPrincipales();
                searchInput.value = '';
                updateBreadcrumb([]);
            });
            return;
        }
        
        renderCategorias(categorias, categoriasPrincipales);
    } catch (error) {
        console.error('Error al buscar categorías:', error);
        showErrorMessage(categoriasPrincipales, 'No se pudieron buscar las categorías. Intente nuevamente más tarde.');
    }
}

// Renderizar categorías
function renderCategorias(categorias, container) {
    container.innerHTML = '';
    
    const cardRow = document.createElement('div');
    cardRow.className = 'row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4';
    
    categorias.forEach(categoria => {
        const cardCol = document.createElement('div');
        cardCol.className = 'col';
        cardCol.innerHTML = `
            <div class="card h-100 categoria-card">
                <div class="card-body text-center">
                    <div class="categoria-icon mb-3">
                        <i class="bi bi-folder fs-1"></i>
                    </div>
                    <h5 class="card-title">${categoria.nombre}</h5>
                    <p class="card-text small text-muted">
                        ${categoria.descripcion ? categoria.descripcion.substring(0, 50) + (categoria.descripcion.length > 50 ? '...' : '') : 'Sin descripción'}
                    </p>
                </div>
                <div class="card-footer bg-transparent text-center">
                    <button class="btn btn-primary btn-sm ver-categoria" data-id="${categoria.id}">
                        <i class="bi bi-eye me-1"></i> Ver productos
                    </button>
                </div>
            </div>
        `;
        
        // Evento para ver productos de la categoría
        cardCol.querySelector('.ver-categoria').addEventListener('click', function() {
            const categoriaId = this.getAttribute('data-id');
            selectCategoria(categoriaId);
        });
        
        cardRow.appendChild(cardCol);
    });
    
    container.appendChild(cardRow);
}

// Renderizar productos de categoría
function renderProductosCategoria(productos) {
    productosCategoriasContainer.innerHTML = '';
    
    if (!productos || productos.length === 0) {
        productosCategoriasContainer.innerHTML = `
            <div class="col-12 text-center py-4">
                <div class="alert alert-info">
                    <i class="bi bi-info-circle me-2"></i>
                    No hay productos disponibles en esta categoría.
                </div>
            </div>
        `;
        return;
    }
    
    const cardRow = document.createElement('div');
    cardRow.className = 'row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4';
    
    productos.forEach(producto => {
        const disponible = producto.stock > 0;
        const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
        
        const cardCol = document.createElement('div');
        cardCol.className = 'col';
        cardCol.innerHTML = `
            <div class="card h-100 ${disponible ? '' : 'opacity-75'}">
                <div class="position-relative">
                    <img src="${producto.imagen || 'images/producto-default.jpg'}" class="card-img-top" 
                        alt="${producto.nombre}" style="height: 180px; object-fit: contain;">
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
                    <div class="mb-2">
                        ${precioOferta ? `
                            <span class="text-decoration-line-through text-muted me-2">${formatPrice(producto.precio)}</span>
                            <span class="fw-bold text-danger">${formatPrice(producto.precioOferta)}</span>
                        ` : `
                            <span class="fw-bold">${formatPrice(producto.precio)}</span>
                        `}
                    </div>
                    <p class="card-text flex-grow-1 small">${producto.descripcion ? producto.descripcion.substring(0, 80) + (producto.descripcion.length > 80 ? '...' : '') : ''}</p>
                    <div class="text-center mt-2">
                        <a href="producto-detalle.html?id=${producto.id}" class="btn btn-primary btn-sm">
                            <i class="bi bi-eye me-1"></i> Ver detalles
                        </a>
                    </div>
                </div>
            </div>
        `;
        
        cardRow.appendChild(cardCol);
    });
    
    productosCategoriasContainer.appendChild(cardRow);
}

// Renderizar paginación para productos de categoría
function renderPaginationCategoria() {
    paginationCategoria.innerHTML = '';
    
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
    paginationCategoria.appendChild(prevLi);
    
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
        paginationCategoria.appendChild(firstLi);
        
        if (startPage > 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
            paginationCategoria.appendChild(ellipsisLi);
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
        paginationCategoria.appendChild(pageLi);
    }
    
    // Última página si no está incluida
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
            paginationCategoria.appendChild(ellipsisLi);
        }
        
        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="#">${totalPages}</a>`;
        lastLi.addEventListener('click', () => goToPage(totalPages));
        paginationCategoria.appendChild(lastLi);
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
    paginationCategoria.appendChild(nextLi);
}

// Navegar a una página específica de productos
function goToPage(page) {
    if (page !== currentPage && page >= 1 && page <= totalPages) {
        loadProductosCategoria(currentCategoriaId, page);
        updateURL({ page });
        window.scrollTo(0, categoriaSeleccionadaContainer.offsetTop - 20);
    }
}

// Seleccionar una categoría
async function selectCategoria(categoriaId) {
    if (categoriaId) {
        currentCategoriaId = categoriaId;
        currentPage = 1;
        
        // Cargar jerarquía para breadcrumb
        const jerarquia = await loadCategoriaJerarquia(categoriaId);
        
        // Cargar productos de la categoría
        loadProductosCategoria(categoriaId);
        
        // Cargar subcategorías
        loadSubcategorias(categoriaId);
        
        // Actualizar URL
        updateURL({ id: categoriaId });
        
        // Desplazarse a la parte superior
        window.scrollTo(0, 0);
    }
}

// Actualizar breadcrumb
function updateBreadcrumb(jerarquia) {
    // Guardar jerarquía actual
    currentBreadcrumb = jerarquia;
    
    // Limpiar breadcrumb actual
    categoriaBreadcrumb.innerHTML = '';
    
    // Añadir home
    const homeLi = document.createElement('li');
    homeLi.className = 'breadcrumb-item';
    homeLi.innerHTML = '<a href="categorias.html">Todas las categorías</a>';
    homeLi.addEventListener('click', function(e) {
        e.preventDefault();
        loadCategoriasPrincipales();
        categoriaSeleccionadaContainer.style.display = 'none';
        subcategoriasContainer.style.display = 'none';
        currentCategoriaId = null;
        updateBreadcrumb([]);
        updateURL({});
    });
    categoriaBreadcrumb.appendChild(homeLi);
    
    // Si no hay jerarquía, terminar
    if (!jerarquia || jerarquia.length === 0) {
        return;
    }
    
    // Añadir cada nivel de la jerarquía
    jerarquia.forEach((categoria, index) => {
        const li = document.createElement('li');
        
        if (index === jerarquia.length - 1) {
            // Último elemento (categoría actual)
            li.className = 'breadcrumb-item active';
            li.setAttribute('aria-current', 'page');
            li.textContent = categoria.nombre;
        } else {
            // Elementos intermedios (categorías padre)
            li.className = 'breadcrumb-item';
            li.innerHTML = `<a href="#" data-id="${categoria.id}">${categoria.nombre}</a>`;
            li.querySelector('a').addEventListener('click', function(e) {
                e.preventDefault();
                selectCategoria(categoria.id);
            });
        }
        
        categoriaBreadcrumb.appendChild(li);
    });
}

// Actualizar contador de productos
function updateTotalCounter() {
    totalProductosCategoria.textContent = `${totalProductos} producto${totalProductos !== 1 ? 's' : ''} encontrado${totalProductos !== 1 ? 's' : ''}`;
}

// Mostrar indicador de carga
function showLoadingIndicator(container) {
    container.innerHTML = `
        <div class="col-12 text-center py-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
            </div>
            <p class="mt-2">Cargando...</p>
        </div>
    `;
}

// Mostrar mensaje de error
function showErrorMessage(container, message) {
    container.innerHTML = `
        <div class="col-12 text-center py-4">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                ${message}
            </div>
            <button class="btn btn-outline-primary mt-3" id="retry-button-${container.id}">
                <i class="bi bi-arrow-clockwise me-2"></i>
                Intentar nuevamente
            </button>
        </div>
    `;
    
    // Añadir evento para reintentar la carga
    const retryButton = document.getElementById(`retry-button-${container.id}`);
    if (retryButton) {
        retryButton.addEventListener('click', function() {
            if (container === categoriasPrincipales) {
                loadCategoriasPrincipales();
            } else if (container === productosCategoriasContainer && currentCategoriaId) {
                loadProductosCategoria(currentCategoriaId, currentPage);
            } else if (container === subcategoriasList && currentCategoriaId) {
                loadSubcategorias(currentCategoriaId);
            }
        });
    }
}

// Actualizar URL con parámetros
function updateURL(params = {}) {
    const urlParams = new URLSearchParams();
    
    // Añadir ID de categoría si existe
    if (params.id || currentCategoriaId) {
        urlParams.set('id', params.id || currentCategoriaId);
    }
    
    // Añadir página si es distinta de 1
    if ((params.page || currentPage) > 1) {
        urlParams.set('page', params.page || currentPage);
    }
    
    // Actualizar URL sin recargar la página
    const newURL = urlParams.toString() ? `?${urlParams.toString()}` : window.location.pathname;
    window.history.pushState({}, '', newURL);
}

// Procesar parámetros de URL
function parseURLParameters() {
    const params = new URLSearchParams(window.location.search);
    
    // Procesar ID de categoría
    if (params.has('id')) {
        const categoriaId = params.get('id');
        
        // Procesar página si existe
        if (params.has('page')) {
            currentPage = parseInt(params.get('page'), 10) || 1;
        }
        
        // Seleccionar categoría
        selectCategoria(categoriaId);
    }
}