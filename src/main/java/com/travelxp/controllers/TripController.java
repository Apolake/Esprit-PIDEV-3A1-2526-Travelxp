package com.travelxp.controllers;

import com.travelxp.Main;
import com.travelxp.models.Activity;
import com.travelxp.models.Trip;
import com.travelxp.services.ActivityService;
import com.travelxp.services.TripService;
import com.travelxp.services.UserService;
import com.travelxp.utils.ThemeManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TripController {

    @FXML private Label pageTitle;
    @FXML private FlowPane userTripsContainer;
    @FXML private ScrollPane userScrollPane;
    @FXML private GridPane adminForm;
    
    @FXML private Button tripsNavBtn;
    @FXML private Button myTripsNavBtn;

    // Admin Table
    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, Long> idCol;
    @FXML private TableColumn<Trip, String> nameCol;
    @FXML private TableColumn<Trip, String> originCol;
    @FXML private TableColumn<Trip, String> destinationCol;
    @FXML private TableColumn<Trip, LocalDate> startDateCol;
    @FXML private TableColumn<Trip, LocalDate> endDateCol;
    @FXML private TableColumn<Trip, String> statusCol;
    @FXML private TableColumn<Trip, Integer> xpCol;

    // Admin Fields
    @FXML private TextField nameField;
    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField budgetField;
    @FXML private ComboBox<String> statusCombo;

    private final TripService tripService = new TripService();
    private final ActivityService activityService = new ActivityService();
    private final UserService userService = new UserService();
    private final ObservableList<Trip> tripData = FXCollections.observableArrayList();

    private boolean isMyTripsMode = false;

    @FXML
    public void initialize() {
        boolean isAdmin = Main.getSession().getUser().getRole().equals("ADMIN");

        if (idCol != null) {
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameCol.setCellValueFactory(new PropertyValueFactory<>("tripName"));
            originCol.setCellValueFactory(new PropertyValueFactory<>("origin"));
            destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
            startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
            endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            xpCol.setCellValueFactory(new PropertyValueFactory<>("totalXpEarned"));

            tripTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) populateForm(newVal);
            });
        }

        if (statusCombo != null) {
            statusCombo.setItems(FXCollections.observableArrayList("PLANNED", "ONGOING", "COMPLETED", "CANCELLED"));
        }

        if (adminForm != null) {
            adminForm.setVisible(isAdmin);
            adminForm.setManaged(isAdmin);
        }
        if (userScrollPane != null) {
            userScrollPane.setVisible(!isAdmin);
            userScrollPane.setManaged(!isAdmin);
        }

        loadTrips();
    }

    public void setMyTripsMode(boolean mode) {
        this.isMyTripsMode = mode;
        if (pageTitle != null) pageTitle.setText(mode ? "My Trips" : "Browse Trips");
        updateNavStyles();
        loadTrips();
    }

    private void updateNavStyles() {
        if (tripsNavBtn != null) {
            tripsNavBtn.getStyleClass().remove("accent");
            if (!tripsNavBtn.getStyleClass().contains("flat")) tripsNavBtn.getStyleClass().add("flat");
        }
        if (myTripsNavBtn != null) {
            myTripsNavBtn.getStyleClass().remove("accent");
            if (!myTripsNavBtn.getStyleClass().contains("flat")) myTripsNavBtn.getStyleClass().add("flat");
        }
        
        if (isMyTripsMode && myTripsNavBtn != null) {
            myTripsNavBtn.getStyleClass().remove("flat");
            myTripsNavBtn.getStyleClass().add("accent");
        } else if (!isMyTripsMode && tripsNavBtn != null) {
            tripsNavBtn.getStyleClass().remove("flat");
            tripsNavBtn.getStyleClass().add("accent");
        }
    }

    private void loadTrips() {
        try {
            boolean isAdmin = Main.getSession().getUser().getRole().equals("ADMIN");
            List<Trip> trips;
            
            if (isAdmin) {
                trips = tripService.getAllTrips();
            } else if (isMyTripsMode) {
                trips = tripService.getTripsByUserId(Main.getSession().getUser().getId());
            } else {
                trips = tripService.getAllTrips().stream()
                        .filter(t -> t.getUserId() == null)
                        .toList();
            }

            tripData.setAll(trips);
            if (tripTable != null) tripTable.setItems(tripData);

            if (userTripsContainer != null) {
                userTripsContainer.getChildren().clear();
                for (Trip t : trips) {
                    userTripsContainer.getChildren().add(createTripCard(t));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Load Failed", e.getMessage());
        }
    }

    private VBox createTripCard(Trip t) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(350);
        card.setPadding(new Insets(20));

        Label nameLab = new Label(t.getTripName());
        nameLab.getStyleClass().add("title-4");
        
        Label routeLab = new Label(t.getOrigin() + " ➔ " + t.getDestination());
        routeLab.getStyleClass().add("text-muted");

        // For Browse mode, show template budget as the entry fee
        double displayCost = isMyTripsMode ? t.getTotalExpenses() : (t.getBudgetAmount() != null ? t.getBudgetAmount() : 0.0);
        String prefix = isMyTripsMode ? "My Expenses: $" : "Entry Cost: $";
        
        Label costLab = new Label(prefix + String.format("%.2f", displayCost));
        costLab.getStyleClass().add("accent");
        costLab.setStyle("-fx-font-weight: bold;");

        Button actionBtn = new Button();
        if (isMyTripsMode) {
            actionBtn.setText("Manage Activities");
            actionBtn.getStyleClass().add("secondary-button");
            actionBtn.setOnAction(e -> handleManageActivities(t));
        } else {
            actionBtn.setText("Participate");
            actionBtn.getStyleClass().add("primary-button");
            actionBtn.setOnAction(e -> handleParticipate(t));
        }
        actionBtn.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(nameLab, routeLab, costLab, actionBtn);
        return card;
    }

    private void handleParticipate(Trip template) {
        double cost = template.getBudgetAmount() != null ? template.getBudgetAmount() : 0.0;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Participate");
        confirm.setHeaderText("Join " + template.getTripName() + " for $" + String.format("%.2f", cost) + "?");
        confirm.setContentText("The entry cost will be deducted from your balance.");
        
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    if (Main.getSession().getUser().getBalance() < cost) {
                        showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Low Balance", "You need $" + cost + " to join this trip.");
                        return;
                    }

                    // Deduct Entry Fee
                    userService.updateBalance(Main.getSession().getUser().getId(), -cost);
                    Main.getSession().setUser(userService.getUserById(Main.getSession().getUser().getId()));

                    // Create personal copy
                    Trip myTrip = new Trip();
                    myTrip.setUserId((long) Main.getSession().getUser().getId());
                    myTrip.setParentId(template.getId());
                    myTrip.setTripName(template.getTripName());
                    myTrip.setOrigin(template.getOrigin());
                    myTrip.setDestination(template.getDestination());
                    myTrip.setStartDate(template.getStartDate());
                    myTrip.setEndDate(template.getEndDate());
                    myTrip.setStatus("PLANNED");
                    myTrip.setTotalExpenses(cost); // Initial expense is the entry fee
                    myTrip.setTotalXpEarned(0);
                    myTrip.setBudgetAmount(cost);
                    
                    tripService.addTrip(myTrip);
                    
                    // Clone Activities
                    List<Activity> templateActivities = activityService.getActivitiesByTripId(template.getId());
                    for (Activity ta : templateActivities) {
                        Activity ma = new Activity();
                        ma.setTripId(myTrip.getId());
                        ma.setTitle(ta.getTitle());
                        ma.setType(ta.getType());
                        ma.setDescription(ta.getDescription());
                        ma.setActivityDate(ta.getActivityDate());
                        ma.setCostAmount(ta.getCostAmount());
                        ma.setXpEarned(ta.getXpEarned());
                        ma.setStatus("PLANNED");
                        activityService.addActivity(ma);
                    }
                    
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Joined Trip", "You are now a participant in " + template.getTripName() + "!");
                    handleMyTrips(null);
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to join", e.getMessage());
                }
            }
        });
    }

    private void handleManageActivities(Trip trip) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Activities: " + trip.getTripName());
        dialog.setHeaderText("Manage your participation in activities.");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(450);
        
        try {
            List<Activity> activities = activityService.getActivitiesByTripId(trip.getId());
            if (activities.isEmpty()) {
                content.getChildren().add(new Label("No activities available for this trip copy."));
            } else {
                for (Activity a : activities) {
                    HBox row = new HBox(10);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    
                    Label info = new Label(a.getTitle() + " ($" + (a.getCostAmount() != null ? a.getCostAmount() : 0.0) + ")");
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    if ("DONE".equals(a.getStatus())) {
                        Label paid = new Label("✓ Participating");
                        paid.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        row.getChildren().addAll(info, spacer, paid);
                    } else {
                        Button payBtn = new Button("Join Activity");
                        payBtn.getStyleClass().add("accent");
                        payBtn.setOnAction(e -> {
                            try {
                                double cost = a.getCostAmount() != null ? a.getCostAmount() : 0.0;
                                if (Main.getSession().getUser().getBalance() < cost) {
                                    showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Low Balance", "You need $" + cost + " to join.");
                                    return;
                                }
                                
                                userService.updateBalance(Main.getSession().getUser().getId(), -cost);
                                Main.getSession().setUser(userService.getUserById(Main.getSession().getUser().getId()));
                                
                                a.setStatus("DONE");
                                activityService.updateActivity(a);
                                
                                trip.setTotalExpenses(trip.getTotalExpenses() + cost);
                                tripService.updateTrip(trip);
                                
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Activity Joined", "Cost added to trip expenses.");
                                dialog.close();
                                loadTrips();
                            } catch (SQLException ex) {
                                showAlert(Alert.AlertType.ERROR, "Error", "Payment Failed", ex.getMessage());
                            }
                        });
                        row.getChildren().addAll(info, spacer, payBtn);
                    }
                    content.getChildren().add(row);
                }
            }
        } catch (SQLException e) {
            content.getChildren().add(new Label("Failed to load activities."));
        }
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void handleViewParticipants() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Selection Required", "Select a trip template first.");
            return;
        }
        
        try {
            List<Trip> participants = tripService.getParticipantsByTripId(selected.getId());
            ListView<String> listView = new ListView<>();
            if (participants.isEmpty()) {
                listView.getItems().add("No participants yet.");
            } else {
                for (Trip pt : participants) {
                    listView.getItems().add("User ID: " + pt.getUserId() + " | Total Spent: $" + String.format("%.2f", pt.getTotalExpenses()));
                }
            }
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Participants: " + selected.getTripName());
            dialog.getDialogPane().setContent(listView);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load participants", e.getMessage());
        }
    }

    @FXML
    private void handleAddTrip() {
        try {
            Trip t = new Trip();
            t.setUserId(null); 
            t.setTripName(nameField.getText());
            t.setOrigin(originField.getText());
            t.setDestination(destinationField.getText());
            t.setStartDate(startDatePicker.getValue());
            t.setEndDate(endDatePicker.getValue());
            t.setStatus(statusCombo.getValue());
            t.setBudgetAmount(Double.parseDouble(budgetField.getText()));
            t.setTotalExpenses(0.0);
            t.setTotalXpEarned(0);
            
            tripService.addTrip(t);
            loadTrips();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Trip Created", "Template added.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Create Failed", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateTrip() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            selected.setTripName(nameField.getText());
            selected.setOrigin(originField.getText());
            selected.setDestination(destinationField.getText());
            selected.setStartDate(startDatePicker.getValue());
            selected.setEndDate(endDatePicker.getValue());
            selected.setStatus(statusCombo.getValue());
            selected.setBudgetAmount(Double.parseDouble(budgetField.getText()));

            tripService.updateTrip(selected);
            loadTrips();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Trip Updated", "Details updated.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTrip() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            tripService.deleteTrip(selected.getId());
            loadTrips();
            clearForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Delete Failed", e.getMessage());
        }
    }

    private void populateForm(Trip t) {
        nameField.setText(t.getTripName());
        originField.setText(t.getOrigin());
        destinationField.setText(t.getDestination());
        startDatePicker.setValue(t.getStartDate());
        endDatePicker.setValue(t.getEndDate());
        statusCombo.setValue(t.getStatus());
        budgetField.setText(t.getBudgetAmount() != null ? String.valueOf(t.getBudgetAmount()) : "0");
    }

    @FXML private void handleClearForm() { clearForm(); }
    private void clearForm() {
        if (nameField != null) nameField.clear();
        if (originField != null) originField.clear();
        if (destinationField != null) destinationField.clear();
        if (startDatePicker != null) startDatePicker.setValue(null);
        if (endDatePicker != null) endDatePicker.setValue(null);
        if (statusCombo != null) statusCombo.setValue("PLANNED");
        if (budgetField != null) budgetField.clear();
        if (tripTable != null) tripTable.getSelectionModel().clearSelection();
    }

    // Navigation
    @FXML private void handleTasks(ActionEvent event) { changeScene(event, "/com/travelxp/views/tasks.fxml"); }
    @FXML private void handleBrowseProperties(ActionEvent event) {
        String fxml = Main.getSession().getUser().getRole().equals("ADMIN") ? "/com/travelxp/views/admin-property-view.fxml" : "/com/travelxp/views/property-view.fxml";
        changeScene(event, fxml);
    }
    @FXML private void handleBrowseTrips(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/travelxp/views/trip-view.fxml"));
            Parent root = loader.load();
            TripController controller = loader.getController();
            controller.setMyTripsMode(false);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            ThemeManager.applyTheme(stage.getScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void handleMyBookings(ActionEvent event) {
        String fxml = Main.getSession().getUser().getRole().equals("ADMIN") ? "/com/travelxp/views/admin-booking-view.fxml" : "/com/travelxp/views/booking-view.fxml";
        changeScene(event, fxml);
    }
    @FXML private void handleMyTrips(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/travelxp/views/trip-view.fxml"));
            Parent root = loader.load();
            TripController controller = loader.getController();
            controller.setMyTripsMode(true);
            Stage stage = (Stage) (event == null ? (pageTitle != null ? pageTitle.getScene().getWindow() : null) : ((Node) event.getSource()).getScene().getWindow());
            if (stage != null) {
                stage.getScene().setRoot(root);
                ThemeManager.applyTheme(stage.getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void handleEditProfile(ActionEvent event) { changeScene(event, "/com/travelxp/views/edit_profile.fxml"); }
    @FXML private void handleChangePassword(ActionEvent event) { changeScene(event, "/com/travelxp/views/change_password.fxml"); }
    @FXML private void handleFeedback(ActionEvent event) { changeScene(event, "/com/travelxp/views/feedback-view.fxml"); }
    
    @FXML private void handleManageProperties(ActionEvent event) { changeScene(event, "/com/travelxp/views/admin-property-view.fxml"); }
    @FXML private void handleManageOffers(ActionEvent event) { changeScene(event, "/com/travelxp/views/offer-view.fxml"); }
    @FXML private void handleManageBookings(ActionEvent event) { changeScene(event, "/com/travelxp/views/admin-booking-view.fxml"); }
    @FXML private void handleManageComments(ActionEvent event) { changeScene(event, "/com/travelxp/views/moderation-view.fxml"); }
    @FXML private void handleManageServices(ActionEvent event) { changeScene(event, "/com/travelxp/views/service-view.fxml"); }
    @FXML private void handleManageTrips(ActionEvent event) { changeScene(event, "/com/travelxp/views/admin-trip-view.fxml"); }
    @FXML private void handleActivities(ActionEvent event) { changeScene(event, "/com/travelxp/views/admin-activity-view.fxml"); }

    @FXML
    private void handleLogout(ActionEvent event) {
        Main.setSession(null);
        changeScene(event, "/com/travelxp/views/login.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        String fxml = Main.getSession().getUser().getRole().equals("ADMIN") ? "/com/travelxp/views/admin_dashboard.fxml" : "/com/travelxp/views/dashboard.fxml";
        changeScene(event, fxml);
    }

    @FXML
    private void toggleTheme(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ThemeManager.toggleTheme(stage.getScene());
    }

    private void changeScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            ThemeManager.applyTheme(stage.getScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(header); alert.setContentText(content);
        alert.showAndWait();
    }
}
