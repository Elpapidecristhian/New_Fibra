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

SELECT * FROM usuario;

SELECT * from tipoespacio;

SELECT * FROM roles;

SELECT * FROM horarios h WHERE h.idEspacio = 1;