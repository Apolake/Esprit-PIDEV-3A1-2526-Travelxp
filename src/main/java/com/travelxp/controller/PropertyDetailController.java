package com.travelxp.controller;

import com.travelxp.model.Property;
import com.travelxp.model.Review;
import com.travelxp.service.ReviewService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class PropertyDetailController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label amenitiesLabel;

    @FXML
    private VBox reviewsBox;

    @FXML
    private Button bookButton;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private StageManager stageManager;

    private static Property selectedProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedProperty != null) {
            displayPropertyDetails();
            loadReviews();
        }
    }

    private void displayPropertyDetails() {
        titleLabel.setText(selectedProperty.getTitle());
        locationLabel.setText(selectedProperty.getAddress() + ", " +
                             selectedProperty.getCity() + ", " +
                             selectedProperty.getCountry());
        priceLabel.setText("$" + selectedProperty.getPricePerNight() + " per night");
        detailsLabel.setText(selectedProperty.getBedrooms() + " bedrooms • " +
                           selectedProperty.getBathrooms() + " bathrooms • " +
                           "Max " + selectedProperty.getMaxGuests() + " guests");
        descriptionArea.setText(selectedProperty.getDescription());
        amenitiesLabel.setText("Amenities: " + selectedProperty.getAmenities());
    }

    private void loadReviews() {
        reviewsBox.getChildren().clear();
        List<Review> reviews = reviewService.getReviewsByProperty(selectedProperty);

        for (Review review : reviews) {
            VBox reviewCard = new VBox(5);
            reviewCard.setStyle("-fx-border-color: #eee; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");

            String stars = "⭐".repeat(review.getRating());
            Label ratingLabel = new Label(stars + " " + review.getRating() + "/5");
            ratingLabel.setStyle("-fx-font-weight: bold;");

            Label reviewerLabel = new Label("By: " + review.getReviewer().getFullName());
            reviewerLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

            Label commentLabel = new Label(review.getComment());
            commentLabel.setWrapText(true);

            reviewCard.getChildren().addAll(ratingLabel, reviewerLabel, commentLabel);
            reviewsBox.getChildren().add(reviewCard);
        }

        if (reviews.isEmpty()) {
            Label noReviews = new Label("No reviews yet. Be the first to review!");
            reviewsBox.getChildren().add(noReviews);
        }
    }

    @FXML
    private void handleBookNow(ActionEvent event) {
        BookingController.setSelectedProperty(selectedProperty);
        stageManager.switchScene(FXMLView.BOOKING);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stageManager.switchScene(FXMLView.PROPERTY_LIST);
    }

    public static void setSelectedProperty(Property property) {
        selectedProperty = property;
    }

    public static Property getSelectedProperty() {
        return selectedProperty;
    }
}
