/**
 * Script mejorado para la página de categorías de iBai
 * 
 * Características principales:
 * - Carga asíncrona de categorías y productos
 * - Gestión avanzada de estado
 * - Animaciones de transición
 * - Gestión eficiente de caché de categorías
 * - Manejo de errores mejorado con reintentos automáticos
 * - Filtros rápidos en productos por categoría
 * - Mejoras de rendimiento
 */

// Namespace para la aplicación de categorías
const CategoriasApp = {
    // Estado de la aplicación
    state: {
        categoriaId: null,
        breadcrumb: [],
        currentPage: 1,
        pageSize: 8,
        totalProductos: 0,
        totalPages: 0,
        searchTerm: '',
        activeFilter: 'all',
        sortOrder: 'precio-asc',
        categoriaCache: new Map(), // Caché para categorías y subcategorías
        productoCache: new Map(),  // Caché para productos por categoría
        loadingRetryCount: 0,
        maxRetries: 3
    },
    
    // Elementos DOM
    elements: {
        categoriasPrincipales: document.getElementById('categorias-principales'),
        categoriaBreadcrumb: document.getElementById('categoria-breadcrumb'),
        categoriaSeleccionadaContainer: document.getElementById('categoria-seleccionada-container'),
        categoriaSeleccionadaTitulo: document.getElementById('categoria-seleccionada-titulo'),
        productosCategoriasContainer: document.getElementById('productos-categoria-container'),
        totalProductosCategoria: document.getElementById('total-productos-categoria'),
        paginationCategoria: document.getElementById('pagination-categoria'),
        subcategoriasContainer: document.getElementById('subcategorias-container'),
        subcategoriasTitulo: document.getElementById('subcategorias-titulo'),
        subcategoriasList: document.getElementById('subcategorias-list'),
        searchInput: document.getElementById('search-input'),
        searchButton: document.getElementById('search-button'),
        filterButtons: document.querySelectorAll('.subcategory-pill[data-filter]'),
        sortSelect: document.getElementById('ordenar-productos')
    },
    
    /**
     * Inicializa la aplicación de categorías
     */
    init: function() {
        // Cargar categorías principales
        this.loadCategoriasPrincipales();
        
        // Configurar eventos
        this.setupEventListeners();
        
        // Verificar parámetros URL
        this.parseURLParameters();
        
        // Animación de entrada para el contenedor de categorías
        this.animate(this.elements.categoriasPrincipales.parentElement, 'fadeIn');
        
        console.log('CategoriasApp inicializada correctamente');
    },
    
    /**
     * Configura los listeners de eventos para la interacción del usuario
     */
    setupEventListeners: function() {
        // Evento de búsqueda
        this.elements.searchButton.addEventListener('click', () => {
            const searchTerm = this.elements.searchInput.value.trim();
            if (searchTerm) {
                this.buscarCategorias(searchTerm);
            }
        });
        
        // Evento de Enter en búsqueda
        this.elements.searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                const searchTerm = this.elements.searchInput.value.trim();
                if (searchTerm) {
                    this.buscarCategorias(searchTerm);
                }
            }
        });
        
        // Eventos para filtros rápidos
        this.elements.filterButtons.forEach(button => {
            button.addEventListener('click', () => {
                // Remover clase active de todos los botones
                this.elements.filterButtons.forEach(btn => btn.classList.remove('active'));
                // Añadir clase active al botón clickeado
                button.classList.add('active');
                
                // Actualizar filtro activo
                this.state.activeFilter = button.getAttribute('data-filter');
                
                // Aplicar filtro
                this.applyProductFilters();
            });
        });
        
        // Evento para ordenar productos
        if (this.elements.sortSelect) {
            this.elements.sortSelect.addEventListener('change', () => {
                this.state.sortOrder = this.elements.sortSelect.value;
                this.applyProductFilters();
            });
        }
    },
    
    /**
     * Carga las categorías principales con animación
     */
    loadCategoriasPrincipales: async function() {
        try {
            this.showLoadingIndicator(this.elements.categoriasPrincipales);
            
            // Verificar si las categorías están en caché
            if (this.state.categoriaCache.has('principales')) {
                const categorias = this.state.categoriaCache.get('principales');
                this.renderCategorias(categorias, this.elements.categoriasPrincipales);
                return;
            }
            
            const categorias = await this.fetchWithRetry('/categorias/principales');
            
            if (categorias.length === 0) {
                this.elements.categoriasPrincipales.innerHTML = `
                    <div class="col-12 text-center py-4">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            No hay categorías disponibles.
                        </div>
                    </div>
                `;
                return;
            }
            
            // Guardar en caché
            this.state.categoriaCache.set('principales', categorias);
            
            // Renderizar con animación
            this.renderCategorias(categorias, this.elements.categoriasPrincipales);
            
        } catch (error) {
            console.error('Error al cargar categorías principales:', error);
            this.showErrorMessage(
                this.elements.categoriasPrincipales, 
                'No se pudieron cargar las categorías. Intente nuevamente más tarde.',
                () => this.loadCategoriasPrincipales()
            );
        }
    },
    
    /**
     * Carga las subcategorías de una categoría
     * @param {number} categoriaId - ID de la categoría padre
     */
    loadSubcategorias: async function(categoriaId) {
        try {
            this.showLoadingIndicator(this.elements.subcategoriasList);
            
            // Verificar caché
            const cacheKey = `subcats-${categoriaId}`;
            if (this.state.categoriaCache.has(cacheKey)) {
                const subcategorias = this.state.categoriaCache.get(cacheKey);
                
                if (subcategorias.length === 0) {
                    this.elements.subcategoriasContainer.style.display = 'none';
                    return;
                }
                
                this.animate(this.elements.subcategoriasContainer, 'fadeIn');
                this.elements.subcategoriasContainer.style.display = 'block';
                this.elements.subcategoriasTitulo.textContent = `Subcategorías de ${this.state.breadcrumb[this.state.breadcrumb.length - 1].nombre}`;
                this.renderCategorias(subcategorias, this.elements.subcategoriasList);
                return;
            }
            
            const subcategorias = await this.fetchWithRetry(`/categorias/${categoriaId}/subcategorias`);
            
            // Guardar en caché
            this.state.categoriaCache.set(cacheKey, subcategorias);
            
            if (subcategorias.length === 0) {
                this.elements.subcategoriasContainer.style.display = 'none';
                return;
            }
            
            this.animate(this.elements.subcategoriasContainer, 'fadeIn');
            this.elements.subcategoriasContainer.style.display = 'block';
            this.elements.subcategoriasTitulo.textContent = `Subcategorías de ${this.state.breadcrumb[this.state.breadcrumb.length - 1].nombre}`;
            this.renderCategorias(subcategorias, this.elements.subcategoriasList);
            
        } catch (error) {
            console.error('Error al cargar subcategorías:', error);
            this.elements.subcategoriasContainer.style.display = 'none';
        }
    },
    
    /**
     * Carga los productos de una categoría específica
     * @param {number} categoriaId - ID de la categoría
     * @param {number} page - Número de página
     */
    loadProductosCategoria: async function(categoriaId, page = 1) {
        try {
            this.state.currentPage = page;
            this.showLoadingIndicator(this.elements.productosCategoriasContainer);
            
            // Construir clave de caché
            const cacheKey = `products-${categoriaId}-page${page}`;
            let result;
            
            // Intentar obtener de caché
            if (this.state.productoCache.has(cacheKey)) {
                result = this.state.productoCache.get(cacheKey);
            } else {
                const url = `/productos/categoria/${categoriaId}?page=${page}&size=${this.state.pageSize}`;
                result = await this.fetchWithRetry(url);
                
                // Guardar en caché (solo si es un resultado válido)
                if (result && (result.data || result.length)) {
                    this.state.productoCache.set(cacheKey, result);
                }
            }
            
            this.state.totalProductos = result.total || result.length;
            this.state.totalPages = Math.ceil(this.state.totalProductos / this.state.pageSize);
            
            this.updateTotalCounter();
            this.renderProductosCategoria(result.data || result);
            this.renderPaginationCategoria();
            
            this.animate(this.elements.categoriaSeleccionadaContainer, 'fadeIn');
            this.elements.categoriaSeleccionadaContainer.style.display = 'block';
            this.elements.categoriaSeleccionadaTitulo.textContent = `Productos en ${this.state.breadcrumb[this.state.breadcrumb.length - 1].nombre}`;
            
        } catch (error) {
            console.error('Error al cargar productos de categoría:', error);
            this.showErrorMessage(
                this.elements.productosCategoriasContainer, 
                'No se pudieron cargar los productos. Intente nuevamente más tarde.',
                () => this.loadProductosCategoria(categoriaId, page)
            );
        }
    },
    
    /**
     * Carga la jerarquía de una categoría para el breadcrumb
     * @param {number} categoriaId - ID de la categoría
     * @returns {Array} Array con la jerarquía de categorías
     */
    loadCategoriaJerarquia: async function(categoriaId) {
        try {
            // Verificar caché
            const cacheKey = `jerarquia-${categoriaId}`;
            if (this.state.categoriaCache.has(cacheKey)) {
                const jerarquia = this.state.categoriaCache.get(cacheKey);
                this.updateBreadcrumb(jerarquia);
                return jerarquia;
            }
            
            const jerarquia = await this.fetchWithRetry(`/categorias/${categoriaId}/jerarquia`);
            
            // Guardar en caché
            this.state.categoriaCache.set(cacheKey, jerarquia);
            
            this.updateBreadcrumb(jerarquia);
            return jerarquia;
        } catch (error) {
            console.error('Error al cargar jerarquía de categoría:', error);
            this.updateBreadcrumb([]);
            return [];
        }
    },
    
    /**
     * Busca categorías por término
     * @param {string} termino - Término de búsqueda
     */
    buscarCategorias: async function(termino) {
        try {
            this.state.searchTerm = termino;
            this.showLoadingIndicator(this.elements.categoriasPrincipales);
            
            // Ocultar contenedores secundarios
            this.elements.categoriaSeleccionadaContainer.style.display = 'none';
            this.elements.subcategoriasContainer.style.display = 'none';
            
            // Actualizar breadcrumb
            this.updateBreadcrumb([{ id: null, nombre: `Resultados para "${termino}"` }]);
            
            const categorias = await this.fetchWithRetry(`/categorias/buscar?nombre=${encodeURIComponent(termino)}`);
            
            if (categorias.length === 0) {
                this.elements.categoriasPrincipales.innerHTML = `
                    <div class="col-12 text-center py-4">
                        <div class="alert alert-info">
                            <i class="bi bi-search me-2"></i>
                            No se encontraron categorías para "${termino}".
                        </div>
                        <button class="btn btn-outline-primary mt-3" id="volver-categorias">
                            <i class="bi bi-arrow-left me-2"></i>
                            Volver a todas las categorías
                        </button>
                    </div>
                `;
                
                document.getElementById('volver-categorias').addEventListener('click', () => {
                    this.loadCategoriasPrincipales();
                    this.elements.searchInput.value = '';
                    this.updateBreadcrumb([]);
                });
                return;
            }
            
            this.renderCategorias(categorias, this.elements.categoriasPrincipales);
        } catch (error) {
            console.error('Error al buscar categorías:', error);
            this.showErrorMessage(
                this.elements.categoriasPrincipales, 
                'No se pudieron buscar las categorías. Intente nuevamente más tarde.',
                () => this.buscarCategorias(termino)
            );
        }
    },
    
    /**
     * Aplica filtros a los productos mostrados
     */
    applyProductFilters: function() {
        // Obtener todos los cards de productos
        const productos = document.querySelectorAll('.product-card');
        if (!productos.length) return;
        
        // Contador para productos visibles
        let visibleCount = 0;
        
        productos.forEach(producto => {
            // Por defecto mostrar todos
            let shouldShow = true;
            
            // Aplicar filtro según el tipo seleccionado
            if (this.state.activeFilter === 'disponibles') {
                shouldShow = !producto.classList.contains('sin-stock');
            } else if (this.state.activeFilter === 'oferta') {
                shouldShow = producto.classList.contains('en-oferta');
            }
            
            // Actualizar visibilidad con animación
            if (shouldShow) {
                producto.classList.remove('d-none');
                producto.style.opacity = '0';
                setTimeout(() => {
                    producto.style.opacity = '1';
                }, 50 * visibleCount); // Escalonar las animaciones
                visibleCount++;
            } else {
                producto.classList.add('d-none');
            }
        });
        
        // Actualizar contador con productos filtrados
        this.elements.totalProductosCategoria.textContent = `${visibleCount} producto${visibleCount !== 1 ? 's' : ''} mostrado${visibleCount !== 1 ? 's' : ''}`;
        
        // Si no hay productos visibles, mostrar mensaje
        if (visibleCount === 0) {
            const noResultsEl = document.createElement('div');
            noResultsEl.className = 'col-12 text-center py-4';
            noResultsEl.innerHTML = `
                <div class="alert alert-info">
                    <i class="bi bi-filter-circle me-2"></i>
                    No hay productos que coincidan con los filtros seleccionados.
                </div>
            `;
            this.elements.productosCategoriasContainer.appendChild(noResultsEl);
        }
    },
    
    /**
     * Aplica ordenación a los productos mostrados
     */
    applySorting: function() {
        const productContainer = this.elements.productosCategoriasContainer;
        const productos = Array.from(productContainer.querySelectorAll('.product-card'));
        if (!productos.length) return;
        
        // Eliminar productos del DOM para reordenarlos
        productos.forEach(p => p.remove());
        
        // Ordenar según criterio seleccionado
        const [field, order] = this.state.sortOrder.split('-');
        
        productos.sort((a, b) => {
            let valA, valB;
            
            if (field === 'precio') {
                valA = parseFloat(a.dataset.precio);
                valB = parseFloat(b.dataset.precio);
            } else { // nombre
                valA = a.dataset.nombre.toLowerCase();
                valB = b.dataset.nombre.toLowerCase();
            }
            
            if (order === 'asc') {
                return valA > valB ? 1 : -1;
            } else {
                return valA < valB ? 1 : -1;
            }
        });
        
        // Reañadir productos ordenados
        productos.forEach(p => {
            productContainer.appendChild(p);
            // Animación suave al reordenar
            p.style.opacity = '0';
            setTimeout(() => {
                p.style.opacity = '1';
            }, 30);
        });
    },
    
    /**
     * Renderiza las categorías en un contenedor
     * @param {Array} categorias - Array de objetos categoría
     * @param {HTMLElement} container - Contenedor donde renderizar
     */
    renderCategorias: function(categorias, container) {
        container.innerHTML = '';
        
        const cardRow = document.createElement('div');
        cardRow.className = 'row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4';
        
        categorias.forEach((categoria, index) => {
            const cardCol = document.createElement('div');
            cardCol.className = 'col';
            
            // Añadir efecto de revelación escalonada
            cardCol.style.opacity = '0';
            cardCol.style.transform = 'translateY(20px)';
            
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
            cardCol.querySelector('.ver-categoria').addEventListener('click', (e) => {
                e.preventDefault();
                const categoriaId = e.currentTarget.getAttribute('data-id');
                this.selectCategoria(categoriaId);
            });
            
            // Animación de entrada escalonada
            setTimeout(() => {
                cardCol.style.opacity = '1';
                cardCol.style.transform = 'translateY(0)';
                cardCol.style.transition = 'all 0.4s ease-out';
            }, 50 * index);
            
            cardRow.appendChild(cardCol);
        });
        
        container.appendChild(cardRow);
    },
    
    /**
     * Renderiza los productos de una categoría
     * @param {Array} productos - Array de objetos producto
     */
    renderProductosCategoria: function(productos) {
        this.elements.productosCategoriasContainer.innerHTML = '';
        
        if (!productos || productos.length === 0) {
            this.elements.productosCategoriasContainer.innerHTML = `
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
        
        productos.forEach((producto, index) => {
            const disponible = producto.stock > 0;
            const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
            const precio = precioOferta ? producto.precioOferta : producto.precio;
            
            const cardCol = document.createElement('div');
            cardCol.className = 'col';
            cardCol.style.opacity = '0';
            cardCol.style.transform = 'translateY(15px)';
            
            const productCard = document.createElement('div');
            productCard.className = `card h-100 product-card ${!disponible ? 'sin-stock opacity-75' : ''} ${precioOferta ? 'en-oferta' : ''}`;
            productCard.dataset.precio = precio;
            productCard.dataset.nombre = producto.nombre;
            
            productCard.innerHTML = `
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
                            <span class="text-decoration-line-through text-muted me-2">${this.formatPrice(producto.precio)}</span>
                            <span class="fw-bold text-danger">${this.formatPrice(producto.precioOferta)}</span>
                        ` : `
                            <span class="fw-bold">${this.formatPrice(producto.precio)}</span>
                        `}
                    </div>
                    <p class="card-text flex-grow-1 small">${producto.descripcion ? producto.descripcion.substring(0, 80) + (producto.descripcion.length > 80 ? '...' : '') : ''}</p>
                    <div class="text-center mt-2">
                        <a href="producto-detalle.html?id=${producto.id}" class="btn btn-primary btn-sm">
                            <i class="bi bi-eye me-1"></i> Ver detalles
                        </a>
                    </div>
                </div>
            `;
            
            // Animación de entrada escalonada
            setTimeout(() => {
                cardCol.style.opacity = '1';
                cardCol.style.transform = 'translateY(0)';
                cardCol.style.transition = 'all 0.3s ease-out';
            }, 40 * index);
            
            cardCol.appendChild(productCard);
            cardRow.appendChild(cardCol);
        });
        
        this.elements.productosCategoriasContainer.appendChild(cardRow);
        
        // Aplicar filtros iniciales
        if (this.state.activeFilter !== 'all') {
            this.applyProductFilters();
        }
    },
    
    /**
     * Renderiza la paginación para productos de categoría
     */
    renderPaginationCategoria: function() {
        const paginationEl = this.elements.paginationCategoria;
        paginationEl.innerHTML = '';
        
        if (this.state.totalPages <= 1) {
            return;
        }
        
        // Botón anterior
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${this.state.currentPage === 1 ? 'disabled' : ''}`;
        prevLi.innerHTML = `
            <a class="page-link" href="#" aria-label="Anterior">
                <span aria-hidden="true">&laquo;</span>
            </a>
        `;
        if (this.state.currentPage > 1) {
            prevLi.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(this.state.currentPage - 1);
            });
        }
        paginationEl.appendChild(prevLi);
        
        // Determinar qué páginas mostrar
        let startPage = Math.max(1, this.state.currentPage - 2);
        let endPage = Math.min(this.state.totalPages, this.state.currentPage + 2);
        
        // Ajustar para mostrar siempre 5 páginas si hay suficientes
        if (endPage - startPage < 4 && this.state.totalPages > 5) {
            if (this.state.currentPage < this.state.totalPages - 2) {
                endPage = Math.min(this.state.totalPages, startPage + 4);
            } else {
                startPage = Math.max(1, endPage - 4);
            }
        }
        
        // Primera página si no está incluida
        if (startPage > 1) {
            const firstLi = document.createElement('li');
            firstLi.className = 'page-item';
            firstLi.innerHTML = '<a class="page-link" href="#">1</a>';
            firstLi.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(1);
            });
            paginationEl.appendChild(firstLi);
            
            if (startPage > 2) {
                const ellipsisLi = document.createElement('li');
                ellipsisLi.className = 'page-item disabled';
                ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
                paginationEl.appendChild(ellipsisLi);
            }
        }
        
        // Páginas numeradas
        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === this.state.currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#">${i}</a>`;
            if (i !== this.state.currentPage) {
                pageLi.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.goToPage(i);
                });
            }
            paginationEl.appendChild(pageLi);
        }
        
        // Última página si no está incluida
        if (endPage < this.state.totalPages) {
            if (endPage < this.state.totalPages - 1) {
                const ellipsisLi = document.createElement('li');
                ellipsisLi.className = 'page-item disabled';
                ellipsisLi.innerHTML = '<a class="page-link" href="#">...</a>';
                paginationEl.appendChild(ellipsisLi);
            }
            
            const lastLi = document.createElement('li');
            lastLi.className = 'page-item';
            lastLi.innerHTML = `<a class="page-link" href="#">${this.state.totalPages}</a>`;
            lastLi.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(this.state.totalPages);
            });
            paginationEl.appendChild(lastLi);
        }
        
        // Botón siguiente
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${this.state.currentPage === this.state.totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = `
            <a class="page-link" href="#" aria-label="Siguiente">
                <span aria-hidden="true">&raquo;</span>
            </a>
        `;
        if (this.state.currentPage < this.state.totalPages) {
            nextLi.addEventListener('click', (e) => {
                e.preventDefault();
                this.goToPage(this.state.currentPage + 1);
            });
        }
        paginationEl.appendChild(nextLi);
        
        // Animación de entrada para la paginación
        paginationEl.style.opacity = '0';
        setTimeout(() => {
            paginationEl.style.opacity = '1';
            paginationEl.style.transition = 'opacity 0.5s ease';
        }, 300);
    },
    
    /**
     * Navega a una página específica de productos
     * @param {number} page - Número de página
     */
    goToPage: function(page) {
        if (page !== this.state.currentPage && page >= 1 && page <= this.state.totalPages) {
            this.loadProductosCategoria(this.state.categoriaId, page);
            this.updateURL({ page });
            window.scrollTo({
                top: this.elements.categoriaSeleccionadaContainer.offsetTop - 20,
                behavior: 'smooth'
            });
        }
    },
    
    /**
     * Selecciona una categoría y carga sus detalles
     * @param {number} categoriaId - ID de la categoría a seleccionar
     */
    selectCategoria: async function(categoriaId) {
        if (categoriaId) {
            this.state.categoriaId = categoriaId;
            this.state.currentPage = 1;
            this.state.activeFilter = 'all';
            
            // Reiniciar filtros visuales
            this.elements.filterButtons.forEach(btn => {
                if (btn.getAttribute('data-filter') === 'all') {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
            
            // Cargar jerarquía para breadcrumb con animación de carga
            this.animate(this.elements.categoriaBreadcrumb, 'pulse');
            const jerarquia = await this.loadCategoriaJerarquia(categoriaId);
            
            // Cargar productos de la categoría
            await this.loadProductosCategoria(categoriaId);
            
            // Cargar subcategorías
            await this.loadSubcategorias(categoriaId);
            
            // Actualizar URL
            this.updateURL({ id: categoriaId });
            
            // Desplazarse a la parte superior con animación suave
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        }
    },
    
    /**
     * Actualiza el breadcrumb con la jerarquía de categorías
     * @param {Array} jerarquia - Array con la jerarquía de categorías
     */
    updateBreadcrumb: function(jerarquia) {
        // Guardar jerarquía actual
        this.state.breadcrumb = jerarquia;
        
        // Limpiar breadcrumb actual
        this.elements.categoriaBreadcrumb.innerHTML = '';
        
        // Añadir home con animación
        const homeLi = document.createElement('li');
        homeLi.className = 'breadcrumb-item';
        homeLi.innerHTML = '<a href="categorias.html">Todas las categorías</a>';
        homeLi.addEventListener('click', (e) => {
            e.preventDefault();
            this.loadCategoriasPrincipales();
            this.elements.categoriaSeleccionadaContainer.style.display = 'none';
            this.elements.subcategoriasContainer.style.display = 'none';
            this.state.categoriaId = null;
            this.updateBreadcrumb([]);
            this.updateURL({});
        });
        this.elements.categoriaBreadcrumb.appendChild(homeLi);
        
        // Si no hay jerarquía, terminar
        if (!jerarquia || jerarquia.length === 0) {
            return;
        }
        
        // Añadir cada nivel de la jerarquía con efecto escalonado
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
                li.querySelector('a').addEventListener('click', (e) => {
                    e.preventDefault();
                    this.selectCategoria(categoria.id);
                });
            }
            
            // Efecto de aparición escalonada
            li.style.opacity = '0';
            this.elements.categoriaBreadcrumb.appendChild(li);
            
            setTimeout(() => {
                li.style.opacity = '1';
                li.style.transition = 'opacity 0.3s ease';
            }, 100 * index);
        });
    },
    
    /**
     * Actualiza el contador de productos
     */
    updateTotalCounter: function() {
        this.elements.totalProductosCategoria.textContent = `${this.state.totalProductos} producto${this.state.totalProductos !== 1 ? 's' : ''} encontrado${this.state.totalProductos !== 1 ? 's' : ''}`;
        
        // Animación del contador
        this.animate(this.elements.totalProductosCategoria, 'fadeIn');
    },
    
    /**
     * Muestra un indicador de carga en un contenedor
     * @param {HTMLElement} container - Contenedor donde mostrar el indicador
     */
    showLoadingIndicator: function(container) {
        container.innerHTML = `
            <div class="col-12 text-center py-4 loading-indicator">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-2">Cargando...</p>
            </div>
        `;
        
        // Animación de pulso para el spinner
        const spinner = container.querySelector('.spinner-border');
        if (spinner) {
            setInterval(() => {
                spinner.style.transform = 'scale(1.1)';
                setTimeout(() => {
                    spinner.style.transform = 'scale(1)';
                }, 300);
            }, 1500);
        }
    },
    
    /**
     * Muestra un mensaje de error en un contenedor
     * @param {HTMLElement} container - Contenedor donde mostrar el error
     * @param {string} message - Mensaje de error
     * @param {Function} retryCallback - Función a ejecutar al reintentar
     */
    showErrorMessage: function(container, message, retryCallback) {
        this.state.loadingRetryCount = 0;
        
        container.innerHTML = `
            <div class="col-12 text-center py-4 error-container">
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    ${message}
                </div>
                <button class="btn btn-outline-primary mt-3" id="retry-button-${container.id || 'generic'}">
                    <i class="bi bi-arrow-clockwise me-2"></i>
                    Intentar nuevamente
                </button>
            </div>
        `;
        
        // Animación de entrada para el error
        const errorContainer = container.querySelector('.error-container');
        errorContainer.style.opacity = '0';
        errorContainer.style.transform = 'translateY(-10px)';
        
        setTimeout(() => {
            errorContainer.style.opacity = '1';
            errorContainer.style.transform = 'translateY(0)';
            errorContainer.style.transition = 'all 0.4s ease';
        }, 10);
        
        // Añadir evento para reintentar la carga
        const retryButton = document.getElementById(`retry-button-${container.id || 'generic'}`);
        if (retryButton && retryCallback) {
            retryButton.addEventListener('click', retryCallback);
        }
    },
    
    /**
     * Realiza una petición a la API con reintentos automáticos
     * @param {string} endpoint - Endpoint de la API
     * @param {Object} options - Opciones de fetch
     * @returns {Promise} Promesa con el resultado
     */
    fetchWithRetry: async function(endpoint, options = {}) {
        try {
            const result = await fetchAPI(endpoint, options);
            this.state.loadingRetryCount = 0; // Reiniciar contador al éxito
            return result;
        } catch (error) {
            this.state.loadingRetryCount++;
            
            if (this.state.loadingRetryCount <= this.state.maxRetries) {
                console.log(`Reintentando petición (${this.state.loadingRetryCount}/${this.state.maxRetries})...`);
                
                // Esperar tiempo exponencial según el número de intentos (backoff exponencial)
                const waitTime = Math.min(1000 * Math.pow(2, this.state.loadingRetryCount - 1), 5000);
                
                await new Promise(resolve => setTimeout(resolve, waitTime));
                return this.fetchWithRetry(endpoint, options);
            }
            
            throw error;
        }
    },
    
    /**
     * Actualiza la URL con parámetros
     * @param {Object} params - Parámetros a actualizar
     */
    updateURL: function(params = {}) {
        const urlParams = new URLSearchParams();
        
        // Añadir ID de categoría si existe
        if (params.id || this.state.categoriaId) {
            urlParams.set('id', params.id || this.state.categoriaId);
        }
        
        // Añadir página si es distinta de 1
        if ((params.page || this.state.currentPage) > 1) {
            urlParams.set('page', params.page || this.state.currentPage);
        }
        
        // Añadir término de búsqueda si existe
        if (this.state.searchTerm) {
            urlParams.set('q', this.state.searchTerm);
        }
        
        // Actualizar URL sin recargar la página
        const newURL = urlParams.toString() ? `?${urlParams.toString()}` : window.location.pathname;
        window.history.pushState({}, '', newURL);
    },
    
    /**
     * Procesa parámetros de URL
     */
    parseURLParameters: function() {
        const params = new URLSearchParams(window.location.search);
        
        // Procesar término de búsqueda
        if (params.has('q')) {
            const searchTerm = params.get('q');
            this.elements.searchInput.value = searchTerm;
            this.buscarCategorias(searchTerm);
            return; // Si hay búsqueda, ignorar otros parámetros
        }
        
        // Procesar ID de categoría
        if (params.has('id')) {
            const categoriaId = params.get('id');
            
            // Procesar página si existe
            if (params.has('page')) {
                this.state.currentPage = parseInt(params.get('page'), 10) || 1;
            }
            
            // Seleccionar categoría
            this.selectCategoria(categoriaId);
        }
    },
    
    /**
     * Aplica una animación a un elemento
     * @param {HTMLElement} element - Elemento a animar
     * @param {string} animationType - Tipo de animación
     */
    animate: function(element, animationType) {
        if (!element) return;
        
        // Remover clases de animación existentes
        element.classList.remove('animate__animated', 'animate__fadeIn', 'animate__pulse', 'animate__fadeOut');
        
        // Force reflow para reiniciar la animación
        void element.offsetWidth;
        
        // Aplicar animación según tipo
        switch (animationType) {
            case 'fadeIn':
                element.style.opacity = '0';
                setTimeout(() => {
                    element.style.opacity = '1';
                    element.style.transition = 'opacity 0.4s ease-in-out';
                }, 10);
                break;
            case 'fadeOut':
                element.style.opacity = '1';
                setTimeout(() => {
                    element.style.opacity = '0';
                    element.style.transition = 'opacity 0.4s ease-in-out';
                }, 10);
                break;
            case 'pulse':
                element.style.transform = 'scale(1.03)';
                element.style.transition = 'transform 0.2s ease-in-out';
                setTimeout(() => {
                    element.style.transform = 'scale(1)';
                }, 300);
                break;
        }
    },
    
    /**
     * Formatea un precio
     * @param {number} price - Precio a formatear
     * @returns {string} Precio formateado
     */
    formatPrice: function(price) {
        return new Intl.NumberFormat('es-ES', {
            style: 'currency',
            currency: 'EUR'
        }).format(price);
    }
};

// Inicializar la aplicación cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', function() {
    CategoriasApp.init();
});