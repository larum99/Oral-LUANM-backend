package com.dental.clinic.modules.appointment.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public final class AppointmentRules {

    private static final int MINUTES_AHEAD = 15;

    private AppointmentRules() {
    }

    public static void validateBookableDateTime(LocalDateTime start, LocalDateTime end, LocalDateTime now) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("La fecha y hora de la cita son obligatorias.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("La hora final de la cita debe ser posterior a la hora inicial.");
        }
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            throw new IllegalArgumentException("La cita debe iniciar y finalizar el mismo dia.");
        }
        if (start.isBefore(now.plusMinutes(MINUTES_AHEAD))) {
            throw new IllegalArgumentException("La cita debe agendarse con al menos 15 minutos de anticipacion.");
        }
        if (start.toLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se agendan citas los domingos.");
        }
        if (isColombianHoliday(start.toLocalDate())) {
            throw new IllegalArgumentException("No se agendan citas en festivos de Colombia.");
        }
    }

    public static boolean isColombianHoliday(LocalDate date) {
        return colombianHolidays(date.getYear()).contains(date);
    }

    private static Set<LocalDate> colombianHolidays(int year) {
        LocalDate easterSunday = calculateEasterSunday(year);
        return Set.of(
                LocalDate.of(year, 1, 1),
                moveToNextMonday(LocalDate.of(year, 1, 6)),
                moveToNextMonday(LocalDate.of(year, 3, 19)),
                easterSunday.minusDays(3),
                easterSunday.minusDays(2),
                LocalDate.of(year, 5, 1),
                moveToNextMonday(easterSunday.plusDays(43)),
                moveToNextMonday(easterSunday.plusDays(64)),
                moveToNextMonday(easterSunday.plusDays(71)),
                moveToNextMonday(LocalDate.of(year, 6, 29)),
                LocalDate.of(year, 7, 20),
                LocalDate.of(year, 8, 7),
                moveToNextMonday(LocalDate.of(year, 8, 15)),
                moveToNextMonday(LocalDate.of(year, 10, 12)),
                moveToNextMonday(LocalDate.of(year, 11, 1)),
                moveToNextMonday(LocalDate.of(year, 11, 11)),
                LocalDate.of(year, 12, 8),
                LocalDate.of(year, 12, 25)
        );
    }

    private static LocalDate moveToNextMonday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            return date;
        }
        return date.plusDays((8 - date.getDayOfWeek().getValue()) % 7);
    }

    private static LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
}
