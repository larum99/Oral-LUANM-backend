-- ============================================================
-- DENTAL CLINIC - SAMPLE DATA
-- Database: dental_clinic
-- MySQL 8+
-- Development / Testing only
-- ============================================================

USE dental_clinic;


-- ============================================================
-- ROLES
-- ============================================================

INSERT INTO roles (
    name,
    description
) VALUES
    (
        'ADMIN',
        'Administración completa de la plataforma'
    ),
    (
        'SECRETARIO',
        'Gestión de pacientes y agenda'
    ),
    (
        'ESPECIALISTA',
        'Consulta de agenda e historias clínicas'
    ),
    (
        'PACIENTE',
        'Acceso a sus propios datos y citas'
    ),
    (
        'AUXILIAR',
        'Apoyo en recepción y esterilización'
    );


-- ============================================================
-- PERMISSIONS
-- ============================================================

INSERT INTO permissions (
    code,
    description
) VALUES
    (
        'CITAS_GESTIONAR',
        'Crear, editar, confirmar y cancelar citas'
    ),
    (
        'PACIENTES_VER',
        'Consultar pacientes'
    ),
    (
        'PACIENTES_GESTIONAR',
        'Crear y actualizar pacientes'
    ),
    (
        'ESPECIALISTAS_GESTIONAR',
        'Gestionar especialistas y servicios'
    ),
    (
        'HISTORIAS_CLINICAS_VER',
        'Consultar historias clínicas'
    );


-- ============================================================
-- ROLE - PERMISSION
--
-- Roles:
-- 1 = ADMIN
-- 2 = SECRETARIO
-- 3 = ESPECIALISTA
-- 4 = PACIENTE
-- 5 = AUXILIAR
--
-- Permissions:
-- 1 = CITAS_GESTIONAR
-- 2 = PACIENTES_VER
-- 3 = PACIENTES_GESTIONAR
-- 4 = ESPECIALISTAS_GESTIONAR
-- 5 = HISTORIAS_CLINICAS_VER
-- ============================================================

INSERT INTO role_permissions (
    id_role,
    id_permission
) VALUES
    -- ADMIN
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),

    -- SECRETARIO
    (2, 1),
    (2, 2),
    (2, 3),

    -- ESPECIALISTA
    (3, 5),

    -- PACIENTE
    (4, 1),

    -- AUXILIAR
    (5, 2);


-- ============================================================
-- USERS
--
-- 1 ADMIN
-- 1 SECRETARIO
-- 5 ESPECIALISTAS
-- 5 PACIENTES
--
-- Expected IDs:
--
-- 1  = ADMIN
-- 2  = SECRETARIO
-- 3  = ESPECIALISTA
-- 4  = ESPECIALISTA
-- 5  = ESPECIALISTA
-- 6  = ESPECIALISTA
-- 7  = ESPECIALISTA
-- 8  = PACIENTE
-- 9  = PACIENTE
-- 10 = PACIENTE
-- 11 = PACIENTE
-- 12 = PACIENTE
-- ============================================================

INSERT INTO users (
    id_role,
    first_name,
    last_name,
    email,
    phone,
    password_hash,
    status
) VALUES
    (
        1,
        'Ana',
        'Martínez',
        'ana.martinez@oralluanm.com',
        '3001234567',
        'hash_admin_001',
        'ACTIVO'
    ),
    (
        2,
        'Carlos',
        'Gómez',
        'carlos.gomez@oralluanm.com',
        '3002345678',
        'hash_secretario_001',
        'ACTIVO'
    ),
    (
        3,
        'Laura',
        'Pérez',
        'laura.perez@oralluanm.com',
        '3003456789',
        'hash_especialista_001',
        'ACTIVO'
    ),
    (
        3,
        'Diego',
        'Torres',
        'diego.torres@oralluanm.com',
        '3004567890',
        'hash_especialista_002',
        'ACTIVO'
    ),
    (
        3,
        'Sofía',
        'Ramírez',
        'sofia.ramirez@oralluanm.com',
        '3005678901',
        'hash_especialista_003',
        'ACTIVO'
    ),
    (
        3,
        'Andrés',
        'López',
        'andres.lopez@oralluanm.com',
        '3006789012',
        'hash_especialista_004',
        'ACTIVO'
    ),
    (
        3,
        'Valentina',
        'Castro',
        'valentina.castro@oralluanm.com',
        '3007890123',
        'hash_especialista_005',
        'ACTIVO'
    ),
    (
        4,
        'Mariana',
        'Gutiérrez',
        'mariana.gutierrez@gmail.com',
        '3101234567',
        'hash_paciente_001',
        'ACTIVO'
    ),
    (
        4,
        'Jorge',
        'Salazar',
        'jorge.salazar@gmail.com',
        '3102234567',
        'hash_paciente_002',
        'ACTIVO'
    ),
    (
        4,
        'Camilo',
        'Ríos',
        'camilo.rios.tutor@gmail.com',
        '3103234567',
        'hash_paciente_003',
        'ACTIVO'
    ),
    (
        4,
        'Patricia',
        'Vargas',
        'patricia.vargas@gmail.com',
        '3104234567',
        'hash_paciente_004',
        'ACTIVO'
    ),
    (
        4,
        'Felipe',
        'Ortiz',
        'felipe.ortiz@gmail.com',
        '3105234567',
        'hash_paciente_005',
        'ACTIVO'
    );


