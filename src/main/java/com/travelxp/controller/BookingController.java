package com.travelxp.controller;

import com.travelxp.model.Booking;
import com.travelxp.model.Property;
import com.travelxp.model.User;
import com.travelxp.service.BookingService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

@Controller
public class BookingController implements Initializable {

    @FXML
    private Label propertyLabel;

    @FXML
    private DatePicker checkInPicker;

    @FXML
    private DatePicker checkOutPicker;

    @FXML
    private Spinner<Integer> guestsSpinner;

    @FXML
    private TextArea specialRequestsArea;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button confirmButton;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private StageManager stageManager;

    private static Property selectedProperty;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = LoginController.getCurrentUser();

        if (selectedProperty != null) {
            propertyLabel.setText("Booking: " + selectedProperty.getTitle());

            SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, selectedProperty.getMaxGuests(), 1);
            guestsSpinner.setValueFactory(valueFactory);

            checkInPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
            checkOutPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
        }
    }

    private void updateTotalPrice() {
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();

        if (checkIn != null && checkOut != null && checkOut.isAfter(checkIn)) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            BigDecimal total = selectedProperty.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            totalPriceLabel.setText("Total: $" + total);
        } else {
            totalPriceLabel.setText("Total: $0.00");
        }
    }

    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();
        Integer guests = guestsSpinner.getValue();
        String specialRequests = specialRequestsArea.getText();

        if (checkIn == null || checkOut == null) {
            showAlert("Please select check-in and check-out dates");
            return;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            showAlert("Check-out date must be after check-in date");
            return;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            showAlert("Check-in date cannot be in the past");
            return;
        }

        try {
            Booking booking = bookingService.createBooking(
                selectedProperty,
                currentUser,
                checkIn,
                checkOut,
                guests,
                specialRequests
            );

            showSuccessAlert("Booking confirmed! You earned " + booking.getXpEarned() + " XP!");
            stageManager.switchScene(FXMLView.DASHBOARD);

        } catch (Exception e) {
            showAlert("Booking failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        stageManager.switchScene(FXMLView.PROPERTY_DETAIL);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Booking Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void setSelectedProperty(Property property) {
        selectedProperty = property;
    }
}
