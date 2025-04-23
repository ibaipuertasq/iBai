/**
 * Funciones y variables comunes para todas las páginas
 */

// Configuración base para peticiones API
const API_BASE_URL = '/api';

// Función para inicializar la autenticación en cualquier página
function initAuth() {
    const token = localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token');
    const userName = localStorage.getItem('user_name') || sessionStorage.getItem('user_name');
    const authMenu = document.getElementById('auth-menu');
    const userMenu = document.getElementById('user-menu');
    const userNameElement = document.getElementById('user-name');
    const logoutBtn = document.getElementById('logout-btn');
    
    if (token) {
        // Validar el token con el servidor
        fetch(`${API_BASE_URL}/auth/validate`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.ok) {
                authMenu.classList.add('d-none');
                userMenu.classList.remove('d-none');
                if (userNameElement) {
                    userNameElement.textContent = userName || 'Usuario';
                }
            } else {
                clearAuthStorage();
                authMenu.classList.remove('d-none');
                userMenu.classList.add('d-none');
            }
        })
        .catch(() => {
            clearAuthStorage();
            authMenu.classList.remove('d-none');
            userMenu.classList.add('d-none');
        });

        if (logoutBtn) {
            logoutBtn.addEventListener('click', function(e) {
                e.preventDefault();
                clearAuthStorage();
                window.location.href = 'login.html';
            });
        }
    } else {
        if (authMenu) authMenu.classList.remove('d-none');
        if (userMenu) userMenu.classList.add('d-none');
    }
}

// Función para limpiar datos de autenticación
function clearAuthStorage() {
    ['auth_token', 'user_id', 'user_email', 'user_name', 'user_role'].forEach(key => {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    });
}

// Función para obtener el token de autenticación
function getAuthToken() {
    return localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token');
}

// Función para crear headers con autenticación
function createAuthHeaders() {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    return headers;
}

// Función para realizar peticiones a la API
async function fetchAPI(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = createAuthHeaders();
    
    const fetchOptions = {
        headers,
        ...options
    };
    
    try {
        const response = await fetch(url, fetchOptions);
        
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || `Error ${response.status}: ${response.statusText}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('Error en fetchAPI:', error);
        throw error;
    }
}

// Función para formatear precios
function formatPrice(price) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR'
    }).format(price);
}

// Función para generar una URL amigable (slug)
function generateSlug(text) {
    return text
        .toString()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim()
        .replace(/\s+/g, '-')
        .replace(/[^\w-]+/g, '')
        .replace(/--+/g, '-');
}

// Inicializar autenticación cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', initAuth);