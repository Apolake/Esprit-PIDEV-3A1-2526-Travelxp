package com.travelxp.controller;

import com.travelxp.model.Property;
import com.travelxp.service.PropertyService;
import com.travelxp.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class PropertyListController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private GridPane propertiesGrid;

    @FXML
    private ScrollPane scrollPane;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private StageManager stageManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProperties();
    }

    private void loadProperties() {
        List<Property> properties = propertyService.getAllProperties();
        displayProperties(properties);
    }

    private void displayProperties(List<Property> properties) {
        propertiesGrid.getChildren().clear();

        int row = 0;
        int col = 0;
        int maxCols = 2;

        for (Property property : properties) {
            VBox propertyCard = createPropertyCard(property);
            propertiesGrid.add(propertyCard, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createPropertyCard(Property property) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 15; " +
                     "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(250);

        Label title = new Label(property.getTitle());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label location = new Label(property.getCity() + ", " + property.getCountry());
        location.setStyle("-fx-text-fill: #666;");

        Label details = new Label(property.getBedrooms() + " beds • " +
                                 property.getBathrooms() + " baths • " +
                                 property.getMaxGuests() + " guests");

        Label price = new Label("$" + property.getPricePerNight() + " / night");
        price.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00a699;");

        Label rating = new Label("⭐ " + String.format("%.1f", property.getAverageRating()));

        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> handleViewProperty(property));

        card.getChildren().addAll(title, location, details, rating, price, viewButton);
        return card;
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchQuery = searchField.getText();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            List<Property> properties = propertyService.getPropertiesByCity(searchQuery);
            displayProperties(properties);
        } else {
            loadProperties();
        }
    }

    private void handleViewProperty(Property property) {
        PropertyDetailController.setSelectedProperty(property);
        stageManager.switchScene(com.travelxp.util.FXMLView.PROPERTY_DETAIL);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stageManager.switchScene(com.travelxp.util.FXMLView.DASHBOARD);
    }
}
