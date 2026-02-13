package com.travelxp.controller;

import com.travelxp.model.Booking;
import com.travelxp.model.User;
import com.travelxp.model.UserAchievement;
import com.travelxp.service.BookingService;
import com.travelxp.service.GamificationService;
import com.travelxp.service.UserService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label xpLabel;

    @FXML
    private ProgressBar xpProgressBar;

    @FXML
    private VBox achievementsBox;

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> propertyColumn;

    @FXML
    private TableColumn<Booking, String> datesColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private Button browseButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private StageManager stageManager;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = LoginController.getCurrentUser();
        if (currentUser != null) {
            loadDashboard();
        }
    }

    private void loadDashboard() {
        currentUser = userService.getUserById(currentUser.getId()).orElse(currentUser);

        welcomeLabel.setText("Welcome back, " + currentUser.getFullName() + "!");
        levelLabel.setText("Level: " + currentUser.getLevel().getLevelNumber() +
                          " - " + currentUser.getLevel().getLevelName() + " " +
                          currentUser.getLevel().getBadgeIcon());
        xpLabel.setText(currentUser.getExperiencePoints() + " XP");

        updateXPProgress();
        loadAchievements();
        loadRecentBookings();
    }

    private void updateXPProgress() {
        int currentXP = currentUser.getExperiencePoints();
        int currentLevelXP = currentUser.getLevel().getXpRequired();
        int nextLevelNumber = currentUser.getLevel().getLevelNumber() + 1;
        int nextLevelXP = userService.getUserById(currentUser.getId())
                .map(User::getLevel)
                .map(level -> currentLevelXP + 500)
                .orElse(currentLevelXP + 500);

        double progress = (double) (currentXP - currentLevelXP) / (nextLevelXP - currentLevelXP);
        xpProgressBar.setProgress(Math.min(progress, 1.0));
    }

    private void loadAchievements() {
        achievementsBox.getChildren().clear();
        List<UserAchievement> achievements = gamificationService.getUserAchievements(currentUser);

        for (UserAchievement ua : achievements) {
            Label achievementLabel = new Label(ua.getAchievement().getIcon() + " " +
                                              ua.getAchievement().getName());
            achievementLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
            achievementsBox.getChildren().add(achievementLabel);
        }

        if (achievements.isEmpty()) {
            Label noAchievements = new Label("No achievements yet. Start booking to earn them!");
            achievementsBox.getChildren().add(noAchievements);
        }
    }

    private void loadRecentBookings() {
        List<Booking> bookings = bookingService.getBookingsByGuest(currentUser);
        ObservableList<Booking> bookingList = FXCollections.observableArrayList(bookings);

        propertyColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getProperty().getTitle()));

        datesColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCheckInDate() + " to " +
                cellData.getValue().getCheckOutDate()));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        bookingsTable.setItems(bookingList);
    }

    @FXML
    private void handleBrowseProperties(ActionEvent event) {
        stageManager.switchScene(FXMLView.PROPERTY_LIST);
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        stageManager.switchScene(FXMLView.PROFILE);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        LoginController.setCurrentUser(null);
        stageManager.switchScene(FXMLView.LOGIN);
    }
}
