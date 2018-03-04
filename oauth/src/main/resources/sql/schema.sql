DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS oauth_access_token;
DROP TABLE IF EXISTS oauth_refresh_token;
DROP TABLE IF EXISTS oauth_client_details;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_role;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cas
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cas
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cas` DEFAULT CHARACTER SET utf8 ;
USE `cas` ;

-- -----------------------------------------------------
-- Table `cas`.`students`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`students` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`teacher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`teacher` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`user` (
  `username` VARCHAR(512) NOT NULL COMMENT 'id',
  `students_id` BIGINT NULL,
  `teacher_id` BIGINT NULL,
  `admin_id` BIGINT NULL,
  `name` TEXT NULL,
  `email` TEXT NULL,
  `password` TEXT NULL,
  `activated` TINYINT NULL DEFAULT 0,
  `activationkey` VARCHAR(50) NULL DEFAULT NULL,
  `resetpasswordkey` VARCHAR(50) NULL DEFAULT NULL,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  `type` VARCHAR(45) NULL,
  `pass_version` VARCHAR(45) NULL DEFAULT 'bc',
  `phone` VARCHAR(128) NULL,
  `last_load` DATETIME NULL,
  `modify_time` DATETIME NULL,
  `is_lock` TINYINT NULL,
  `load_counter` INT NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `is_deleted` TINYINT NULL,
  PRIMARY KEY (`username`),
  INDEX `fk_users_students1_idx` (`students_id` ASC),
  INDEX `fk_users_teacher1_idx` (`teacher_id` ASC),
  INDEX `fk_users_admin1_idx` (`admin_id` ASC),
  UNIQUE INDEX `admin_id_UNIQUE` (`admin_id` ASC),
  CONSTRAINT `fk_users_students1`
    FOREIGN KEY (`students_id`)
    REFERENCES `cas`.`students` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_teacher1`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `cas`.`teacher` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_admin1`
    FOREIGN KEY (`admin_id`)
    REFERENCES `cas`.`admin` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`advertising`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`advertising` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `img_ids` VARCHAR(1024) NULL,
  `img_names` VARCHAR(1024) NULL,
  `img_links` VARCHAR(1024) NULL,
  `school_ids` TEXT NULL,
  `school_names` TEXT NULL,
  `province` VARCHAR(45) NULL,
  `city` VARCHAR(45) NULL,
  `enable_type` INT NULL,
  `start_time` DATETIME NULL,
  `expire_time` DATETIME NULL,
  `display_pages` INT NULL COMMENT 'anonymous, school, teacher, student',
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `title` VARCHAR(512) NULL,
  `deleted` TINYINT NULL DEFAULT 0 COMMENT 'is deleted',
  `pending` INT NULL COMMENT 'is lock or not',
  `click` BIGINT NULL COMMENT 'click count',
  `view` BIGINT NULL COMMENT 'view count',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`timestamps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`timestamps` (
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL);


-- -----------------------------------------------------
-- Table `cas`.`timestamps_1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`timestamps_1` (
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL);


-- -----------------------------------------------------
-- Table `cas`.`schools`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`schools` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `discription` TEXT NULL,
  `status` VARCHAR(45) NULL,
  `deleted` TINYINT NULL DEFAULT 0,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `access_key` VARCHAR(1024) NULL COMMENT 'access_chain for privilege management',
  `contact` VARCHAR(255) NULL,
  `phone` VARCHAR(64) NULL,
  `province` VARCHAR(255) NULL,
  `city` VARCHAR(255) NULL,
  `county` VARCHAR(255) NULL,
  `province_code` VARCHAR(64) NULL,
  `city_code` VARCHAR(64) NULL,
  `county_code` VARCHAR(64) NULL,
  `address` VARCHAR(1024) NULL,
  `fax` VARCHAR(128) NULL,
  `email` VARCHAR(255) NULL,
  `web` VARCHAR(1024) NULL,
  `post` VARCHAR(64) NULL,
  `start_time` DATETIME NULL,
  `expire_time` DATETIME NULL,
  `is_payment` TINYINT NULL DEFAULT 0,
  `teacher_no` BIGINT NULL,
  `student_no` BIGINT NULL,
  `contract_id` VARCHAR(128) NULL,
  `contract` VARCHAR(1024) NULL,
  `is_lock` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`campus`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`campus` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`, `schools_id`),
  INDEX `fk_campus_schools1_idx` (`schools_id` ASC),
  CONSTRAINT `fk_campus_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `cas`.`schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`class_rooms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`class_rooms` (
  `schools_id` BIGINT NOT NULL,
  `campus_id` BIGINT NOT NULL,
  `campus_schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`schools_id`),
  INDEX `fk_class_rooms_campus1_idx` (`campus_id` ASC, `campus_schools_id` ASC),
  CONSTRAINT `fk_class_rooms_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `cas`.`schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_class_rooms_campus1`
    FOREIGN KEY (`campus_id` , `campus_schools_id`)
    REFERENCES `cas`.`campus` (`id` , `schools_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`announcement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`user_authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`user_authority` (
  `username` VARCHAR(45) NOT NULL,
  `authority` VARCHAR(45) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now())
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`dict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` VARCHAR(1024) NULL,
  `key` VARCHAR(1024) NULL,
  `school_id` BIGINT NULL,
  `desc` VARCHAR(2048) NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `is_deleted` TINYINT NULL,
  `type` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`, `create_time`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`todos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`todos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`school_ calendar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`school_ calendar` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`payment_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`payment_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`arrange_rules`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`arrange_rules` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`courses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`courses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `schools_id` BIGINT NOT NULL,
  `campus_id` BIGINT NOT NULL,
  `campus_schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`, `schools_id`, `campus_id`, `campus_schools_id`),
  INDEX `fk_courses_schools1_idx` (`schools_id` ASC),
  INDEX `fk_courses_campus1_idx` (`campus_id` ASC, `campus_schools_id` ASC),
  CONSTRAINT `fk_courses_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `cas`.`schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_courses_campus1`
    FOREIGN KEY (`campus_id` , `campus_schools_id`)
    REFERENCES `cas`.`campus` (`id` , `schools_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`payment_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`payment_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`course_arrangement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`course_arrangement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`users_has_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`users_has_roles` (
  `users_id` BIGINT NOT NULL,
  `roles_id` BIGINT NOT NULL,
  PRIMARY KEY (`users_id`, `roles_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`teacher_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`teacher_groups` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `img` TEXT NULL,
  `link` TEXT NULL,
  `school_id` BIGINT NULL COMMENT 'refer school id',
  `school_name` VARCHAR(256) NULL,
  `province` VARCHAR(45) NULL,
  `city` VARCHAR(45) NULL,
  `enable_type` INT NULL,
  `start_time` DATETIME NULL,
  `expire_time` DATETIME NULL,
  `display_pages` INT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `title` VARCHAR(512) NULL,
  `delete` INT NULL,
  `pending` INT NULL,
  `click` BIGINT NULL,
  `view` BIGINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`student_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`student_groups` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `img` TEXT NULL,
  `link` TEXT NULL,
  `school_id` BIGINT NULL COMMENT 'refer school id',
  `school_name` VARCHAR(256) NULL,
  `province` VARCHAR(45) NULL,
  `city` VARCHAR(45) NULL,
  `enable_type` INT NULL,
  `start_time` DATETIME NULL,
  `expire_time` DATETIME NULL,
  `display_pages` INT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT now(),
  `update_time` TIMESTAMP NULL,
  `title` VARCHAR(512) NULL,
  `delete` INT NULL,
  `pending` INT NULL,
  `click` BIGINT NULL,
  `view` BIGINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`schools_has_advertising`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`schools_has_advertising` (
  `schools_id` BIGINT NOT NULL,
  `advertising_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`schools_id`, `advertising_id`),
  INDEX `fk_schools_has_advertising_advertising1_idx` (`advertising_id` ASC),
  INDEX `fk_schools_has_advertising_schools1_idx` (`schools_id` ASC),
  CONSTRAINT `fk_schools_has_advertising_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `cas`.`schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schools_has_advertising_advertising1`
    FOREIGN KEY (`advertising_id`)
    REFERENCES `cas`.`advertising` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`user_audit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`user_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `login_time` TIMESTAMP NULL,
  `logout_time` TIMESTAMP NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`ads_files`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`ads_files` (
  `id` VARCHAR(512) NOT NULL,
  `advertising_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(1024) NULL,
  `location` VARCHAR(45) NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `fk_ads_files_advertising1_idx` (`advertising_id` ASC),
  CONSTRAINT `fk_ads_files_advertising1`
    FOREIGN KEY (`advertising_id`)
    REFERENCES `cas`.`advertising` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`sys_dict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`sys_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` TEXT NULL,
  `key` TEXT NULL,
  `school_id` BIGINT NULL,
  `desc` TEXT NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `is_deleted` INT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`oauth_access_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`oauth_access_token` (
  `token_id` VARCHAR(256) NOT NULL,
  `token` BLOB NULL,
  `authentication_id` VARCHAR(256) NULL,
  `user_name` TEXT NULL,
  `client_id` VARCHAR(256) NULL,
  `authentication` BLOB NULL,
  `refresh_token` VARCHAR(256) NULL,
  PRIMARY KEY (`token_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`oauth_refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`oauth_refresh_token` (
  `token_id` VARCHAR(256) NOT NULL,
  `token` BLOB NULL,
  `authentication` BLOB NULL,
  PRIMARY KEY (`token_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`oauth_client_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`oauth_client_details` (
  `client_id` VARCHAR(256) NOT NULL,
  `resource_ids` VARCHAR(256) NULL,
  `client_secret` VARCHAR(256) NULL,
  `scope` TEXT NULL,
  `authorized_grant_types` VARCHAR(256) NULL,
  `web_server_redirect_uri` TEXT NULL,
  `authorities` VARCHAR(256) NULL,
  `access_token_validity` INT NULL,
  `refresh_token_validity` INT NULL,
  `additional_information` VARCHAR(4096) NULL,
  `autoapprove` VARCHAR(256) NULL,
  PRIMARY KEY (`client_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`authority` (
  `name` VARCHAR(512) NOT NULL COMMENT 'id',
  `authority` VARCHAR(512) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`authority`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`role` (
  `rolename` VARCHAR(512) NOT NULL,
  `full_name` TEXT NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`rolename`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`user_role` (
  `username` VARCHAR(512) NOT NULL,
  `rolename` VARCHAR(512) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now())
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`assets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`assets` (
  `id` VARCHAR(512) NOT NULL,
  `filename` VARCHAR(1024) NULL,
  `region` VARCHAR(128) NULL,
  `region_url` VARCHAR(45) NULL,
  `size` INT NULL,
  `path` VARCHAR(1024) NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `description` VARCHAR(1024) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`privilege` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1024) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `create_time` TIMESTAMP NULL,
  `permission_ids` VARCHAR(2018) NULL,
  `type` VARCHAR(64) NULL,
  `school_id` BIGINT NULL,
  `description` TEXT NULL,
  `is_deleted` TINYINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cas`.`user_privilege`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`user_privilege` (
  `username` VARCHAR(768) NOT NULL,
  `privilegeid` BIGINT NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `create_time` TIMESTAMP NULL,
  PRIMARY KEY (`username`, `privilegeid`))
ENGINE = InnoDB;

USE `cas` ;

-- -----------------------------------------------------
-- Placeholder table for view `cas`.`view1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`view1` (`id` INT);

-- -----------------------------------------------------
-- Placeholder table for view `cas`.`view2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cas`.`view2` (`id` INT);

-- -----------------------------------------------------
-- View `cas`.`view1`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cas`.`view1`;
USE `cas`;


-- -----------------------------------------------------
-- View `cas`.`view2`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cas`.`view2`;
USE `cas`;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
