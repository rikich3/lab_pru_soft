package com.tracker.services;

import com.tracker.models.Habit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HabitServiceTest {
    private HabitService service;
    private static final String FILE_NAME = "habits.json";

    @BeforeEach
    public void setUp() {
        // Delete pre-existing persistence to ensure absolute isolation
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
        service = new HabitService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testAddHabitValid() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        Habit habit = new Habit("Meditar", LocalDate.now(), 20, days, 0);

        assertDoesNotThrow(() -> service.addHabit(habit));
        assertEquals(1, service.getAllHabits().size());
        
        Habit retrieved = service.getHabitById(habit.getId());
        assertNotNull(retrieved);
        assertEquals("Meditar", retrieved.getName());
    }

    @Test
    public void testAddHabitEmptyName() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);
        
        Habit nullName = new Habit(null, LocalDate.now(), 30, days, 0);
        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.addHabit(nullName));
        assertTrue(ex1.getMessage().contains("nombre"));

        Habit emptyName = new Habit("   ", LocalDate.now(), 30, days, 0);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.addHabit(emptyName));
        assertTrue(ex2.getMessage().contains("nombre"));
    }

    @Test
    public void testAddHabitDuplicateName() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);
        Habit h1 = new Habit("Estudiar", LocalDate.now(), 60, days, 0);
        assertDoesNotThrow(() -> service.addHabit(h1));

        // Exact duplicate
        Habit h2 = new Habit("Estudiar", LocalDate.now(), 30, days, 0);
        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.addHabit(h2));
        assertTrue(ex1.getMessage().contains("Ya existe un hábito"));

        // Case-insensitive duplicate with spacing
        Habit h3 = new Habit("  esTUDIar  ", LocalDate.now(), 30, days, 0);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.addHabit(h3));
        assertTrue(ex2.getMessage().contains("Ya existe un hábito"));
    }

    @Test
    public void testAddHabitEmptySchedule() {
        Habit h = new Habit("Cantar", LocalDate.now(), 60, EnumSet.noneOf(DayOfWeek.class), 0);
        ValidationException ex = assertThrows(ValidationException.class, () -> service.addHabit(h));
        assertTrue(ex.getMessage().contains("día de la semana"));
    }

    @Test
    public void testAddHabitDurationBounds() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);

        // Zero duration
        Habit hZero = new Habit("Cantar", LocalDate.now(), 0, days, 0);
        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.addHabit(hZero));
        assertTrue(ex1.getMessage().contains("duración"));

        // Negative duration
        Habit hNeg = new Habit("Cantar", LocalDate.now(), -10, days, 0);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.addHabit(hNeg));
        assertTrue(ex2.getMessage().contains("duración"));

        // Excess of 24h
        Habit hExcess = new Habit("Cantar", LocalDate.now(), 1441, days, 0);
        ValidationException ex3 = assertThrows(ValidationException.class, () -> service.addHabit(hExcess));
        assertTrue(ex3.getMessage().contains("más de 24 horas"));

        // Exactly 24h (valid)
        Habit hMax = new Habit("Dormir", LocalDate.now(), 1440, days, 0);
        assertDoesNotThrow(() -> service.addHabit(hMax));
    }

    @Test
    public void testAddHabitCumulativeDurationLimit() {
        Set<DayOfWeek> commonDays = EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
        
        // Habit 1: 15 hours on Tue and Thu (900 mins)
        Habit h1 = new Habit("Trabajo", LocalDate.now(), 900, commonDays, 0);
        assertDoesNotThrow(() -> service.addHabit(h1));

        // Habit 2: 8 hours on Tue and Thu (480 mins)
        // Total so far on Tue/Thu is 23 hours. Still valid!
        Habit h2 = new Habit("Estudios", LocalDate.now(), 480, commonDays, 0);
        assertDoesNotThrow(() -> service.addHabit(h2));

        // Habit 3: 2 hours on Tue and Thu (120 mins)
        // Adding Habit 3 makes total Tue/Thu = 25 hours. Invalid!
        Habit h3 = new Habit("Gimnasio", LocalDate.now(), 120, commonDays, 0);
        ValidationException ex = assertThrows(ValidationException.class, () -> service.addHabit(h3));
        assertTrue(ex.getMessage().contains("supera las 24 horas"));

        // Habit 3 alternate: 2 hours scheduled ONLY on Monday and Friday
        // Mon/Fri total = 2 hours. This is valid!
        Habit h3Valid = new Habit("Gimnasio", LocalDate.now(), 120, EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 0);
        assertDoesNotThrow(() -> service.addHabit(h3Valid));
    }

    @Test
    public void testAddHabitStartDateFuture() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        Habit hFuture = new Habit("Plantar", tomorrow, 30, days, 0);
        ValidationException ex = assertThrows(ValidationException.class, () -> service.addHabit(hFuture));
        assertTrue(ex.getMessage().contains("futuro"));
    }

    @Test
    public void testPastCompletionDaysBounds() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        
        // 5 calendar days ago
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        // Daily habit -> 5 scheduled days passed in the interval [fiveDaysAgo, today)
        
        // 1. Valid: exactly 5 days
        Habit hValidMax = new Habit("Correr", fiveDaysAgo, 30, days, 5);
        assertDoesNotThrow(() -> service.addHabit(hValidMax));
        
        // Clear
        service.deleteHabit(hValidMax.getId());

        // 2. Invalid: 6 days (more than scheduled passed)
        Habit hInvalidHigh = new Habit("Correr", fiveDaysAgo, 30, days, 6);
        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.addHabit(hInvalidHigh));
        assertTrue(ex1.getMessage().contains("transcurridos"));

        // 3. Invalid: negative completed days
        Habit hInvalidNeg = new Habit("Correr", fiveDaysAgo, 30, days, -1);
        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.addHabit(hInvalidNeg));
        assertTrue(ex2.getMessage().contains("negativa"));

        // 4. Invalid: start date is today but completed days in past is not 0
        Habit hTodayInvalid = new Habit("Correr", LocalDate.now(), 30, days, 1);
        ValidationException ex3 = assertThrows(ValidationException.class, () -> service.addHabit(hTodayInvalid));
        assertTrue(ex3.getMessage().contains("inicia hoy"));
    }

    @Test
    public void testPastCompletionDaysWithScheduledSubset() {
        // Scheduled ONLY on Monday, Wednesday, Friday
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        
        // Let's set start date to exactly 7 calendar days ago
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        
        // Calculate the exact scheduled days in that interval
        int scheduledDays = 0;
        LocalDate temp = sevenDaysAgo;
        LocalDate today = LocalDate.now();
        while (temp.isBefore(today)) {
            if (days.contains(temp.getDayOfWeek())) {
                scheduledDays++;
            }
            temp = temp.plusDays(1);
        }

        // Valid boundary
        Habit hValid = new Habit("Cocinar", sevenDaysAgo, 45, days, scheduledDays);
        assertDoesNotThrow(() -> service.addHabit(hValid));

        // Invalid: scheduledDays + 1
        service.deleteHabit(hValid.getId());
        Habit hInvalid = new Habit("Cocinar", sevenDaysAgo, 45, days, scheduledDays + 1);
        ValidationException ex = assertThrows(ValidationException.class, () -> service.addHabit(hInvalid));
        assertTrue(ex.getMessage().contains("transcurridos"));
    }

    @Test
    public void testUpdateHabitChecksUniqueName() {
        Set<DayOfWeek> days = EnumSet.of(DayOfWeek.MONDAY);
        Habit h1 = new Habit("Hábito A", LocalDate.now(), 30, days, 0);
        Habit h2 = new Habit("Hábito B", LocalDate.now(), 30, days, 0);

        assertDoesNotThrow(() -> service.addHabit(h1));
        assertDoesNotThrow(() -> service.addHabit(h2));

        // Update h2 name to h1 name -> should fail
        h2.setName("Hábito A");
        ValidationException ex = assertThrows(ValidationException.class, () -> service.updateHabit(h2));
        assertTrue(ex.getMessage().contains("Ya existe un hábito"));

        // Update h2 name to a new unique name -> should pass
        h2.setName("Hábito C");
        assertDoesNotThrow(() -> service.updateHabit(h2));
    }
}
