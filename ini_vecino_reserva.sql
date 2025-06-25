INSERT INTO roles (id_rol, nombre) VALUES (1, "Vecino");
INSERT INTO roles (id_rol, nombre) VALUES (2, "SuperAdmin");
INSERT INTO roles (id_rol, nombre) VALUES (3, "Coordinador");
INSERT INTO roles (id_rol, nombre) VALUES (4, "Admin");


INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (1, 'Sebastian', 'Ramirez', 'dhannysebas@hotmail.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'CATO', 72519702, 111999222,1, TRUE, '1990-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (2, 'Pepito', 'Perez', 'a20210845@pucp.edu.pe', '$2a$12$jVJd4bnruBz0B8J903JABOAAYlaX3cVSN9dUZGiURUpE6ZAwCtPgq', 'PUCP', 12345678, 111333222,2, TRUE, '1993-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (3, 'Lujo', 'Poga', 'lujancarrion@pucp.edu.pe', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'MI CASA', 87654321, 111333222,4, TRUE, '1996-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (4, 'Ariana', 'Zuñiga', 'arianazuniga01@gmail.com', '$2a$12$jVJd4bnruBz0B8J903JABOAAYlaX3cVSN9dUZGiURUpE6ZAwCtPgq', 'Av. Monte de los Olivos 286', 72885602, 994615847,3, TRUE, '2004-01-07');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (5, 'Joaquin', 'Arriaran', 'joaquinadmin@gmail.com', '$2a$12$jVJd4bnruBz0B8J903JABOAAYlaX3cVSN9dUZGiURUpE6ZAwCtPgq', 'Av. Ejercito', 72875845, 995856147,4, TRUE, '2006-01-07');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (6, 'Admin', 'Sistema', 'admin@sistema.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'Av. La Mar', 72875885, 995856245,4, TRUE, '2003-01-07');

INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (1, 'Piscina');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (2, 'Canchas Fútbol');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (3, 'Pistas de Atletismo');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (4, 'Estadios');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (5, 'Gimnasios');

INSERT INTO listafotos (id_lista_fotos) VALUES (1);
INSERT INTO listafotos (id_lista_fotos) VALUES (2);
INSERT INTO listafotos (id_lista_fotos) VALUES (3);
INSERT INTO listafotos (id_lista_fotos) VALUES (4);
INSERT INTO listafotos (id_lista_fotos) VALUES (5);

INSERT INTO fotos (id_fotos, foto, id_lista_fotos, foto_nombre, foto_tipo_archivo)
VALUES (1, LOAD_FILE('ruta_a_imagen.jpg'), 1, 'medidas_lg.jpg', 'image/jpeg');

INSERT INTO espaciosdeportivos (id_espacio, nombre, ubicacion, id_tipo_espacio, id_lista_fotos, descripcion_corta, descripcion_larga, num_contacto, correo_contacto, operativo, costo_horario, maps_url, hora_abre, hora_cierra) VALUES (2, 'Gimnasio', 'San Miguel', 5, 1, 'Esta es una descripcion corta', 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga', 111222333, 'a20220378@pucp.edu.pe', true, 25.0, 'https://acortar.link/4DdNym', '09:00', '21:00');
INSERT INTO `espaciosdeportivos` (
    id_espacio, nombre, ubicacion, id_tipo_espacio, id_lista_fotos,
    descripcion_corta, descripcion_larga, num_contacto, correo_contacto,
    hora_abre, hora_cierra, operativo, costo_horario, aforo,
    latitud, longitud, radio_cobertura, maps_url
) VALUES
(1, 'Piscina Diego Ferre', 'San Miguel', 1, 1,
 'Esta es una descripcion corta',
 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga',
 111222333, 'a20220378@pucp.edu.pe',
 '09:00:00', '21:00:00', 1, 10.0, 30,
 -12.06308641, -77.08134412, 150, 'https://maps.app.goo.gl/Gmt7mV7DKHSBCQRaA'),

(3, 'Piscina Olímpica San Miguel', 'Complejo Deportivo San Miguel', 1, 1,
 'Piscina olímpica de 50 metros',
 'Piscina olímpica de 50 metros con 8 carriles, ideal para natación recreativa y competitiva',
 987654321, 'piscina@sanmiguel.gob.pe',
 '06:00:00', '22:00:00', 1, 25.00, 50,
 -12.07650000, -77.03680000, 100, 'https://maps.app.goo.gl/69SE2DU3uieoSx6A9'),

(4, 'Cancha de Fútbol Principal', 'Complejo Deportivo San Miguel', 2, 2,
 'Cancha de fútbol con césped natural',
 'Cancha de fútbol profesional con césped natural, iluminación nocturna y graderías',
 987654322, 'cancha@sanmiguel.gob.pe',
 '06:00:00', '23:00:00', 1, 80.00, 200,
 -12.07660497, -77.09124934, 150, 'https://maps.app.goo.gl/ZtpUgxiE4GJEs3yN6'),

(5, 'Pista de Atletismo', 'Complejo Deportivo San Miguel', 3, 3,
 'Pista de atletismo de 400 metros',
 'Pista de atletismo de 400 metros con superficie de tartán, 8 carriles',
 987654323, 'pista@sanmiguel.gob.pe',
 '05:00:00', '21:00:00', 1, 15.00, 100,
 -12.07660497, -77.09124934, 150, 'https://maps.app.goo.gl/ZtpUgxiE4GJEs3yN6'),

(6, 'Adelfo Magallanes', 'Estadio Municipal San Miguel', 4, 4,
 'Estadio municipal con capacidad para 5000 personas',
 'Estadio municipal con césped natural, iluminación profesional y capacidad para 5000 espectadores',
 987654324, 'estadio@sanmiguel.gob.pe',
 '08:00:00', '22:00:00', 1, 200.00, 5000,
 -12.08516404, -77.09591446, 200, 'https://maps.app.goo.gl/ZtmUUgHnHjQqEsaE7');

INSERT INTO piscinas(id_espacio, tipo_piscina, profundidad_min, profundidad_max, is_climatizada, requisitos, num_carril_max) VALUES (1, 'Olímpica', 1.0, 2.5, true, 'Llevar gorro y lentes para piscina', 8);
INSERT INTO gimnasios(id_espacio, cantidad_maquinas, tipos_maquinas, tiene_sauna, tiene_duchas, costo_semanal, costo_mensual, costo_anual) VALUES (2, 20, 'Yo q se', true, true, 10.0, 20.0, 30.0);

INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('9:00:00', '10:00:00', 1, 1);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('10:00:00', '11:00:00', 1, 2);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('12:00:00', '13:00:00', 1, 3);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('13:00:00', '14:00:00', 1, 4);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('14:00:00', '15:00:00', 1, 5);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('15:00:00', '16:00:00', 1, 6);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('16:00:00', '17:00:00', 1, 7);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('17:00:00', '18:00:00', 1, 8);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('18:00:00', '19:00:00', 1, 9);


-- 6. Insertar horarios para cada espacio
INSERT IGNORE INTO horarios (`id_horarios`, `hora_inicio`, `hora_fin`, `id_espacio`) VALUES
-- Horarios para Cancha (id_espacio = 2)
(13, '08:00:00', '10:00:00', 2),
(14, '10:00:00', '12:00:00', 2),
(15, '14:00:00', '16:00:00', 2),
(16, '16:00:00', '18:00:00', 2),
(10, '18:00:00', '20:00:00', 2),
-- Horarios para Pista (id_espacio = 3)
(11, '06:00:00', '08:00:00', 3),
(12, '08:00:00', '10:00:00', 3),
(13, '16:00:00', '18:00:00', 3),
(14, '18:00:00', '20:00:00', 3),
-- Horarios para Estadio (id_espacio = 4)
(15, '09:00:00', '12:00:00', 4),
(16, '14:00:00', '17:00:00', 4),
(17, '18:00:00', '21:00:00', 4);

INSERT INTO mediospago (`nombre`, `tipo_pago`, `requiere_verificacion`, `activo`, `descripcion`, `datos_cuenta`, `icono`) VALUES
('Tarjeta de Crédito/Débito', 'AUTOMATICO', 0, 1, 'Pago con tarjeta a través de pasarela segura', NULL, 'credit-card.png'),
('Transferencia Bancaria', 'MANUAL', 1, 1, 'Transferencia a cuenta bancaria', 'Banco: BCP\nCuenta: 123-456789-0-12\nCCI: 00212312345678901234', 'bank-transfer.png'),
('Yape', 'MANUAL', 1, 1, 'Pago mediante Yape', 'Número Yape: 987654321\nNombre: Espacios Deportivos SAC', 'yape.png'),
('Plin', 'MANUAL', 1, 1, 'Pago mediante Plin', 'Número Plin: 987654321\nNombre: Espacios Deportivos SAC', 'plin.png');

INSERT IGNORE INTO pagos (`id_pagos`, `cantidad`, `id_medios_pago`, `estado_pago`, `fecha_pago`, `numero_transaccion`) VALUES
(1, 25.00, 3, 'PENDIENTE', '2024-01-15 10:30:00', 'YAPE-20240115-001'),
(2, 80.00, 2, 'PENDIENTE', '2024-01-15 14:20:00', 'TRANS-20240115-002'),
(3, 15.00, 1, 'APROBADO', '2024-01-16 09:15:00', 'CARD-20240116-003'),
(4, 25.00, 3, 'APROBADO', '2024-01-16 16:45:00', 'YAPE-20240116-004'),
(5, 200.00, 2, 'PENDIENTE', '2024-01-17 11:30:00', 'TRANS-20240117-005');

-- Reservas
INSERT IGNORE INTO reservas
(`id_reservas`, `id_usuario`, `id_espacio`, `id_pagos`, `id_horarios`, `registro_timestamp`, `fecha_reserva`, `estado_reserva`) VALUES
-- Reservas para Piscina (id_tipo_espacio = 1)
(1, 1, 1, 1, 1, '2024-01-15 10:30:00', '2025-01-20', 'ACTIVA'),
(2, 2, 1, 4, 4, '2024-01-16 16:45:00', '2025-01-22', 'ACTIVA'),
-- Reservas para Cancha (id_tipo_espacio = 2)
(3, 2, 2, 2, 8, '2024-01-15 14:20:00', '2025-06-21', 'ACTIVA'),
-- Reservas para Pista (id_tipo_espacio = 3)
(4, 3, 3, 3, 11, '2024-01-16 09:15:00', '2025-07-23', 'ACTIVA'),
-- Reservas para Estadio (id_tipo_espacio = 4)
(5, 1, 4, 5, 15, '2024-01-17 11:30:00', '2025-07-25', 'ACTIVA'),
(6, 1, 4, 5, 15, '2024-01-17 11:30:00', '2025-08-25', 'ACTIVA');

-- Horario para coordinador
INSERT INTO horarioscoordinador
(id_usuario, id_espacio, hora_entrada, hora_salida, fecha_inicio, fecha_fin)
VALUES
(
    4,                      -- id_usuario (Ariana Zuñiga)
    6,                      -- id_espacio
    '03:00:00',             -- hora_entrada (3 AM)
    '22:00:00',             -- hora_salida (10 PM)
    '2025-06-16 03:00:00',  -- fecha_inicio (16/06/2025 a las 3 AM)
    '2025-06-16 22:00:00'   -- fecha_fin (mismo día a las 10 PM)
);

-- Crear horario de coordinador para la semana actual
-- Esto modificarlo segun necesidades
-- SET @coordinador_id = (SELECT id_usuario FROM usuario WHERE correo = 'arianazuniga01@gmail.com' LIMIT 1);
-- SET @espacio_id = (SELECT id_espacio FROM espaciosdeportivos WHERE nombre = 'Loma Amarilla' LIMIT 1);

-- Insertar horario para la semana actual (Lunes a Viernes, 8:00 - 16:00)
-- INSERT IGNORE INTO horarioscoordinador (
--    id_usuario, id_espacio, hora_entrada, hora_salida, fecha_inicio, fecha_fin
-- ) VALUES
-- (@coordinador_id, @espacio_id, '08:00:00', '16:00:00',
 -- DATE(DATE_ADD(CURDATE(), INTERVAL (1-DAYOFWEEK(CURDATE())) DAY)),
 -- DATE(DATE_ADD(CURDATE(), INTERVAL (5-DAYOFWEEK(CURDATE())) DAY)));

-- UPDATE usuario SET activo = true where id_usuario=1;

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.id_espacio = 1;

SELECT * FROM horarioreservado;

SELECT * FROM reservas;

SELECT * FROM espaciosdeportivos;

SELECT * FROM fotos;

SELECT * FROM canchasfutbol;

INSERT INTO espaciosdeportivos (
    nombre, ubicacion, id_tipo_espacio, id_lista_fotos,
    descripcion_corta, descripcion_larga, num_contacto, correo_contacto,
    maps_url, latitud, longitud, radio_cobertura, aforo,
    hora_abre, hora_cierra, operativo, costo_horario
) VALUES
(
    'Loma Amarilla',
    'Av. Monte de los Olivos 286, Santiago de Surco 15039',
    1,
    1,
    'Parque recreativo con áreas verdes',
    'Espacio ideal para actividades al aire libre y recreativas, rodeado de naturaleza.',
    987654321,
    'contacto@lomaamarilla.pe',
    'https://maps.app.goo.gl/69SE2DU3uieoSx6A9',
    -12.134697,
    -76.986939,
    100,
    150,
    '08:00:00',
    '20:00:00',
    1,
    50.0
),
(
    'PUCP',
    'Av. Universitaria 1801, San Miguel 15088',
    2,
    2,
    'Campus universitario moderno y amplio',
    'La PUCP ofrece espacios académicos, culturales y recreativos, con amplias zonas verdes y de estudio.',
    912345678,
    'contacto@pucp.edu.pe',
    'https://maps.app.goo.gl/m8ZsE6vN3dgGorch9',
    -12.07308330,
    -77.08192338,
    100,
    500,
    '07:30:00',
    '22:00:00',
    1,
    75.0
);
