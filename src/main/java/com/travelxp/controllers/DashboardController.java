package com.travelxp.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import com.travelxp.Main;
import com.travelxp.models.Gamification;
import com.travelxp.models.Offer;
import com.travelxp.models.Property;
import com.travelxp.models.Trip;
import com.travelxp.models.User;
import com.travelxp.services.GamificationService;
import com.travelxp.services.PropertyRecommendationService;
import com.travelxp.services.PropertyService;
import com.travelxp.services.StripeService;
import com.travelxp.services.TripService;
import com.travelxp.services.UserService;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ImageView profileImageView;
    @FXML private Label emailLabel;
    @FXML private Label birthdayLabel;
    @FXML private Label bioLabel;
    @FXML private Label balanceLabel;
    @FXML private Label titleLabel;
    @FXML private Label levelLabel;
    @FXML private Label xpLabel;
    @FXML private ProgressBar xpProgressBar;
    @FXML private Pane animatedBg;
    
    @FXML private TilePane propertyShowcase;
    @FXML private ScrollPane propertyScrollPane;
    @FXML private TilePane tripShowcase;
    @FXML private ScrollPane tripScrollPane;
    @FXML private VBox recommendedSection;
    @FXML private HBox recommendedContainer;

    private final UserService userService = new UserService();
    private final GamificationService gamificationService = new GamificationService();
    private final PropertyService propertyService = new PropertyService();
    private final TripService tripService = new TripService();
    private final StripeService stripeService = new StripeService();
    private final PropertyRecommendationService recommendationService = new PropertyRecommendationService();
    private final Random random = new Random();

    @FXML
    public void initialize() {
        // Apply circular clip
        Circle clip = new Circle(60, 60, 60); // Center X, Y, Radius
        profileImageView.setClip(clip);
        
        User user = Main.getSession().getUser();
        if (user != null) {
            updateProfileUI(user);
            updateGamificationUI(user.getId());
        }

        loadRecommendedDeals();
        loadPropertyShowcase();
        loadTripShowcase();
        Platform.runLater(this::startBackgroundAnimation);
    }

    // ═══════════════════════════════════════════════════════
    // Recommended Deals – properties with 30 %+ active offers
    // ═══════════════════════════════════════════════════════

    private void loadRecommendedDeals() {
        if (recommendedContainer == null) return;
        recommendedContainer.getChildren().clear();
        try {
            java.util.Map<Property, Offer> recs = recommendationService.getRecommendationsWithOffers();
            if (recs.isEmpty()) {
                if (recommendedSection != null) {
                    recommendedSection.setVisible(false);
                    recommendedSection.setManaged(false);
                }
                return;
            }
            if (recommendedSection != null) {
                recommendedSection.setVisible(true);
                recommendedSection.setManaged(true);
            }
            for (java.util.Map.Entry<Property, Offer> entry : recs.entrySet()) {
                recommendedContainer.getChildren().add(
                    createRecommendedCard(entry.getKey(), entry.getValue())
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (recommendedSection != null) {
                recommendedSection.setVisible(false);
                recommendedSection.setManaged(false);
            }
        }
    }

    private VBox createRecommendedCard(Property p, Offer offer) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(280);
        card.setMinWidth(280);
        card.setPadding(new Insets(12));
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setStyle("-fx-border-color: #D4AF37; -fx-border-width: 2; -fx-border-radius: 8;");

        Label badge = new Label(offer.getDiscountPercentage().intValue() + "% OFF");
        badge.setStyle("-fx-background-color: #D4AF37; -fx-text-fill: white; -fx-padding: 4 12; "
                + "-fx-font-weight: bold; -fx-background-radius: 12;");

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(140);
        imageContainer.getStyleClass().add("showcase-image-container");
        ImageView iv = new ImageView();
        iv.setFitHeight(140);
        iv.setFitWidth(250);
        iv.setPreserveRatio(true);
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(250, 140);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageContainer.setClip(clip);
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            File imgFile = new File(p.getImages());
            if (imgFile.exists()) {
                iv.setImage(new Image(imgFile.toURI().toString(), true));
            }
        }
        imageContainer.getChildren().add(iv);

        Label title = new Label(p.getTitle());
        title.getStyleClass().add("title-4");
        title.setStyle("-fx-font-size: 15px;");

        Label loc = new Label(p.getCity() + ", " + p.getCountry());
        loc.getStyleClass().add("text-muted");

        Label price = new Label("$" + p.getPricePerNight() + " / night");
        price.setStyle("-fx-font-weight: bold;");

        Label offerTitle = new Label(offer.getTitle());
        offerTitle.setStyle("-fx-text-fill: #D4AF37; -fx-font-style: italic;");

        HBox buttons = new HBox(8);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        Button bookBtn = new Button("Book Deal");
        bookBtn.getStyleClass().add("accent");
        bookBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bookBtn, Priority.ALWAYS);
        bookBtn.setOnAction(e -> handleBrowseProperties(e));

        Button mapBtn = new Button("View on Map");
        mapBtn.getStyleClass().add("flat");
        mapBtn.setOnAction(e -> openPropertyOnMap(p));

        buttons.getChildren().addAll(bookBtn, mapBtn);

        card.getChildren().addAll(badge, imageContainer, title, loc, price, offerTitle, buttons);
        return card;
    }

    // ═══════════════════════════════════════════════════════
    // View on Map – opens OpenStreetMap in the system browser
    // ═══════════════════════════════════════════════════════

    private void openPropertyOnMap(Property p) {
        double lat, lon;
        if (p.getLatitude() != null && p.getLongitude() != null) {
            lat = p.getLatitude();
            lon = p.getLongitude();
        } else {
            // Fall back to geocoding the address on-the-fly
            com.travelxp.services.NominatimGeocodingService geocoder = new com.travelxp.services.NominatimGeocodingService();
            String addr = (p.getAddress() != null ? p.getAddress() + ", " : "") + p.getCity() + ", " + p.getCountry();
            com.travelxp.services.NominatimGeocodingService.GeocodingResult geo = geocoder.geocode(addr);
            if (geo == null) {
                new Alert(Alert.AlertType.WARNING, "Could not determine the property location.").show();
                return;
            }
            lat = geo.getLatitude();
            lon = geo.getLongitude();
        }
        String url = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "#map=16/" + lat + "/" + lon;
        try {
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open browser: " + ex.getMessage()).show();
        }
    }

    private void loadPropertyShowcase() {
        if (propertyShowcase == null) return;
        propertyShowcase.getChildren().clear();
        try {
            List<Property> properties = propertyService.getAllProperties();
            for (Property p : properties) {
                propertyShowcase.getChildren().add(createPropertyCard(p));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTripShowcase() {
        if (tripShowcase == null) return;
        tripShowcase.getChildren().clear();
        try {
            List<Trip> trips = tripService.getAllTrips().stream()
                    .filter(t -> t.getUserId() == null)
                    .limit(6)
                    .toList();
            for (Trip t : trips) {
                tripShowcase.getChildren().add(createTripCard(t));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createPropertyCard(Property p) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(350);
        card.setMinWidth(350);
        card.setMaxWidth(350);
        card.setPadding(new Insets(15));
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(180);
        imageContainer.setMaxHeight(180);
        imageContainer.getStyleClass().add("showcase-image-container");
        
        ImageView iv = new ImageView();
        iv.setFitHeight(180);
        iv.setFitWidth(320);
        iv.setPreserveRatio(true);
        
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(320, 180);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageContainer.setClip(clip);

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            File imgFile = new File(p.getImages());
            if (imgFile.exists()) {
                iv.setImage(new Image(imgFile.toURI().toString(), true));
            }
        }
        
        imageContainer.getChildren().add(iv);

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10, 0, 0, 0));

        HBox topInfo = new HBox(10);
        topInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox textInfo = new VBox(2);
        Label title = new Label(p.getTitle());
        title.getStyleClass().add("title-4");
        title.setStyle("-fx-font-size: 16px;");
        title.setWrapText(true);

        Label location = new Label(p.getCity() + ", " + p.getCountry());
        location.getStyleClass().add("text-muted");
        textInfo.getChildren().addAll(title, location);
        HBox.setHgrow(textInfo, Priority.ALWAYS);

        VBox priceInfo = new VBox(0);
        priceInfo.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label price = new Label("$" + p.getPricePerNight());
        price.getStyleClass().add("accent");
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Label night = new Label("/ night");
        night.getStyleClass().add("text-muted");
        priceInfo.getChildren().addAll(price, night);

        topInfo.getChildren().addAll(textInfo, priceInfo);

        Button viewBtn = new Button("Book This Property");
        viewBtn.getStyleClass().add("accent");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setPrefHeight(35);
        viewBtn.setOnAction(e -> handleBrowseProperties(e));

        Button mapBtn = new Button("View on Map");
        mapBtn.getStyleClass().add("flat");
        mapBtn.setMaxWidth(Double.MAX_VALUE);
        mapBtn.setPrefHeight(35);
        mapBtn.setOnAction(e -> openPropertyOnMap(p));

        infoBox.getChildren().addAll(topInfo, viewBtn, mapBtn);
        
        card.getChildren().addAll(imageContainer, infoBox);
        return card;
    }

    private VBox createTripCard(Trip t) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(350);
        card.setMinWidth(350);
        card.setMaxWidth(350);
        card.setPadding(new Insets(15));
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10, 0, 0, 0));

        HBox topInfo = new HBox(10);
        topInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox textInfo = new VBox(2);
        Label title = new Label(t.getTripName());
        title.getStyleClass().add("title-4");
        title.setStyle("-fx-font-size: 16px;");
        title.setWrapText(true);

        Label location = new Label(t.getOrigin() + " ➔ " + t.getDestination());
        location.getStyleClass().add("text-muted");
        textInfo.getChildren().addAll(title, location);
        HBox.setHgrow(textInfo, Priority.ALWAYS);

        VBox priceInfo = new VBox(0);
        priceInfo.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label price = new Label("$" + (t.getBudgetAmount() != null ? t.getBudgetAmount() : 0.0));
        price.getStyleClass().add("accent");
        price.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Label entry = new Label("Entry");
        entry.getStyleClass().add("text-muted");
        priceInfo.getChildren().addAll(price, entry);

        topInfo.getChildren().addAll(textInfo, priceInfo);

        Button viewBtn = new Button("Participate Now");
        viewBtn.getStyleClass().add("accent");
        viewBtn.setMaxWidth(Double.MAX_VALUE);
        viewBtn.setPrefHeight(35);
        viewBtn.setOnAction(e -> handleBrowseTrips(e));

        infoBox.getChildren().addAll(topInfo, viewBtn);
        
        card.getChildren().addAll(infoBox);
        return card;
    }

    @FXML
    private void scrollPropertiesUp() {
        propertyScrollPane.setVvalue(Math.max(0, propertyScrollPane.getVvalue() - 0.2));
    }

    @FXML
    private void scrollPropertiesDown() {
        propertyScrollPane.setVvalue(Math.min(1, propertyScrollPane.getVvalue() + 0.2));
    }

    @FXML
    private void scrollTripsUp() {
        tripScrollPane.setVvalue(Math.max(0, tripScrollPane.getVvalue() - 0.2));
    }

    @FXML
    private void scrollTripsDown() {
        tripScrollPane.setVvalue(Math.min(1, tripScrollPane.getVvalue() + 0.2));
    }

    private void startBackgroundAnimation() {
        if (animatedBg == null) return;
        for (int i = 0; i < 10; i++) {
            Circle circle = createCircle();
            animatedBg.getChildren().add(circle);
            animateCircle(circle);
        }
    }

    private Circle createCircle() {
        double radius = 30 + random.nextDouble() * 120;
        Circle circle = new Circle(radius);
        circle.setCenterX(random.nextDouble() * 1200);
        circle.setCenterY(random.nextDouble() * 900);
        double opacity = 0.03 + random.nextDouble() * 0.05;
        boolean isDark = com.travelxp.utils.ThemeManager.isDark();
        String color = isDark ? "#D4AF37" : "#002b5c";
        circle.setFill(Color.web(color, opacity));
        circle.setStroke(Color.web(color, opacity * 1.5));
        circle.setStrokeWidth(1.5);
        circle.setEffect(new javafx.scene.effect.BoxBlur(10, 10, 2));
        return circle;
    }

    private void animateCircle(Circle circle) {
        double duration = 6 + random.nextDouble() * 6;
        TranslateTransition tt = new TranslateTransition(Duration.seconds(duration), circle);
        tt.setByX(random.nextDouble() * 500 - 250);
        tt.setByY(random.nextDouble() * 500 - 250);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        tt.play();
    }

    @FXML
    private void handleRecharge(ActionEvent event) {
        if (!stripeService.isConfigured()) {
            // Stripe not configured — fall back to direct balance credit
            rechargeWithoutStripe();
            return;
        }

        TextInputDialog dialog = new TextInputDialog("50.00");
        dialog.setTitle("Recharge Balance");
        dialog.setHeaderText("Add funds to your account via Stripe.");
        dialog.setContentText("Amount ($):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) throw new NumberFormatException();

                String email = Main.getSession().getUser().getEmail();

                // Show a waiting indicator
                Alert waitAlert = new Alert(Alert.AlertType.INFORMATION);
                waitAlert.setTitle("Stripe Payment");
                waitAlert.setHeaderText("A payment page has been opened in your browser.");
                waitAlert.setContentText("Complete the payment in your browser.\nThis dialog will close automatically when done.");
                waitAlert.getButtonTypes().setAll(ButtonType.CANCEL);
                waitAlert.show();

                stripeService.createCheckoutAndWaitForPayment(amount, email)
                        .thenAccept(success -> Platform.runLater(() -> {
                            waitAlert.close();
                            if (success) {
                                try {
                                    int userId = Main.getSession().getUser().getId();
                                    if (userService.updateBalance(userId, amount)) {
                                        Main.getSession().setUser(userService.getUserById(userId));
                                        updateProfileUI(Main.getSession().getUser());
                                        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                                                "Payment successful! $" + String.format("%.2f", amount) + " has been added to your balance.");
                                        ok.show();
                                    }
                                } catch (SQLException ex) {
                                    new Alert(Alert.AlertType.ERROR, "Payment succeeded but failed to update balance. Contact support.").show();
                                    ex.printStackTrace();
                                }
                            } else {
                                new Alert(Alert.AlertType.WARNING, "Payment was cancelled or timed out.").show();
                            }
                        }));

                // If user clicks Cancel on the wait dialog, the CompletableFuture will still
                // resolve eventually (success or timeout), but we won’t bother them again.
                waitAlert.setOnCloseRequest(e -> waitAlert.close());

            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Please enter a valid positive amount.").show();
            }
        });
    }

    /** Fallback recharge when Stripe is not configured (no API key in db.properties). */
    private void rechargeWithoutStripe() {
        TextInputDialog dialog = new TextInputDialog("50.00");
        dialog.setTitle("Recharge Balance");
        dialog.setHeaderText("Add funds to your account.");
        dialog.setContentText("Amount ($):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) throw new NumberFormatException();

                int userId = Main.getSession().getUser().getId();
                if (userService.updateBalance(userId, amount)) {
                    Main.getSession().setUser(userService.getUserById(userId));
                    updateProfileUI(Main.getSession().getUser());
                    new Alert(Alert.AlertType.INFORMATION, "Success! Your balance has been updated.").show();
                }
            } catch (NumberFormatException | SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid amount or system error.").show();
            }
        });
    }

    private void updateProfileUI(User user) {
        welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");
        emailLabel.setText(user.getEmail());
        birthdayLabel.setText(user.getBirthday().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        bioLabel.setText(user.getBio() != null ? user.getBio() : "No bio yet.");
        if (balanceLabel != null) {
            balanceLabel.setText(String.format("$%.2f", user.getBalance()));
        }
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            try {
                File file = new File(user.getProfileImage());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    profileImageView.setImage(image);
                }
            } catch (Exception e) {}
        }
    }

    private void updateGamificationUI(int userId) {
        try {
            Gamification gamification = gamificationService.getGamificationByUserId(userId);
            if (gamification != null) {
                titleLabel.setText(gamification.getTitle());
                levelLabel.setText(String.valueOf(gamification.getLevel()));
                int currentLevel = gamification.getLevel();
                int currentXp = gamification.getXp();
                int nextLevelXp = gamificationService.getXpForNextLevel(currentLevel);
                int prevLevelXp = (currentLevel > 1) ? gamificationService.getXpForNextLevel(currentLevel - 1) : 0;
                xpLabel.setText(currentXp + " / " + nextLevelXp + " XP");
                double progress = (currentLevel == 1) ? (double) currentXp / nextLevelXp : (double) (currentXp - prevLevelXp) / (nextLevelXp - prevLevelXp);
                xpProgressBar.setProgress(progress);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleTasks(ActionEvent event) { changeScene(event, "/com/travelxp/views/tasks.fxml"); }
    @FXML private void handleEditProfile(ActionEvent event) { changeScene(event, "/com/travelxp/views/edit_profile.fxml"); }
    @FXML private void handleChangePassword(ActionEvent event) { changeScene(event, "/com/travelxp/views/change_password.fxml"); }
    @FXML private void handleFeedback(ActionEvent event) { changeScene(event, "/com/travelxp/views/feedback-view.fxml"); }
    @FXML private void handleGeminiChat(ActionEvent event) { changeScene(event, "/com/travelxp/views/gemini-chat.fxml"); }

    @FXML
    private void handleBrowseProperties(ActionEvent event) {
        String fxml = Main.getSession().getUser().getRole().equals("ADMIN") ? "/com/travelxp/views/admin-property-view.fxml" : "/com/travelxp/views/property-view.fxml";
        changeScene(event, fxml);
    }

    @FXML
    private void handleMyBookings(ActionEvent event) {
        String fxml = Main.getSession().getUser().getRole().equals("ADMIN") ? "/com/travelxp/views/admin-booking-view.fxml" : "/com/travelxp/views/booking-view.fxml";
        changeScene(event, fxml);
    }

    @FXML
    private void handleBrowseTrips(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/travelxp/views/trip-view.fxml"));
            Parent root = loader.load();
            TripController controller = loader.getController();
            controller.setMyTripsMode(false);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            com.travelxp.utils.ThemeManager.applyTheme(stage.getScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyTrips(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/travelxp/views/trip-view.fxml"));
            Parent root = loader.load();
            TripController controller = loader.getController();
            controller.setMyTripsMode(true);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            com.travelxp.utils.ThemeManager.applyTheme(stage.getScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void toggleTheme(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        com.travelxp.utils.ThemeManager.toggleTheme(stage.getScene());
    }

    @FXML private void handleLogout(ActionEvent event) {
        Main.setSession(null);
        changeScene(event, "/com/travelxp/views/login.fxml");
    }

    private void changeScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            com.travelxp.utils.ThemeManager.applyTheme(stage.getScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
