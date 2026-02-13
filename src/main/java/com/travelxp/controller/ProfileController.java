package com.travelxp.controller;

import com.travelxp.model.User;
import com.travelxp.model.UserAchievement;
import com.travelxp.service.GamificationService;
import com.travelxp.service.UserService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ProfileController implements Initializable {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label xpLabel;

    @FXML
    private ProgressBar levelProgressBar;

    @FXML
    private VBox achievementsBox;

    @FXML
    private TextArea bioArea;

    @Autowired
    private UserService userService;

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private StageManager stageManager;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = LoginController.getCurrentUser();
        if (currentUser != null) {
            loadProfile();
        }
    }

    private void loadProfile() {
        currentUser = userService.getUserById(currentUser.getId()).orElse(currentUser);

        usernameLabel.setText("@" + currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        levelLabel.setText("Level " + currentUser.getLevel().getLevelNumber() +
                          " - " + currentUser.getLevel().getLevelName() + " " +
                          currentUser.getLevel().getBadgeIcon());
        xpLabel.setText(currentUser.getExperiencePoints() + " XP");
        bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "No bio yet");

        updateLevelProgress();
        loadAchievements();
    }

    private void updateLevelProgress() {
        int currentXP = currentUser.getExperiencePoints();
        int currentLevelXP = currentUser.getLevel().getXpRequired();
        int nextLevelXP = currentLevelXP + 500;

        double progress = (double) (currentXP - currentLevelXP) / (nextLevelXP - currentLevelXP);
        levelProgressBar.setProgress(Math.min(Math.max(progress, 0), 1.0));
    }

    private void loadAchievements() {
        achievementsBox.getChildren().clear();
        List<UserAchievement> achievements = gamificationService.getUserAchievements(currentUser);

        Label header = new Label("Achievements (" + achievements.size() + ")");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        achievementsBox.getChildren().add(header);

        for (UserAchievement ua : achievements) {
            VBox achievementCard = new VBox(5);
            achievementCard.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; " +
                                   "-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-radius: 5;");

            Label nameLabel = new Label(ua.getAchievement().getIcon() + " " +
                                       ua.getAchievement().getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label descLabel = new Label(ua.getAchievement().getDescription());
            descLabel.setStyle("-fx-text-fill: #666;");

            Label xpLabel = new Label("+" + ua.getAchievement().getXpReward() + " XP");
            xpLabel.setStyle("-fx-text-fill: #00a699; -fx-font-weight: bold;");

            achievementCard.getChildren().addAll(nameLabel, descLabel, xpLabel);
            achievementsBox.getChildren().add(achievementCard);
        }

        if (achievements.isEmpty()) {
            Label noAchievements = new Label("No achievements yet. Start your journey!");
            achievementsBox.getChildren().add(noAchievements);
        }
    }

    @FXML
    private void handleUpdateBio(ActionEvent event) {
        currentUser.setBio(bioArea.getText());
        userService.updateUser(currentUser);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Updated");
        alert.setHeaderText(null);
        alert.setContentText("Your bio has been updated successfully!");
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stageManager.switchScene(FXMLView.DASHBOARD);
    }
}
