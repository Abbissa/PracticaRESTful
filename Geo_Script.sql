-- MySQL Script generated by MySQL Workbench
-- dom 27 mar 2022 19:51:59 CEST
-- Model: New Model    Version: 1.0
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Usuario` (
  `username` VARCHAR(45) NOT NULL,
  `nombre` VARCHAR(45) NULL,
  `edad` INT NULL,
  `localidad` VARCHAR(45) NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `mydb`.`Tesoro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Tesoro` (
  `idTesoro` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `terreno` VARCHAR(45) NULL,
  `dificultad` VARCHAR(45) NULL,
  `latitud` decimal(8,5) NULL CHECK(Latitud>=-180&&Latitud<=180),
  `longitud` decimal(8,5) NULL CHECK(Longitud>=-180&&Longitud<=180),
  `pista` VARCHAR(140) NULL,
  `tamanyo` VARCHAR(45) NULL,
  `fecha_creado` DATE NULL,
	FOREIGN KEY (`username`)
    REFERENCES `mydb`.`Usuario` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (`idTesoro`))
ENGINE = InnoDB;



-- -----------------------------------------------------
-- Table `mydb`.`Tesoro_encontrado_por_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Tesoro_encontrado_por_usuario` (
  `Usuario_idUsuario` VARCHAR(45) NOT NULL,
  `Tesoro_idTesoro` INT NOT NULL,
  `Fecha_encontrado` DATE NOT NULL,
  PRIMARY KEY (`Usuario_idUsuario`, `Tesoro_idTesoro`),
  INDEX `fk_Usuario_has_Tesoro_Tesoro2_idx` (`Tesoro_idTesoro` ASC),
  INDEX `fk_Usuario_has_Tesoro_Usuario1_idx` (`Usuario_idUsuario` ASC),
  CONSTRAINT `fk_Usuario_has_Tesoro_Usuario1`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `mydb`.`Usuario` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Usuario_has_Tesoro_Tesoro2`
    FOREIGN KEY (`Tesoro_idTesoro`)
    REFERENCES `mydb`.`Tesoro` (`idTesoro`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Usuario_amigo_de_Usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`Usuario_amigo_de_Usuario` (
  `Usuario_idUsuario` VARCHAR(45) NOT NULL,
  `Usuario_idUsuario1` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`Usuario_idUsuario`, `Usuario_idUsuario1`),
  INDEX `fk_Usuario_has_Usuario_Usuario2_idx` (`Usuario_idUsuario1` ASC),
  INDEX `fk_Usuario_has_Usuario_Usuario1_idx` (`Usuario_idUsuario` ASC),
  CONSTRAINT `fk_Usuario_has_Usuario_Usuario1`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `mydb`.`Usuario` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Usuario_has_Usuario_Usuario2`
    FOREIGN KEY (`Usuario_idUsuario1`)
    REFERENCES `mydb`.`Usuario` (`username`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- Creacion de usuarios

INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("marcelo14","Marcelo",23,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("antonio77","Antonio",27,"Salamanca");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("terco","Alberto",36,"Teruel");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("paulina","Paula",51,"Murcia");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("martinaSA","Martina",83,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("oscari123","Oscar",11,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("tajo","Paco",19,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("marialms","Maria",51,"Canarias");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("patrck","Patrick",23,"A Coru??a");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("carlos13","Carlos",54,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("master902","Nuria",43,"Guadalajara");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("treasure_finder77","Bruno",53,"Caceres");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("helen_fdz","Elena",45,"Leon");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("tercoscu","Teresa",27,"Madrid");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("rouz","Rocio",12,"Sevilla");
INSERT INTO mydb.Usuario(`username`,`nombre`,`edad` ,`localidad`) VALUES ("Althor","Rand",19,"Torrejon");

-- Creacion de tesoros

INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`,`fecha`) VALUES ("marcelo14","agua","normal",74.321,95.3265,"cueva","grande","2010-06-13");
INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`,`fecha`) VALUES ("tajo","llano","normal",32.321,45.3265,"roca","pequenyo","2020-08-20");
INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`,`fecha`) VALUES ("patrick","llano","dificil",15.321,-130.3265,"agujero","medio","2022-11-30");
INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`,`fecha`) VALUES ("terco","llano","dificil",142.321,-90.3265,"arbol","pequenyo","2013-03-01");
INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`,`fecha`) VALUES ("Althor","llano","normal",87.321,-65.3265,"edificio","grande","2013-01-14");


-- Creacion de tesoros encontrados








SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
