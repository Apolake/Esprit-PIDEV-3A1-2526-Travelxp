package com.travelxp.controller;

import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ShellController {

    @FXML
    private StackPane contentContainer;

    @Autowired
    private StageManager stageManager;

    public void setContent(Parent content) {
        if (contentContainer != null) {
            contentContainer.getChildren().setAll(content);
        }
    }

    @FXML
    private void handleHome() {
        stageManager.switchScene(FXMLView.DASHBOARD);
    }

    @FXML
    private void handleBrowse() {
        stageManager.switchScene(FXMLView.PROPERTY_LIST);
    }

    @FXML
    private void handleTrips() {
        stageManager.switchScene(FXMLView.TRIPS);
    }

    @FXML
    private void handleBookings() {
        stageManager.switchScene(FXMLView.BOOKING);
    }

    @FXML
    private void handleComments() {
        stageManager.switchScene(FXMLView.REVIEW_COMMENTS);
    }

    @FXML
    private void handleProfile() {
        stageManager.switchScene(FXMLView.PROFILE);
    }

    @FXML
    private void handleAdmin() {
        stageManager.switchScene(FXMLView.ADMIN);
    }

    @FXML
    private void handleLogout() {
        stageManager.switchScene(FXMLView.LOGIN);
    }
}
