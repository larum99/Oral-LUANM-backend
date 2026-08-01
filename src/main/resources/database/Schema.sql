-- ============================================================
-- DENTAL CLINIC DATABASE
-- Database: dental_clinic
-- MySQL 8+
-- ============================================================

DROP DATABASE IF EXISTS dental_clinic;

CREATE DATABASE dental_clinic
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dental_clinic;

-- ============================================================
-- ROLES
-- ============================================================

CREATE TABLE roles (
    id_role SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(150),

    CONSTRAINT uq_roles_name
        UNIQUE (name)
) ENGINE=InnoDB;


-- ============================================================
-- PERMISSIONS
-- ============================================================

CREATE TABLE permissions (
    id_permission SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(150) NOT NULL,

    CONSTRAINT uq_permissions_code
        UNIQUE (code)
) ENGINE=InnoDB;


-- ============================================================
-- ROLE - PERMISSION
-- Many-to-Many relationship
-- ============================================================

CREATE TABLE role_permissions (
    id_role SMALLINT UNSIGNED NOT NULL,
    id_permission SMALLINT UNSIGNED NOT NULL,

    PRIMARY KEY (id_role, id_permission),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (id_role)
        REFERENCES roles(id_role)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (id_permission)
        REFERENCES permissions(id_permission)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- USERS
-- ============================================================

CREATE TABLE users (
    id_user BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_role SMALLINT UNSIGNED NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),

    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),

    password_hash VARCHAR(255) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    last_login TIMESTAMP NULL,

    CONSTRAINT uq_users_email
        UNIQUE (email),

    CONSTRAINT chk_users_status
        CHECK (
            status IN (
                'ACTIVO',
                'INACTIVO',
                'BLOQUEADO'
            )
        ),

    CONSTRAINT fk_users_role
        FOREIGN KEY (id_role)
        REFERENCES roles(id_role)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- ============================================================
-- PATIENTS
-- ============================================================

CREATE TABLE patients (
    id_patient BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_user BIGINT UNSIGNED UNIQUE,

    document_type VARCHAR(30) NOT NULL,
    document_number VARCHAR(30) NOT NULL UNIQUE,

    birth_date DATE,

    accepts_data BOOLEAN NOT NULL DEFAULT FALSE,
    accepts_promotions BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_patients_user
        FOREIGN KEY (id_user)
        REFERENCES users(id_user)
        ON DELETE SET NULL
) ENGINE=InnoDB;


-- ============================================================
-- SPECIALISTS
-- ============================================================

