CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATETIME NOT NULL,
    ultima_conexion DATETIME,
    activo BOOLEAN DEFAULT TRUE,
    rol ENUM('CLIENTE', 'ADMIN') NOT NULL DEFAULT 'CLIENTE'
);

CREATE TABLE direccion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo ENUM('ENVIO', 'FACTURACION') NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    calle VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    estado VARCHAR(100),
    codigo_postal VARCHAR(20) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    predeterminada BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE metodo_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo ENUM('TARJETA', 'PAYPAL', 'TRANSFERENCIA') NOT NULL,
    titular VARCHAR(150),
    numero_tarjeta VARCHAR(30),
    fecha_expiracion VARCHAR(10),
    predeterminado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria_padre_id BIGINT,
    imagen VARCHAR(255),
    activa BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_padre_id) REFERENCES categoria(id) ON DELETE SET NULL
);

CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_sku VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    precio_oferta DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0,
    categoria_id BIGINT,
    fecha_creacion DATETIME NOT NULL,
    ultima_actualizacion DATETIME,
    activo BOOLEAN DEFAULT TRUE,
    destacado BOOLEAN DEFAULT FALSE,
    peso DECIMAL(10,2),
    dimensiones VARCHAR(50),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE SET NULL
);

CREATE TABLE imagen_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    es_principal BOOLEAN DEFAULT FALSE,
    orden INT,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE
);

CREATE TABLE valoracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    puntuacion TINYINT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario TEXT,
    fecha DATETIME NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    UNIQUE (producto_id, usuario_id)
);

CREATE TABLE carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    session_id VARCHAR(100),
    fecha_creacion DATETIME NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE item_carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_agregado DATETIME NOT NULL,
    FOREIGN KEY (carrito_id) REFERENCES carrito(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    UNIQUE (carrito_id, producto_id)
);

CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    codigo_referencia VARCHAR(50) NOT NULL UNIQUE,
    fecha_pedido DATETIME NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADO', 'ENVIADO', 'ENTREGADO', 'CANCELADO') NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    impuestos DECIMAL(10,2) NOT NULL,
    gastos_envio DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    direccion_envio_id BIGINT NOT NULL,
    direccion_facturacion_id BIGINT NOT NULL,
    metodo_pago_id BIGINT NOT NULL,
    notas TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT,
    FOREIGN KEY (direccion_envio_id) REFERENCES direccion(id) ON DELETE RESTRICT,
    FOREIGN KEY (direccion_facturacion_id) REFERENCES direccion(id) ON DELETE RESTRICT,
    FOREIGN KEY (metodo_pago_id) REFERENCES metodo_pago(id) ON DELETE RESTRICT
);

CREATE TABLE detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT
);