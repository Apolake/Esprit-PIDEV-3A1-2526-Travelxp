package com.travelxp.controller;

import com.travelxp.model.Activity;
import com.travelxp.model.Booking;
import com.travelxp.model.Trip;
import com.travelxp.model.TripBooking;
import com.travelxp.model.TripMilestone;
import com.travelxp.model.User;
import com.travelxp.service.BookingService;
import com.travelxp.service.TripService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class TripController implements Initializable {

    @FXML
    private TextField tripNameField;

    @FXML
    private DatePicker tripStartDatePicker;

    @FXML
    private DatePicker tripEndDatePicker;

    @FXML
    private Button createTripButton;

    @FXML
    private ListView<Trip> tripsListView;

    @FXML
    private Label tripTitleLabel;

    @FXML
    private Label tripDateLabel;

    @FXML
    private Label tripStatusLabel;

    @FXML
    private Label tripXpLabel;

    @FXML
    private ComboBox<Booking> bookingSelector;

    @FXML
    private Button addBookingButton;

    @FXML
    private TableView<TripBooking> tripBookingsTable;

    @FXML
    private TableColumn<TripBooking, String> bookingPropertyColumn;

    @FXML
    private TableColumn<TripBooking, String> bookingDatesColumn;

    @FXML
    private TableColumn<TripBooking, String> bookingStatusColumn;

    @FXML
    private TextField activityTitleField;

    @FXML
    private DatePicker activityDatePicker;

    @FXML
    private TextField activityLocationField;

    @FXML
    private Spinner<Integer> activityXpSpinner;

    @FXML
    private TextField activityCostField;

    @FXML
    private TextArea activityDescriptionArea;

    @FXML
    private Button addActivityButton;

    @FXML
    private TableView<Activity> activitiesTable;

    @FXML
    private TableColumn<Activity, String> activityTitleColumn;

    @FXML
    private TableColumn<Activity, LocalDate> activityDateColumn;

    @FXML
    private TableColumn<Activity, String> activityLocationColumn;

    @FXML
    private TableColumn<Activity, Integer> activityXpColumn;

    @FXML
    private TextField milestoneTitleField;

    @FXML
    private TextArea milestoneDescriptionArea;

    @FXML
    private Spinner<Integer> milestoneXpSpinner;

    @FXML
    private Button addMilestoneButton;

    @FXML
    private TableView<TripMilestone> milestonesTable;

    @FXML
    private TableColumn<TripMilestone, String> milestoneTitleColumn;

    @FXML
    private TableColumn<TripMilestone, String> milestoneStatusColumn;

    @FXML
    private TableColumn<TripMilestone, Integer> milestoneXpColumn;

    @FXML
    private Button completeMilestoneButton;

    @Autowired
    private TripService tripService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private StageManager stageManager;

    private User currentUser;
    private Trip selectedTrip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = LoginController.getCurrentUser();
        setupControls();
        loadTrips();
    }

    private void setupControls() {
        activityXpSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 10));
        milestoneXpSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 10));

        tripsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldTrip, newTrip) -> {
            selectedTrip = newTrip;
            refreshTripDetails();
        });

        tripsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Trip trip, boolean empty) {
                super.updateItem(trip, empty);
                if (empty || trip == null) {
                    setText(null);
                } else {
                    setText(trip.getTripName() + " (" + trip.getStartDate() + " → " + trip.getEndDate() + ")");
                }
            }
        });

        bookingPropertyColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getBooking().getProperty().getTitle()));
        bookingDatesColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getBooking().getCheckInDate() + " to " +
                cellData.getValue().getBooking().getCheckOutDate()));
        bookingStatusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getBooking().getStatus()));

        activityTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        activityDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getActivityDate()));
        activityLocationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getLocation() != null ? cellData.getValue().getLocation() : ""));
        activityXpColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getXpReward()));

        milestoneTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        milestoneStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                Boolean.TRUE.equals(cellData.getValue().getCompleted()) ? "Done" : "Pending"));
        milestoneXpColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getXpReward()));

        bookingSelector.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Booking booking, boolean empty) {
                super.updateItem(booking, empty);
                if (empty || booking == null) {
                    setText(null);
                } else {
                    setText(booking.getProperty().getTitle() + " | " + booking.getCheckInDate() + " → " + booking.getCheckOutDate());
                }
            }
        });
        bookingSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Booking booking, boolean empty) {
                super.updateItem(booking, empty);
                if (empty || booking == null) {
                    setText(null);
                } else {
                    setText(booking.getProperty().getTitle() + " | " + booking.getCheckInDate() + " → " + booking.getCheckOutDate());
                }
            }
        });
    }

    private void loadTrips() {
        if (currentUser == null) {
            return;
        }
        List<Trip> trips = tripService.getTripsForUser(currentUser);
        tripsListView.setItems(FXCollections.observableArrayList(trips));
        if (!trips.isEmpty()) {
            tripsListView.getSelectionModel().selectFirst();
        }
    }

    private void refreshTripDetails() {
        if (selectedTrip == null) {
            clearTripDetails();
            return;
        }

        selectedTrip = tripService.getTripForUser(selectedTrip.getId(), currentUser);
        tripTitleLabel.setText(selectedTrip.getTripName());
        tripDateLabel.setText(selectedTrip.getStartDate() + " to " + selectedTrip.getEndDate());
        tripStatusLabel.setText(selectedTrip.getStatus());
        tripXpLabel.setText(selectedTrip.getTotalXpEarned() + " XP earned");

        List<TripBooking> tripBookings = tripService.getTripBookings(selectedTrip.getId(), currentUser);
        tripBookingsTable.setItems(FXCollections.observableArrayList(tripBookings));

        List<Activity> activities = tripService.getActivities(selectedTrip.getId(), currentUser);
        activitiesTable.setItems(FXCollections.observableArrayList(activities));

        List<TripMilestone> milestones = tripService.getMilestones(selectedTrip.getId(), currentUser);
        milestonesTable.setItems(FXCollections.observableArrayList(milestones));

        populateAvailableBookings(tripBookings);
    }

    private void clearTripDetails() {
        tripTitleLabel.setText("No trip selected");
        tripDateLabel.setText("");
        tripStatusLabel.setText("");
        tripXpLabel.setText("");
        tripBookingsTable.getItems().clear();
        activitiesTable.getItems().clear();
        milestonesTable.getItems().clear();
        bookingSelector.getItems().clear();
    }

    private void populateAvailableBookings(List<TripBooking> tripBookings) {
        List<Long> existingBookingIds = tripBookings.stream()
                .map(tb -> tb.getBooking().getId())
                .collect(Collectors.toList());

        List<Booking> userBookings = bookingService.getBookingsByGuest(currentUser);
        List<Booking> available = userBookings.stream()
                .filter(b -> !existingBookingIds.contains(b.getId()))
                .collect(Collectors.toList());

        bookingSelector.setItems(FXCollections.observableArrayList(available));
        if (!available.isEmpty()) {
            bookingSelector.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleCreateTrip(ActionEvent event) {
        try {
            String name = tripNameField.getText();
            LocalDate start = tripStartDatePicker.getValue();
            LocalDate end = tripEndDatePicker.getValue();

            if (name == null || name.isBlank() || start == null || end == null) {
                showAlert("Please fill trip name and dates");
                return;
            }

            Trip trip = tripService.createTrip(currentUser, name.trim(), start, end);
            tripsListView.getItems().add(trip);
            tripsListView.getSelectionModel().select(trip);
            clearTripForm();
        } catch (Exception ex) {
            showAlert("Could not create trip: " + ex.getMessage());
        }
    }

    private void clearTripForm() {
        tripNameField.clear();
        tripStartDatePicker.setValue(null);
        tripEndDatePicker.setValue(null);
    }

    @FXML
    private void handleAddBooking(ActionEvent event) {
        if (selectedTrip == null) {
            showAlert("Select a trip first");
            return;
        }
        Booking booking = bookingSelector.getSelectionModel().getSelectedItem();
        if (booking == null) {
            showAlert("No booking selected");
            return;
        }
        try {
            tripService.addBookingToTrip(selectedTrip.getId(), booking.getId(), currentUser);
            refreshTripDetails();
        } catch (Exception ex) {
            showAlert("Could not add booking: " + ex.getMessage());
        }
    }

    @FXML
    private void handleAddActivity(ActionEvent event) {
        if (selectedTrip == null) {
            showAlert("Select a trip first");
            return;
        }

        String title = activityTitleField.getText();
        LocalDate date = activityDatePicker.getValue();
        if (title == null || title.isBlank() || date == null) {
            showAlert("Activity needs a title and date");
            return;
        }

        String location = activityLocationField.getText();
        String costText = activityCostField.getText();
        BigDecimal cost = BigDecimal.ZERO;
        if (costText != null && !costText.isBlank()) {
            try {
                cost = new BigDecimal(costText.trim());
            } catch (NumberFormatException e) {
                showAlert("Invalid cost value");
                return;
            }
        }

        int xp = activityXpSpinner.getValue();
        String description = activityDescriptionArea.getText();

        try {
            tripService.addActivity(selectedTrip.getId(), currentUser, title.trim(), description, date,
                    location, cost, xp);
            refreshTripDetails();
            clearActivityForm();
        } catch (Exception ex) {
            showAlert("Could not add activity: " + ex.getMessage());
        }
    }

    private void clearActivityForm() {
        activityTitleField.clear();
        activityDatePicker.setValue(null);
        activityLocationField.clear();
        activityCostField.clear();
        activityXpSpinner.getValueFactory().setValue(0);
        activityDescriptionArea.clear();
    }

    @FXML
    private void handleAddMilestone(ActionEvent event) {
        if (selectedTrip == null) {
            showAlert("Select a trip first");
            return;
        }

        String title = milestoneTitleField.getText();
        if (title == null || title.isBlank()) {
            showAlert("Milestone needs a title");
            return;
        }

        int xp = milestoneXpSpinner.getValue();
        String description = milestoneDescriptionArea.getText();

        try {
            tripService.addMilestone(selectedTrip.getId(), currentUser, title.trim(), description, xp);
            refreshTripDetails();
            clearMilestoneForm();
        } catch (Exception ex) {
            showAlert("Could not add milestone: " + ex.getMessage());
        }
    }

    private void clearMilestoneForm() {
        milestoneTitleField.clear();
        milestoneDescriptionArea.clear();
        milestoneXpSpinner.getValueFactory().setValue(0);
    }

    @FXML
    private void handleCompleteMilestone(ActionEvent event) {
        if (selectedTrip == null) {
            showAlert("Select a trip first");
            return;
        }

        TripMilestone milestone = milestonesTable.getSelectionModel().getSelectedItem();
        if (milestone == null) {
            showAlert("Select a milestone to complete");
            return;
        }

        try {
            tripService.completeMilestone(selectedTrip.getId(), milestone.getId(), currentUser);
            refreshTripDetails();
        } catch (Exception ex) {
            showAlert("Could not complete milestone: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stageManager.switchScene(FXMLView.DASHBOARD);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Trip Manager");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
