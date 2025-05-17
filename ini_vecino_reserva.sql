INSERT INTO roles (id_rol, nombre) VALUES (1, "Vecino");

INSERT INTO usuario (id_usuario, nombres, apellidos, correo, contrasenia, direccion, dni, num_celular, id_rol, activo, fecha_nacimiento) VALUES (1, 'Sebastian', 'Ramirez', 'dhannysebas@hotmail.com', '$2a$12$gDEZAWdRcxdYAyqVX5WoxO/UD.JUZVVZ89b8YsdG47y5sB9QJTZfy', 'CATO', 72519702, 111999222,1, TRUE, '1990-01-01');

INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (1, 'Piscina');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (2, 'Canchas Fútbol');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (3, 'Pistas de Atletismo');
INSERT INTO tipoespacio (id_tipo_espacio, nombre) VALUES (4, 'Estadios');

INSERT INTO listafotos (id_lista_fotos) VALUES (1);

INSERT INTO espaciosdeportivos (id_espacio, nombre, ubicacion, id_tipo_espacio, id_lista_fotos, descripcion_corta, descripcion_larga, num_contacto, correo_contacto, operativo, costo_horario, maps_url) VALUES (1, 'Piscina Diego Ferre', 'San Miguel', 1, 1, 'Esta es una descripcion corta', 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga', 111222333, 'a20220378@pucp.edu.pe', true, 10.0, 'https://acortar.link/4DdNym');

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

INSERT INTO mediospago(id_medios_pago, nombre) VALUES (1, 'Yape');

INSERT INTO pagos(id_pagos, cantidad, id_medios_pago) VALUES (1, 25, 1);

UPDATE usuario SET activo = true where id_usuario=1;

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.id_espacio = 1;

SELECT * FROM horarioreservado;

SELECT * FROM reservas;
