package com.dental.clinic.modules.appointment.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentRulesTest {

    @Test
    void rejectsSundayAppointments() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime start = LocalDateTime.of(2026, 1, 4, 9, 0);

        assertThrows(IllegalArgumentException.class,
                () -> AppointmentRules.validateBookableDateTime(start, start.plusMinutes(30), now));
    }

    @Test
    void rejectsColombianHolidays() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 8, 0);
        LocalDateTime start = LocalDateTime.of(2026, 7, 20, 9, 0);

        assertThrows(IllegalArgumentException.class,
                () -> AppointmentRules.validateBookableDateTime(start, start.plusMinutes(30), now));
    }

    @Test
    void rejectsAppointmentsWithoutMinimumNotice() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 9, 0);
        LocalDateTime start = now.plusMinutes(10);

        assertThrows(IllegalArgumentException.class,
                () -> AppointmentRules.validateBookableDateTime(start, start.plusMinutes(30), now));
    }

    @Test
    void acceptsRegularBusinessDayWithMinimumNotice() {
        LocalDateTime now = LocalDateTime.of(2026, 2, 3, 8, 0);
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 9, 0);

        assertDoesNotThrow(() -> AppointmentRules.validateBookableDateTime(start, start.plusMinutes(30), now));
    }

    @Test
    void calculatesMondayMovedColombianHoliday() {
        assertTrue(AppointmentRules.isColombianHoliday(LocalDate.of(2026, 1, 12)));
    }
}
