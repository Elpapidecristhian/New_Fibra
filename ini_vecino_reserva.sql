INSERT INTO roles (id_rol, nombre) VALUES (1, "Vecino");
INSERT INTO roles (id_rol, nombre) VALUES (2, "Coordinador");
INSERT INTO roles (id_rol, nombre) VALUES (3, "Admin");
INSERT INTO roles (id_rol, nombre) VALUES (4, "SuperAdmin");

INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (1, 'Sebastian', 'Ramirez', 'dhannysebas@hotmail.com', 'sebas2401', 'CATO', 72519702, 111999222,1, FALSE, '1990-01-01');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (2, 'Ariana', 'Zuñiga', 'arianazuniga01@gmail.com', 'ariana0701', 'Av. Monte de los Olivos 286', 73992601, 992533648, 2, FALSE, '2000-05-08');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (3, 'Joaquin', 'Arriarán', 'joaqui.arri23@gmail.com', 'mau33434', 'Av. Velazco Astete 2917', 66258978, 980012200,2, FALSE, '1993-01-15');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (4, 'Samantha', 'Villanueva', 'sam.villa24@hotmail.com', 'sam234', 'Jr. Benito Juarez 342', 72699945, 978465111,3, FALSE, '2001-11-05');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (5, 'Diego', 'Linares', 'linares.zeus87@gmail.com', 'zeus989', 'Av. El Sol 4655', 85237790, 978456178,3, FALSE, '2004-12-10');
INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, is_baneado, fecha_nacimiento) VALUES (6, 'Sebastian', 'Panez', 'saap2931@hotmail.com', 'saap9686', 'Av. Gregorio Escobedo 345', 72519881, 966945817,1, FALSE, '2007-08-25');

INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (1, 'Piscina');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (2, 'Canchas Fútbol');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (3, 'Pistas de Atletismo');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (4, 'Estadios');

INSERT INTO listafotos (id_lista_fotos) VALUES (1);

INSERT INTO espaciosdeportivos (id_espacio, nombre, ubicacion, id_tipo_espacio, id_lista_fotos, descripcion_corta, descripcion_larga, num_contacto, correo_contacto, operativo, costo_horario) VALUES (1, 'Piscina Diego Ferre', 'San Miguel', 1, 1, 'Esta es una descripcion corta', 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga', 111222333, 'a20220378@pucp.edu.pe', true, 10.0);

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

INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (1, '2025-04-25', false, 1);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (2, '2025-04-25', true, 2);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (3, '2025-04-25', false, 3);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (4, '2025-04-25', false, 4);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (5, '2025-04-25', true, 5);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (6, '2025-04-25', false, 6);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (7, '2025-04-25', false, 7);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (8, '2025-04-25', true, 8);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (9, '2025-04-25', false, 9);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (10, '2025-04-25', false, 10);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (11, '2025-04-25', true, 11);
INSERT INTO horarioreservado(id_horario_reservado, fecha, is_reservado, id_horarios) VALUES (12, '2025-04-25', false, 12);

INSERT INTO mediospago(id_medios_pago, nombre) VALUES (1, 'Yape');

INSERT INTO pagos(id_pagos, cantidad, id_medios_pago) VALUES (1, 25, 1);



UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 1;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 2;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 3;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 4;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 5;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 6;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 7;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 8;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 9;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 10;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 11;
UPDATE horarioreservado SET is_reservado=false WHERE id_horario_reservado = 12;

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.id_espacio = 1;

SELECT * FROM horarioreservado;

SELECT * FROM reservas;
