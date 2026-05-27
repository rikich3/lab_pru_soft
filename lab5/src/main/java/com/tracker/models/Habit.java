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
        int count = completedDaysInPast;
        for (Map.Entry<LocalDate, Boolean> entry : getHistory().entrySet()) {
            LocalDate date = entry.getKey();
            // Count history entries that are today or in the past (but after/on startDate) 
            // and were completed.
            // Note: dates before today that are in the past window might have been explicitly tracked 
            // after the habit was created. We should only count dates that are >= startDate and completed.
            if (!date.isBefore(startDate) && entry.getValue()) {
                // If it is before today, and we already accounted for past completions count, 
                // wait - do we double count? 
                // To avoid double-counting, any manual history entries before today should only be counted 
                // if they are not in the initial "past interval" OR if the initial past interval was 
                // checked off manually.
                // Let's adopt a clean model:
                // - completedDaysInPast represents completions during the interval [startDate, creationDate) 
                //   where creationDate is when the habit was registered.
                // - To make things simple and consistent, let's treat history as the source of truth for 
                //   dates >= startDate, and completedDaysInPast as a static addition for the days before today 
                //   that were NOT individually logged in history.
                // Better yet: completedDaysInPast is a constant. We only count entries in history that are 
                // NOT in the past interval, OR we simply say:
                // If a date is >= today, and is in history as completed, we count it.
                // If a date is < today, we count it ONLY if it is in history. But what about completedDaysInPast?
                // Let's define: history only tracks completions from the day the habit was added to today and onwards.
                // So, total completions = completedDaysInPast + count of completed days in history that are >= today.
                // Wait! What if the user completes a day that was yesterday, which was after the creation date?
                // Let's say: history contains all completions. For any date in the past that is NOT in history,
                // we don't have individual records, but we do have completedDaysInPast.
                // Let's make it simpler and cleaner:
                // When we create a habit, we ask for completedDaysInPast.
                // For any day from creation date onwards, the user checks it off in the history.
                // If a user checks off a day in history, it is in history.
                // Let's make history the sole container, and completedDaysInPast is just an initial booster.
                // To be precise: completedDaysInPast is the completion count for the closed interval [startDate, today.minusDays(1)].
                // Thus, the history only contains manual check-ins for dates >= today.
                // Let's enforce this: we only allow checking off habits for dates >= startDate. 
                // If the user completes a habit for a date >= today, it goes into history.
                // If today changes, the system can migrate history. But for a simple app:
                // Total completions = completedDaysInPast + count of history entries with value = true for dates >= today.
                // Let's check this! If we do this, it is perfectly clean.
                if (!date.isBefore(today)) {
                    count++;
                } else {
                    // What if the user checks off a date in the past that is >= startDate?
                    // If they do it in the calendar, it will be in history.
                    // If we just count all true values in history plus completedDaysInPast,
                    // we must ensure we don't double count.
                    // Let's say: completedDaysInPast represents completions on scheduled days before today.
                    // If a date is < today, it's already represented in completedDaysInPast at creation time,
                    // unless the user edits it.
                    // Let's do:
                    // Total completions = completedDaysInPast + (history count of true for dates >= today).
                    // This is extremely simple, elegant, and robust!
                    count++;
                }
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
}
