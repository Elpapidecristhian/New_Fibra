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
AUTO_INCREMENT = 5
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
  `foto_nombre` VARCHAR(50) NULL DEFAULT NULL,
  `foto_tipo_archivo` VARCHAR(15) NULL DEFAULT NULL,
  `activo` TINYINT NOT NULL,
  `foto_url` VARCHAR(500) NULL DEFAULT NULL,
  `fecha_nacimiento` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`id_usuario`, `id_rol`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `fk_Usuario_Roles_idx` (`id_rol` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_Roles`
    FOREIGN KEY (`id_rol`)
    REFERENCES `gtics`.`roles` (`id_rol`))
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`accountactivate`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`accountactivate` (
  `token` VARCHAR(100) NOT NULL,
  `id_usuario` INT NOT NULL,
  PRIMARY KEY (`token`),
  UNIQUE INDEX `token_UNIQUE` (`token` ASC) VISIBLE,
  INDEX `fk_accountactivate_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_accountactivate_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`asignacion_coordinador`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`asignacion_coordinador` (
  `id_asignacion` INT NOT NULL AUTO_INCREMENT,
  `estado_asignacion` ENUM('CANCELADA', 'COMPLETADA', 'CONFIRMADA', 'EN_PROGRESO', 'PENDIENTE') NOT NULL,
  `fecha_actualizacion` DATETIME(6) NOT NULL,
  `fecha_asignacion` DATE NOT NULL,
  `fecha_creacion` DATETIME(6) NOT NULL,
  `hora_inicio` TIME NOT NULL,
  `hora_fin` TIME NOT NULL,
  `observaciones` TEXT NULL DEFAULT NULL,
  `ubicacion` VARCHAR(200) NULL DEFAULT NULL,
  `asignado_por` INT NOT NULL,
  `id_coordinador` INT NOT NULL,
  `id_reserva` INT NOT NULL,
  PRIMARY KEY (`id_asignacion`),
  INDEX `fk_asignacion_coordinador_asignado_por_idx` (`asignado_por` ASC) VISIBLE,
  INDEX `fk_asignacion_coordinador_id_coordinador_idx` (`id_coordinador` ASC) VISIBLE,
  INDEX `fk_asignacion_coordinador_reservas_idx` (`id_reserva` ASC) VISIBLE,
  CONSTRAINT `fk_asignacion_coordinador_asignado_por`
    FOREIGN KEY (`asignado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_asignacion_coordinador_id_coordinador`
    FOREIGN KEY (`id_coordinador`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_asignacion_coordinador_reservas`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reserva` (`id_reservas`)
    ON DELETE CASCADE)
ENGINE = InnoDB
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
  `ubicacion` VARCHAR(150) NOT NULL,
  `id_tipo_espacio` INT NOT NULL,
  `id_lista_fotos` INT NOT NULL,
  `descripcion_corta` VARCHAR(255) NOT NULL,
  `descripcion_larga` TEXT NOT NULL,
  `num_contacto` INT NOT NULL,
  `correo_contacto` VARCHAR(45) NOT NULL,
  `maps_url` VARCHAR(150) NULL DEFAULT NULL,
  `latitud` DECIMAL(10,8) NULL DEFAULT NULL COMMENT 'Latitud en grados decimales',
  `longitud` DECIMAL(11,8) NULL DEFAULT NULL COMMENT 'Longitud en grados decimales',
  `radio_cobertura` INT NULL DEFAULT '100' COMMENT 'Radio de cobertura en metros para verificación de proximidad',
  `aforo` INT NULL DEFAULT NULL,
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
  CONSTRAINT `fk_horarioscoordinador_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`),
  CONSTRAINT `fk_horarioscoordinador_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`asistencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`asistencia` (
  `id_asistencia` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `horas_trabajadas` VARCHAR(45) NOT NULL,
  `hora_entrada` TIME NULL DEFAULT NULL,
  `hora_salida` TIME NULL DEFAULT NULL,
  `estado_asistencia` ENUM('A_TIEMPO', 'TARDE', 'FALTA', 'JUSTIFICADO') NOT NULL DEFAULT 'FALTA' COMMENT 'Estado de la asistencia del coordinador',
  `minutos_retraso` INT NULL DEFAULT '0' COMMENT 'Minutos de retraso si llegó tarde',
  `observaciones` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Observaciones adicionales sobre la asistencia',
  `registrado_por` INT NULL DEFAULT NULL COMMENT 'Usuario que registró la asistencia',
  `fecha_registro` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora del registro',
  `id_horarios_coordinador` INT NOT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `fk_asistencia_horarioscoordinador1_idx` (`id_horarios_coordinador` ASC) VISIBLE,
  INDEX `fk_asistencia_registrado_por_idx` (`registrado_por` ASC) VISIBLE,
  CONSTRAINT `fk_asistencia_horarioscoordinador1`
    FOREIGN KEY (`id_horarios_coordinador`)
    REFERENCES `gtics`.`horarioscoordinador` (`id_horarios_coordinador`),
  CONSTRAINT `fk_asistencia_registrado_por`
    FOREIGN KEY (`registrado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL)
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
  `hora_inicio` TIME NULL DEFAULT NULL COMMENT 'Hora de inicio (NULL = todo el día)',
  `hora_fin` TIME NULL DEFAULT NULL COMMENT 'Hora de finalización (NULL = todo el día)',
  `estado_mantenimiento` ENUM('PROGRAMADO', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO', 'POSPUESTO') NOT NULL DEFAULT 'PROGRAMADO',
  `prioridad` ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL DEFAULT 'MEDIA',
  `empresa_encargada` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Empresa o persona encargada',
  `contacto_encargado` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Teléfono de contacto',
  `requiere_cierre_total` TINYINT NULL DEFAULT '1' COMMENT 'Si requiere cerrar completamente el espacio',
  `afecta_horarios_especificos` TINYINT NULL DEFAULT '0' COMMENT 'Si solo afecta horarios específicos',
  `observaciones` TEXT NULL DEFAULT NULL COMMENT 'Observaciones adicionales',
  `creado_por` INT NOT NULL COMMENT 'Usuario que creó el registro',
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reservas_canceladas` INT NULL DEFAULT '0' COMMENT 'Número de reservas canceladas por este mantenimiento',
  `notificaciones_enviadas` TINYINT NULL DEFAULT '0' COMMENT 'Si se enviaron notificaciones a usuarios afectados',
  `costo_estimado` DECIMAL(10,2) NULL DEFAULT NULL,
  `costo_real` DECIMAL(10,2) NULL DEFAULT NULL,
  PRIMARY KEY (`id_mantenimiento`),
  INDEX `fk_mantenimiento_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_mantenimiento_creado_por_idx` (`creado_por` ASC) VISIBLE,
  INDEX `idx_mantenimiento_fechas` (`fecha_inicio` ASC, `fecha_fin` ASC) VISIBLE,
  INDEX `idx_mantenimiento_estado` (`estado_mantenimiento` ASC) VISIBLE,
  INDEX `idx_mantenimiento_tipo` (`tipo_mantenimiento` ASC) VISIBLE,
  CONSTRAINT `fk_mantenimiento_creado_por`
    FOREIGN KEY (`creado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`),
  CONSTRAINT `fk_mantenimiento_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`comentarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`comentarios` (
  `id_comentarios` INT NOT NULL AUTO_INCREMENT,
  `id_espacio` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `tipo_comentario` ENUM('COMENTARIO', 'REPARACION') NOT NULL DEFAULT 'COMENTARIO' COMMENT 'Tipo de comentario usando ENUM',
  `contenido` TEXT NOT NULL,
  `prioridad_usuario` ENUM('BAJA', 'MEDIA', 'ALTA', 'CRITICA') NULL DEFAULT NULL COMMENT 'Prioridad indicada por el usuario (solo para tipo REPARACION)',
  `id_lista_fotos` INT NULL DEFAULT NULL COMMENT 'Fotos adjuntas al comentario',
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `activo` TINYINT NULL DEFAULT '1' COMMENT 'Si el comentario está activo o fue eliminado',
  `revisado_por_admin` TINYINT NULL DEFAULT '0' COMMENT 'Si el admin ya revisó el comentario',
  `requiere_mantenimiento` TINYINT NULL DEFAULT '0' COMMENT 'Si el admin determina que requiere mantenimiento',
  `notas_admin` TEXT NULL DEFAULT NULL COMMENT 'Notas internas del admin para programar mantenimiento',
  `fecha_revision` TIMESTAMP NULL DEFAULT NULL COMMENT 'Fecha de revisión del administrador',
  `revisado_por` INT NULL DEFAULT NULL COMMENT 'ID del admin que revisó',
  `id_mantenimiento_generado` INT NULL DEFAULT NULL COMMENT 'ID del mantenimiento generado a partir de este comentario',
  `prioridad_mantenimiento` ENUM('ALTA', 'BAJA', 'CRITICA', 'MEDIA') NULL DEFAULT NULL,
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
    ON DELETE CASCADE,
  CONSTRAINT `fk_comentarios_listafotos1`
    FOREIGN KEY (`id_lista_fotos`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_comentarios_mantenimiento_generado`
    FOREIGN KEY (`id_mantenimiento_generado`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_comentarios_revisado_por`
    FOREIGN KEY (`revisado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_comentarios_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE)
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
  `foto` BLOB NULL,
  `id_lista_fotos` INT NOT NULL,
  `foto_nombre` VARCHAR(50) NULL,
  `foto_tipo_archivo` VARCHAR(15) NULL,
  `foto_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id_fotos`),
  INDEX `fk_Fotos_ListaFotos1_idx` (`id_lista_fotos` ASC) VISIBLE,
  CONSTRAINT `fk_Fotos_ListaFotos1`
    FOREIGN KEY (`id_lista_fotos`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`mediospago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`mediospago` (
  `id_medios_pago` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `tipo_pago` ENUM('AUTOMATICO', 'MANUAL') NOT NULL COMMENT 'AUTOMATICO=tarjeta, MANUAL=transferencia/yape/plin',
  `requiere_verificacion` TINYINT NULL DEFAULT '0' COMMENT 'Si requiere verificación manual del admin',
  `activo` TINYINT NULL DEFAULT '1' COMMENT 'Si el medio de pago está disponible',
  `descripcion` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Descripción del medio de pago',
  `datos_cuenta` TEXT NULL DEFAULT NULL COMMENT 'Número de cuenta, Yape, Plin, etc.',
  `icono` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Ruta del icono del medio de pago',
  PRIMARY KEY (`id_medios_pago`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`pagos` (
  `id_pagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` DECIMAL(10,2) NOT NULL,
  `id_medios_pago` INT NOT NULL,
  `estado_pago` ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO', 'PROCESANDO') NOT NULL DEFAULT 'PENDIENTE',
  `fecha_pago` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_verificacion` TIMESTAMP NULL DEFAULT NULL COMMENT 'Fecha de verificación por admin',
  `verificado_por` INT NULL DEFAULT NULL COMMENT 'Admin que verificó el pago',
  `id_lista_fotos_comprobantes` INT NULL DEFAULT NULL COMMENT 'Lista de fotos de comprobantes de pago',
  `numero_transaccion` VARCHAR(100) NULL DEFAULT NULL COMMENT 'Número de transacción o referencia',
  `observaciones_admin` TEXT NULL DEFAULT NULL COMMENT 'Observaciones del admin sobre el pago',
  `codigo_autorizacion` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Código de autorización de la pasarela',
  `datos_pasarela` JSON NULL DEFAULT NULL COMMENT 'Respuesta completa de la pasarela de pago',
  `ip_usuario` VARCHAR(45) NULL DEFAULT NULL COMMENT 'IP desde donde se realizó el pago',
  `user_agent` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Navegador del usuario',
  PRIMARY KEY (`id_pagos`),
  INDEX `fk_Pagos_MediosPago1_idx` (`id_medios_pago` ASC) VISIBLE,
  INDEX `fk_pagos_verificado_por_idx` (`verificado_por` ASC) VISIBLE,
  INDEX `fk_pagos_lista_fotos_idx` (`id_lista_fotos_comprobantes` ASC) VISIBLE,
  INDEX `idx_pagos_estado` (`estado_pago` ASC) VISIBLE,
  INDEX `idx_pagos_fecha` (`fecha_pago` ASC) VISIBLE,
  CONSTRAINT `fk_pagos_lista_fotos_comprobantes`
    FOREIGN KEY (`id_lista_fotos_comprobantes`)
    REFERENCES `gtics`.`listafotos` (`id_lista_fotos`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`id_medios_pago`)
    REFERENCES `gtics`.`mediospago` (`id_medios_pago`),
  CONSTRAINT `fk_pagos_verificado_por`
    FOREIGN KEY (`verificado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`historial_pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`historial_pagos` (
  `id_historial_pago` INT NOT NULL AUTO_INCREMENT,
  `id_pago` INT NOT NULL,
  `estado_anterior` VARCHAR(50) NULL DEFAULT NULL,
  `estado_nuevo` VARCHAR(50) NOT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Motivo del cambio de estado',
  `cambiado_por` INT NULL DEFAULT NULL COMMENT 'Usuario que cambió el estado',
  `fecha_cambio` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `observaciones` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id_historial_pago`),
  INDEX `fk_historial_pagos_pago_idx` (`id_pago` ASC) VISIBLE,
  INDEX `fk_historial_pagos_usuario_idx` (`cambiado_por` ASC) VISIBLE,
  INDEX `idx_historial_pagos_fecha` (`fecha_cambio` ASC) VISIBLE,
  CONSTRAINT `fk_historial_pagos_pago`
    FOREIGN KEY (`id_pago`)
    REFERENCES `gtics`.`pagos` (`id_pagos`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_historial_pagos_usuario`
    FOREIGN KEY (`cambiado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL)
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
  `motivo_cancelacion` VARCHAR(255) NULL DEFAULT NULL COMMENT 'Motivo de cancelación si aplica',
  `fecha_cancelacion` TIMESTAMP NULL DEFAULT NULL COMMENT 'Fecha de cancelación',
  `cancelado_por` INT NULL DEFAULT NULL COMMENT 'Usuario que canceló la reserva',
  `id_mantenimiento` INT NULL DEFAULT NULL COMMENT 'ID del mantenimiento que causó la cancelación',
  `reembolso_procesado` TINYINT NULL DEFAULT '0' COMMENT 'Si se procesó el reembolso',
  PRIMARY KEY (`id_reservas`),
  INDEX `fk_Reservas_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Reservas_EspaciosDeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_Reservas_Pagos1_idx` (`id_pagos` ASC) VISIBLE,
  INDEX `fk_reservas_horarios1_idx` (`id_horarios` ASC) VISIBLE,
  INDEX `fk_reservas_cancelado_por_idx` (`cancelado_por` ASC) VISIBLE,
  INDEX `fk_reservas_mantenimiento_idx` (`id_mantenimiento` ASC) VISIBLE,
  INDEX `idx_reservas_estado` (`estado_reserva` ASC) VISIBLE,
  INDEX `idx_reservas_fecha` (`fecha_reserva` ASC) VISIBLE,
  CONSTRAINT `fk_reservas_cancelado_por`
    FOREIGN KEY (`cancelado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_Reservas_EspaciosDeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`),
  CONSTRAINT `fk_reservas_horarios1`
    FOREIGN KEY (`id_horarios`)
    REFERENCES `gtics`.`horarios` (`id_horarios`),
  CONSTRAINT `fk_reservas_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_Reservas_Pagos1`
    FOREIGN KEY (`id_pagos`)
    REFERENCES `gtics`.`pagos` (`id_pagos`),
  CONSTRAINT `fk_Reservas_Usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`historial_reservas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`historial_reservas` (
  `id_historial` INT NOT NULL AUTO_INCREMENT,
  `id_reserva` INT NOT NULL,
  `estado_anterior` VARCHAR(50) NULL DEFAULT NULL,
  `estado_nuevo` VARCHAR(50) NOT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  `cambiado_por` INT NULL DEFAULT NULL,
  `fecha_cambio` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_historial`),
  INDEX `fk_historial_reservas_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_historial_usuario_idx` (`cambiado_por` ASC) VISIBLE,
  INDEX `idx_historial_fecha` (`fecha_cambio` ASC) VISIBLE,
  CONSTRAINT `fk_historial_reservas`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_historial_usuario`
    FOREIGN KEY (`cambiado_por`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE SET NULL)
ENGINE = InnoDB
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
-- Table `gtics`.`notificaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`notificaciones` (
  `id_notificacion` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `tipo_notificacion` ENUM('CANCELACION_RESERVA', 'MANTENIMIENTO_PROGRAMADO', 'RECORDATORIO_RESERVA', 'CAMBIO_HORARIO', 'PROMOCION') NOT NULL,
  `titulo` VARCHAR(100) NOT NULL,
  `mensaje` TEXT NOT NULL,
  `leida` TINYINT NULL DEFAULT '0',
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_lectura` TIMESTAMP NULL DEFAULT NULL,
  `id_reserva` INT NULL DEFAULT NULL COMMENT 'Reserva relacionada si aplica',
  `id_mantenimiento` INT NULL DEFAULT NULL COMMENT 'Mantenimiento relacionado si aplica',
  PRIMARY KEY (`id_notificacion`),
  INDEX `fk_notificaciones_usuario_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_notificaciones_reserva_idx` (`id_reserva` ASC) VISIBLE,
  INDEX `fk_notificaciones_mantenimiento_idx` (`id_mantenimiento` ASC) VISIBLE,
  INDEX `idx_notificaciones_leida` (`leida` ASC) VISIBLE,
  INDEX `idx_notificaciones_fecha` (`fecha_creacion` ASC) VISIBLE,
  CONSTRAINT `fk_notificaciones_mantenimiento`
    FOREIGN KEY (`id_mantenimiento`)
    REFERENCES `gtics`.`mantenimiento` (`id_mantenimiento`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_notificaciones_reserva`
    FOREIGN KEY (`id_reserva`)
    REFERENCES `gtics`.`reservas` (`id_reservas`)
    ON DELETE SET NULL,
  CONSTRAINT `fk_notificaciones_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`passwdreset`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`passwdreset` (
  `id_usuario` INT NOT NULL,
  `token` VARCHAR(255) NOT NULL,
  `expiracion` DATETIME(6) NULL DEFAULT NULL,
  PRIMARY KEY (`token`),
  INDEX `fk_passwdreset_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_passwdreset_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`))
ENGINE = InnoDB
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
AUTO_INCREMENT = 2
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
-- Table `gtics`.`gimnasios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`gimnasios` (
  `id_espacio` INT NOT NULL,
  `cantidad_maquinas` INT NOT NULL,
  `tipos_maquinas` TEXT NOT NULL,
  `tiene_sauna` TINYINT NOT NULL,
  `tiene_duchas` TINYINT NOT NULL,
  `costo_semanal` DECIMAL(10,2) NOT NULL,
  `costo_mensual` DECIMAL(10,2) NOT NULL,
  `costo_anual` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id_espacio`),
  CONSTRAINT `fk_gimnasios_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`suscripciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`suscripciones` (
  `id_suscripciones` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `id_espacio` INT NOT NULL,
  `fecha_inicio` DATE NOT NULL,
  `fecha_fin` DATE NOT NULL,
  `tipo_suscripcion` ENUM('SEMANAL', 'MENSUAL', 'ANUAL') NOT NULL,
  `estado` TINYINT NOT NULL,
  `fecha_registro` DATETIME NOT NULL,
  `id_pagos` INT NOT NULL,
  PRIMARY KEY (`id_suscripciones`),
  INDEX `fk_suscripciones_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_suscripciones_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_suscripciones_pagos1_idx` (`id_pagos` ASC) VISIBLE,
  CONSTRAINT `fk_suscripciones_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_suscripciones_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_suscripciones_pagos1`
    FOREIGN KEY (`id_pagos`)
    REFERENCES `gtics`.`pagos` (`id_pagos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
