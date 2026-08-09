SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'appointments'
      AND column_name = 'version'
);

SET @ddl = IF(
    @column_exists = 0,
    'ALTER TABLE appointments ADD COLUMN version BIGINT NOT NULL DEFAULT 0 AFTER status',
    'SELECT 1'
);

PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;