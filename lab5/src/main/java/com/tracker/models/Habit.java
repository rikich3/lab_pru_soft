package com.tracker.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Habit {
    private UUID id;
    private String name;
    private LocalDate startDate;
    private int dailyDurationMinutes; // 1 to 1440
    private Set<DayOfWeek> daysOfWeek;
    private int completedDaysInPast; // for start date < today
    private Map<LocalDate, Boolean> history; // date -> completed
    private LocalDateTime createdAt;

    public Habit() {
        this.id = UUID.randomUUID();
        this.daysOfWeek = new HashSet<>();
        this.history = new HashMap<>();
        this.createdAt = LocalDateTime.now();
    }

    public Habit(String name, LocalDate startDate, int dailyDurationMinutes, Set<DayOfWeek> daysOfWeek, int completedDaysInPast) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.startDate = startDate;
        this.dailyDurationMinutes = dailyDurationMinutes;
        this.daysOfWeek = daysOfWeek != null ? new HashSet<>(daysOfWeek) : new HashSet<>();
        this.completedDaysInPast = completedDaysInPast;
        this.history = new HashMap<>();
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDailyDurationMinutes() {
        return dailyDurationMinutes;
    }

    public void setDailyDurationMinutes(int dailyDurationMinutes) {
        this.dailyDurationMinutes = dailyDurationMinutes;
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek != null ? new HashSet<>(daysOfWeek) : new HashSet<>();
    }

    public int getCompletedDaysInPast() {
        return completedDaysInPast;
    }

    public void setCompletedDaysInPast(int completedDaysInPast) {
        this.completedDaysInPast = completedDaysInPast;
    }

    public Map<LocalDate, Boolean> getHistory() {
        if (history == null) {
            history = new HashMap<>();
        }
        return history;
    }

    public void setHistory(Map<LocalDate, Boolean> history) {
        this.history = history != null ? new HashMap<>(history) : new HashMap<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- Helpers for Scheduling and Statistics ---

    /**
     * Checks if the habit is active (scheduled) on a given calendar date.
     */
    public boolean isActiveOnDate(LocalDate date) {
        if (date.isBefore(startDate)) {
            return false;
        }
        return daysOfWeek.contains(date.getDayOfWeek());
    }

    /**
     * Calculates the count of scheduled days between start (inclusive) and end (exclusive).
     */
    public int getScheduledDaysCount(LocalDate start, LocalDate end) {
        if (start == null || end == null || start.isAfter(end) || start.isEqual(end)) {
            return 0;
        }
        int count = 0;
        LocalDate current = start;
        while (current.isBefore(end)) {
            if (daysOfWeek.contains(current.getDayOfWeek())) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    /**
     * Returns whether the habit was marked completed on a specific date.
     */
    public boolean isCompletedOn(LocalDate date) {
        if (date.isBefore(startDate)) {
            return false;
        }
        // If date is before today, we check if we have a record in history.
        // If not in history, and it is in the past, it was not completed.
        return getHistory().getOrDefault(date, false);
    }

    /**
     * Sets completion for a specific date.
     */
    public void setCompletedOn(LocalDate date, boolean completed) {
        if (date.isBefore(startDate)) {
            throw new IllegalArgumentException("Cannot set completion before the start date.");
        }
        getHistory().put(date, completed);
    }

    /**
     * Calculates the total completions: past completions + active completions in history.
     */
    public int getTotalCompletionsCount(LocalDate today) {
        int count = completedDaysInPast; // Será 0 para hábitos nuevos, pero mantiene compatibilidad con datos viejos
        for (Map.Entry<LocalDate, Boolean> entry : getHistory().entrySet()) {
            LocalDate date = entry.getKey();
            // Contamos cualquier día que esté registrado como completado (true) y sea posterior o igual a la fecha de inicio
            if (!date.isBefore(startDate) && entry.getValue()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the total scheduled days up to today (inclusive).
     */
    public int getTotalScheduledDaysCount(LocalDate today) {
        int pastScheduled = getScheduledDaysCount(startDate, today);
        int todayScheduled = isActiveOnDate(today) ? 1 : 0;
        return pastScheduled + todayScheduled;
    }

    /**
     * Calculates the completion percentage.
     */
    public double getCompletionRate(LocalDate today) {
        int totalScheduled = getTotalScheduledDaysCount(today);
        if (totalScheduled == 0) {
            return 0.0;
        }
        int totalCompleted = getTotalCompletionsCount(today);
        return Math.min(1.0, (double) totalCompleted / totalScheduled);
    }

    /**
     * Migra los días completados en el pasado directamente al mapa de historial
     * para tener una única fuente de verdad y evitar doble conteo.
     */
    public void initializeHistoryFromPastCompletions() {
        LocalDate today = LocalDate.now();

        // 1. Limpiamos cualquier registro previo en el historial de días pasados programados.
        // Esto asegura que si el usuario edita el número (ej. de 2 a 1, o a 0), el historial se adapte correctamente.
        getHistory().keySet().removeIf(date -> date.isBefore(today) && isActiveOnDate(date));

        // 2. Si no hay días que migrar, terminamos aquí
        if (completedDaysInPast <= 0) {
            return;
        }

        LocalDate current = startDate;
        int completionCounter = 0;

        // 3. Recorremos los días programados desde la fecha de inicio hasta hoy (exclusivo)
        while (current.isBefore(today) && completionCounter < completedDaysInPast) {
            // Solo procesamos días que están programados
            if (daysOfWeek.contains(current.getDayOfWeek())) {
                getHistory().put(current, true);
                completionCounter++;
            }
            current = current.plusDays(1);
        }

        // 4. Limpiamos completedDaysInPast ya que ahora está en el historial
        completedDaysInPast = 0;
    }
}
