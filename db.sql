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
-- Table `gtics`.`listafotos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`listafotos` ;

CREATE TABLE IF NOT EXISTS `gtics`.`listafotos` (
  `idListaFotos` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idListaFotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`tipoespacio`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`tipoespacio` ;

CREATE TABLE IF NOT EXISTS `gtics`.`tipoespacio` (
  `idTipoEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombreTipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTipoEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`espaciosdeportivos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`espaciosdeportivos` ;

CREATE TABLE IF NOT EXISTS `gtics`.`espaciosdeportivos` (
  `idEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `ubicacion` VARCHAR(45) NOT NULL,
  `idTipoEspacio` INT NOT NULL,
  `idListaFotos` INT NOT NULL,
  `descripcion_corta` VARCHAR(255) NULL DEFAULT NULL,
  `descripcion_larga` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idEspacio`, `idTipoEspacio`),
  INDEX `fk_EspaciosDeportivos_TipoEspacio1_idx` (`idTipoEspacio` ASC) VISIBLE,
  INDEX `fk_EspaciosDeportivos_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_EspaciosDeportivos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `gtics`.`listafotos` (`idListaFotos`),
  CONSTRAINT `fk_EspaciosDeportivos_TipoEspacio1`
    FOREIGN KEY (`idTipoEspacio`)
    REFERENCES `gtics`.`tipoespacio` (`idTipoEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`roles` ;

CREATE TABLE IF NOT EXISTS `gtics`.`roles` (
  `idRol` INT NOT NULL,
  `rolNombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`usuario`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`usuario` ;

CREATE TABLE IF NOT EXISTS `gtics`.`usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `nombres` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(90) NULL DEFAULT NULL,
  `dni` INT NOT NULL,
  `numCelular` INT NULL DEFAULT NULL,
  `idRol` INT NOT NULL,
  `fotoPerfil` BLOB NULL DEFAULT NULL,
  `isBaneado` TINYINT NOT NULL,
  `fechaNacimiento` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`idUsuario`, `idRol`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `fk_Usuario_Roles_idx` (`idRol` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_Roles`
    FOREIGN KEY (`idRol`)
    REFERENCES `gtics`.`roles` (`idRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`asistencia`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`asistencia` ;

CREATE TABLE IF NOT EXISTS `gtics`.`asistencia` (
  `idAsistencia` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  `idUsuario` INT NOT NULL,
  `idEspacio` INT NOT NULL,
  PRIMARY KEY (`idAsistencia`),
  INDEX `fk_Asistencia_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Asistencia_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_Asistencia_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`),
  CONSTRAINT `fk_Asistencia_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gtics`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`fotos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`fotos` ;

CREATE TABLE IF NOT EXISTS `gtics`.`fotos` (
  `idFotos` INT NOT NULL,
  `foto` BLOB NOT NULL,
  `idListaFotos` INT NOT NULL,
  PRIMARY KEY (`idFotos`),
  INDEX `fk_Fotos_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_Fotos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `gtics`.`listafotos` (`idListaFotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`horarios`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`horarios` ;

CREATE TABLE IF NOT EXISTS `gtics`.`horarios` (
  `idHorarios` INT NOT NULL,
  `horaInicio` TIME NOT NULL,
  `horaFin` TIME NOT NULL,
  `idEspacio` INT NOT NULL,
  PRIMARY KEY (`idHorarios`),
  INDEX `fk_Horarios_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_Horarios_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`mediospago`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`mediospago` ;

CREATE TABLE IF NOT EXISTS `gtics`.`mediospago` (
  `idMediosPago` INT NOT NULL AUTO_INCREMENT,
  `medioPago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMediosPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pagos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`pagos` ;

CREATE TABLE IF NOT EXISTS `gtics`.`pagos` (
  `idPagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` FLOAT NOT NULL,
  `idMediosPago` INT NOT NULL,
  PRIMARY KEY (`idPagos`, `idMediosPago`),
  INDEX `fk_Pagos_MediosPago1_idx` (`idMediosPago` ASC) VISIBLE,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`idMediosPago`)
    REFERENCES `gtics`.`mediospago` (`idMediosPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`reservas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`reservas` ;

CREATE TABLE IF NOT EXISTS `gtics`.`reservas` (
  `idReservas` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idEspacio` INT NOT NULL,
  `Pagos_idPagos` INT NOT NULL,
  `registroTimestamp` TIMESTAMP NOT NULL,
  `fechaReserva` DATE NOT NULL,
  PRIMARY KEY (`idReservas`),
  INDEX `fk_Reservas_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Reservas_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  INDEX `fk_Reservas_Pagos1_idx` (`Pagos_idPagos` ASC) VISIBLE,
  CONSTRAINT `fk_Reservas_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`),
  CONSTRAINT `fk_Reservas_Pagos1`
    FOREIGN KEY (`Pagos_idPagos`)
    REFERENCES `gtics`.`pagos` (`idPagos`),
  CONSTRAINT `fk_Reservas_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `gtics`.`usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`horarioreservado`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `gtics`.`horarioreservado` ;

CREATE TABLE IF NOT EXISTS `gtics`.`horarioreservado` (
  `idHorarioReservado` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `idReservas` INT NOT NULL,
  `idHorarios` INT NOT NULL,
  PRIMARY KEY (`idHorarioReservado`, `idReservas`),
  INDEX `fk_HorarioReservado_Reservas1_idx` (`idReservas` ASC) VISIBLE,
  INDEX `fk_HorarioReservado_Horarios1_idx` (`idHorarios` ASC) VISIBLE,
  CONSTRAINT `fk_HorarioReservado_Horarios1`
    FOREIGN KEY (`idHorarios`)
    REFERENCES `gtics`.`horarios` (`idHorarios`),
  CONSTRAINT `fk_HorarioReservado_Reservas1`
    FOREIGN KEY (`idReservas`)
    REFERENCES `gtics`.`reservas` (`idReservas`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
