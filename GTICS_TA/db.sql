-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema gtics
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema gtics
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `gtics` DEFAULT CHARACTER SET utf8mb3 ;
USE `gtics` ;

-- -----------------------------------------------------
-- Table `gtics`.`roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`roles` (
  `id_rol` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_rol`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nombres` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(60) NOT NULL,
  `direccion` VARCHAR(90) NULL DEFAULT NULL,
  `dni` INT NOT NULL,
  `num_celular` INT NULL DEFAULT NULL,
  `id_rol` INT NOT NULL,
  `foto` LONGBLOB NULL DEFAULT NULL,
  `foto_nombre` VARCHAR(50) NULL,
  `foto_tipo_archivo` VARCHAR(15) NULL,
  `activo` TINYINT NOT NULL,
  `fecha_nacimiento` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`id_usuario`, `id_rol`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `fk_Usuario_Roles_idx` (`id_rol` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_Roles`
    FOREIGN KEY (`id_rol`)
    REFERENCES `gtics`.`roles` (`id_rol`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`listafotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`listafotos` (
  `id_lista_fotos` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id_lista_fotos`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`tipoespacio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`tipoespacio` (
  `id_tipo_espacio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_espacio`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`espaciosdeportivos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`espaciosdeportivos` (
  `id_espacio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `ubicacion` VARCHAR(45) NOT NULL,
  `id_tipo_espacio` INT NOT NULL,
  `id_lista_fotos` INT NOT NULL,
  `descripcion_corta` VARCHAR(255) NOT NULL,
  `descripcion_larga` TEXT NOT NULL,
  `num_contacto` INT NOT NULL,
  `correo_contacto` VARCHAR(45) NOT NULL,
  `maps_url` VARCHAR(150) NULL,
  `latitud` DECIMAL(10, 8) NULL COMMENT 'Latitud en grados decimales',
  `longitud` DECIMAL(11, 8) NULL COMMENT 'Longitud en grados decimales',
  `radio_cobertura` INT NULL DEFAULT 100 COMMENT 'Radio de cobertura en metros para verificación de proximidad',
  `aforo` INT NULL,
  `hora_abre` TIME NOT NULL,
  `hora_cierra` TIME NOT NULL,
  `operativo` TINYINT NOT NULL,
  `costo_horario` FLOAT NOT NULL,
  PRIMARY KEY (`id_espacio`, `id_tipo_espacio`),
  INDEX `fk_EspaciosDeportivos_TipoEspacio1_idx` (`id_tipo_espacio` ASC) VISIBLE,
  INDEX `fk_EspaciosDeportivos_ListaFotos1_idx` (`id_lista_fotos` ASC) VISIBLE,
  CONSTRAINT `fk_EspaciosDeportivos_ListaFotos1`
    FOREIGN KEY (`id_lista_fotos`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`),
  CONSTRAINT `fk_EspaciosDeportivos_TipoEspacio1`
    FOREIGN KEY (`id_tipo_espacio`)
    REFERENCES `gtics`.`tipoespacio` (`id_tipo_espacio`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`horarioscoordinador`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`horarioscoordinador` (
  `id_horarios_coordinador` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `id_espacio` INT NOT NULL,
  `hora_entrada` TIME NOT NULL,
  `hora_salida` TIME NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_fin` DATE NOT NULL,
  PRIMARY KEY (`id_horarios_coordinador`, `id_usuario`, `id_espacio`),
  INDEX `fk_horarioscoordinador_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_horarioscoordinador_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  CONSTRAINT `fk_horarioscoordinador_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_horarioscoordinador_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`asistencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`asistencia` (
  `id_asistencia` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `horas_trabajadas` VARCHAR(45) NOT NULL,
  `hora_entrada` TIME NULL,
  `hora_salida` TIME NULL,
  `estado_asistencia` ENUM('A_TIEMPO', 'TARDE', 'FALTA', 'JUSTIFICADO') NOT NULL DEFAULT 'FALTA' COMMENT 'Estado de la asistencia del coordinador',
  `minutos_retraso` INT NULL DEFAULT 0 COMMENT 'Minutos de retraso si llegó tarde',
  `observaciones` VARCHAR(255) NULL COMMENT 'Observaciones adicionales sobre la asistencia',
  `registrado_por` INT NULL COMMENT 'Usuario que registró la asistencia',
  `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del registro',
  `id_horarios_coordinador` INT NOT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `fk_asistencia_horarioscoordinador1_idx` (`id_horarios_coordinador` ASC) VISIBLE,
  INDEX `fk_asistencia_registrado_por_idx` (`registrado_por` ASC) VISIBLE,
  CONSTRAINT `fk_asistencia_horarioscoordinador1`
    FOREIGN KEY (`id_horarios_coordinador`)
    REFERENCES `gtics`.`horarioscoordinador` (`id_horarios_coordinador`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_asistencia_registrado_por`
    FOREIGN KEY (`registrado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`canchasfutbol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`canchasfutbol` (
  `id_espacio` INT NOT NULL AUTO_INCREMENT,
  `tipo_superficie` ENUM('Grass', 'Losa') NOT NULL,
  `iluminacion_nocturna` TINYINT NOT NULL,
  `balones_disponibles` TINYINT NOT NULL,
  `ancho` FLOAT NOT NULL,
  `alto` FLOAT NOT NULL,
  PRIMARY KEY (`id_espacio`),
  INDEX `fk_canchasfutbol_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  CONSTRAINT `fk_canchasfutbol_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`estadios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`estadios` (
  `id_espacio` INT NOT NULL,
  `aforo` INT NOT NULL,
  `uso_permitido` VARCHAR(150) NOT NULL,
  `seguridad_disponible` TINYINT NOT NULL,
  `sonido_pantallas_disponible` TINYINT NOT NULL,
  `iluminacion_profesional_disponible` TINYINT NOT NULL,
  PRIMARY KEY (`id_espacio`),
  CONSTRAINT `fk_estadios_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`fotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`fotos` (
  `id_fotos` INT NOT NULL AUTO_INCREMENT,
  `foto` BLOB NOT NULL,
  `id_lista_fotos` INT NOT NULL,
  `foto_nombre` VARCHAR(50) NOT NULL,
  `foto_tipo_archivo` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`id_fotos`),
  INDEX `fk_Fotos_ListaFotos1_idx` (`id_lista_fotos` ASC) VISIBLE,
  CONSTRAINT `fk_Fotos_ListaFotos1`
    FOREIGN KEY (`id_lista_fotos`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`horarios` (
  `id_horarios` INT NOT NULL AUTO_INCREMENT,
  `hora_inicio` TIME NOT NULL,
  `hora_fin` TIME NOT NULL,
  `id_espacio` INT NOT NULL,
  PRIMARY KEY (`id_horarios`),
  INDEX `fk_Horarios_EspaciosDeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  CONSTRAINT `fk_Horarios_EspaciosDeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`horarioreservado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`horarioreservado` (
  `id_horario_reservado` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `id_horarios` INT NOT NULL,
  PRIMARY KEY (`id_horario_reservado`),
  INDEX `fk_HorarioReservado_Horarios1_idx` (`id_horarios` ASC) VISIBLE,
  CONSTRAINT `fk_HorarioReservado_Horarios1`
    FOREIGN KEY (`id_horarios`)
    REFERENCES `gtics`.`horarios` (`id_horarios`))
ENGINE = InnoDB
AUTO_INCREMENT = 13
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`mediospago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`mediospago` (
  `id_medios_pago` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `tipo_pago` ENUM('AUTOMATICO', 'MANUAL') NOT NULL COMMENT 'AUTOMATICO=tarjeta, MANUAL=transferencia/yape/plin',
  `requiere_verificacion` TINYINT DEFAULT 0 COMMENT 'Si requiere verificación manual del admin',
  `activo` TINYINT DEFAULT 1 COMMENT 'Si el medio de pago está disponible',
  `descripcion` VARCHAR(255) NULL COMMENT 'Descripción del medio de pago',
  `datos_cuenta` TEXT NULL COMMENT 'Número de cuenta, Yape, Plin, etc.',
  `icono` VARCHAR(100) NULL COMMENT 'Ruta del icono del medio de pago',
  PRIMARY KEY (`id_medios_pago`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`pagos` (
  `id_pagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` DECIMAL(10, 2) NOT NULL,
  `id_medios_pago` INT NOT NULL,
  `estado_pago` ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE',
  `fecha_pago` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_verificacion` TIMESTAMP NULL COMMENT 'Fecha de verificación por admin',
  `verificado_por` INT NULL COMMENT 'Admin que verificó el pago',
  `id_lista_fotos_comprobantes` INT NULL COMMENT 'Lista de fotos de comprobantes de pago',
  `numero_transaccion` VARCHAR(100) NULL COMMENT 'Número de transacción o referencia',
  `observaciones_admin` TEXT NULL COMMENT 'Observaciones del admin sobre el pago',
  `codigo_autorizacion` VARCHAR(50) NULL COMMENT 'Código de autorización de la pasarela',
  `datos_pasarela` JSON NULL COMMENT 'Respuesta completa de la pasarela de pago',
  `ip_usuario` VARCHAR(45) NULL COMMENT 'IP desde donde se realizó el pago',
  `user_agent` VARCHAR(255) NULL COMMENT 'Navegador del usuario',
  PRIMARY KEY (`id_pagos`),
  INDEX `fk_Pagos_MediosPago1_idx` (`id_medios_pago` ASC) VISIBLE,
  INDEX `fk_pagos_verificado_por_idx` (`verificado_por` ASC) VISIBLE,
  INDEX `fk_pagos_lista_fotos_idx` (`id_lista_fotos_comprobantes` ASC) VISIBLE,
  INDEX `idx_pagos_estado` (`estado_pago` ASC) VISIBLE,
  INDEX `idx_pagos_fecha` (`fecha_pago` ASC) VISIBLE,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`id_medios_pago`)
    REFERENCES `gtics`.`mediospago` (`id_medios_pago`),
  CONSTRAINT `fk_pagos_verificado_por`
    FOREIGN KEY (`verificado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_pagos_lista_fotos_comprobantes`
    FOREIGN KEY (`id_lista_fotos_comprobantes`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`piscinas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`piscinas` (
  `id_espacio` INT NOT NULL AUTO_INCREMENT,
  `tipo_piscina` ENUM('Olimpica', 'Publica') NOT NULL,
  `profundidad_min` FLOAT NOT NULL,
  `profundidad_max` FLOAT NOT NULL,
  `is_climatizada` TINYINT NOT NULL,
  `requisitos` VARCHAR(150) NOT NULL,
  `num_carril_max` INT NOT NULL,
  PRIMARY KEY (`id_espacio`),
  CONSTRAINT `fk_piscinas_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pistasatletismo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`pistasatletismo` (
  `id_espacio` INT NOT NULL,
  `tipo_superficie` ENUM('Tartan', 'Asfalto', 'Tierra') NOT NULL,
  `longitud` FLOAT NOT NULL,
  `implementos` VARCHAR(150) NOT NULL,
  PRIMARY KEY (`id_espacio`),
  CONSTRAINT `fk_pistasatletismo_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`reservas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`reservas` (
  `id_reservas` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `id_espacio` INT NOT NULL,
  `id_pagos` INT NOT NULL,
  `id_horarios` INT NOT NULL,
  `registro_timestamp` TIMESTAMP NOT NULL,
  `fecha_reserva` DATE NOT NULL,
  `estado_reserva` ENUM('ACTIVA', 'CANCELADA_USUARIO', 'CANCELADA_MANTENIMIENTO', 'CANCELADA_ADMIN', 'COMPLETADA') NOT NULL DEFAULT 'ACTIVA' COMMENT 'Estado de la reserva',
  `motivo_cancelacion` VARCHAR(255) NULL COMMENT 'Motivo de cancelación si aplica',
  `fecha_cancelacion` TIMESTAMP NULL COMMENT 'Fecha de cancelación',
  `cancelado_por` INT NULL COMMENT 'Usuario que canceló la reserva',
  `id_mantenimiento` INT NULL COMMENT 'ID del mantenimiento que causó la cancelación',
  `reembolso_procesado` TINYINT DEFAULT 0 COMMENT 'Si se procesó el reembolso',
  PRIMARY KEY (`id_reservas`),
  INDEX `fk_Reservas_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Reservas_EspaciosDeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_Reservas_Pagos1_idx` (`id_pagos` ASC) VISIBLE,
  INDEX `fk_reservas_horarios1_idx` (`id_horarios` ASC) VISIBLE,
  INDEX `fk_reservas_cancelado_por_idx` (`cancelado_por` ASC) VISIBLE,
  INDEX `fk_reservas_mantenimiento_idx` (`id_mantenimiento` ASC) VISIBLE,
  INDEX `idx_reservas_estado` (`estado_reserva` ASC) VISIBLE,
  INDEX `idx_reservas_fecha` (`fecha_reserva` ASC) VISIBLE,
  CONSTRAINT `fk_Reservas_EspaciosDeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`),
  CONSTRAINT `fk_Reservas_Pagos1`
    FOREIGN KEY (`id_pagos`)
    REFERENCES `gtics`.`pagos` (`id_pagos`),
  CONSTRAINT `fk_Reservas_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`),
  CONSTRAINT `fk_reservas_horarios1`
    FOREIGN KEY (`id_horarios`)
    REFERENCES `gtics`.`horarios` (`id_horarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reservas_cancelado_por`
    FOREIGN KEY (`cancelado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`tipocomentario` - DEPRECATED: Se reemplaza por ENUM
-- -----------------------------------------------------
-- Esta tabla se mantiene por compatibilidad pero se recomienda usar el ENUM en comentarios


-- -----------------------------------------------------
-- Table `gtics`.`comentarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`comentarios` (
  `id_comentarios` INT NOT NULL AUTO_INCREMENT,
  `id_espacio` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `tipo_comentario` ENUM('COMENTARIO', 'REPARACION') NOT NULL DEFAULT 'COMENTARIO' COMMENT 'Tipo de comentario usando ENUM',
  `contenido` TEXT NOT NULL,
  `id_lista_fotos` INT NULL COMMENT 'Fotos adjuntas al comentario',
  `fecha_creacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `activo` TINYINT DEFAULT 1 COMMENT 'Si el comentario está activo o fue eliminado',
  `revisado_por_admin` TINYINT DEFAULT 0 COMMENT 'Si el admin ya revisó el comentario',
  `requiere_mantenimiento` TINYINT DEFAULT 0 COMMENT 'Si el admin determina que requiere mantenimiento',
  `prioridad_mantenimiento` ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NULL COMMENT 'Prioridad asignada por el admin',
  `notas_admin` TEXT NULL COMMENT 'Notas internas del admin para programar mantenimiento',
  `fecha_revision` TIMESTAMP NULL COMMENT 'Fecha de revisión del administrador',
  `revisado_por` INT NULL COMMENT 'ID del admin que revisó',
  `id_mantenimiento_generado` INT NULL COMMENT 'ID del mantenimiento generado a partir de este comentario',
  PRIMARY KEY (`id_comentarios`),
  INDEX `fk_comentarios_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_comentarios_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_comentarios_listafotos1_idx` (`id_lista_fotos` ASC) VISIBLE,
  INDEX `fk_comentarios_revisado_por_idx` (`revisado_por` ASC) VISIBLE,
  INDEX `fk_comentarios_mantenimiento_idx` (`id_mantenimiento_generado` ASC) VISIBLE,
  INDEX `idx_comentarios_tipo` (`tipo_comentario` ASC) VISIBLE,
  INDEX `idx_comentarios_fecha` (`fecha_creacion` ASC) VISIBLE,
  INDEX `idx_comentarios_revision` (`revisado_por_admin` ASC) VISIBLE,
  INDEX `idx_comentarios_mantenimiento_req` (`requiere_mantenimiento` ASC) VISIBLE,
  CONSTRAINT `fk_comentarios_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_listafotos1`
    FOREIGN KEY (`id_lista_fotos`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_revisado_por`
    FOREIGN KEY (`revisado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_mantenimiento_generado`
    FOREIGN KEY (`id_mantenimiento_generado`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`mantenimiento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`mantenimiento` (
  `id_mantenimiento` INT NOT NULL AUTO_INCREMENT,
  `id_espacio` INT NOT NULL,
  `tipo_mantenimiento` ENUM('PREVENTIVO', 'MEJORA', 'LIMPIEZA_PROFUNDA') NOT NULL COMMENT 'Tipo de mantenimiento',
  `titulo` VARCHAR(100) NOT NULL COMMENT 'Título descriptivo del mantenimiento',
  `descripcion` TEXT NOT NULL COMMENT 'Descripción detallada del mantenimiento',
  `fecha_inicio` DATE NOT NULL COMMENT 'Fecha de inicio del mantenimiento',
  `fecha_fin` DATE NOT NULL COMMENT 'Fecha de finalización del mantenimiento',
  `hora_inicio` TIME NULL COMMENT 'Hora de inicio (NULL = todo el día)',
  `hora_fin` TIME NULL COMMENT 'Hora de finalización (NULL = todo el día)',
  `estado_mantenimiento` ENUM('PROGRAMADO', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO', 'POSPUESTO') NOT NULL DEFAULT 'PROGRAMADO',
  `prioridad` ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL DEFAULT 'MEDIA',
  `empresa_encargada` VARCHAR(100) NULL COMMENT 'Empresa o persona encargada',
  `contacto_encargado` VARCHAR(50) NULL COMMENT 'Teléfono de contacto',
  `requiere_cierre_total` TINYINT DEFAULT 1 COMMENT 'Si requiere cerrar completamente el espacio',
  `afecta_horarios_especificos` TINYINT DEFAULT 0 COMMENT 'Si solo afecta horarios específicos',
  `observaciones` TEXT NULL COMMENT 'Observaciones adicionales',
  `creado_por` INT NOT NULL COMMENT 'Usuario que creó el registro',
  `fecha_creacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reservas_canceladas` INT DEFAULT 0 COMMENT 'Número de reservas canceladas por este mantenimiento',
  `notificaciones_enviadas` TINYINT DEFAULT 0 COMMENT 'Si se enviaron notificaciones a usuarios afectados',
  PRIMARY KEY (`id_mantenimiento`),
  INDEX `fk_mantenimiento_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_mantenimiento_creado_por_idx` (`creado_por` ASC) VISIBLE,
  INDEX `idx_mantenimiento_fechas` (`fecha_inicio` ASC, `fecha_fin` ASC) VISIBLE,
  INDEX `idx_mantenimiento_estado` (`estado_mantenimiento` ASC) VISIBLE,
  INDEX `idx_mantenimiento_tipo` (`tipo_mantenimiento` ASC) VISIBLE,
  CONSTRAINT `fk_mantenimiento_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_mantenimiento_creado_por`
    FOREIGN KEY (`creado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `chk_mantenimiento_fechas` CHECK (`fecha_fin` >= `fecha_inicio`),
  CONSTRAINT `chk_mantenimiento_horarios` CHECK (
    (`hora_inicio` IS NULL AND `hora_fin` IS NULL) OR
    (`hora_inicio` IS NOT NULL AND `hora_fin` IS NOT NULL AND `hora_fin` > `hora_inicio`)
  ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Campos de geolocalización ya incluidos en la definición de la tabla espaciosdeportivos
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Agregar relación de mantenimiento en reservas
-- -----------------------------------------------------
ALTER TABLE `gtics`.`reservas`
ADD CONSTRAINT `fk_reservas_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION;

-- -----------------------------------------------------
-- Tabla de historial de cambios de estado de reservas
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`historial_reservas` (
  `id_historial` INT NOT NULL AUTO_INCREMENT,
  `id_reserva` INT NOT NULL,
  `estado_anterior` VARCHAR(50) NULL,
  `estado_nuevo` VARCHAR(50) NOT NULL,
  `motivo` VARCHAR(255) NULL,
  `cambiado_por` INT NULL,
  `fecha_cambio` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_historial`),
  INDEX `fk_historial_reservas_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_historial_usuario_idx` (`cambiado_por` ASC) VISIBLE,
  INDEX `idx_historial_fecha` (`fecha_cambio` ASC) VISIBLE,
  CONSTRAINT `fk_historial_reservas`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_historial_usuario`
    FOREIGN KEY (`cambiado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Tabla de notificaciones para usuarios
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`notificaciones` (
  `id_notificacion` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `tipo_notificacion` ENUM('CANCELACION_RESERVA', 'MANTENIMIENTO_PROGRAMADO', 'RECORDATORIO_RESERVA', 'CAMBIO_HORARIO', 'PROMOCION') NOT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `mensaje` TEXT NOT NULL,
  `leida` TINYINT DEFAULT 0,
  `fecha_creacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_lectura` TIMESTAMP NULL,
  `id_reserva` INT NULL COMMENT 'Reserva relacionada si aplica',
  `id_mantenimiento` INT NULL COMMENT 'Mantenimiento relacionado si aplica',
  PRIMARY KEY (`id_notificacion`),
  INDEX `fk_notificaciones_usuario_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_notificaciones_reserva_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_notificaciones_mantenimiento_idx` (`id_mantenimiento` ASC) VISIBLE,
  INDEX `idx_notificaciones_leida` (`leida` ASC) VISIBLE,
  INDEX `idx_notificaciones_fecha` (`fecha_creacion` ASC) VISIBLE,
  CONSTRAINT `fk_notificaciones_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_notificaciones_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_notificaciones_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Triggers eliminados: Toda la lógica se maneja desde Java
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `gtics`.`historial_pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`historial_pagos` (
  `id_historial_pago` INT NOT NULL AUTO_INCREMENT,
  `id_pago` INT NOT NULL,
  `estado_anterior` VARCHAR(50) NULL,
  `estado_nuevo` VARCHAR(50) NOT NULL,
  `motivo` VARCHAR(255) NULL COMMENT 'Motivo del cambio de estado',
  `cambiado_por` INT NULL COMMENT 'Usuario que cambió el estado',
  `fecha_cambio` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `observaciones` TEXT NULL,
  PRIMARY KEY (`id_historial_pago`),
  INDEX `fk_historial_pagos_pago_idx` (`id_pago` ASC) VISIBLE,
  INDEX `fk_historial_pagos_usuario_idx` (`cambiado_por` ASC) VISIBLE,
  INDEX `idx_historial_pagos_fecha` (`fecha_cambio` ASC) VISIBLE,
  CONSTRAINT `fk_historial_pagos_pago`
    FOREIGN KEY (`id_pago`)
    REFERENCES `gtics`.`pagos` (`id_pagos`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_historial_pagos_usuario`
    FOREIGN KEY (`cambiado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Datos iniciales para medios de pago
-- -----------------------------------------------------
INSERT INTO `gtics`.`mediospago` (`id_medios_pago`, `nombre`, `tipo_pago`, `requiere_verificacion`, `activo`, `descripcion`, `datos_cuenta`, `icono`) VALUES
(1, 'Tarjeta de Crédito/Débito', 'AUTOMATICO', 0, 1, 'Pago con tarjeta a través de pasarela segura', NULL, 'credit-card.png'),
(2, 'Transferencia Bancaria', 'MANUAL', 1, 1, 'Transferencia a cuenta bancaria', 'Banco: BCP\nCuenta: 123-456789-0-12\nCCI: 00212312345678901234', 'bank-transfer.png'),
(3, 'Yape', 'MANUAL', 1, 1, 'Pago mediante Yape', 'Número Yape: 987654321\nNombre: Espacios Deportivos SAC', 'yape.png'),
(4, 'Plin', 'MANUAL', 1, 1, 'Pago mediante Plin', 'Número Plin: 987654321\nNombre: Espacios Deportivos SAC', 'plin.png');

-- -----------------------------------------------------
-- Agregar relación de mantenimiento en reservas
-- -----------------------------------------------------
ALTER TABLE `gtics`.`reservas`
ADD CONSTRAINT `fk_reservas_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION;

-- -----------------------------------------------------
-- Tabla de historial de cambios de estado de reservas
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`historial_reservas` (
  `id_historial` INT NOT NULL AUTO_INCREMENT,
  `id_reserva` INT NOT NULL,
  `estado_anterior` VARCHAR(50) NULL,
  `estado_nuevo` VARCHAR(50) NOT NULL,
  `motivo` VARCHAR(255) NULL,
  `cambiado_por` INT NULL,
  `fecha_cambio` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_historial`),
  INDEX `fk_historial_reservas_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_historial_usuario_idx` (`cambiado_por` ASC) VISIBLE,
  INDEX `idx_historial_fecha` (`fecha_cambio` ASC) VISIBLE,
  CONSTRAINT `fk_historial_reservas`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_historial_usuario`
    FOREIGN KEY (`cambiado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Tabla de notificaciones para usuarios
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`notificaciones` (
  `id_notificacion` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `tipo_notificacion` ENUM('CANCELACION_RESERVA', 'MANTENIMIENTO_PROGRAMADO', 'RECORDATORIO_RESERVA', 'CAMBIO_HORARIO', 'PROMOCION') NOT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `mensaje` TEXT NOT NULL,
  `leida` TINYINT DEFAULT 0,
  `fecha_creacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `fecha_lectura` TIMESTAMP NULL,
  `id_reserva` INT NULL COMMENT 'Reserva relacionada si aplica',
  `id_mantenimiento` INT NULL COMMENT 'Mantenimiento relacionado si aplica',
  PRIMARY KEY (`id_notificacion`),
  INDEX `fk_notificaciones_usuario_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_notificaciones_reserva_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_notificaciones_mantenimiento_idx` (`id_mantenimiento` ASC) VISIBLE,
  INDEX `idx_notificaciones_leida` (`leida` ASC) VISIBLE,
  INDEX `idx_notificaciones_fecha` (`fecha_creacion` ASC) VISIBLE,
  CONSTRAINT `fk_notificaciones_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_notificaciones_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_notificaciones_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

-- -----------------------------------------------------
-- Trigger eliminado: Cualquier usuario puede comentar
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Procedimientos eliminados: La lógica se maneja desde Java
-- -----------------------------------------------------



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
