-- ============================================================
-- DENTAL CLINIC - BASE CONFIGURATION
-- Database: dental_clinic
-- MySQL 8+
-- ============================================================

USE dental_clinic;

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Roles required by authentication and authorization.
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administración completa de la plataforma'),
    ('SECRETARIO', 'Gestión de pacientes y agenda'),
    ('ESPECIALISTA', 'Consulta de agenda e historias clínicas'),
    ('PACIENTE', 'Acceso a sus propios datos y citas'),
    ('AUXILIAR', 'Apoyo en recepción y esterilización');

-- Permissions required by the role management feature.
INSERT INTO permissions (code, description) VALUES
    ('CITAS_GESTIONAR', 'Crear, editar, confirmar y cancelar citas'),
    ('PACIENTES_VER', 'Consultar pacientes'),
    ('PACIENTES_GESTIONAR', 'Crear y actualizar pacientes'),
    ('ESPECIALISTAS_GESTIONAR', 'Gestionar especialistas y servicios'),
    ('HISTORIAS_CLINICAS_VER', 'Consultar historias clínicas');

-- Base role-permission assignments.
INSERT INTO role_permissions (id_role, id_permission) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 5),
    (4, 1),
    (5, 2);

-- The backend creates the only initial user:
-- admin@admin.com / 12345678
-- Operational tables intentionally start empty.
