INSERT INTO roles (id_rol, nombre) VALUES (1, "Vecino");
INSERT INTO roles (id_rol, nombre) VALUES (2, "SuperAdmin");
INSERT INTO roles (id_rol, nombre) VALUES (3, "Coordinador");
INSERT INTO roles (id_rol, nombre) VALUES (4, "Admin");


INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (1, 'Sebastian', 'Ramirez', 'dhannysebas@hotmail.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'CATO', 72519702, 111999222,1, TRUE, '1990-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (2, 'Pepito', 'Perez', 'a20210845@pucp.edu.pe', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'PUCP', 12345678, 111333222,2, TRUE, '1993-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (3, 'Lujo', 'Poga', 'lujancarrion@pucp.edu.pe', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'MI CASA', 87654321, 111333222,4, TRUE, '1996-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (4, 'Ariana', 'Zuñiga', 'arianazuniga01@gmail.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'Av. Monte de los Olivos 286', 72885602, 994615847,3, TRUE, '2004-01-07');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (5, 'Joaquin', 'Arriaran', 'joaquinadmin@gmail.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'Av. Ejercito', 72875845, 995856147,4, TRUE, '2006-01-07');

INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (1, 'Piscina');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (2, 'Canchas Fútbol');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (3, 'Pistas de Atletismo');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (4, 'Estadios');

INSERT INTO listafotos (id_lista_fotos) VALUES (1);
INSERT INTO listafotos (id_lista_fotos) VALUES (2);
INSERT INTO listafotos (id_lista_fotos) VALUES (3);
INSERT INTO listafotos (id_lista_fotos) VALUES (4);
INSERT INTO listafotos (id_lista_fotos) VALUES (5);

INSERT INTO fotos (id_fotos, foto, id_lista_fotos, foto_nombre, foto_tipo_archivo) 
VALUES (1, LOAD_FILE('ruta_a_imagen.jpg'), 1, 'medidas_lg.jpg', 'image/jpeg');

INSERT INTO espaciosdeportivos (id_espacio, nombre, ubicacion, id_tipo_espacio, id_lista_fotos, descripcion_corta, descripcion_larga, num_contacto, correo_contacto, operativo, costo_horario, maps_url, hora_abre, hora_cierra) VALUES (1, 'Piscina Diego Ferre', 'San Miguel', 1, 1, 'Esta es una descripcion corta', 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga', 111222333, 'a20220378@pucp.edu.pe', true, 10.0, 'https://acortar.link/4DdNym', '09:00', '21:00');
INSERT IGNORE INTO `gtics`.`espaciosdeportivos` 
(`id_espacio`, `nombre`, `ubicacion`, `id_tipo_espacio`, `id_lista_fotos`, `descripcion_corta`, `descripcion_larga`, `num_contacto`, `correo_contacto`, `hora_abre`, `hora_cierra`, `operativo`, `costo_horario`, `aforo`) VALUES 
(2, 'Piscina Olímpica San Miguel', 'Complejo Deportivo San Miguel', 1, 1, 'Piscina olímpica de 50 metros', 'Piscina olímpica de 50 metros con 8 carriles, ideal para natación recreativa y competitiva', 987654321, 'piscina@sanmiguel.gob.pe', '06:00:00', '22:00:00', 1, 25.00, 50),
(3, 'Cancha de Fútbol Principal', 'Complejo Deportivo San Miguel', 2, 2, 'Cancha de fútbol con césped natural', 'Cancha de fútbol profesional con césped natural, iluminación nocturna y graderías', 987654322, 'cancha@sanmiguel.gob.pe', '06:00:00', '23:00:00', 1, 80.00, 200),
(4, 'Pista de Atletismo', 'Complejo Deportivo San Miguel', 3, 3, 'Pista de atletismo de 400 metros', 'Pista de atletismo de 400 metros con superficie de tartán, 8 carriles', 987654323, 'pista@sanmiguel.gob.pe', '05:00:00', '21:00:00', 1, 15.00, 100),
(5, 'Estadio Municipal', 'Estadio Municipal San Miguel', 4, 4, 'Estadio municipal con capacidad para 5000 personas', 'Estadio municipal con césped natural, iluminación profesional y capacidad para 5000 espectadores', 987654324, 'estadio@sanmiguel.gob.pe', '08:00:00', '22:00:00', 1, 200.00, 5000);

INSERT INTO piscinas(id_espacio, tipo_piscina, profundidad_min, profundidad_max, is_climatizada, requisitos, num_carril_max) VALUES (1, 'Olímpica', 1.0, 2.5, true, 'Llevar gorro y lentes para piscina', 8);

INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('9:00:00', '10:00:00', 1, 1);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('10:00:00', '11:00:00', 1, 2);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('12:00:00', '13:00:00', 1, 3);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('13:00:00', '14:00:00', 1, 4);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('14:00:00', '15:00:00', 1, 5);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('15:00:00', '16:00:00', 1, 6);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('16:00:00', '17:00:00', 1, 7);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('17:00:00', '18:00:00', 1, 8);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('18:00:00', '19:00:00', 1, 9);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('19:00:00', '20:00:00', 1, 10);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('20:00:00', '21:00:00', 1, 11);
INSERT INTO horarios(hora_inicio, hora_fin, id_espacio, id_horarios) VALUES ('21:00:00', '22:00:00', 1, 12);

-- 6. Insertar horarios para cada espacio
INSERT IGNORE INTO `gtics`.`horarios` (`id_horarios`, `hora_inicio`, `hora_fin`, `id_espacio`) VALUES 
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

INSERT INTO `gtics`.`mediospago` (`nombre`, `tipo_pago`, `requiere_verificacion`, `activo`, `descripcion`, `datos_cuenta`, `icono`) VALUES
('Tarjeta de Crédito/Débito', 'AUTOMATICO', 0, 1, 'Pago con tarjeta a través de pasarela segura', NULL, 'credit-card.png'),
('Transferencia Bancaria', 'MANUAL', 1, 1, 'Transferencia a cuenta bancaria', 'Banco: BCP\nCuenta: 123-456789-0-12\nCCI: 00212312345678901234', 'bank-transfer.png'),
('Yape', 'MANUAL', 1, 1, 'Pago mediante Yape', 'Número Yape: 987654321\nNombre: Espacios Deportivos SAC', 'yape.png'),
('Plin', 'MANUAL', 1, 1, 'Pago mediante Plin', 'Número Plin: 987654321\nNombre: Espacios Deportivos SAC', 'plin.png');

INSERT IGNORE INTO `gtics`.`pagos` (`id_pagos`, `cantidad`, `id_medios_pago`, `estado_pago`, `fecha_pago`, `numero_transaccion`) VALUES 
(1, 25.00, 3, 'PENDIENTE', '2024-01-15 10:30:00', 'YAPE-20240115-001'),
(2, 80.00, 2, 'PENDIENTE', '2024-01-15 14:20:00', 'TRANS-20240115-002'),
(3, 15.00, 1, 'APROBADO', '2024-01-16 09:15:00', 'CARD-20240116-003'),
(4, 25.00, 3, 'APROBADO', '2024-01-16 16:45:00', 'YAPE-20240116-004'), 
(5, 200.00, 2, 'PENDIENTE', '2024-01-17 11:30:00', 'TRANS-20240117-005');

-- Reservas
INSERT IGNORE INTO `gtics`.`reservas` 
(`id_reservas`, `id_usuario`, `id_espacio`, `id_pagos`, `id_horarios`, `registro_timestamp`, `fecha_reserva`, `estado_reserva`) VALUES 
-- Reservas para Piscina (id_tipo_espacio = 1)
(6, 1, 1, 1, 1, '2024-01-15 10:30:00', '2024-01-20', 'ACTIVA'),
(7, 2, 1, 4, 4, '2024-01-16 16:45:00', '2024-01-22', 'ACTIVA'),
-- Reservas para Cancha (id_tipo_espacio = 2)  
(8, 2, 2, 2, 8, '2024-01-15 14:20:00', '2024-01-21', 'ACTIVA'),
-- Reservas para Pista (id_tipo_espacio = 3)
(9, 3, 3, 3, 11, '2024-01-16 09:15:00', '2024-01-23', 'ACTIVA'),
-- Reservas para Estadio (id_tipo_espacio = 4)
(10, 1, 4, 5, 15, '2024-01-17 11:30:00', '2024-01-25', 'ACTIVA'),
(11, 1, 4, 5, 15, '2024-01-17 11:30:00', '2024-01-25', 'ACTIVA');

UPDATE usuario SET activo = true where id_usuario=1;

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.id_espacio = 1;

SELECT * FROM horarioreservado;

SELECT * FROM reservas;

SELECT * FROM espaciosdeportivos;

SELECT * FROM fotos;

SELECT * FROM canchasfutbol;
