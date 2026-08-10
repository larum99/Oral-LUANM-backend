INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Administracion completa de la plataforma'),
    ('SECRETARIO', 'Gestion de pacientes y agenda'),
    ('ESPECIALISTA', 'Consulta de agenda e historias clinicas'),
    ('PACIENTE', 'Acceso a sus propios datos y citas')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);

INSERT INTO permissions (code, description)
VALUES
    ('CITAS_GESTIONAR', 'Crear, editar, confirmar y cancelar citas'),
    ('PACIENTES_VER', 'Consultar pacientes'),
    ('PACIENTES_GESTIONAR', 'Crear y actualizar pacientes'),
    ('ESPECIALISTAS_GESTIONAR', 'Gestionar especialistas y servicios'),
    ('HISTORIAS_CLINICAS_VER', 'Consultar historias clinicas')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);

INSERT IGNORE INTO role_permissions (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r
JOIN permissions p
WHERE r.name = 'ADMIN';

INSERT IGNORE INTO role_permissions (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r
JOIN permissions p ON p.code IN ('CITAS_GESTIONAR', 'PACIENTES_VER', 'PACIENTES_GESTIONAR')
WHERE r.name = 'SECRETARIO';

INSERT IGNORE INTO role_permissions (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r
JOIN permissions p ON p.code IN ('HISTORIAS_CLINICAS_VER')
WHERE r.name = 'ESPECIALISTA';

INSERT IGNORE INTO role_permissions (id_role, id_permission)
SELECT r.id_role, p.id_permission
FROM roles r
JOIN permissions p ON p.code IN ('CITAS_GESTIONAR')
WHERE r.name = 'PACIENTE';