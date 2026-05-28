package com.tracker.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.tracker.models.Habit;
import com.tracker.services.HabitService;
import com.tracker.services.ValidationException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController {

    // --- FXML Bindings ---
    @FXML private Label lblActiveCount;
    @FXML private Label lblCommittedHours;
    @FXML private Label lblCompletionRate;
    @FXML private VBox habitsContainer;

    @FXML private Label lblFormTitle;
    @FXML private TextField nameField;
    @FXML private Spinner<Integer> hoursSpinner;
    @FXML private Spinner<Integer> minutesSpinner;
    @FXML private DatePicker startDatePicker;
    @FXML private VBox pastCompletionContainer;
    @FXML private Spinner<Integer> pastCompletionSpinner;
    @FXML private Label lblPastLimitInfo;

    @FXML private ToggleButton btnMon;
    @FXML private ToggleButton btnTue;
    @FXML private ToggleButton btnWed;
    @FXML private ToggleButton btnThu;
    @FXML private ToggleButton btnFri;
    @FXML private ToggleButton btnSat;
    @FXML private ToggleButton btnSun;

    @FXML private Label feedbackLabel;
    @FXML private Button btnClear;
    @FXML private Button btnSave;

    // --- Inline style constants (light theme) ---
    // Centralizados aquí para facilitar mantenimiento
    private static final String STYLE_HABIT_NAME =
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;";

    private static final String STYLE_BADGE_COMPLETED =
            "-fx-font-size: 10px; -fx-font-weight: bold; " +
            "-fx-background-color: #dcfce7; -fx-text-fill: #166534; " +
            "-fx-padding: 3px 10px; -fx-background-radius: 20px; " +
            "-fx-border-color: #86efac; -fx-border-width: 1px; -fx-border-radius: 20px;";

    private static final String STYLE_BADGE_PENDING =
            "-fx-font-size: 10px; -fx-font-weight: bold; " +
            "-fx-background-color: #fef3c7; -fx-text-fill: #92400e; " +
            "-fx-padding: 3px 10px; -fx-background-radius: 20px; " +
            "-fx-border-color: #fcd34d; -fx-border-width: 1px; -fx-border-radius: 20px;";

    private static final String STYLE_BADGE_INACTIVE =
            "-fx-font-size: 10px; -fx-font-weight: bold; " +
            "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; " +
            "-fx-padding: 3px 10px; -fx-background-radius: 20px; " +
            "-fx-border-color: #cbd5e1; -fx-border-width: 1px; -fx-border-radius: 20px;";

    private static final String STYLE_META_LABEL =
            "-fx-font-size: 12px; -fx-text-fill: #64748b;";

    private static final String STYLE_HISTORY_TITLE =
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0d9488;";

    private static final String STYLE_DAY_LABEL =
            "-fx-font-size: 10px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;";

    private static final String STYLE_TODAY_LABEL =
            "-fx-font-size: 10px; -fx-text-fill: #0d9488; -fx-font-weight: bold;";

    private static final String STYLE_NO_DAYS_LABEL =
            "-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-font-style: italic;";

    private static final String STYLE_PLACEHOLDER =
            "-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 14px;";

    // --- State Variables ---
    private HabitService habitService;
    private UUID editingHabitId = null;
    private final LocalDate today = LocalDate.now();

    @FXML
    public void initialize() {
        this.habitService = new HabitService();

        // 1. Configure spin controls
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0));
        minutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        pastCompletionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));

        // 2. Set default values
        startDatePicker.setValue(today);
        updatePastCompletionsSpinnerState(today);

        // 3. Attach listeners for real-time input validations
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateFormInputsRealTime());
        hoursSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateFormInputsRealTime());
        minutesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateFormInputsRealTime());
        
        startDatePicker.valueProperty().addListener((obs, oldVal, newDate) -> {
            updatePastCompletionsSpinnerState(newDate);
            validateFormInputsRealTime();
        });

        // Set up toggle buttons change listeners
        List<ToggleButton> dayButtons = Arrays.asList(btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun);
        for (ToggleButton btn : dayButtons) {
            btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updatePastCompletionsSpinnerState(startDatePicker.getValue());
                validateFormInputsRealTime();
            });
        }

        // 4. Load initial dashboard
        refreshDashboard();
        validateFormInputsRealTime();
    }

    /**
     * Controls the enablement and maximum value of the "Completed Days in Past" spinner
     * based on the start date and week days schedule.
     */
    private void updatePastCompletionsSpinnerState(LocalDate selectedDate) {
        if (selectedDate == null) {
            pastCompletionContainer.setDisable(true);
            return;
        }

        if (selectedDate.isBefore(today)) {
            pastCompletionContainer.setDisable(false);
            
            Set<DayOfWeek> selectedDays = getSelectedDaysOfWeek();
            int pastScheduled = getScheduledDaysCountFor(selectedDate, today, selectedDays);
            
            int currentSpinnerVal = pastCompletionSpinner.getValue();
            if (currentSpinnerVal > pastScheduled) {
                currentSpinnerVal = pastScheduled;
            }
            
            pastCompletionSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, pastScheduled, currentSpinnerVal));
            lblPastLimitInfo.setText("Límite: " + pastScheduled + " días (programados transcurridos)");
        } else {
            pastCompletionContainer.setDisable(true);
            pastCompletionSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
            lblPastLimitInfo.setText("Límite: 0 días");
        }
    }

    private int getScheduledDaysCountFor(LocalDate start, LocalDate end, Set<DayOfWeek> days) {
        if (start.isAfter(end) || start.isEqual(end) || days == null || days.isEmpty()) {
            return 0;
        }
        int count = 0;
        LocalDate current = start;
        while (current.isBefore(end)) {
            if (days.contains(current.getDayOfWeek())) {
                count++;
            }
            current = current.plusDays(1);
        }
        return count;
    }

    /**
     * Checks all form inputs in real-time to enable/disable the "Save" button.
     */
    private void validateFormInputsRealTime() {
        String name = nameField.getText();
        int hours = hoursSpinner.getValue() != null ? hoursSpinner.getValue() : 0;
        int minutes = minutesSpinner.getValue() != null ? minutesSpinner.getValue() : 0;
        int totalMinutes = hours * 60 + minutes;
        LocalDate startDate = startDatePicker.getValue();
        Set<DayOfWeek> days = getSelectedDaysOfWeek();

        boolean isValid = name != null && !name.trim().isEmpty()
                && totalMinutes > 0 && totalMinutes <= 1440
                && startDate != null && !startDate.isAfter(today)
                && !days.isEmpty();

        btnSave.setDisable(!isValid);
    }

    private Set<DayOfWeek> getSelectedDaysOfWeek() {
        Set<DayOfWeek> days = new HashSet<>();
        if (btnMon.isSelected()) days.add(DayOfWeek.MONDAY);
        if (btnTue.isSelected()) days.add(DayOfWeek.TUESDAY);
        if (btnWed.isSelected()) days.add(DayOfWeek.WEDNESDAY);
        if (btnThu.isSelected()) days.add(DayOfWeek.THURSDAY);
        if (btnFri.isSelected()) days.add(DayOfWeek.FRIDAY);
        if (btnSat.isSelected()) days.add(DayOfWeek.SATURDAY);
        if (btnSun.isSelected()) days.add(DayOfWeek.SUNDAY);
        return days;
    }

    private void setSelectedDaysOfWeek(Set<DayOfWeek> days) {
        if (days == null) return;
        btnMon.setSelected(days.contains(DayOfWeek.MONDAY));
        btnTue.setSelected(days.contains(DayOfWeek.TUESDAY));
        btnWed.setSelected(days.contains(DayOfWeek.WEDNESDAY));
        btnThu.setSelected(days.contains(DayOfWeek.THURSDAY));
        btnFri.setSelected(days.contains(DayOfWeek.FRIDAY));
        btnSat.setSelected(days.contains(DayOfWeek.SATURDAY));
        btnSun.setSelected(days.contains(DayOfWeek.SUNDAY));
    }

    /**
     * Calculates statistics and rebuilds the habit list cards.
     */
    private void refreshDashboard() {
        List<Habit> habits = habitService.getAllHabits();

        // 1. Update overall statistics
        lblActiveCount.setText(String.valueOf(habits.size()));

        int todayCommittedMinutes = 0;
        for (Habit h : habits) {
            if (h.isActiveOnDate(today)) {
                todayCommittedMinutes += h.getDailyDurationMinutes();
            }
        }
        int h = todayCommittedMinutes / 60;
        int m = todayCommittedMinutes % 60;
        lblCommittedHours.setText(h + "h " + m + "m");

        int totalScheduledDays = 0;
        int totalCompletions = 0;
        for (Habit habit : habits) {
            totalScheduledDays += habit.getTotalScheduledDaysCount(today);
            totalCompletions += habit.getTotalCompletionsCount(today);
        }
        if (totalScheduledDays == 0) {
            lblCompletionRate.setText("0.0%");
        } else {
            double rate = (double) totalCompletions / totalScheduledDays;
            lblCompletionRate.setText(String.format(Locale.US, "%.1f%%", rate * 100));
        }

        // 2. Clear and rebuild the habit list cards
        habitsContainer.getChildren().clear();
        if (habits.isEmpty()) {
            Label placeholder = new Label("No hay hábitos registrados. ¡Crea uno a la derecha!");
            placeholder.setStyle(STYLE_PLACEHOLDER);
            habitsContainer.getChildren().add(placeholder);
        } else {
            for (Habit habit : habits) {
                habitsContainer.getChildren().add(createHabitCard(habit));
            }
        }
    }

    /**
     * Dynamically builds a card component for a habit – light theme.
     */
    private VBox createHabitCard(Habit habit) {
        VBox card = new VBox(12.0);
        card.getStyleClass().add("bg-card");

        // Row 1: Habit Name + status badge
        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER_LEFT);
        
        Label lblName = new Label(habit.getName());
        lblName.setStyle(STYLE_HABIT_NAME);
        HBox.setHgrow(lblName, Priority.ALWAYS);

        // Completion indicator for today
        Label lblTodayStatus = new Label();
        if (habit.isActiveOnDate(today)) {
            boolean completedToday = habit.isCompletedOn(today);
            lblTodayStatus.setText(completedToday ? "COMPLETADO HOY" : "PENDIENTE HOY");
            lblTodayStatus.setStyle(completedToday ? STYLE_BADGE_COMPLETED : STYLE_BADGE_PENDING);
        } else {
            lblTodayStatus.setText("NO PROGRAMADO HOY");
            lblTodayStatus.setStyle(STYLE_BADGE_INACTIVE);
        }

        row1.getChildren().addAll(lblName, lblTodayStatus);

        // Row 2: Metadata
        int hours = habit.getDailyDurationMinutes() / 60;
        int mins  = habit.getDailyDurationMinutes() % 60;
        String durationStr = (hours > 0 ? hours + "h " : "") + mins + "m/día";
        
        int comps  = habit.getTotalCompletionsCount(today);
        int scheds = habit.getTotalScheduledDaysCount(today);
        double rate = habit.getCompletionRate(today);
        
        Label lblMeta = new Label(String.format(
                "⏱ %s  •  📅 Desde: %s  •  Progreso: %d/%d días (%.1f%%)",
                durationStr, habit.getStartDate().toString(), comps, scheds, rate * 100));
        lblMeta.setStyle(STYLE_META_LABEL);

        // Row 3: Progress Bar
        ProgressBar progressBar = new ProgressBar(rate);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        // Row 4: History checklist (last 5 active days)
        VBox historySection = new VBox(6.0);
        Label lblHistoryTitle = new Label("HISTORIAL DE LOS ÚLTIMOS 5 DÍAS:");
        lblHistoryTitle.setStyle(STYLE_HISTORY_TITLE);
        
        HBox historyGrid = new HBox(12.0);
        historyGrid.setAlignment(Pos.CENTER_LEFT);
        
        boolean hasPastChecklist = false;
        for (int i = 4; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            if (habit.isActiveOnDate(date)) {
                hasPastChecklist = true;
                
                VBox dayCol = new VBox(4.0);
                dayCol.setAlignment(Pos.CENTER);
                dayCol.setMinWidth(48.0);
                
                String labelText = getDayLabel(date);
                Label lblDay = new Label(labelText);
                // Resalta "Hoy" con teal
                lblDay.setStyle(date.isEqual(today) ? STYLE_TODAY_LABEL : STYLE_DAY_LABEL);
                
                CheckBox chk = new CheckBox();
                chk.getStyleClass().add("check-item");
                chk.setSelected(habit.isCompletedOn(date));
                chk.setOnAction(e -> {
                    habitService.toggleCompletion(habit.getId(), date);
                    refreshDashboard();
                });
                
                dayCol.getChildren().addAll(lblDay, chk);
                historyGrid.getChildren().add(dayCol);
            }
        }
        if (!hasPastChecklist) {
            Label lblNoDays = new Label("No hay días programados en el período de 5 días.");
            lblNoDays.setStyle(STYLE_NO_DAYS_LABEL);
            historyGrid.getChildren().add(lblNoDays);
        }
        historySection.getChildren().addAll(lblHistoryTitle, historyGrid);

        // Row 5: Action buttons
        HBox row5 = new HBox(10.0);
        row5.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnEdit = new Button("Editar");
        btnEdit.getStyleClass().add("btn-secondary");
        btnEdit.setStyle("-fx-padding: 6px 14px; -fx-font-size: 12px;");
        btnEdit.setOnAction(e -> loadHabitIntoForm(habit));
        
        Button btnDelete = new Button("Eliminar");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setOnAction(e -> handleDeleteHabit(habit));

        row5.getChildren().addAll(btnEdit, btnDelete);

        card.getChildren().addAll(row1, lblMeta, progressBar, historySection, row5);
        return card;
    }

    private String getDayLabel(LocalDate date) {
        if (date.isEqual(today)) {
            return "Hoy";
        } else if (date.isEqual(today.minusDays(1))) {
            return "Ayer";
        } else {
            String name = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

    // --- Action Handlers ---

    private void loadHabitIntoForm(Habit habit) {
        editingHabitId = habit.getId();
        lblFormTitle.setText("Editar Hábito");
        btnSave.setText("Actualizar Hábito");

        nameField.setText(habit.getName());
        hoursSpinner.getValueFactory().setValue(habit.getDailyDurationMinutes() / 60);
        minutesSpinner.getValueFactory().setValue(habit.getDailyDurationMinutes() % 60);
        startDatePicker.setValue(habit.getStartDate());
        
        Platform.runLater(() -> {
            if (habit.getStartDate().isBefore(today)) {
                pastCompletionSpinner.getValueFactory().setValue(habit.getCompletedDaysInPast());
            }
        });

        setSelectedDaysOfWeek(habit.getDaysOfWeek());
        clearFeedback();
        validateFormInputsRealTime();
    }

    @FXML
    private void handleSaveHabit() {
        String name       = nameField.getText();
        int hours         = hoursSpinner.getValue()   != null ? hoursSpinner.getValue()   : 0;
        int minutes       = minutesSpinner.getValue() != null ? minutesSpinner.getValue() : 0;
        int totalMinutes  = hours * 60 + minutes;
        LocalDate startDate = startDatePicker.getValue();
        int completedPast = pastCompletionSpinner.getValue() != null ? pastCompletionSpinner.getValue() : 0;
        Set<DayOfWeek> days = getSelectedDaysOfWeek();

        Habit habit;
        if (editingHabitId == null) {
            // Creating a new habit
            habit = new Habit(name, startDate, totalMinutes, days, completedPast);
        } else {
            // Editing an existing habit: create a temporary clone with updated values
            Habit original = habitService.getHabitById(editingHabitId);
            if (original == null) {
                showFeedback("Error: El hábito a editar ya no existe.", true);
                return;
            }
            
            // Create a temporary clone to validate BEFORE modifying the original
            habit = new Habit(name, startDate, totalMinutes, days, completedPast);
            habit.setId(editingHabitId);
            habit.setCreatedAt(original.getCreatedAt());
            habit.setHistory(new HashMap<>(original.getHistory()));
        }

        try {
            if (editingHabitId == null) {
                habitService.addHabit(habit);
                showFeedback("¡Hábito '" + name + "' creado exitosamente!", false);
            } else {
                // Validation happens inside updateHabit before any changes
                habitService.updateHabit(habit);
                showFeedback("¡Hábito actualizado exitosamente!", false);
            }
            handleClearForm();
            refreshDashboard();
        } catch (ValidationException e) {
            showFeedback("Error: " + e.getMessage(), true);
        }
    }

    @FXML
    private void handleClearForm() {
        editingHabitId = null;
        lblFormTitle.setText("Crear Nuevo Hábito");
        btnSave.setText("Guardar Hábito");

        nameField.clear();
        hoursSpinner.getValueFactory().setValue(0);
        minutesSpinner.getValueFactory().setValue(30);
        startDatePicker.setValue(today);
        pastCompletionSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
        
        btnMon.setSelected(false);
        btnTue.setSelected(false);
        btnWed.setSelected(false);
        btnThu.setSelected(false);
        btnFri.setSelected(false);
        btnSat.setSelected(false);
        btnSun.setSelected(false);

        // De-selected feedback is kept unless specifically cleared, let's keep it clean
        validateFormInputsRealTime();
    }

    private void handleDeleteHabit(Habit habit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Eliminar hábito");
        alert.setContentText(
                "¿Está seguro de que desea eliminar el hábito '" + habit.getName() + "'?");
        
        // Aplicar hoja de estilos al diálogo de confirmación
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/views/styles.css").toExternalForm());
        // Usa bg-panel para que el diálogo herede el fondo blanco del tema claro
        dialogPane.getStyleClass().add("bg-panel");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            habitService.deleteHabit(habit.getId());
            showFeedback("Hábito eliminado.", false);
            refreshDashboard();
            if (editingHabitId != null && editingHabitId.equals(habit.getId())) {
                handleClearForm();
            }
        }
    }

    // --- Feedback Helpers ---

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackLabel.getStyleClass().setAll(isError ? "error-msg" : "success-msg");
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
        feedbackLabel.getStyleClass().clear();
    }
}