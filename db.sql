-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Roles` (
  `idRol` INT NOT NULL,
  `rolNombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`ListaFotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`ListaFotos` (
  `idListaFotos` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idListaFotos`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Usuario` (
  `idUsuario` INT NOT NULL AUTO_INCREMENT,
  `nombres` VARCHAR(45) NOT NULL,
  `apellidos` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(45) NOT NULL,
  `direccion` VARCHAR(90) NOT NULL,
  `dni` INT NOT NULL,
  `numCelular` INT NULL,
  `idRol` INT NOT NULL,
  `idListaFotos` INT NOT NULL,
  PRIMARY KEY (`idUsuario`, `idRol`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `fk_Usuario_Roles_idx` (`idRol` ASC) VISIBLE,
  INDEX `fk_Usuario_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_Usuario_Roles`
    FOREIGN KEY (`idRol`)
    REFERENCES `mydb`.`Roles` (`idRol`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Usuario_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `mydb`.`ListaFotos` (`idListaFotos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`TipoEspacio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`TipoEspacio` (
  `idTipoEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombreTipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTipoEspacio`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`EspaciosDeportivos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`EspaciosDeportivos` (
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
    REFERENCES `mydb`.`TipoEspacio` (`idTipoEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_EspaciosDeportivos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `mydb`.`ListaFotos` (`idListaFotos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`MediosPago`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`MediosPago` (
  `idMediosPago` INT NOT NULL AUTO_INCREMENT,
  `medioPago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMediosPago`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Pagos` (
  `idPagos` INT NOT NULL AUTO_INCREMENT,
  `cantidad` FLOAT NOT NULL,
  `idMediosPago` INT NOT NULL,
  `Pagoscol` VARCHAR(45) NULL,
  PRIMARY KEY (`idPagos`, `idMediosPago`),
  INDEX `fk_Pagos_MediosPago1_idx` (`idMediosPago` ASC) VISIBLE,
  CONSTRAINT `fk_Pagos_MediosPago1`
    FOREIGN KEY (`idMediosPago`)
    REFERENCES `mydb`.`MediosPago` (`idMediosPago`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Reservas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Reservas` (
  `idReservas` INT NOT NULL AUTO_INCREMENT,
  `idUsuario` INT NOT NULL,
  `idEspacio` INT NOT NULL,
  `Pagos_idPagos` INT NOT NULL,
  PRIMARY KEY (`idReservas`),
  INDEX `fk_Reservas_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Reservas_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  INDEX `fk_Reservas_Pagos1_idx` (`Pagos_idPagos` ASC) VISIBLE,
  CONSTRAINT `fk_Reservas_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reservas_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `mydb`.`EspaciosDeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Reservas_Pagos1`
    FOREIGN KEY (`Pagos_idPagos`)
    REFERENCES `mydb`.`Pagos` (`idPagos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Fotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Fotos` (
  `idFotos` INT NOT NULL,
  `foto` BLOB NOT NULL,
  `idListaFotos` INT NOT NULL,
  PRIMARY KEY (`idFotos`),
  INDEX `fk_Fotos_ListaFotos1_idx` (`idListaFotos` ASC) VISIBLE,
  CONSTRAINT `fk_Fotos_ListaFotos1`
    FOREIGN KEY (`idListaFotos`)
    REFERENCES `mydb`.`ListaFotos` (`idListaFotos`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Asistencia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Asistencia` (
  `idAsistencia` INT NOT NULL AUTO_INCREMENT,
  `fecha` DATE NOT NULL,
  `hora` TIME NOT NULL,
  `idUsuario` INT NOT NULL,
  `idEspacio` INT NOT NULL,
  `isBaneado` TINYINT NOT NULL,
  PRIMARY KEY (`idAsistencia`),
  INDEX `fk_Asistencia_Usuario1_idx` (`idUsuario` ASC) VISIBLE,
  INDEX `fk_Asistencia_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_Asistencia_Usuario1`
    FOREIGN KEY (`idUsuario`)
    REFERENCES `mydb`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Asistencia_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `mydb`.`EspaciosDeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Horarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Horarios` (
  `idHorarios` INT NOT NULL,
  `isDisponible` TINYINT NOT NULL,
  `horaInicio` TIME NOT NULL,
  `horaFin` TIME NOT NULL,
  `fecha` DATE NOT NULL,
  `idEspacio` INT NOT NULL,
  PRIMARY KEY (`idHorarios`),
  INDEX `fk_Horarios_EspaciosDeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_Horarios_EspaciosDeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `mydb`.`EspaciosDeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