-- ============================================================
-- PATIENTS
-- ============================================================

INSERT INTO patients (
    id_user,
    document_type,
    document_number,
    birth_date,
    accepts_data,
    accepts_promotions
) VALUES
    (
        8,
        'CC',
        '1001111111',
        '1990-05-14',
        TRUE,
        TRUE
    ),
    (
        9,
        'CC',
        '1002222222',
        '1985-11-02',
        TRUE,
        FALSE
    ),
    (
        10,
        'TI',
        '1003333333',
        '2010-03-21',
        TRUE,
        FALSE
    ),
    (
        11,
        'CC',
        '1004444444',
        '1978-07-30',
        TRUE,
        TRUE
    ),
    (
        12,
        'CE',
        '1005555555',
        '1995-01-09',
        TRUE,
        TRUE
    );


-- ============================================================
-- SPECIALISTS
-- ============================================================

INSERT INTO specialists (
    id_user,
    specialty,
    professional_license,
    active
) VALUES
    (
        3,
        'Ortodoncia',
        'RP-1001',
        TRUE
    ),
    (
        4,
        'Endodoncia',
        'RP-1002',
        TRUE
    ),
    (
        5,
        'Odontopediatría',
        'RP-1003',
        TRUE
    ),
    (
        6,
        'Cirugía Oral',
        'RP-1004',
        TRUE
    ),
    (
        7,
        'Periodoncia',
        'RP-1005',
        TRUE
    );

-- ============================================================
-- SERVICES
-- ============================================================

INSERT INTO services (
    name,
    description,
    duration_minutes,
    price,
    active
) VALUES
    (
        'Limpieza dental',
        'Profilaxis y remoción de placa bacteriana',
        30,
        80000.00,
        TRUE
    ),
    (
        'Blanqueamiento dental',
        'Aclaramiento del color de los dientes',
        60,
        250000.00,
        TRUE
    ),
    (
        'Extracción simple',
        'Extracción de pieza dental sin complicaciones',
        30,
        120000.00,
        TRUE
    ),
    (
        'Control de ortodoncia',
        'Ajuste mensual de brackets',
        20,
        90000.00,
        TRUE
    ),
    (
        'Tratamiento de conducto',
        'Endodoncia completa de una pieza dental',
        90,
        450000.00,
        TRUE
    );


-- ============================================================
-- SPECIALIST - SERVICE
-- ============================================================

INSERT INTO specialist_services (
    id_specialist,
    id_service
) VALUES
    (1, 4),
    (2, 5),
    (3, 1),
    (4, 3),
    (5, 1);


-- ============================================================
-- SPECIALIST SCHEDULES
--
-- 1 = Lunes
-- 2 = Martes
-- 3 = Miércoles
-- 4 = Jueves
-- 5 = Viernes
-- 6 = Sábado
-- 7 = Domingo
-- ============================================================

INSERT INTO specialist_schedules (
    id_specialist,
    day_of_week,
    start_time,
    end_time,
    active
) VALUES
    (
        1,
        1,
        '08:00:00',
        '12:00:00',
        TRUE
    ),
    (
        2,
        2,
        '13:00:00',
        '17:00:00',
        TRUE
    ),
    (
        3,
        3,
        '08:00:00',
        '14:00:00',
        TRUE
    ),
    (
        4,
        4,
        '09:00:00',
        '13:00:00',
        TRUE
    ),
    (
        5,
        5,
        '14:00:00',
        '18:00:00',
        TRUE
    );


-- ============================================================
-- APPOINTMENTS
-- ============================================================

