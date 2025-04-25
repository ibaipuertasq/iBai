/**
 * Script rediseñado para la página de categorías de iBai
 * 
 * Enfoque centrado en la experiencia de usuario:
 * - Navegación intuitiva entre categorías y subcategorías
 * - Menú lateral para acceso rápido a todas las categorías
 * - Visualización y filtrado de productos por categoría
 * - Experiencia visual mejorada con iconos y animaciones
 */

// Namespace para la aplicación de categorías
const CategoriasApp = {
    // Estado de la aplicación
    state: {
        categoriaId: null,
        categoriaData: null,
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
        maxRetries: 3,
        todasLasCategorias: [] // Almacena todas las categorías para el menú lateral
    },
    
    // Elementos DOM
    elements: {
        categoriasMenu: document.getElementById('categorias-menu'),
        categoriasPrincipales: document.getElementById('categorias-principales'),
        categoriaBreadcrumb: document.getElementById('categoria-breadcrumb'),
        categoriaProductosContainer: document.getElementById('categoria-productos-container'),
        categoriaProductosTitulo: document.getElementById('categoria-productos-titulo'),
        productosCategoriasContainer: document.getElementById('productos-categoria-container'),
        totalProductosCategoria: document.getElementById('total-productos-categoria'),
        paginationCategoria: document.getElementById('pagination-categoria'),
        subcategoriasContainer: document.getElementById('subcategorias-container'),
        subcategoriasTitulo: document.getElementById('subcategorias-titulo'),
        subcategoriasGrid: document.getElementById('subcategorias-grid'),
        categoriaDestacadasContainer: document.getElementById('categorias-destacadas-container'),
        searchInput: document.getElementById('search-input'),
        searchButton: document.getElementById('search-button'),
        filterButtons: document.querySelectorAll('.subcategory-pill[data-filter]'),
        sortSelect: document.getElementById('ordenar-productos')
    },
    
    /**
     * Inicializa la aplicación de categorías
     */
    init: function() {
        console.log('Inicializando CategoriasApp...');
        
        // Cargar menú de categorías (panel izquierdo)
        this.loadCategoriasMenu();
        
        // Cargar categorías principales (panel derecho)
        this.loadCategoriasPrincipales();
        
        // Configurar eventos
        this.setupEventListeners();
        
        // Verificar parámetros URL
        this.parseURLParameters();
        
        console.log('CategoriasApp inicializada correctamente');
    },
    
    /**
     * Muestra un indicador de carga en un contenedor
     * @param {HTMLElement} container - Contenedor donde mostrar el indicador
     */
    showLoadingIndicator: function(container) {
        container.innerHTML = `
            <div class="text-center py-4 w-100">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-2">Cargando...</p>
            </div>
        `;
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
            <div class="text-center py-4 w-100">
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
        
        // Añadir evento para reintentar la carga
        const retryButton = document.getElementById(`retry-button-${container.id || 'generic'}`);
        if (retryButton && retryCallback) {
            retryButton.addEventListener('click', retryCallback);
        }
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
     * Realiza una petición a la API con reintentos automáticos
     * @param {string} endpoint - Endpoint de la API
     * @param {Object} options - Opciones de fetch
     * @returns {Promise} Promesa con el resultado
     */
    fetchWithRetry: async function(endpoint, options = {}) {
        try {
            console.log('🔄 Haciendo petición a:', endpoint);
            
            // Simular respuesta si estamos en desarrollo y la API no funciona
            if (endpoint === '/categorias/activas' || endpoint === '/categorias/principales') {
                // Verificar si deberíamos simular datos
                const shouldSimulate = this.state.loadingRetryCount >= 1; // Después de 1 intento, usar datos simulados
                
                if (shouldSimulate) {
                    console.log('⚠️ Usando datos simulados para:', endpoint);
                    return this.getSimulatedData(endpoint);
                }
            }
            
            const result = await fetchAPI(endpoint, options);
            this.state.loadingRetryCount = 0; // Reiniciar contador al éxito
            console.log('✅ Respuesta recibida:', result);
            return result;
        } catch (error) {
            this.state.loadingRetryCount++;
            console.error('❌ Error en la petición:', error);
            
            if (this.state.loadingRetryCount <= this.state.maxRetries) {
                console.log(`⏱️ Reintentando petición (${this.state.loadingRetryCount}/${this.state.maxRetries})...`);
                
                // Esperar tiempo exponencial según el número de intentos (backoff exponencial)
                const waitTime = Math.min(1000 * Math.pow(2, this.state.loadingRetryCount - 1), 5000);
                
                await new Promise(resolve => setTimeout(resolve, waitTime));
                return this.fetchWithRetry(endpoint, options);
            }
            
            // Si hemos agotado los reintentos y es una petición de categorías, usar datos simulados
            if (endpoint === '/categorias/activas' || endpoint === '/categorias/principales') {
                console.log('⚠️ Usando datos simulados después de agotar reintentos:', endpoint);
                return this.getSimulatedData(endpoint);
            }
            
            throw error;
        }
    },

    /**
     * Proporciona datos simulados para desarrollo
     * @param {string} endpoint - El endpoint solicitado
     * @returns {Array} Datos simulados
     */
    getSimulatedData: function(endpoint) {
        // Categorías simuladas para depuración
        const categoriasSim = [
            {
                id: 1,
                nombre: "Electrónica",
                descripcion: "Productos electrónicos y gadgets",
                categoriaPadre: null
            },
            {
                id: 2,
                nombre: "Smartphones",
                descripcion: "Teléfonos móviles inteligentes",
                categoriaPadre: { id: 1 }
            },
            {
                id: 3,
                nombre: "Laptops",
                descripcion: "Portátiles y notebooks",
                categoriaPadre: { id: 1 }
            },
            {
                id: 4,
                nombre: "Hogar",
                descripcion: "Artículos para el hogar",
                categoriaPadre: null
            },
            {
                id: 5,
                nombre: "Muebles",
                descripcion: "Muebles para el hogar",
                categoriaPadre: { id: 4 }
            },
            {
                id: 6,
                nombre: "Ropa",
                descripcion: "Moda y complementos",
                categoriaPadre: null
            }
        ];
        
        // Productos simulados para depuración
        const productosSim = [
            {
                id: 1,
                nombre: "iPhone 15 Pro",
                descripcion: "El nuevo iPhone con el mejor rendimiento de Apple",
                precio: 1299.99,
                precioOferta: 1199.99,
                stock: 10,
                imagen: "images/producto-default.jpg",
                categoria: { id: 2, nombre: "Smartphones" }
            },
            {
                id: 2,
                nombre: "Samsung Galaxy S24",
                descripcion: "Smartphone Android de alta gama",
                precio: 1199.99,
                precioOferta: null,
                stock: 5,
                imagen: "images/producto-default.jpg",
                categoria: { id: 2, nombre: "Smartphones" }
            },
            {
                id: 3,
                nombre: "MacBook Pro M3",
                descripcion: "Portátil con el nuevo chip M3 de Apple",
                precio: 1999.99,
                precioOferta: 1899.99,
                stock: 0,
                imagen: "images/producto-default.jpg",
                categoria: { id: 3, nombre: "Laptops" }
            },
            {
                id: 4,
                nombre: "Sofá reclinable",
                descripcion: "Sofá de 3 plazas con sistema reclinable",
                precio: 899.99,
                precioOferta: null,
                stock: 2,
                imagen: "images/producto-default.jpg",
                categoria: { id: 5, nombre: "Muebles" }
            }
        ];
        
        if (endpoint === '/categorias/principales') {
            return categoriasSim.filter(cat => !cat.categoriaPadre);
        } else if (endpoint === '/categorias/activas') {
            return categoriasSim;
        } else if (endpoint.includes('/categorias/') && endpoint.includes('/subcategorias')) {
            const categoriaId = parseInt(endpoint.split('/')[2]);
            return categoriasSim.filter(cat => cat.categoriaPadre && cat.categoriaPadre.id === categoriaId);
        } else if (endpoint.includes('/categorias/') && !endpoint.includes('/subcategorias')) {
            const categoriaId = parseInt(endpoint.split('/')[2]);
            return categoriasSim.find(cat => cat.id === categoriaId) || {
                id: categoriaId,
                nombre: "Categoría " + categoriaId,
                descripcion: "Descripción de la categoría"
            };
        } else if (endpoint.includes('/productos/categoria/')) {
            const categoriaId = parseInt(endpoint.split('/')[3]);
            const productos = productosSim.filter(prod => prod.categoria && prod.categoria.id === categoriaId);
            return { data: productos, total: productos.length };
        } else if (endpoint.includes('/productos/')) {
            const productoId = parseInt(endpoint.split('/')[2]);
            return productosSim.find(prod => prod.id === productoId) || null;
        }
        
        return [];
    },
    
    /**
     * Carga el menú de árbol de categorías (panel izquierdo)
     */
    loadCategoriasMenu: async function() {
        try {
            this.showLoadingIndicator(this.elements.categoriasMenu);
            
            // Verificar caché
            if (this.state.categoriaCache.has('todas')) {
                const categorias = this.state.categoriaCache.get('todas');
                this.renderCategoriasMenu(categorias);
                return;
            }
            
            // Cargar todas las categorías
            const categorias = await this.fetchWithRetry('/categorias/activas');
            
            // Guardar en caché
            this.state.categoriaCache.set('todas', categorias);
            this.state.todasLasCategorias = categorias;
            
            // Organizar categorías por jerarquía
            const categoriasOrganizadas = this.organizarCategoriasPorJerarquia(categorias);
            
            // Renderizar en el menú
            this.renderCategoriasMenu(categoriasOrganizadas);
            
        } catch (error) {
            console.error('Error al cargar el menú de categorías:', error);
            this.showErrorMessage(
                this.elements.categoriasMenu, 
                'No se pudo cargar el menú de categorías. Intente nuevamente más tarde.',
                () => this.loadCategoriasMenu()
            );
        }
    },
    
    /**
     * Organiza las categorías en una estructura jerárquica
     * @param {Array} categorias - Lista plana de todas las categorías
     * @returns {Array} - Categorías organizadas jerárquicamente
     */
    organizarCategoriasPorJerarquia: function(categorias) {
        // Primero identificar las categorías principales (sin categoría padre)
        const categoriasPrincipales = categorias.filter(cat => !cat.categoriaPadre);
        
        // Para cada categoría principal, añadir sus subcategorías
        categoriasPrincipales.forEach(categoriaPrincipal => {
            // Encontrar todas las subcategorías directas
            categoriaPrincipal.subcategorias = categorias.filter(cat => 
                cat.categoriaPadre && cat.categoriaPadre.id === categoriaPrincipal.id
            );
        });
        
        return categoriasPrincipales;
    },
    
    /**
     * Renderiza el menú de árbol de categorías
     * @param {Array} categorias - Categorías principales con subcategorías
     */
    renderCategoriasMenu: function(categorias) {
        this.elements.categoriasMenu.innerHTML = '';
        
        // Añadir item para "Todas las categorías"
        const todasItem = document.createElement('a');
        todasItem.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
        todasItem.href = '#';
        todasItem.innerHTML = `
            <span>
                <i class="bi bi-grid-3x3-gap categoria-icon"></i>
                Todas las categorías
            </span>
            <span class="badge bg-primary rounded-pill">${categorias.length}</span>
        `;
        
        todasItem.addEventListener('click', (e) => {
            e.preventDefault();
            this.resetCategoriaView();
        });
        
        this.elements.categoriasMenu.appendChild(todasItem);
        
        // Añadir cada categoría principal
        categorias.forEach(categoria => {
            const tieneSubcategorias = categoria.subcategorias && categoria.subcategorias.length > 0;
            
            // Crear el elemento de categoría principal
            const categoriaItem = document.createElement('a');
            categoriaItem.className = 'list-group-item list-group-item-action';
            categoriaItem.href = '#';
            categoriaItem.setAttribute('data-id', categoria.id);
            categoriaItem.innerHTML = `
                <div class="categoria-item">
                    <span>
                        <i class="bi bi-folder categoria-icon"></i>
                        ${categoria.nombre}
                    </span>
                    <span class="categoria-counter">${tieneSubcategorias ? categoria.subcategorias.length : '0'}</span>
                </div>
            `;
            
            // Añadir evento para seleccionar categoría
            categoriaItem.addEventListener('click', (e) => {
                e.preventDefault();
                // Eliminar clase active de todos los items
                this.elements.categoriasMenu.querySelectorAll('.list-group-item').forEach(item => {
                    item.classList.remove('active');
                });
                // Añadir clase active al item seleccionado
                categoriaItem.classList.add('active');
                // Cargar la categoría
                this.selectCategoria(categoria.id);
            });
            
            this.elements.categoriasMenu.appendChild(categoriaItem);
            
            // Si tiene subcategorías, añadirlas
            if (tieneSubcategorias) {
                categoria.subcategorias.forEach(subcategoria => {
                    const subcategoriaItem = document.createElement('a');
                    subcategoriaItem.className = 'list-group-item list-group-item-action subcategoria-item';
                    subcategoriaItem.href = '#';
                    subcategoriaItem.setAttribute('data-id', subcategoria.id);
                    subcategoriaItem.innerHTML = `
                        <span>
                            <i class="bi bi-tag categoria-icon"></i>
                            ${subcategoria.nombre}
                        </span>
                    `;
                    
                    // Añadir evento para seleccionar subcategoría
                    subcategoriaItem.addEventListener('click', (e) => {
                        e.preventDefault();
                        // Eliminar clase active de todos los items
                        this.elements.categoriasMenu.querySelectorAll('.list-group-item').forEach(item => {
                            item.classList.remove('active');
                        });
                        // Añadir clase active al item seleccionado
                        subcategoriaItem.classList.add('active');
                        // Cargar la subcategoría
                        this.selectCategoria(subcategoria.id);
                    });
                    
                    this.elements.categoriasMenu.appendChild(subcategoriaItem);
                });
            }
        });
    },
    
    /**
     * Carga las categorías principales (panel derecho, parte superior)
     */
    loadCategoriasPrincipales: async function() {
        try {
            this.showLoadingIndicator(this.elements.categoriasPrincipales);
            
            // Verificar si las categorías están en caché
            if (this.state.categoriaCache.has('principales')) {
                const categorias = this.state.categoriaCache.get('principales');
                this.renderCategoriasPrincipales(categorias);
                return;
            }
            
            const categorias = await this.fetchWithRetry('/categorias/principales');
            
            if (categorias.length === 0) {
                this.elements.categoriasPrincipales.innerHTML = `
                    <div class="text-center py-4">
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
            this.renderCategoriasPrincipales(categorias);
            
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
     * Renderiza las categorías principales en el panel derecho
     * @param {Array} categorias - Array de categorías principales
     * Esta función debe reemplazar la función renderCategoriasPrincipales en el objeto CategoriasApp
     */
    renderCategoriasPrincipales: function(categorias) {
        this.elements.categoriasPrincipales.innerHTML = '';
        
        // Crear el grid de categorías
        const categoriasGrid = document.createElement('div');
        categoriasGrid.className = 'categories-grid';
        
        // Mostrar todas las categorías disponibles
        categorias.forEach(categoria => {
            const categoryCard = document.createElement('div');
            categoryCard.className = 'category-card';
            categoryCard.setAttribute('data-id', categoria.id);
            
            // Elegir un icono según la categoría
            const icono = this.getCategoryIcon(categoria.nombre);
            
            categoryCard.innerHTML = `
                <div class="category-icon">
                    <i class="bi ${icono}"></i>
                </div>
                <h5 class="category-name">${categoria.nombre}</h5>
                <p class="text-muted small">
                    ${categoria.descripcion ? categoria.descripcion.substring(0, 40) + (categoria.descripcion.length > 40 ? '...' : '') : 'Explorar productos'}
                </p>
            `;
            
            // Añadir evento para seleccionar categoría
            categoryCard.addEventListener('click', () => {
                this.selectCategoria(categoria.id);
                
                // También actualizar el menú lateral
                const menuItem = this.elements.categoriasMenu.querySelector(`[data-id="${categoria.id}"]`);
                if (menuItem) {
                    this.elements.categoriasMenu.querySelectorAll('.list-group-item').forEach(item => {
                        item.classList.remove('active');
                    });
                    menuItem.classList.add('active');
                }
            });
            
            categoriasGrid.appendChild(categoryCard);
        });
        
        this.elements.categoriasPrincipales.appendChild(categoriasGrid);
    },
    
    /**
     * Asigna un icono apropiado según el nombre de la categoría
     * @param {string} nombre - Nombre de la categoría
     * @returns {string} - Clase de icono de Bootstrap
     */
    getCategoryIcon: function(nombre) {
        if (!nombre) return 'bi-folder';
        
        const nombre_lower = nombre.toLowerCase();
        
        if (nombre_lower.includes('electrónica') || nombre_lower.includes('electronica')) {
            return 'bi-laptop';
        } else if (nombre_lower.includes('ropa') || nombre_lower.includes('moda')) {
            return 'bi-bag';
        } else if (nombre_lower.includes('hogar') || nombre_lower.includes('casa')) {
            return 'bi-house';
        } else if (nombre_lower.includes('deporte')) {
            return 'bi-bicycle';
        } else if (nombre_lower.includes('juguete')) {
            return 'bi-puzzle';
        } else if (nombre_lower.includes('mascota')) {
            return 'bi-egg';
        } else if (nombre_lower.includes('libro') || nombre_lower.includes('lectura')) {
            return 'bi-book';
        } else if (nombre_lower.includes('belleza') || nombre_lower.includes('cosmética')) {
            return 'bi-stars';
        } else if (nombre_lower.includes('alimento') || nombre_lower.includes('comida')) {
            return 'bi-cart3';
        } else if (nombre_lower.includes('jardín') || nombre_lower.includes('jardin')) {
            return 'bi-flower1';
        } else if (nombre_lower.includes('auto') || nombre_lower.includes('coche')) {
            return 'bi-car-front';
        } else if (nombre_lower.includes('niño') || nombre_lower.includes('infantil')) {
            return 'bi-emoji-smile';
        } else {
            return 'bi-folder';
        }
    },
    
    /**
     * Busca categorías por término de búsqueda
     * @param {string} termino - Término a buscar
     */
    buscarCategorias: async function(termino) {
        try {
            this.state.searchTerm = termino;
            this.showLoadingIndicator(this.elements.categoriasPrincipales);
            
            // Ocultar contenedores de productos y subcategorías
            this.elements.categoriaProductosContainer.style.display = 'none';
            this.elements.subcategoriasContainer.style.display = 'none';
            
            // Actualizar breadcrumb
            this.updateBreadcrumb([{ id: null, nombre: `Resultados para "${termino}"` }]);
            
            const categorias = await this.fetchWithRetry(`/categorias/buscar?nombre=${encodeURIComponent(termino)}`);
            
            if (categorias.length === 0) {
                this.elements.categoriasPrincipales.innerHTML = `
                    <div class="text-center py-4">
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
                    this.resetCategoriaView();
                    this.elements.searchInput.value = '';
                });
                return;
            }
            
            this.renderCategoriasPrincipales(categorias);
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
     * Carga las subcategorías de una categoría
     * @param {Object} categoria - Categoría padre
     */
    loadSubcategorias: async function(categoria) {
        try {
            // Si la categoría no tiene ID, salir
            if (!categoria || !categoria.id) return;
            
            // Mostrar indicador de carga
            this.elements.subcategoriasGrid.innerHTML = '<div class="text-center py-4 w-100"><div class="spinner-border text-primary"></div><p class="mt-2">Cargando subcategorías...</p></div>';
            
            // Verificar caché
            const cacheKey = `subcats-${categoria.id}`;
            let subcategorias;
            
            if (this.state.categoriaCache.has(cacheKey)) {
                subcategorias = this.state.categoriaCache.get(cacheKey);
            } else {
                subcategorias = await this.fetchWithRetry(`/categorias/${categoria.id}/subcategorias`);
                // Guardar en caché
                this.state.categoriaCache.set(cacheKey, subcategorias);
            }
            
            // Si no hay subcategorías, ocultar el contenedor
            if (!subcategorias || subcategorias.length === 0) {
                this.elements.subcategoriasContainer.style.display = 'none';
                return;
            }
            
            // Mostrar contenedor de subcategorías
            this.elements.subcategoriasContainer.style.display = 'block';
            this.elements.subcategoriasTitulo.innerHTML = `<i class="bi bi-layers me-2"></i> Subcategorías de ${categoria.nombre}`;
            
            // Renderizar subcategorías
            this.renderSubcategorias(subcategorias);
            
        } catch (error) {
            console.error('Error al cargar subcategorías:', error);
            this.elements.subcategoriasContainer.style.display = 'none';
        }
    },
    
    /**
     * Renderiza subcategorías en el grid
     * @param {Array} subcategorias - Lista de subcategorías
     * Esta función debe reemplazar la función renderSubcategorias en el objeto CategoriasApp
     */
    renderSubcategorias: function(subcategorias) {
        this.elements.subcategoriasGrid.innerHTML = '';
        
        // Crear grid de subcategorías
        const subcategoriasGrid = document.createElement('div');
        subcategoriasGrid.className = 'subcategories-grid';
        
        subcategorias.forEach(subcategoria => {
            const subcategoriaCard = document.createElement('div');
            subcategoriaCard.className = 'subcategory-card';
            
            // Elegir un icono según la subcategoría
            const icono = this.getCategoryIcon(subcategoria.nombre);
            
            subcategoriaCard.innerHTML = `
                <div class="category-icon">
                    <i class="bi ${icono}"></i>
                </div>
                <h6 class="category-name">${subcategoria.nombre}</h6>
                <div class="category-count">
                    <i class="bi bi-box"></i> Productos
                </div>
            `;
            
            // Evento para seleccionar subcategoría
            subcategoriaCard.addEventListener('click', () => {
                this.selectCategoria(subcategoria.id);
                
                // Actualizar estado activo en menú lateral
                const menuItem = this.elements.categoriasMenu.querySelector(`[data-id="${subcategoria.id}"]`);
                if (menuItem) {
                    this.elements.categoriasMenu.querySelectorAll('.list-group-item').forEach(item => {
                        item.classList.remove('active');
                    });
                    menuItem.classList.add('active');
                }
            });
            
            subcategoriasGrid.appendChild(subcategoriaCard);
        });
        
        this.elements.subcategoriasGrid.innerHTML = '';
        this.elements.subcategoriasGrid.appendChild(subcategoriasGrid);
    },
    
    /**
     * Carga los productos de una categoría
     * @param {number} categoriaId - ID de la categoría
     * @param {number} page - Número de página
     */
    loadProductosCategoria: async function(categoriaId, page = 1) {
        try {
            // Actualizar estado de la página
            this.state.currentPage = page;
            
            // Mostrar indicador de carga
            this.showLoadingIndicator(this.elements.productosCategoriasContainer);
            
            // Mostrar contenedor de productos
            this.elements.categoriaProductosContainer.style.display = 'block';
            
            // Clave para caché de productos
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
            
            // Actualizar estado de paginación
            this.state.totalProductos = result.total || result.length;
            this.state.totalPages = Math.ceil(this.state.totalProductos / this.state.pageSize);
            
            // Buscar el nombre de la categoría si no lo tenemos
            const categoriaData = this.state.categoriaData;
            const categoriaNombre = categoriaData ? categoriaData.nombre : 'Categoría';
            
            // Actualizar título y contador
            this.elements.categoriaProductosTitulo.innerHTML = `<i class="bi bi-box me-2"></i> Productos en ${categoriaNombre}`;
            this.updateTotalCounter();
            
            // Renderizar productos y paginación
            this.renderProductosCategoria(result.data || result);
            this.renderPaginationCategoria();
            
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
     * Actualiza el contador de productos
     */
    updateTotalCounter: function() {
        this.elements.totalProductosCategoria.textContent = `${this.state.totalProductos} producto${this.state.totalProductos !== 1 ? 's' : ''} encontrado${this.state.totalProductos !== 1 ? 's' : ''}`;
    },
    
    /**
     * Renderiza los productos de una categoría
     * @param {Array} productos - Lista de productos
     */
    // Reemplaza la función renderProductosCategoria en categorias.js
    // Esta función está dentro del objeto CategoriasApp
    // Busca CategoriasApp.renderProductosCategoria y reemplázala con esta versión

    renderProductosCategoria: function(productos) {
        this.elements.productosCategoriasContainer.innerHTML = '';
        
        if (!productos || productos.length === 0) {
            this.elements.productosCategoriasContainer.innerHTML = `
                <div class="text-center py-4">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle me-2"></i>
                        No hay productos disponibles en esta categoría.
                    </div>
                </div>
            `;
            return;
        }
        
        // Crear grid de productos
        const productsGrid = document.createElement('div');
        productsGrid.className = 'products-grid';
        
        productos.forEach(producto => {
            const disponible = producto.stock > 0;
            const precioOferta = producto.precioOferta !== null && producto.precioOferta < producto.precio;
            
            // Crear tarjeta de producto con el formato estandarizado
            const productCard = document.createElement('div');
            productCard.className = `product-card ${!disponible ? 'opacity-75 sin-stock' : ''} ${precioOferta ? 'en-oferta' : ''}`;
            
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
                            <div class="product-price-original">${this.formatPrice(producto.precio)}</div>
                            <div class="product-price product-price-offer">${this.formatPrice(producto.precioOferta)}</div>
                        ` : `
                            <div class="product-price">${this.formatPrice(producto.precio)}</div>
                        `}
                    </div>
                    ${producto.descripcion ? `
                        <div class="product-description">
                            ${producto.descripcion.substring(0, 80)}${producto.descripcion.length > 80 ? '...' : ''}
                        </div>
                    ` : ''}
                    <div class="product-footer">
                        <a href="producto-detalle.html?id=${producto.id}" class="btn btn-primary w-100">
                            <i class="bi bi-eye me-1"></i> Ver detalles
                        </a>
                    </div>
                </div>
            `;
            
            productsGrid.appendChild(productCard);
        });
        
        this.elements.productosCategoriasContainer.appendChild(productsGrid);
        
        // Aplicar filtros iniciales
        if (this.state.activeFilter !== 'all') {
            this.applyProductFilters();
        }
    },
    
    /**
     * Renderiza la paginación para productos
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
    },
    
    /**
     * Navega a una página específica
     * @param {number} page - Número de página
     */
    goToPage: function(page) {
        if (page !== this.state.currentPage && page >= 1 && page <= this.state.totalPages) {
            this.loadProductosCategoria(this.state.categoriaId, page);
            this.updateURL({ page: page });
            
            // Desplazarse a la parte superior de los productos
            const container = this.elements.categoriaProductosContainer;
            window.scrollTo({
                top: container.offsetTop - 20,
                behavior: 'smooth'
            });
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
            
            // Actualizar visibilidad
            if (shouldShow) {
                producto.classList.remove('d-none');
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
            noResultsEl.className = 'text-center py-4';
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
     * Selecciona una categoría y carga su contenido
     * @param {number} categoriaId - ID de la categoría
     */
    selectCategoria: async function(categoriaId) {
        if (!categoriaId) return;
        
        this.state.categoriaId = categoriaId;
        this.state.currentPage = 1;
        
        try {
            // Cargar información de la categoría
            const categoria = await this.fetchWithRetry(`/categorias/${categoriaId}`);
            this.state.categoriaData = categoria;
            
            // Actualizar breadcrumb con la jerarquía
            const jerarquia = await this.loadCategoriaJerarquia(categoriaId);
            
            // Mostrar productos de la categoría
            await this.loadProductosCategoria(categoriaId);
            
            // Cargar subcategorías si existe
            this.loadSubcategorias(categoria);
            
            // Ocultar categorías destacadas
            this.elements.categoriaDestacadasContainer.style.display = 'none';
            
            // Actualizar URL
            this.updateURL({ id: categoriaId });
            
            // Reiniciar filtros de productos
            this.elements.filterButtons.forEach(btn => {
                if (btn.getAttribute('data-filter') === 'all') {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });
            this.state.activeFilter = 'all';
            
        } catch (error) {
            console.error('Error al seleccionar categoría:', error);
            this.showErrorMessage(
                this.elements.productosCategoriasContainer,
                'No se pudo cargar la información de la categoría. Intente nuevamente más tarde.',
                () => this.selectCategoria(categoriaId)
            );
        }
    },
    
    /**
     * Resetea la vista a la página principal de categorías
     */
    resetCategoriaView: function() {
        // Limpiar estado
        this.state.categoriaId = null;
        this.state.categoriaData = null;
        this.state.currentPage = 1;
        this.state.searchTerm = '';
        
        // Ocultar productos y subcategorías
        this.elements.categoriaProductosContainer.style.display = 'none';
        this.elements.subcategoriasContainer.style.display = 'none';
        
        // Mostrar categorías destacadas
        this.elements.categoriaDestacadasContainer.style.display = 'block';
        
        // Quitar selección en el menú
        this.elements.categoriasMenu.querySelectorAll('.list-group-item').forEach(item => {
            item.classList.remove('active');
        });
        // Seleccionar "Todas las categorías"
        const primeraOpcion = this.elements.categoriasMenu.querySelector('.list-group-item');
        if (primeraOpcion) {
            primeraOpcion.classList.add('active');
        }
        
        // Actualizar breadcrumb
        this.updateBreadcrumb([]);
        
        // Recargar categorías principales
        this.loadCategoriasPrincipales();
        
        // Actualizar URL
        this.updateURL({});
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
     * Carga la jerarquía de categorías para el breadcrumb
     * @param {number} categoriaId - ID de la categoría
     */
    loadCategoriaJerarquia: async function(categoriaId) {
        try {
            // Verificar caché
            const cacheKey = `jerarquia-${categoriaId}`;
            let jerarquia;
            
            if (this.state.categoriaCache.has(cacheKey)) {
                jerarquia = this.state.categoriaCache.get(cacheKey);
            } else {
                jerarquia = await this.fetchWithRetry(`/categorias/${categoriaId}/jerarquia`);
                // Guardar en caché
                this.state.categoriaCache.set(cacheKey, jerarquia);
            }
            
            // Actualizar breadcrumb
            this.updateBreadcrumb(jerarquia);
            
            return jerarquia;
        } catch (error) {
            console.error('Error al cargar jerarquía de categoría:', error);
            this.updateBreadcrumb([{ id: categoriaId, nombre: 'Categoría' }]);
            return [];
        }
    },
    
    /**
     * Actualiza el breadcrumb con la jerarquía de categorías
     * @param {Array} jerarquia - Array con la jerarquía de categorías
     */
    updateBreadcrumb: function(jerarquia) {
        // Guardar jerarquía
        this.state.breadcrumb = jerarquia;
        
        // Limpiar breadcrumb
        this.elements.categoriaBreadcrumb.innerHTML = '';
        
        // Añadir "Inicio" siempre
        const homeLi = document.createElement('li');
        homeLi.className = 'breadcrumb-item';
        homeLi.innerHTML = '<a href="categorias.html">Todas las categorías</a>';
        homeLi.addEventListener('click', (e) => {
            e.preventDefault();
            this.resetCategoriaView();
        });
        this.elements.categoriaBreadcrumb.appendChild(homeLi);
        
        // Si no hay jerarquía o está vacía, terminar
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
                li.innerHTML = `<a href="#">${categoria.nombre}</a>`;
                li.querySelector('a').addEventListener('click', (e) => {
                    e.preventDefault();
                    this.selectCategoria(categoria.id);
                });
            }
            
            this.elements.categoriaBreadcrumb.appendChild(li);
        });
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
    console.log('DOM cargado, inicializando CategoriasApp...');
    CategoriasApp.init();
});