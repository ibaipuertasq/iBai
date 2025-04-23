document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('register-form');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const errorMessage = document.getElementById('error-message');
    
    // Validación del formulario en el evento submit
    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        // Validar formulario
        if (!validateForm()) {
            return;
        }
        
        // Preparar datos para enviar
        const userData = {
            email: document.getElementById('email').value,
            password: passwordInput.value,
            nombre: document.getElementById('nombre').value,
            apellidos: document.getElementById('apellidos').value,
            telefono: document.getElementById('telefono').value || null
        };
        
        // Enviar solicitud de registro
        registerUser(userData);
    });
    
    // Validación del formulario
    function validateForm() {
        let isValid = true;
        
        // Eliminar clases de validación anteriores
        registerForm.querySelectorAll('.form-control').forEach(input => {
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
        
        // Validar contraseña
        const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/;
        if (!passwordRegex.test(passwordInput.value)) {
            passwordInput.classList.add('is-invalid');
            isValid = false;
        } else {
            passwordInput.classList.add('is-valid');
        }
        
        // Validar confirmación de contraseña
        if (passwordInput.value !== confirmPasswordInput.value) {
            confirmPasswordInput.classList.add('is-invalid');
            isValid = false;
        } else if (passwordInput.value && confirmPasswordInput.value) {
            confirmPasswordInput.classList.add('is-valid');
        }
        
        // Validar campos de texto obligatorios
        ['nombre', 'apellidos'].forEach(fieldId => {
            const input = document.getElementById(fieldId);
            if (!input.value.trim()) {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.add('is-valid');
            }
        });
        
        return isValid;
    }
    
    // Función para enviar solicitud de registro
    function registerUser(userData) {
        // Mostrar indicador de carga
        document.querySelector('button[type="submit"]').disabled = true;
        document.querySelector('button[type="submit"]').innerHTML = 
            '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Registrando...';
        
        // Ocultar mensaje de error previo
        errorMessage.classList.add('d-none');
        
        // Enviar solicitud a la API
        fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Error al registrar el usuario');
                });
            }
            return response.json();
        })
        .then(data => {
            // Guardar token en localStorage
            localStorage.setItem('auth_token', data.token);
            localStorage.setItem('user_id', data.id);
            localStorage.setItem('user_email', data.email);
            localStorage.setItem('user_name', data.nombre);
            localStorage.setItem('user_role', data.rol);
            
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
            document.querySelector('button[type="submit"]').textContent = 'Registrarme';
        });
    }
    
    // Validación en tiempo real para la contraseña
    passwordInput.addEventListener('input', function() {
        const value = this.value;
        const password = value;
        
        // Validar requisitos de contraseña
        const hasLowerCase = /[a-z]/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasNumber = /[0-9]/.test(password);
        const hasSpecialChar = /[@#$%^&+=]/.test(password);
        const hasMinLength = password.length >= 8;
        
        // Actualizar mensaje de retroalimentación
        const feedback = document.getElementById('password-feedback');
        
        if (!hasMinLength) {
            feedback.textContent = 'La contraseña debe tener al menos 8 caracteres.';
        } else if (!hasLowerCase) {
            feedback.textContent = 'La contraseña debe incluir al menos una letra minúscula.';
        } else if (!hasUpperCase) {
            feedback.textContent = 'La contraseña debe incluir al menos una letra mayúscula.';
        } else if (!hasNumber) {
            feedback.textContent = 'La contraseña debe incluir al menos un número.';
        } else if (!hasSpecialChar) {
            feedback.textContent = 'La contraseña debe incluir al menos un carácter especial (@#$%^&+=).';
        }
    });
    
    // Validación en tiempo real para la confirmación de contraseña
    confirmPasswordInput.addEventListener('input', function() {
        if (this.value && this.value === passwordInput.value) {
            this.classList.remove('is-invalid');
            this.classList.add('is-valid');
        } else if (this.value) {
            this.classList.remove('is-valid');
            this.classList.add('is-invalid');
        }
    });
});