INSERT INTO appointments (
    id_patient,
    id_specialist,
    id_service,
    start_datetime,
    end_datetime,
    reason,
    notes,
    status,
    created_by
) VALUES
    (
        1,
        1,
        4,
        '2026-08-03 08:00:00',
        '2026-08-03 08:20:00',
        'Ajuste de brackets',
        NULL,
        'PENDIENTE',
        8
    ),
    (
        2,
        2,
        5,
        '2026-08-04 13:00:00',
        '2026-08-04 14:30:00',
        'Dolor en muela posterior',
        NULL,
        'CONFIRMADA',
        9
    ),
    (
        3,
        3,
        1,
        '2026-08-05 08:30:00',
        '2026-08-05 09:00:00',
        'Limpieza dental de control',
        NULL,
        'ATENDIDA',
        2
    ),
    (
        4,
        4,
        3,
        '2026-08-06 09:00:00',
        '2026-08-06 09:30:00',
        'Extracción de muela del juicio',
        NULL,
        'CANCELADA',
        11
    ),
    (
        5,
        5,
        1,
        '2026-08-07 14:00:00',
        '2026-08-07 14:30:00',
        'Limpieza dental anual',
        NULL,
        'NO_ASISTIO',
        2
    );


-- ============================================================
-- APPOINTMENT HISTORY
-- ============================================================

INSERT INTO appointment_history (
    id_appointment,
    previous_status,
    new_status,
    comment,
    changed_by
) VALUES
    (
        1,
        NULL,
        'PENDIENTE',
        'Cita agendada por el paciente desde la aplicación',
        8
    ),
    (
        2,
        'PENDIENTE',
        'CONFIRMADA',
        'Paciente confirmó su propia cita desde la aplicación',
        9
    ),
    (
        3,
        'CONFIRMADA',
        'ATENDIDA',
        'Consulta realizada sin novedades',
        5
    ),
    (
        4,
        'PENDIENTE',
        'CANCELADA',
        'Paciente canceló su cita desde la aplicación por viaje',
        11
    ),
    (
        5,
        'CONFIRMADA',
        'NO_ASISTIO',
        'Paciente no se presentó, marcado por recepción',
        2
    );


-- ============================================================
-- MEDICAL HISTORIES
-- ============================================================

INSERT INTO medical_histories (
    id_patient,
    medical_history,
    allergies,
    notes
) VALUES
    (
        1,
        'Sin antecedentes relevantes',
        'Ninguna conocida',
        'Uso de ortodoncia desde 2024'
    ),
    (
        2,
        'Hipertensión controlada',
        'Penicilina',
        'Requiere premedicación antes de procedimientos'
    ),
    (
        3,
        'Paciente pediátrico, sin antecedentes',
        'Ninguna conocida',
        'Primera visita al odontólogo'
    ),
    (
        4,
        'Diabetes tipo 2',
        'Ninguna conocida',
        'Control periodontal cada 3 meses'
    ),
    (
        5,
        'Sin antecedentes relevantes',
        'Látex',
        'Se usan guantes libres de látex en su atención'
    );


-- ============================================================
-- CLINICAL EVOLUTIONS
-- ============================================================

INSERT INTO clinical_evolutions (
    id_medical_history,
    id_appointment,
    id_specialist,
    diagnosis,
    treatment,
    notes
) VALUES
    (
        1,
        1,
        1,
        'Desgaste normal de brackets',
        'Ajuste de arco y cambio de ligas',
        'Próximo control en 4 semanas'
    ),
    (
        2,
        2,
        2,
        'Pulpitis irreversible en pieza 36',
        'Inicio de tratamiento de conducto',
        'Se programa segunda sesión'
    ),
    (
        3,
        3,
        3,
        'Placa bacteriana leve',
        'Profilaxis y fluorización',
        'Buena higiene oral en general'
    ),
    (
        4,
        4,
        4,
        'Pieza 38 en posición horizontal',
        'Se recomienda extracción quirúrgica',
        'Paciente canceló, pendiente reprogramar'
    ),
    (
        5,
        5,
        5,
        'Gingivitis leve',
        'Limpieza profunda y educación en higiene',
        'Paciente no asistió, se debe contactar'
    );


-- ============================================================
-- PASSWORD RESETS
-- Development data only.
-- These are NOT real password hashes or reset tokens.
-- ============================================================

INSERT INTO password_resets (
    id_user,
    token_hash,
    expires_at,
    used_at
) VALUES
    (
        1,
        'token_hash_admin_20260701',
        '2026-07-01 10:30:00',
        '2026-07-01 10:05:00'
    ),
    (
        2,
        'token_hash_secretario_20260705',
        '2026-07-05 09:15:00',
        NULL
    ),
    (
        3,
        'token_hash_especialista_1_20260710',
        '2026-07-10 16:45:00',
        '2026-07-10 16:20:00'
    ),
    (
        4,
        'token_hash_especialista_2_20260712',
        '2026-07-12 11:00:00',
        NULL
    ),
    (
        5,
        'token_hash_especialista_3_20260715',
        '2026-07-15 08:50:00',
        '2026-07-15 08:40:00'
    );