INSERT INTO roles (idRol, rolNombre) VALUES (1, "Vecino");

INSERT INTO usuario (idUsuario, nombres, apellidos, correo, contrasenia, direccion, dni, numCelular, idRol, isBaneado, fechaNacimiento) VALUES (1, 'Sebastian', 'Ramirez', 'dhannysebas@hotmail.com', 'sebas2401', 'CATO', 72519702, 111999222,1, FALSE, '1990-01-01');

INSERT INTO tipoespacio (idTipoEspacio, nombreTipo) VALUES (1, 'Piscina');

INSERT INTO listafotos (idListaFotos) VALUES (1);

INSERT INTO espaciosdeportivos (idEspacio, nombre, ubicacion, idTipoEspacio, idListaFotos, descripcion_corta, descripcion_larga) VALUES (1, 'Piscina Diego Ferre', 'San Miguel', 1, 1, 'Esta es una descripcion corta', 'Esta es una descripcion laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaarga');

INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('9:00:00', '10:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('10:00:00', '11:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('12:00:00', '13:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('13:00:00', '14:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('14:00:00', '15:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('15:00:00', '16:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('16:00:00', '17:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('17:00:00', '18:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('18:00:00', '19:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('19:00:00', '20:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('20:00:00', '21:00:00', 1);
INSERT INTO horarios(horaInicio, horaFin, idEspacio) VALUES ('21:00:00', '22:00:00', 1);

INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (1, '2025-04-25', false, 1);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (2, '2025-04-25', true, 2);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (3, '2025-04-25', false, 3);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (4, '2025-04-25', false, 4);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (5, '2025-04-25', true, 5);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (6, '2025-04-25', false, 6);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (7, '2025-04-25', false, 7);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (8, '2025-04-25', true, 8);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (9, '2025-04-25', false, 9);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (10, '2025-04-25', false, 10);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (11, '2025-04-25', true, 11);
INSERT INTO horarioreservado(idHorarioReservado, fecha, isReservado, idHorarios) VALUES (12, '2025-04-25', false, 12);

UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 1;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 2;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 3;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 4;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 5;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 6;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 7;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 8;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 9;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 10;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 11;
UPDATE horarioreservado SET isReservado=false WHERE idHorarioReservado = 12;

INSERT INTO mediospago(idMediosPago, medioPago) VALUES (1, 'Yape');

INSERT INTO pagos(idPagos, cantidad, idMediosPago) VALUES (1, 25, 1);

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.idEspacio = 1;

SELECT * FROM horarioreservado;

SELECT * FROM reservas;