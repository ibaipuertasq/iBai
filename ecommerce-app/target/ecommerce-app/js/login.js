document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const errorMessage = document.getElementById('error-message');
    
    // Verificar si ya hay una sesión activa
    function checkAuthStatus() {
        const token = localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token');
        
        if (token) {
            // Validar el token con el servidor
            fetch('/api/auth/validate', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                if (response.ok) {
                    // Token válido, redirigir a la página principal
                    window.location.href = 'index.html';
                } else {
                    // Token inválido, eliminar del almacenamiento
                    localStorage.removeItem('auth_token');
                    sessionStorage.removeItem('auth_token');
                    // También eliminar datos del usuario
                    ['user_id', 'user_email', 'user_name', 'user_role'].forEach(key => {
                        localStorage.removeItem(key);
                        sessionStorage.removeItem(key);
                    });
                }
            })
            .catch(() => {
                // Error al validar, eliminar del almacenamiento
                localStorage.removeItem('auth_token');
                sessionStorage.removeItem('auth_token');
                ['user_id', 'user_email', 'user_name', 'user_role'].forEach(key => {
                    localStorage.removeItem(key);
                    sessionStorage.removeItem(key);
                });
            });
        }
    }
    
    // Validación del formulario en el evento submit
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Validar formulario
        if (!validateForm()) {
            return;
        }
        
        // Preparar datos para enviar
        const userData = {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value
        };
        
        // Enviar solicitud de login
        loginUser(userData);
    });
    
    // Validación del formulario
    function validateForm() {
        let isValid = true;
        
        // Eliminar clases de validación anteriores
        loginForm.querySelectorAll('.form-control').forEach(input => {
            input.classList.remove('is-invalid', 'is-valid');
        });
        
        // Validar email
        const emailInput = document.getElementById('email');
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailInput.value)) {
            emailInput.classList.add('is-invalid');
            isValid = false;
        } else {
            emailInput.classList.add('is-valid');
        }
        
        // Validar contraseña (solo verificar que no esté vacía)
        const passwordInput = document.getElementById('password');
        if (!passwordInput.value) {
            passwordInput.classList.add('is-invalid');
            isValid = false;
        } else {
            passwordInput.classList.add('is-valid');
        }
        
        return isValid;
    }
    
    // Función para enviar solicitud de login
    function loginUser(userData) {
        // Mostrar indicador de carga
        document.querySelector('button[type="submit"]').disabled = true;
        document.querySelector('button[type="submit"]').innerHTML = 
            '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Iniciando sesión...';
        
        // Ocultar mensaje de error previo
        errorMessage.classList.add('d-none');
        
        // Enviar solicitud a la API
        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Error al iniciar sesión');
                });
            }
            return response.json();
        })
        .then(data => {
            // Guardar token en localStorage o sessionStorage según "Recordarme"
            const storage = document.getElementById('remember-me').checked ? localStorage : sessionStorage;
            
            storage.setItem('auth_token', data.token);
            storage.setItem('user_id', data.id);
            storage.setItem('user_email', data.email);
            storage.setItem('user_name', data.nombre);
            storage.setItem('user_role', data.rol);
            
            // Redirigir a la página principal
            window.location.href = 'index.html';
        })
        .catch(error => {
            // Mostrar mensaje de error
            errorMessage.textContent = error.message;
            errorMessage.classList.remove('d-none');
            errorMessage.classList.add('error-shake');
            
            // Quitar animación después de terminar
            setTimeout(() => {
                errorMessage.classList.remove('error-shake');
            }, 500);
        })
        .finally(() => {
            // Restaurar botón de submit
            document.querySelector('button[type="submit"]').disabled = false;
            document.querySelector('button[type="submit"]').textContent = 'Iniciar sesión';
        });
    }
    
    // Verificar estado de autenticación al cargar la página
    checkAuthStatus();
});