package com.tracker.services;

import com.google.gson.*;
import com.tracker.models.Habit;

import java.io.*;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HabitService {
    private static final String FILE_NAME = "habits.json";
    private final List<Habit> habits;
    private final Gson gson;

    public HabitService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        this.habits = loadHabits();
    }

    public List<Habit> getAllHabits() {
        return new ArrayList<>(habits);
    }

    public Habit getHabitById(UUID id) {
        for (Habit h : habits) {
            if (h.getId().equals(id)) {
                return h;
            }
        }
        return null;
    }

    public void addHabit(Habit habit) throws ValidationException {
        validateHabit(habit);
        habits.add(habit);
        saveHabits();
    }

    public void updateHabit(Habit updatedHabit) throws ValidationException {
        // Validate FIRST before modifying any existing habit
        validateHabit(updatedHabit);
        
        // Only proceed with the update if validation passed
        for (int i = 0; i < habits.size(); i++) {
            if (habits.get(i).getId().equals(updatedHabit.getId())) {
                habits.set(i, updatedHabit);
                saveHabits();
                return;
            }
        }
        throw new ValidationException("No se encontró el hábito a actualizar.");
    }

    public void deleteHabit(UUID id) {
        habits.removeIf(h -> h.getId().equals(id));
        saveHabits();
    }

    public void toggleCompletion(UUID id, LocalDate date) {
        for (Habit h : habits) {
            if (h.getId().equals(id)) {
                boolean current = h.isCompletedOn(date);
                h.setCompletedOn(date, !current);
                saveHabits();
                return;
            }
        }
    }

    /**
     * Enforces all business invariants on a habit.
     */
    public void validateHabit(Habit habit) throws ValidationException {
        LocalDate today = LocalDate.now();

        // 1. Name validation
        if (habit.getName() == null || habit.getName().trim().isEmpty()) {
            throw new ValidationException("El nombre del hábito no puede estar vacío.");
        }

        // 2. Duplicate Name validation (case-insensitive, trimmed)
        for (Habit existing : habits) {
            if (!existing.getId().equals(habit.getId())) {
                if (existing.getName().trim().equalsIgnoreCase(habit.getName().trim())) {
                    throw new ValidationException("Ya existe un hábito con el nombre '" + habit.getName().trim() + "'.");
                }
            }
        }

        // 3. Days of the week validation
        if (habit.getDaysOfWeek() == null || habit.getDaysOfWeek().isEmpty()) {
            throw new ValidationException("Debe seleccionar al menos un día de la semana.");
        }

        // 4. Individual Duration validation
        if (habit.getDailyDurationMinutes() <= 0) {
            throw new ValidationException("La duración diaria debe ser mayor a 0 minutos.");
        }
        if (habit.getDailyDurationMinutes() > 1440) {
            throw new ValidationException("Un hábito no puede tomar más de 24 horas (1440 minutos) al día.");
        }

        // 5. Start Date validation
        if (habit.getStartDate() == null) {
            throw new ValidationException("La fecha de inicio no puede ser nula.");
        }
        if (habit.getStartDate().isAfter(today)) {
            throw new ValidationException("La fecha de inicio no puede ser en el futuro.");
        }

        // 6. Cumulative Duration validation per day of the week
        for (DayOfWeek day : habit.getDaysOfWeek()) {
            int totalMinutesOnDay = habit.getDailyDurationMinutes();
            for (Habit existing : habits) {
                if (!existing.getId().equals(habit.getId()) && existing.getDaysOfWeek().contains(day)) {
                    totalMinutesOnDay += existing.getDailyDurationMinutes();
                }
            }
            if (totalMinutesOnDay > 1440) {
                throw new ValidationException(String.format(
                        "La suma de duración de los hábitos para los días %s supera las 24 horas (actual: %d minutos).",
                        translateDayOfWeek(day), totalMinutesOnDay
                ));
            }
        }

        // 7. Completed Days in Past validation
        if (habit.getStartDate().isBefore(today)) {
            int scheduledPastDays = habit.getScheduledDaysCount(habit.getStartDate(), today);
            if (habit.getCompletedDaysInPast() < 0) {
                throw new ValidationException("La cantidad de días cumplidos en el pasado no puede ser negativa.");
            }
            if (habit.getCompletedDaysInPast() > scheduledPastDays) {
                throw new ValidationException(String.format(
                        "La cantidad de días cumplidos en el pasado (%d) no puede ser mayor que la cantidad de días programados transcurridos (%d).",
                        habit.getCompletedDaysInPast(), scheduledPastDays
                ));
            }
        } else {
            // Start date is today or later (though we block future start dates)
            if (habit.getCompletedDaysInPast() != 0) {
                throw new ValidationException("No se pueden declarar días cumplidos en el pasado si el hábito inicia hoy.");
            }
        }
    }

    private String translateDayOfWeek(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return day.toString();
        }
    }

    // --- JSON Persistence ---

    private synchronized void saveHabits() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(habits, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized List<Habit> loadHabits() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Habit>>() {}.getType();
            List<Habit> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- Custom Adapters for Java 8 Dates ---

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}
