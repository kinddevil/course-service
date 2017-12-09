DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS oauth_access_token;
DROP TABLE IF EXISTS oauth_refresh_token;
DROP TABLE IF EXISTS oauth_client_details;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_role;



-- -----------------------------------------------------
-- Table `students`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `students` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `teacher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `teacher` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `school_id` BIGINT NULL,
  `school_name` TEXT NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
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
  PRIMARY KEY (`username`),
  INDEX `fk_users_students1_idx` (`students_id` ASC),
  INDEX `fk_users_teacher1_idx` (`teacher_id` ASC),
  INDEX `fk_users_admin1_idx` (`admin_id` ASC),
  UNIQUE INDEX `admin_id_UNIQUE` (`admin_id` ASC),
  CONSTRAINT `fk_users_students1`
    FOREIGN KEY (`students_id`)
    REFERENCES `students` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_teacher1`
    FOREIGN KEY (`teacher_id`)
    REFERENCES `teacher` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_admin1`
    FOREIGN KEY (`admin_id`)
    REFERENCES `admin` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `advertising`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `advertising` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `img` TEXT NULL,
  `link` TEXT NULL,
  `school_ids` TEXT NULL,
  `school_names` TEXT NULL,
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
;


-- -----------------------------------------------------
-- Table `timestamps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `timestamps` (
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL);


-- -----------------------------------------------------
-- Table `timestamps_1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `timestamps_1` (
  `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL);


-- -----------------------------------------------------
-- Table `schools`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `schools` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `discription` TEXT NULL,
  `status` VARCHAR(45) NULL,
  `deleted` TINYINT NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL,
  `access_key` VARCHAR(1024) NULL COMMENT 'access_chain for privilege management',
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `campus`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `campus` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`, `schools_id`),
  INDEX `fk_campus_schools1_idx` (`schools_id` ASC),
  CONSTRAINT `fk_campus_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `class_rooms`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `class_rooms` (
  `schools_id` BIGINT NOT NULL,
  `campus_id` BIGINT NOT NULL,
  `campus_schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`schools_id`),
  INDEX `fk_class_rooms_campus1_idx` (`campus_id` ASC, `campus_schools_id` ASC),
  CONSTRAINT `fk_class_rooms_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_class_rooms_campus1`
    FOREIGN KEY (`campus_id` , `campus_schools_id`)
    REFERENCES `campus` (`id` , `schools_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `announcement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `user_authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_authority` (
  `username` VARCHAR(45) NOT NULL,
  `authority` VARCHAR(45) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now())
;


-- -----------------------------------------------------
-- Table `dict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` TEXT NULL,
  `key` TEXT NULL,
  `desc` TEXT NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `is_deleted` INT NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `todos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `todos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `school_ calendar`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `school_ calendar` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `payment_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `arrange_rules`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `arrange_rules` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `courses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `courses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `schools_id` BIGINT NOT NULL,
  `campus_id` BIGINT NOT NULL,
  `campus_schools_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`, `schools_id`, `campus_id`, `campus_schools_id`),
  INDEX `fk_courses_schools1_idx` (`schools_id` ASC),
  INDEX `fk_courses_campus1_idx` (`campus_id` ASC, `campus_schools_id` ASC),
  CONSTRAINT `fk_courses_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_courses_campus1`
    FOREIGN KEY (`campus_id` , `campus_schools_id`)
    REFERENCES `campus` (`id` , `schools_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `payment_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `course_arrangement`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `course_arrangement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `users_has_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users_has_roles` (
  `users_id` BIGINT NOT NULL,
  `roles_id` BIGINT NOT NULL,
  PRIMARY KEY (`users_id`, `roles_id`))
;


-- -----------------------------------------------------
-- Table `teacher_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `teacher_groups` (
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
;


-- -----------------------------------------------------
-- Table `student_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `student_groups` (
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
;


-- -----------------------------------------------------
-- Table `schools_has_advertising`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `schools_has_advertising` (
  `schools_id` BIGINT NOT NULL,
  `advertising_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`schools_id`, `advertising_id`),
  INDEX `fk_schools_has_advertising_advertising1_idx` (`advertising_id` ASC),
  INDEX `fk_schools_has_advertising_schools1_idx` (`schools_id` ASC),
  CONSTRAINT `fk_schools_has_advertising_schools1`
    FOREIGN KEY (`schools_id`)
    REFERENCES `schools` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schools_has_advertising_advertising1`
    FOREIGN KEY (`advertising_id`)
    REFERENCES `advertising` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `user_audit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `login_time` TIMESTAMP NULL,
  `logout_time` TIMESTAMP NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `ads_files`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ads_files` (
  `id` VARCHAR(512) NOT NULL,
  `advertising_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(45) NULL,
  `location` VARCHAR(45) NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`id`),
  INDEX `fk_ads_files_advertising1_idx` (`advertising_id` ASC),
  CONSTRAINT `fk_ads_files_advertising1`
    FOREIGN KEY (`advertising_id`)
    REFERENCES `advertising` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `sys_dict`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_dict` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `value` TEXT NULL,
  `key` TEXT NULL,
  `desc` TEXT NULL,
  `create_time` TIMESTAMP NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  `is_deleted` INT NULL,
  PRIMARY KEY (`id`))
;


-- -----------------------------------------------------
-- Table `oauth_access_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oauth_access_token` (
  `token_id` VARCHAR(256) NOT NULL,
  `token` BLOB NULL,
  `authentication_id` VARCHAR(256) NULL,
  `user_name` TEXT NULL,
  `client_id` VARCHAR(256) NULL,
  `authentication` BLOB NULL,
  `refresh_token` VARCHAR(256) NULL,
  PRIMARY KEY (`token_id`))
;


-- -----------------------------------------------------
-- Table `oauth_refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oauth_refresh_token` (
  `token_id` VARCHAR(256) NOT NULL,
  `token` BLOB NULL,
  `authentication` BLOB NULL,
  PRIMARY KEY (`token_id`))
;


-- -----------------------------------------------------
-- Table `oauth_client_details`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `oauth_client_details` (
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
;


-- -----------------------------------------------------
-- Table `authority`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `authority` (
  `name` VARCHAR(512) NOT NULL COMMENT 'id',
  `authority` VARCHAR(512) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`authority`))
;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role` (
  `rolename` VARCHAR(512) NOT NULL,
  `full_name` TEXT NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`rolename`))
;


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_role` (
  `username` VARCHAR(512) NOT NULL,
  `rolename` VARCHAR(512) NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now())
;