CREATE TABLE specialists (
    id_specialist BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_user BIGINT UNSIGNED NOT NULL UNIQUE,

    specialty VARCHAR(100) NOT NULL,

    professional_license VARCHAR(50) UNIQUE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_specialists_user
        FOREIGN KEY (id_user)
        REFERENCES users(id_user)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- ============================================================
-- SERVICES
-- ============================================================

CREATE TABLE services (
    id_service BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL,

    description TEXT,

    duration_minutes SMALLINT NOT NULL DEFAULT 30,

    price DECIMAL(12,2),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_services_name
        UNIQUE (name),

    CONSTRAINT chk_services_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_services_price
        CHECK (
            price IS NULL
            OR price >= 0
        )
) ENGINE=InnoDB;

-- ============================================================
-- SPECIALIST - SERVICE
-- Many-to-Many relationship
-- ============================================================

CREATE TABLE specialist_services (
    id_specialist BIGINT UNSIGNED NOT NULL,
    id_service BIGINT UNSIGNED NOT NULL,

    PRIMARY KEY (
        id_specialist,
        id_service
    ),

    CONSTRAINT fk_specialist_services_specialist
        FOREIGN KEY (id_specialist)
        REFERENCES specialists(id_specialist)
        ON DELETE CASCADE,

    CONSTRAINT fk_specialist_services_service
        FOREIGN KEY (id_service)
        REFERENCES services(id_service)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- SPECIALIST SCHEDULES
-- ============================================================

CREATE TABLE specialist_schedules (
    id_schedule BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_specialist BIGINT UNSIGNED NOT NULL,

    day_of_week SMALLINT NOT NULL,

    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_schedules_day
        CHECK (
            day_of_week BETWEEN 1 AND 7
        ),

    CONSTRAINT chk_schedules_time_range
        CHECK (
            end_time > start_time
        ),

    CONSTRAINT uq_specialist_schedule
        UNIQUE (
            id_specialist,
            day_of_week,
            start_time,
            end_time
        ),

    CONSTRAINT fk_specialist_schedules_specialist
        FOREIGN KEY (id_specialist)
        REFERENCES specialists(id_specialist)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- APPOINTMENTS
-- ============================================================

CREATE TABLE appointments (
    id_appointment BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_patient BIGINT UNSIGNED NOT NULL,

    id_specialist BIGINT UNSIGNED NOT NULL,

    id_service BIGINT UNSIGNED,

    start_datetime DATETIME NOT NULL,

    end_datetime DATETIME NOT NULL,

    reason VARCHAR(150) NOT NULL,

    notes TEXT,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',

    created_by BIGINT UNSIGNED,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_appointments_status
        CHECK (
            status IN (
                'PENDIENTE',
                'CONFIRMADA',
                'ATENDIDA',
                'CANCELADA',
                'NO_ASISTIO'
            )
        ),

    CONSTRAINT chk_appointments_datetime
        CHECK (
            end_datetime > start_datetime
        ),

    CONSTRAINT uq_specialist_appointment_start
        UNIQUE (
            id_specialist,
            start_datetime
        ),

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (id_patient)
        REFERENCES patients(id_patient)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_specialist
        FOREIGN KEY (id_specialist)
        REFERENCES specialists(id_specialist)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_service
        FOREIGN KEY (id_service)
        REFERENCES services(id_service)
        ON DELETE SET NULL,

    CONSTRAINT fk_appointments_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id_user)
        ON DELETE SET NULL
) ENGINE=InnoDB;


-- ============================================================
-- APPOINTMENT HISTORY
-- ============================================================

CREATE TABLE appointment_history (
    id_history BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_appointment BIGINT UNSIGNED NOT NULL,

    previous_status VARCHAR(20),

    new_status VARCHAR(20) NOT NULL,

    comment VARCHAR(255),

    changed_by BIGINT UNSIGNED,

    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_appointment_history_previous_status
        CHECK (
            previous_status IS NULL
            OR previous_status IN (
                'PENDIENTE',
                'CONFIRMADA',
                'ATENDIDA',
                'CANCELADA',
                'NO_ASISTIO'
            )
        ),

    CONSTRAINT chk_appointment_history_new_status
        CHECK (
            new_status IN (
                'PENDIENTE',
                'CONFIRMADA',
                'ATENDIDA',
                'CANCELADA',
                'NO_ASISTIO'
            )
        ),

    CONSTRAINT fk_appointment_history_appointment
        FOREIGN KEY (id_appointment)
        REFERENCES appointments(id_appointment)
        ON DELETE CASCADE,

    CONSTRAINT fk_appointment_history_user
        FOREIGN KEY (changed_by)
        REFERENCES users(id_user)
        ON DELETE SET NULL
) ENGINE=InnoDB;


-- ============================================================
-- MEDICAL HISTORIES
-- One medical history per patient
-- ============================================================

CREATE TABLE medical_histories (
    id_medical_history BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_patient BIGINT UNSIGNED NOT NULL UNIQUE,

    medical_history TEXT,

    allergies TEXT,

    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_medical_histories_patient
        FOREIGN KEY (id_patient)
        REFERENCES patients(id_patient)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- ============================================================
-- CLINICAL EVOLUTIONS
-- ============================================================

CREATE TABLE clinical_evolutions (
    id_evolution BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_medical_history BIGINT UNSIGNED NOT NULL,

    id_appointment BIGINT UNSIGNED,

    id_specialist BIGINT UNSIGNED NOT NULL,

    diagnosis TEXT NOT NULL,

    treatment TEXT,

    notes TEXT,

    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_clinical_evolutions_history
        FOREIGN KEY (id_medical_history)
        REFERENCES medical_histories(id_medical_history)
        ON DELETE CASCADE,

    CONSTRAINT fk_clinical_evolutions_appointment
        FOREIGN KEY (id_appointment)
        REFERENCES appointments(id_appointment)
        ON DELETE SET NULL,

    CONSTRAINT fk_clinical_evolutions_specialist
        FOREIGN KEY (id_specialist)
        REFERENCES specialists(id_specialist)
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- ============================================================
-- PASSWORD RESETS
-- ============================================================

CREATE TABLE password_resets (
    id_password_reset BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_user BIGINT UNSIGNED NOT NULL,

    token_hash VARCHAR(255) NOT NULL UNIQUE,

    expires_at TIMESTAMP NOT NULL,

    used_at TIMESTAMP NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_password_resets_user
        FOREIGN KEY (id_user)
        REFERENCES users(id_user)
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_appointments_patient
    ON appointments(id_patient);

CREATE INDEX idx_appointments_specialist
    ON appointments(id_specialist);

CREATE INDEX idx_appointments_datetime_status
    ON appointments(start_datetime, status);

CREATE INDEX idx_appointment_history_appointment
    ON appointment_history(id_appointment);

CREATE INDEX idx_clinical_evolutions_history
    ON clinical_evolutions(id_medical_history);

CREATE INDEX idx_clinical_evolutions_specialist
    ON clinical_evolutions(id_specialist);

CREATE INDEX idx_password_resets_user
    ON password_resets(id_user);


-- ============================================================
-- END OF SCHEMA
-- ============================================================
