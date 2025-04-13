-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema GTICS
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema GTICS
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `GTICS` DEFAULT CHARACTER SET utf8mb3 ;
-- -----------------------------------------------------
-- Schema GTICS
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema GTICS
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `GTICS` DEFAULT CHARACTER SET utf8mb3 ;
USE `GTICS` ;
USE `GTICS` ;

-- -----------------------------------------------------
-- Table `GTICS`.`TipoEspacio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`TipoEspacio` (
  `idTipoEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombreTipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTipoEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`ListaFotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`ListaFotos` (
  `idListaFotos` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idListaFotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`EspaciosDeportivos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`EspaciosDeportivos` (
  `idEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `ubicacion` VARCHAR(45) NOT NULL,
  `idTipoEspacio` INT NOT NULL,
  `idListaFotos` INT NOT NULL,
  PRIMARY KEY (`idEspacio`, `idTipoEspacio`),
  INDEX `fk_EspaciosDeportivos_TipoEspacio1_idx` (`idTipoEspacio` ASC) VISIBLE,
  INDEX `fk_EspaciosDeportivos_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_EspaciosDeportivos_TipoEspacio1`
    FOREIGN KEY (`idTipoEspacio`)
    REFERENCES `GTICS`.`TipoEspacio` (`idTipoEspacio`),
  CONSTRAINT `fk_EspaciosDeportivos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `GTICS`.`ListaFotos` (`idListaFotos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Roles` (
  `idRol` INT NOT NULL,
  `rolNombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `nombres` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(90) NULL,
  `dni` INT NOT NULL,
  `numCelular` INT NULL DEFAULT NULL,
  `idRol` INT NOT NULL,
  `fotoPerfil` BLOB NULL,
  `isBaneado` TINYINT NOT NULL,
  PRIMARY KEY (`idUsuario`, `idRol`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `fk_Usuario_Roles_idx` (`idRol` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_Roles`
    FOREIGN KEY (`idRol`)
    REFERENCES `GTICS`.`Roles` (`idRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Asistencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Asistencia` (
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
    REFERENCES `GTICS`.`EspaciosDeportivos` (`idEspacio`),
  CONSTRAINT `fk_Asistencia_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `GTICS`.`Usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Fotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Fotos` (
  `idFotos` INT NOT NULL,
  `foto` BLOB NOT NULL,
  `idListaFotos` INT NOT NULL,
  PRIMARY KEY (`idFotos`),
  INDEX `fk_Fotos_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_Fotos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `GTICS`.`ListaFotos` (`idListaFotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Horarios` (
  `idHorarios` INT NOT NULL,
  `horaInicio` TIME NOT NULL,
  `horaFin` TIME NOT NULL,
  `idEspacio` INT NOT NULL,
  PRIMARY KEY (`idHorarios`),
  INDEX `fk_Horarios_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_Horarios_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `GTICS`.`EspaciosDeportivos` (`idEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`MediosPago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`MediosPago` (
  `idMediosPago` INT NOT NULL AUTO_INCREMENT,
  `medioPago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMediosPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Pagos` (
  `idPagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` FLOAT NOT NULL,
  `idMediosPago` INT NOT NULL,
  PRIMARY KEY (`idPagos`, `idMediosPago`),
  INDEX `fk_Pagos_MediosPago1_idx` (`idMediosPago` ASC) VISIBLE,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`idMediosPago`)
    REFERENCES `GTICS`.`MediosPago` (`idMediosPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`Reservas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`Reservas` (
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
    REFERENCES `GTICS`.`EspaciosDeportivos` (`idEspacio`),
  CONSTRAINT `fk_Reservas_Pagos1`
    FOREIGN KEY (`Pagos_idPagos`)
    REFERENCES `GTICS`.`Pagos` (`idPagos`),
  CONSTRAINT `fk_Reservas_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `GTICS`.`Usuario` (`idUsuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `GTICS`.`HorarioReservado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `GTICS`.`HorarioReservado` (
  `idHorarioReservado` INT NOT NULL,
  `fecha` DATE NOT NULL,
  `idReservas` INT NOT NULL,
  `idHorarios` INT NOT NULL,
  PRIMARY KEY (`idHorarioReservado`, `idReservas`),
  INDEX `fk_HorarioReservado_Reservas1_idx` (`idReservas` ASC) VISIBLE,
  INDEX `fk_HorarioReservado_Horarios1_idx` (`idHorarios` ASC) VISIBLE,
  CONSTRAINT `fk_HorarioReservado_Reservas1`
    FOREIGN KEY (`idReservas`)
    REFERENCES `GTICS`.`Reservas` (`idReservas`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_HorarioReservado_Horarios1`
    FOREIGN KEY (`idHorarios`)
    REFERENCES `GTICS`.`Horarios` (`idHorarios`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
