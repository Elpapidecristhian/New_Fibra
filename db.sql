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
  `hora_entrada` TIME NOT NULL,
  `hora_salida` TIME NOT NULL,
  `id_horarios_coordinador` INT NOT NULL,
  PRIMARY KEY (`id_asistencia`),
  INDEX `fk_asistencia_horarioscoordinador1_idx` (`id_horarios_coordinador` ASC) VISIBLE,
  CONSTRAINT `fk_asistencia_horarioscoordinador1`
    FOREIGN KEY (`id_horarios_coordinador`)
    REFERENCES `gtics`.`horarioscoordinador` (`id_horarios_coordinador`)
    ON DELETE NO ACTION
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
  PRIMARY KEY (`id_medios_pago`))
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`pagos` (
  `id_pagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` FLOAT NOT NULL,
  `id_medios_pago` INT NOT NULL,
  PRIMARY KEY (`id_pagos`, `id_medios_pago`),
  INDEX `fk_Pagos_MediosPago1_idx` (`id_medios_pago` ASC) VISIBLE,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`id_medios_pago`)
    REFERENCES `gtics`.`mediospago` (`id_medios_pago`))
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
  PRIMARY KEY (`id_reservas`),
  INDEX `fk_Reservas_Usuario1_idx` (`id_usuario` ASC) VISIBLE,
  INDEX `fk_Reservas_EspaciosDeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_Reservas_Pagos1_idx` (`id_pagos` ASC) VISIBLE,
  INDEX `fk_reservas_horarios1_idx` (`id_horarios` ASC) VISIBLE,
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
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`tipocomentario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`tipocomentario` (
  `id_tipo_comentario` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_tipo_comentario`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`comentarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`comentarios` (
  `id_comentarios` INT NOT NULL AUTO_INCREMENT,
  `id_espacio` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `id_tipo_comentario` INT NOT NULL,
  `contenido` VARCHAR(300) NOT NULL,
  PRIMARY KEY (`id_comentarios`),
  INDEX `fk_comentarios_espaciosdeportivos1_idx` (`id_espacio` ASC) VISIBLE,
  INDEX `fk_comentarios_tipocomentario1_idx` (`id_tipo_comentario` ASC) VISIBLE,
  INDEX `fk_comentarios_usuario1_idx` (`id_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_comentarios_espaciosdeportivos1`
    FOREIGN KEY (`id_espacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`id_espacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_tipocomentario1`
    FOREIGN KEY (`id_tipo_comentario`)
    REFERENCES `gtics`.`tipocomentario` (`id_tipo_comentario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentarios_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `gtics`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
