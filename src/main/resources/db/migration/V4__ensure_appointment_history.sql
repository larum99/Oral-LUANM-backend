CREATE TABLE IF NOT EXISTS appointment_history (
    id_history BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_appointment BIGINT UNSIGNED NOT NULL,

    previous_status ENUM(
        'PENDIENTE',
        'CONFIRMADA',
        'CANCELADA',
        'ATENDIDA',
        'NO_ASISTIO'
    ),

    new_status ENUM(
        'PENDIENTE',
        'CONFIRMADA',
        'CANCELADA',
        'ATENDIDA',
        'NO_ASISTIO'
    ) NOT NULL,

    comment VARCHAR(255),

    changed_by BIGINT UNSIGNED,

    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_appointment_history_previous_status
        CHECK (
            previous_status IS NULL
            OR previous_status IN (
                'PENDIENTE',
                'CONFIRMADA',
                'CANCELADA',
                'ATENDIDA',
                'NO_ASISTIO'
            )
        ),

    CONSTRAINT chk_appointment_history_new_status
        CHECK (
            new_status IN (
                'PENDIENTE',
                'CONFIRMADA',
                'CANCELADA',
                'ATENDIDA',
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

SET @idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'appointment_history'
      AND index_name = 'idx_appointment_history_appointment'
);

SET @ddl = IF(
    @idx_exists = 0,
    'CREATE INDEX idx_appointment_history_appointment ON appointment_history(id_appointment)',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;