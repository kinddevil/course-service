-- StandardPasswordEncoder
-- INSERT INTO user (username,email, password, activated) VALUES ('admin', 'admin@mail.me', 'b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1', true);
-- INSERT INTO user (username,email, password, activated) VALUES ('user', 'user@mail.me', 'd6dfa9ff45e03b161e7f680f35d90d5ef51d243c2a8285aa7e11247bc2c92acde0c2bb626b1fac74', true);
-- INSERT INTO user (username,email, password, activated) VALUES ('rajith', 'rajith@abc.com', 'd6dfa9ff45e03b161e7f680f35d90d5ef51d243c2a8285aa7e11247bc2c92acde0c2bb626b1fac74', true);

-- BCryptPasswordEncoder
INSERT INTO user (username, name, type, email, password, activated) VALUES ('admin001', 'admin', 'admin', 'admin@mail.me', '$2a$10$qcbyEFYeVmHEYh78TZRHJe.FMSuBwUxmyAXZPEX5tOy6ayJmNSBnK', true);
INSERT INTO user (username, name, type, school_id, school_name, email,  password, activated) VALUES ('admin002', 'admin2', 'school', 1, 'mock_school', 'admin2@mail.me', '$2a$10$qcbyEFYeVmHEYh78TZRHJe.FMSuBwUxmyAXZPEX5tOy6ayJmNSBnK', true);
INSERT INTO user (username, name, type, school_id, school_name, email,  password, activated) VALUES ('teacher001', 'teacher', 'teacher', 1, 'mock_school', 'user@mail.me', '$2a$10$qcbyEFYeVmHEYh78TZRHJe.FMSuBwUxmyAXZPEX5tOy6ayJmNSBnK', true);
INSERT INTO user (username, name, type, school_id, school_name, email,  password, activated) VALUES ('student001', 'student', 'student', 1, 'mock_school', 'student@abc.com', '$2a$10$qcbyEFYeVmHEYh78TZRHJe.FMSuBwUxmyAXZPEX5tOy6ayJmNSBnK', true);

INSERT INTO role (rolename, full_name) VALUES ('superadmin', '系统管理员');
INSERT INTO role (rolename, full_name) VALUES ('admin', '学校管理员');
INSERT INTO role (rolename, full_name) VALUES ('teacher', 'teacher');
INSERT INTO role (rolename, full_name) VALUES ('student', 'student');

INSERT INTO user_role (username, rolename) VALUES ('admin001', 'superadmin');
INSERT INTO user_role (username, rolename) VALUES ('admin002', 'admin');
INSERT INTO user_role (username, rolename) VALUES ('teacher001', 'teacher');
INSERT INTO user_role (username, rolename) VALUES ('student001', 'student');

INSERT INTO authority (authority, name) VALUES ('ROLE_ADMIN', 'admin');
INSERT INTO authority (authority, name) VALUES ('ROLE_USER', 'user');

INSERT INTO user_authority (username,authority) VALUES ('student001', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('teacher001', 'ROLE_USER');
INSERT INTO user_authority (username,authority) VALUES ('admin001', 'ROLE_ADMIN');
INSERT INTO user_authority (username,authority) VALUES ('admin002', 'ROLE_ADMIN');

-- authority: admin user
-- scope: read,write,
-- client: need to confirm apis 1. admin 2. user with rw 3. user with r

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('fooClientIdPassword', 'secret', 'foo,read,write',
    'password,authorization_code,refresh_token', null, null, 36000, 36000, null, true);

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES
    ('base_course_app', 'secret', 'foo,read,write',
    'password,authorization_code,refresh_token', null, 'ROLE_ADMIN,ROLE_USER', 36000, 36000, null, true);