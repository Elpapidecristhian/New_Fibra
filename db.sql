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
-- -----------------------------------------------------
-- Schema hr
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema hr
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `hr` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `gtics` ;

-- -----------------------------------------------------
-- Table `gtics`.`listafotos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`listafotos` (
  `idListaFotos` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idListaFotos`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`tipoespacio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`tipoespacio` (
  `idTipoEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombreTipo` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idTipoEspacio`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`espaciosdeportivos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`espaciosdeportivos` (
  `idEspacio` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `ubicacion` VARCHAR(45) NOT NULL,
  `idTipoEspacio` INT NOT NULL,
  `idListaFotos` INT NOT NULL,
  `descripcion_corta` VARCHAR(255) NOT NULL,
  `descripcion_larga` TEXT NOT NULL,
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
CREATE TABLE IF NOT EXISTS `gtics`.`roles` (
  `idRol` INT NOT NULL,
  `rolNombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idRol`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`usuario`
-- -----------------------------------------------------
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
CREATE TABLE IF NOT EXISTS `gtics`.`mediospago` (
  `idMediosPago` INT NOT NULL AUTO_INCREMENT,
  `medioPago` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idMediosPago`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `gtics`.`pagos`
-- -----------------------------------------------------
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


-- -----------------------------------------------------
-- Table `gtics`.`canchasfutbol`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`canchasfutbol` (
  `idEspacio` INT NOT NULL,
  `tipoSuperficie` ENUM('Grass', 'Loza') NOT NULL,
  `iluminacionNocturna` TINYINT NOT NULL,
  `balonesDisponibles` TINYINT NOT NULL,
  PRIMARY KEY (`idEspacio`),
  INDEX `fk_canchasfutbol_espaciosdeportivos1_idx` (`idEspacio` ASC) VISIBLE,
  CONSTRAINT `fk_canchasfutbol_espaciosdeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`piscinas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`piscinas` (
  `idEspacio` INT NOT NULL,
  `tipoPiscina` ENUM('Olimpica', 'Publica') NOT NULL,
  `profundidadMin` FLOAT NOT NULL,
  `profundidadMax` FLOAT NOT NULL,
  `isClimatizada` TINYINT NOT NULL,
  `requisitos` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idEspacio`),
  CONSTRAINT `fk_piscinas_espaciosdeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`pistasatletismo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`pistasatletismo` (
  `idEspacio` INT NOT NULL,
  `tipoSuperficie` ENUM('Tartan', 'Asfalto', 'Tierra') NOT NULL,
  `longitud` FLOAT NOT NULL,
  `implementos` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idEspacio`),
  CONSTRAINT `fk_pistasatletismo_espaciosdeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `gtics`.`estadios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `gtics`.`estadios` (
  `idEspacio` INT NOT NULL,
  `aforo` INT NOT NULL,
  `usoPermitido` VARCHAR(45) NOT NULL,
  `seguridadDisponible` TINYINT NOT NULL,
  `sonidoPantallasDisponible` TINYINT NOT NULL,
  `iluminacionProfesionalDisponible` TINYINT NOT NULL,
  PRIMARY KEY (`idEspacio`),
  CONSTRAINT `fk_estadios_espaciosdeportivos1`
    FOREIGN KEY (`idEspacio`)
    REFERENCES `gtics`.`espaciosdeportivos` (`idEspacio`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `hr` ;

-- -----------------------------------------------------
-- Table `hr`.`regions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`regions` (
  `region_id` DECIMAL(22,0) NOT NULL,
  `region_name` VARCHAR(25) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  PRIMARY KEY (`region_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`countries`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`countries` (
  `country_id` CHAR(2) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `country_name` VARCHAR(40) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `region_id` DECIMAL(22,0) NULL DEFAULT NULL,
  PRIMARY KEY (`country_id`),
  INDEX `countr_reg_fk` (`region_id` ASC) VISIBLE,
  CONSTRAINT `countr_reg_fk`
    FOREIGN KEY (`region_id`)
    REFERENCES `hr`.`regions` (`region_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`locations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`locations` (
  `location_id` INT NOT NULL,
  `street_address` VARCHAR(40) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `postal_code` VARCHAR(12) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `city` VARCHAR(30) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `state_province` VARCHAR(25) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `country_id` CHAR(2) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  PRIMARY KEY (`location_id`),
  INDEX `loc_city_ix` (`city` ASC) VISIBLE,
  INDEX `loc_country_ix` (`country_id` ASC) VISIBLE,
  INDEX `loc_state_province_ix` (`state_province` ASC) VISIBLE,
  INDEX `loc_c_id_fk` (`country_id` ASC) VISIBLE,
  CONSTRAINT `loc_c_id_fk`
    FOREIGN KEY (`country_id`)
    REFERENCES `hr`.`countries` (`country_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`jobs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`jobs` (
  `job_id` VARCHAR(10) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `job_title` VARCHAR(35) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `min_salary` INT NULL DEFAULT NULL,
  `max_salary` INT NULL DEFAULT NULL,
  PRIMARY KEY (`job_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`employees`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`employees` (
  `employee_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(20) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `last_name` VARCHAR(25) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `email` VARCHAR(25) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `phone_number` VARCHAR(20) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NULL DEFAULT NULL,
  `hire_date` DATETIME NOT NULL,
  `job_id` VARCHAR(10) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `salary` DECIMAL(8,2) NULL DEFAULT NULL,
  `commission_pct` DECIMAL(2,2) NULL DEFAULT NULL,
  `manager_id` INT NULL DEFAULT NULL,
  `department_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE INDEX `emp_email_uk` (`email` ASC) VISIBLE,
  INDEX `emp_department_ix` (`department_id` ASC) VISIBLE,
  INDEX `emp_job_ix` (`job_id` ASC) VISIBLE,
  INDEX `emp_manager_ix` (`manager_id` ASC) VISIBLE,
  INDEX `emp_name_ix` (`last_name` ASC, `first_name` ASC) VISIBLE,
  INDEX `emp_job_fk` (`job_id` ASC) VISIBLE,
  CONSTRAINT `emp_dept_fk`
    FOREIGN KEY (`department_id`)
    REFERENCES `hr`.`departments` (`department_id`),
  CONSTRAINT `emp_job_fk`
    FOREIGN KEY (`job_id`)
    REFERENCES `hr`.`jobs` (`job_id`),
  CONSTRAINT `emp_manager_fk`
    FOREIGN KEY (`manager_id`)
    REFERENCES `hr`.`employees` (`employee_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 211
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`departments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`departments` (
  `department_id` INT NOT NULL,
  `department_name` VARCHAR(30) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `manager_id` INT NULL DEFAULT NULL,
  `location_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`department_id`),
  INDEX `dept_location_ix` (`location_id` ASC) VISIBLE,
  INDEX `dept_mgr_fk` (`manager_id` ASC) VISIBLE,
  CONSTRAINT `dept_loc_fk`
    FOREIGN KEY (`location_id`)
    REFERENCES `hr`.`locations` (`location_id`),
  CONSTRAINT `dept_mgr_fk`
    FOREIGN KEY (`manager_id`)
    REFERENCES `hr`.`employees` (`employee_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `hr`.`job_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hr`.`job_history` (
  `employee_id` INT NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `job_id` VARCHAR(10) CHARACTER SET 'latin1' COLLATE 'latin1_bin' NOT NULL,
  `department_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`employee_id`, `start_date`),
  INDEX `jhist_department_ix` (`department_id` ASC) VISIBLE,
  INDEX `jhist_employee_ix` (`employee_id` ASC) VISIBLE,
  INDEX `jhist_job_ix` (`job_id` ASC) VISIBLE,
  INDEX `jhist_job_fk` (`job_id` ASC) VISIBLE,
  CONSTRAINT `jhist_dept_fk`
    FOREIGN KEY (`department_id`)
    REFERENCES `hr`.`departments` (`department_id`),
  CONSTRAINT `jhist_emp_fk`
    FOREIGN KEY (`employee_id`)
    REFERENCES `hr`.`employees` (`employee_id`),
  CONSTRAINT `jhist_job_fk`
    FOREIGN KEY (`job_id`)
    REFERENCES `hr`.`jobs` (`job_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
