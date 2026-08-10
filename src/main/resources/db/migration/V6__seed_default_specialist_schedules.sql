-- Seed baseline availability only for specialists that do not have any schedule yet.
-- Custom schedules remain untouched.
INSERT INTO specialist_schedules (id_specialist, day_of_week, start_time, end_time, active)
SELECT s.id_specialist, days.day_of_week, days.start_time, days.end_time, TRUE
FROM specialists s
JOIN (
    SELECT 1 AS day_of_week, TIME '08:00:00' AS start_time, TIME '18:00:00' AS end_time
    UNION ALL SELECT 2, TIME '08:00:00', TIME '18:00:00'
    UNION ALL SELECT 3, TIME '08:00:00', TIME '18:00:00'
    UNION ALL SELECT 4, TIME '08:00:00', TIME '18:00:00'
    UNION ALL SELECT 5, TIME '08:00:00', TIME '18:00:00'
    UNION ALL SELECT 6, TIME '08:00:00', TIME '13:00:00'
) days
WHERE NOT EXISTS (
    SELECT 1
    FROM specialist_schedules existing
    WHERE existing.id_specialist = s.id_specialist
);