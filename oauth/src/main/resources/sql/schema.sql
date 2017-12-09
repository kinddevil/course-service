DROP TABLE IF EXISTS user_authority;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS oauth_access_token;
DROP TABLE IF EXISTS oauth_refresh_token;
DROP TABLE IF EXISTS oauth_client_details;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS user_role;


CREATE TABLE IF NOT EXISTS role (
  `rolename` TEXT NOT NULL,
  `full_name` TEXT NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now());

CREATE TABLE IF NOT EXISTS user_role (
  `username` TEXT NOT NULL,
  `rolename` TEXT NOT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now());

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `username` VARCHAR(45) NOT NULL COMMENT 'id',
  `students_id` BIGINT NULL,
  `teacher_id` BIGINT NULL,
  `admin_id` BIGINT NULL,
  `name` TEXT NULL,
  `email` TEXT NULL,
  `password` TEXT NULL,
  `activated` TINYINT NULL DEFAULT 0,
  school_id BIGINT,
  school_name TEXT,
  type TEXT,
  `activationkey` VARCHAR(50) NULL DEFAULT NULL,
  `resetpasswordkey` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`username`))
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
  `name` VARCHAR(45) NOT NULL COMMENT 'id',
  `authority` TEXT NULL,
  `update_time` TIMESTAMP NULL DEFAULT now(),
  PRIMARY KEY (`name`))
;

