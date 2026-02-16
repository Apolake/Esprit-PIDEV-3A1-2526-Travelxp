package com.travelxp.controller;

import com.travelxp.model.*;
import com.travelxp.model.ReviewComment;
import com.travelxp.service.AuthService;
import com.travelxp.service.BookingService;
import com.travelxp.service.PropertyService;
import com.travelxp.service.ReviewCommentService;
import com.travelxp.service.ReviewService;
import com.travelxp.service.TripService;
import com.travelxp.service.UserService;
import com.travelxp.service.ServiceOfferingService;
import com.travelxp.service.BookingServiceItemService;
import com.travelxp.service.OfferService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ListCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class AdminController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Long> userIdColumn;
    @FXML
    private TableColumn<User, String> userUsernameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userFullNameColumn;
    @FXML
    private TableColumn<User, String> userLevelColumn;
    @FXML
    private TextField userUsernameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField userFullNameField;
    @FXML
    private TextField userPasswordField;

    @FXML
    private TableView<Property> propertiesTable;
    @FXML
    private TableColumn<Property, Long> propertyIdColumn;
    @FXML
    private TableColumn<Property, String> propertyTitleColumn;
    @FXML
    private TableColumn<Property, String> propertyCityColumn;
    @FXML
    private TableColumn<Property, BigDecimal> propertyPriceColumn;
    @FXML
    private TableColumn<Property, Boolean> propertyActiveColumn;
    @FXML
    private ComboBox<User> propertyOwnerCombo;
    @FXML
    private TextField propertyTitleField;
    @FXML
    private TextField propertyCityField;
    @FXML
    private TextField propertyCountryField;
    @FXML
    private TextField propertyAddressField;
    @FXML
    private TextField propertyTypeField;
    @FXML
    private TextField propertyPriceField;
    @FXML
    private TextField propertyGuestsField;
    @FXML
    private TextField propertyBedroomsField;
    @FXML
    private TextField propertyBathroomsField;
    
    @FXML
    private void handlePropertyCreate(ActionEvent event) {
        propertiesTable.getSelectionModel().clearSelection();
        handlePropertySave(event);
    }
    
    @FXML
    private void handleActivityCreate(ActionEvent event) {
        activitiesTable.getSelectionModel().clearSelection();
        handleActivitySave(event);
    }

    @FXML
    private TextField propertyAmenitiesField;
    @FXML
    private CheckBox propertyActiveCheck;

    @FXML
    private TableView<Booking> bookingsTable;
    @FXML
    private TableColumn<Booking, Long> bookingIdColumn;
    @FXML
    private TableColumn<Booking, String> bookingPropertyColumn;
    @FXML
    private TableColumn<Booking, String> bookingGuestColumn;
    @FXML
    private TableColumn<Booking, String> bookingDatesColumn;
    @FXML
    private TableColumn<Booking, String> bookingStatusColumn;
    @FXML
    private ComboBox<Property> bookingPropertyCombo;
    @FXML
    private ComboBox<User> bookingGuestCombo;
    @FXML
    private DatePicker bookingCheckInPicker;
    @FXML
    private DatePicker bookingCheckOutPicker;
    @FXML
    private Spinner<Integer> bookingGuestsSpinner;
    @FXML
    private TextField bookingStatusField;
    @FXML
    private TextArea bookingRequestsArea;

    @FXML
    private TableView<Trip> tripsTable;
    
    @FXML
    private void handleMilestoneCreate(ActionEvent event) {
        milestonesTable.getSelectionModel().clearSelection();
        handleMilestoneSave(event);
    }

    @FXML
    private TableColumn<Trip, Long> tripIdColumn;
    @FXML
    private TableColumn<Trip, String> tripNameColumn;
    @FXML
    private TableColumn<Trip, String> tripUserColumn;
    @FXML
    private TableColumn<Trip, String> tripDatesColumn;
    @FXML
    private TableColumn<Trip, String> tripStatusColumn;
    @FXML
    private ComboBox<User> tripUserCombo;
    @FXML
    private TextField tripNameField;
    @FXML
    private DatePicker tripStartPicker;
    @FXML
    private DatePicker tripEndPicker;
    @FXML
    private TextField tripStatusField;

    @FXML
    private TableView<Activity> activitiesTable;
    @FXML
    private TableColumn<Activity, Long> activityIdColumn;
    @FXML
    private TableColumn<Activity, String> activityTripColumn;
    @FXML
    private TableColumn<Activity, String> activityTitleColumn;
    @FXML
    private TableColumn<Activity, LocalDate> activityDateColumn;
    @FXML
    private TableColumn<Activity, String> activityLocationColumn;
    @FXML
    private TableColumn<Activity, Integer> activityXpColumn;
    @FXML
    private ComboBox<Trip> activityTripCombo;
    @FXML
    private TextField activityTitleField;
    
    @FXML
    private void handleReviewCreate(ActionEvent event) {
        reviewsTable.getSelectionModel().clearSelection();
        handleReviewSave(event);
    }

    @FXML
    private DatePicker activityDatePicker;
    @FXML
    private TextField activityLocationField;
    @FXML
    private TextField activityCostField;
    @FXML
    private Spinner<Integer> activityXpSpinner;
    @FXML
    private TextArea activityDescriptionArea;

    @FXML
    private TableView<TripMilestone> milestonesTable;
    @FXML
    private TableColumn<TripMilestone, Long> milestoneIdColumn;
    @FXML
    private TableColumn<TripMilestone, String> milestoneTripColumn;
    @FXML
    private TableColumn<TripMilestone, String> milestoneTitleColumn;
    @FXML
    private TableColumn<TripMilestone, String> milestoneStatusColumn;
    @FXML
    private TableColumn<TripMilestone, Integer> milestoneXpColumn;
    @FXML
    private ComboBox<Trip> milestoneTripCombo;
    @FXML
    private TextField milestoneTitleField;
    @FXML
    private Spinner<Integer> milestoneXpSpinner;
    @FXML
    private CheckBox milestoneCompletedCheck;
    @FXML
    private TextArea milestoneDescriptionArea;

    @FXML
    private TableView<Review> reviewsTable;
    @FXML
    private TableColumn<Review, Long> reviewIdColumn;
    @FXML
    private TableColumn<Review, String> reviewPropertyColumn;
    @FXML
    private TableColumn<Review, String> reviewReviewerColumn;
    @FXML
    private TableColumn<Review, Integer> reviewRatingColumn;
    @FXML
    private TableColumn<Review, String> reviewCommentColumn;
    @FXML
    private ComboBox<Booking> reviewBookingCombo;
    @FXML
    private Label reviewPropertyLabel;
    @FXML
    private Label reviewUserLabel;
    @FXML
    private Spinner<Integer> reviewRatingSpinner;
    @FXML
    private TextArea reviewCommentArea;
    
    @FXML
    private TableView<ReviewComment> commentsTable;
    @FXML
    private TableColumn<ReviewComment, Long> commentIdColumn;
    @FXML
    private TableColumn<ReviewComment, String> commentReviewColumn;
    @FXML
    private TableColumn<ReviewComment, String> commentUserColumn;
    @FXML
    private TableColumn<ReviewComment, String> commentSnippetColumn;
    @FXML
    private TableColumn<ReviewComment, String> commentCreatedColumn;
    @FXML
    private ComboBox<Review> commentReviewCombo;
    @FXML
    private ComboBox<User> commentUserCombo;
    @FXML
    private TextArea commentTextArea;

    @FXML
    private TableView<ServiceOffering> servicesTable;
    @FXML
    private TableColumn<ServiceOffering, Long> serviceIdColumn;
    @FXML
    private TableColumn<ServiceOffering, String> serviceNameColumn;
    @FXML
    private TableColumn<ServiceOffering, BigDecimal> servicePriceColumn;
    @FXML
    private TableColumn<ServiceOffering, String> serviceDescColumn;
    @FXML
    private TextField serviceNameField;
    @FXML
    private TextField servicePriceField;
    @FXML
    private TextArea serviceDescArea;

    @FXML
    private TableView<BookingServiceItem> bookingServicesTable;
    @FXML
    private TableColumn<BookingServiceItem, Long> bookingServiceIdColumn;
    @FXML
    private TableColumn<BookingServiceItem, String> bookingServiceBookingColumn;
    @FXML
    private TableColumn<BookingServiceItem, String> bookingServiceServiceColumn;
    @FXML
    private TableColumn<BookingServiceItem, Integer> bookingServiceQtyColumn;
    @FXML
    private TableColumn<BookingServiceItem, BigDecimal> bookingServicePriceColumn;
    @FXML
    private ComboBox<Booking> bookingServiceBookingCombo;
    @FXML
    private ComboBox<ServiceOffering> bookingServiceServiceCombo;
    @FXML
    private Spinner<Integer> bookingServiceQtySpinner;
    @FXML
    private TextField bookingServicePriceField;

    @FXML
    private TableView<Offer> offersTable;
    @FXML
    private TableColumn<Offer, Long> offerIdColumn;
    @FXML
    private TableColumn<Offer, String> offerPropertyColumn;
    @FXML
    private TableColumn<Offer, String> offerTitleColumn;
    @FXML
    private TableColumn<Offer, BigDecimal> offerDiscountColumn;
    @FXML
    private TableColumn<Offer, String> offerDatesColumn;
    @FXML
    private TableColumn<Offer, Boolean> offerActiveColumn;
    @FXML
    private ComboBox<Property> offerPropertyCombo;
    @FXML
    private TextField offerTitleField;
    @FXML
    private TextField offerDiscountField;
    @FXML
    private DatePicker offerStartDatePicker;
    @FXML
    private DatePicker offerEndDatePicker;
    @FXML
    private CheckBox offerActiveCheck;
    @FXML
    private TextArea offerDescriptionArea;

    @Autowired
    private UserService userService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private TripService tripService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewCommentService reviewCommentService;
    @Autowired
    private ServiceOfferingService serviceOfferingService;
    @Autowired
    private BookingServiceItemService bookingServiceItemService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private AuthService authService;
    @Autowired
    private StageManager stageManager;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Property> properties = FXCollections.observableArrayList();
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();
    private ObservableList<Trip> trips = FXCollections.observableArrayList();
    private ObservableList<Activity> activities = FXCollections.observableArrayList();
    private ObservableList<TripMilestone> milestones = FXCollections.observableArrayList();
    private ObservableList<Review> reviews = FXCollections.observableArrayList();
    private final DateTimeFormatter commentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private ObservableList<ReviewComment> comments = FXCollections.observableArrayList();
    private ObservableList<ServiceOffering> services = FXCollections.observableArrayList();
    private ObservableList<BookingServiceItem> bookingServices = FXCollections.observableArrayList();
    private ObservableList<Offer> offers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSpinners();
        setupTables();
        loadAllData();
        wireSelectionListeners();
    }

    private void setupSpinners() {
        bookingGuestsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        activityXpSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 10));
        milestoneXpSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0, 10));
        reviewRatingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
        bookingServiceQtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    private void setupTables() {
        userIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        userUsernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        userEmailColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        userFullNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFullName()));
        userLevelColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getLevel() != null ? cell.getValue().getLevel().getLevelName() : ""));

        propertyIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        propertyTitleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        propertyCityColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCity()));
        propertyPriceColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPricePerNight()));
        propertyActiveColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIsActive()));

        bookingIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        bookingPropertyColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getProperty().getTitle()));
        bookingGuestColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getGuest().getUsername()));
        bookingDatesColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getCheckInDate() + " to " + cell.getValue().getCheckOutDate()));
        bookingStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        tripIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        tripNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTripName()));
        tripUserColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getUser() != null ? cell.getValue().getUser().getUsername() : ""));
        tripDatesColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getStartDate() + " to " + cell.getValue().getEndDate()));
        tripStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        activityIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        activityTripColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getTrip() != null ? cell.getValue().getTrip().getTripName() : ""));
        activityTitleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        activityDateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getActivityDate()));
        activityLocationColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getLocation() != null ? cell.getValue().getLocation() : ""));
        activityXpColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getXpReward()));

        milestoneIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        milestoneTripColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getTrip() != null ? cell.getValue().getTrip().getTripName() : ""));
        milestoneTitleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        milestoneStatusColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            Boolean.TRUE.equals(cell.getValue().getCompleted()) ? "Done" : "Pending"));
        milestoneXpColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getXpReward()));

        reviewIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        reviewPropertyColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getProperty() != null ? cell.getValue().getProperty().getTitle() : ""));
        reviewReviewerColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getReviewer() != null ? cell.getValue().getReviewer().getUsername() : ""));
        reviewRatingColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRating()));
        reviewCommentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getComment()));

        commentIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        commentReviewColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getReview() != null && cell.getValue().getReview().getProperty() != null
                ? cell.getValue().getReview().getProperty().getTitle()
                : (cell.getValue().getReview() != null ? "Review #" + cell.getValue().getReview().getId() : "")));
        commentUserColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getCommenter() != null ? cell.getValue().getCommenter().getUsername() : ""));
        commentSnippetColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            snippet(cell.getValue().getComment())));
        commentCreatedColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getCreatedAt() != null ? commentFormatter.format(cell.getValue().getCreatedAt()) : ""));

        serviceIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        serviceNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        servicePriceColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPrice()));
        serviceDescColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));

        bookingServiceIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        bookingServiceBookingColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getBooking() != null ? "#" + cell.getValue().getBooking().getId() + " " + cell.getValue().getBooking().getProperty().getTitle() : ""));
        bookingServiceServiceColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getService() != null ? cell.getValue().getService().getName() : ""));
        bookingServiceQtyColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getQuantity()));
        bookingServicePriceColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPriceAtBooking()));

        offerIdColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        offerPropertyColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getProperty() != null ? cell.getValue().getProperty().getTitle() : ""));
        offerTitleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        offerDiscountColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDiscountPercentage()));
        offerDatesColumn.setCellValueFactory(cell -> new SimpleStringProperty(
            cell.getValue().getStartDate() + " to " + cell.getValue().getEndDate()));
        offerActiveColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIsActive()));

        setupComboCellFactories();
    }

    private void setupComboCellFactories() {
        propertyOwnerCombo.setCellFactory(list -> userCell());
        propertyOwnerCombo.setButtonCell(userCell());
        bookingPropertyCombo.setCellFactory(list -> propertyCell());
        bookingPropertyCombo.setButtonCell(propertyCell());
        bookingGuestCombo.setCellFactory(list -> userCell());
        bookingGuestCombo.setButtonCell(userCell());
        tripUserCombo.setCellFactory(list -> userCell());
        tripUserCombo.setButtonCell(userCell());
        activityTripCombo.setCellFactory(list -> tripCell());
        activityTripCombo.setButtonCell(tripCell());
        milestoneTripCombo.setCellFactory(list -> tripCell());
        milestoneTripCombo.setButtonCell(tripCell());
        reviewBookingCombo.setCellFactory(list -> bookingCell());
        reviewBookingCombo.setButtonCell(bookingCell());
        commentReviewCombo.setCellFactory(list -> reviewCell());
        commentReviewCombo.setButtonCell(reviewCell());
        commentUserCombo.setCellFactory(list -> userCell());
        commentUserCombo.setButtonCell(userCell());
        bookingServiceBookingCombo.setCellFactory(list -> bookingCell());
        bookingServiceBookingCombo.setButtonCell(bookingCell());
        bookingServiceServiceCombo.setCellFactory(list -> serviceCell());
        bookingServiceServiceCombo.setButtonCell(serviceCell());
        offerPropertyCombo.setCellFactory(list -> propertyCell());
        offerPropertyCombo.setButtonCell(propertyCell());
    }

    private ListCell<User> userCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getUsername());
            }
        };
    }

    private ListCell<Review> reviewCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Review item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String title = item.getProperty() != null ? item.getProperty().getTitle() : "";
                    setText("#" + item.getId() + " " + title);
                }
            }
        };
    }

    private ListCell<Property> propertyCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Property item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        };
    }

    private ListCell<Trip> tripCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Trip item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTripName());
            }
        };
    }

    private ListCell<Booking> bookingCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Booking item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("#" + item.getId() + " " + item.getProperty().getTitle());
                }
            }
        };
    }

    private ListCell<ServiceOffering> serviceCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(ServiceOffering item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        };
    }

    private void loadAllData() {
        users.setAll(userService.getAllUsers());
        properties.setAll(propertyService.getAllPropertiesAdmin());
        bookings.setAll(bookingService.getAllBookings());
        trips.setAll(tripService.getAllTrips());
        activities.setAll(tripService.getAllActivities());
        milestones.setAll(tripService.getAllMilestones());
        reviews.setAll(reviewService.getAllReviews());
        comments.setAll(reviewCommentService.getAllComments());
        services.setAll(serviceOfferingService.getAll());
        bookingServices.setAll(bookingServiceItemService.getAll());
        offers.setAll(offerService.getAll());

        usersTable.setItems(users);
        propertiesTable.setItems(properties);
        bookingsTable.setItems(bookings);
        tripsTable.setItems(trips);
        activitiesTable.setItems(activities);
        milestonesTable.setItems(milestones);
        reviewsTable.setItems(reviews);
        commentsTable.setItems(comments);
        servicesTable.setItems(services);
        bookingServicesTable.setItems(bookingServices);
        offersTable.setItems(offers);

        propertyOwnerCombo.setItems(users);
        bookingPropertyCombo.setItems(properties);
        bookingGuestCombo.setItems(users);
        tripUserCombo.setItems(users);
        activityTripCombo.setItems(trips);
        milestoneTripCombo.setItems(trips);
        reviewBookingCombo.setItems(bookings);
        commentReviewCombo.setItems(reviews);
        commentUserCombo.setItems(users);
        bookingServiceBookingCombo.setItems(bookings);
        bookingServiceServiceCombo.setItems(services);
        offerPropertyCombo.setItems(properties);
    }

    private void wireSelectionListeners() {
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateUserForm(n));
        propertiesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populatePropertyForm(n));
        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateBookingForm(n));
        tripsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateTripForm(n));
        activitiesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateActivityForm(n));
        milestonesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateMilestoneForm(n));
        reviewsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateReviewForm(n));
        reviewBookingCombo.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateReviewDerivedFields(n));
        commentsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateCommentForm(n));
        servicesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateServiceForm(n));
        bookingServicesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateBookingServiceForm(n));
        offersTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> populateOfferForm(n));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stageManager.switchScene(FXMLView.DASHBOARD);
    }

    @FXML
    private void handleUserSave(ActionEvent event) {
        try {
            User selected = usersTable.getSelectionModel().getSelectedItem();
            String username = userUsernameField.getText();
            String email = userEmailField.getText();
            String fullName = userFullNameField.getText();
            String password = userPasswordField.getText();

            if (username == null || username.isBlank() || email == null || email.isBlank() || fullName == null || fullName.isBlank()) {
                showAlert("Username, email, and full name are required.");
                return;
            }

            if (selected == null) {
                User created = authService.register(username.trim(), email.trim(), password == null ? "" : password.trim(), fullName.trim());
                users.add(created);
                usersTable.getSelectionModel().select(created);
                setStatus("User created.");
            } else {
                selected.setUsername(username.trim());
                selected.setEmail(email.trim());
                selected.setFullName(fullName.trim());
                if (password != null && !password.isBlank()) {
                    selected.setPassword(password.trim());
                }
                User updated = userService.updateUser(selected);
                refreshUsers(updated);
                setStatus("User updated.");
            }
        } catch (Exception ex) {
            showAlert("User save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleUserDelete(ActionEvent event) {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a user to delete.");
            return;
        }
        try {
            userService.deleteUser(selected.getId());
            users.remove(selected);
            handleUserReset(null);
            setStatus("User deleted.");
        } catch (Exception ex) {
            showAlert("User delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleUserReset(ActionEvent event) {
        userUsernameField.clear();
        userEmailField.clear();
        userFullNameField.clear();
        userPasswordField.clear();
        usersTable.getSelectionModel().clearSelection();
    }

    private void populateUserForm(User user) {
        if (user == null) {
            handleUserReset(null);
            return;
        }
        userUsernameField.setText(user.getUsername());
        userEmailField.setText(user.getEmail());
        userFullNameField.setText(user.getFullName());
        userPasswordField.clear();
    }

    private void refreshUsers(User updated) {
        loadAllData();
        usersTable.getSelectionModel().select(updated);
    }

    @FXML
    private void handlePropertySave(ActionEvent event) {
        try {
            User owner = propertyOwnerCombo.getSelectionModel().getSelectedItem();
            Property selected = propertiesTable.getSelectionModel().getSelectedItem();
            Property property = selected != null ? selected : new Property();
            property.setOwner(owner);
            property.setTitle(text(propertyTitleField, "").trim());
            property.setCity(text(propertyCityField, "").trim());
            property.setCountry(text(propertyCountryField, "").trim());
            property.setAddress(text(propertyAddressField, "").trim());
            property.setPropertyType(text(propertyTypeField, "").trim());
            property.setPricePerNight(parseDecimal(propertyPriceField.getText()));
            property.setMaxGuests(parseInt(propertyGuestsField.getText(), 0));
            property.setBedrooms(parseInt(propertyBedroomsField.getText(), 0));
            property.setBathrooms(parseInt(propertyBathroomsField.getText(), 0));
            property.setAmenities(text(propertyAmenitiesField));
            property.setIsActive(propertyActiveCheck.isSelected());

            Property saved = selected == null
                    ? propertyService.createProperty(property)
                    : propertyService.updateProperty(property);

            loadAllData();
            propertiesTable.getSelectionModel().select(saved);
            setStatus("Property saved.");
        } catch (Exception ex) {
            showAlert("Property save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handlePropertyDelete(ActionEvent event) {
        Property selected = propertiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a property to delete.");
            return;
        }
        try {
            propertyService.deleteProperty(selected.getId());
            properties.remove(selected);
            handlePropertyReset(null);
            setStatus("Property deleted.");
        } catch (Exception ex) {
            showAlert("Property delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handlePropertyReset(ActionEvent event) {
        propertyOwnerCombo.getSelectionModel().clearSelection();
        propertyTitleField.clear();
        propertyCityField.clear();
        propertyCountryField.clear();
        propertyAddressField.clear();
        propertyTypeField.clear();
        propertyPriceField.clear();
        propertyGuestsField.clear();
        propertyBedroomsField.clear();
        propertyBathroomsField.clear();
        propertyAmenitiesField.clear();
        propertyActiveCheck.setSelected(true);
        propertiesTable.getSelectionModel().clearSelection();
    }

    private void populatePropertyForm(Property property) {
        if (property == null) {
            handlePropertyReset(null);
            return;
        }
        propertyOwnerCombo.getSelectionModel().select(property.getOwner());
        propertyTitleField.setText(property.getTitle());
        propertyCityField.setText(property.getCity());
        propertyCountryField.setText(property.getCountry());
        propertyAddressField.setText(property.getAddress());
        propertyTypeField.setText(property.getPropertyType());
        propertyPriceField.setText(property.getPricePerNight() != null ? property.getPricePerNight().toPlainString() : "");
        propertyGuestsField.setText(property.getMaxGuests() != null ? property.getMaxGuests().toString() : "");
        propertyBedroomsField.setText(property.getBedrooms() != null ? property.getBedrooms().toString() : "");
        propertyBathroomsField.setText(property.getBathrooms() != null ? property.getBathrooms().toString() : "");
        propertyAmenitiesField.setText(property.getAmenities());
        propertyActiveCheck.setSelected(Boolean.TRUE.equals(property.getIsActive()));
    }

    @FXML
    private void handleBookingCreate(ActionEvent event) {
        try {
            Property property = bookingPropertyCombo.getSelectionModel().getSelectedItem();
            User guest = bookingGuestCombo.getSelectionModel().getSelectedItem();
            LocalDate checkIn = bookingCheckInPicker.getValue();
            LocalDate checkOut = bookingCheckOutPicker.getValue();
            Integer guestsCount = bookingGuestsSpinner.getValue();
            String requests = bookingRequestsArea.getText();

            if (property == null || guest == null || checkIn == null || checkOut == null) {
                showAlert("Property, guest, and dates are required.");
                return;
            }

            Booking created = bookingService.createBooking(property, guest, checkIn, checkOut, guestsCount, requests);
            loadAllData();
            bookingsTable.getSelectionModel().select(created);
            setStatus("Booking created.");
        } catch (Exception ex) {
            showAlert("Booking create failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBookingUpdate(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a booking to update.");
            return;
        }
        try {
            Property property = bookingPropertyCombo.getSelectionModel().getSelectedItem();
            User guest = bookingGuestCombo.getSelectionModel().getSelectedItem();
            LocalDate checkIn = bookingCheckInPicker.getValue();
            LocalDate checkOut = bookingCheckOutPicker.getValue();

            selected.setProperty(property != null ? property : selected.getProperty());
            selected.setGuest(guest != null ? guest : selected.getGuest());
            if (checkIn != null) {
                selected.setCheckInDate(checkIn);
            }
            if (checkOut != null) {
                selected.setCheckOutDate(checkOut);
            }
            selected.setNumberOfGuests(bookingGuestsSpinner.getValue());
            selected.setStatus(text(bookingStatusField, selected.getStatus()));
            selected.setSpecialRequests(text(bookingRequestsArea));

            bookingService.updateBooking(selected);
            loadAllData();
            bookingsTable.getSelectionModel().select(selected);
            setStatus("Booking updated.");
        } catch (Exception ex) {
            showAlert("Booking update failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBookingDelete(ActionEvent event) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a booking to delete.");
            return;
        }
        try {
            bookingService.deleteBooking(selected.getId());
            bookings.remove(selected);
            handleBookingReset(null);
            setStatus("Booking deleted.");
        } catch (Exception ex) {
            showAlert("Booking delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBookingReset(ActionEvent event) {
        bookingPropertyCombo.getSelectionModel().clearSelection();
        bookingGuestCombo.getSelectionModel().clearSelection();
        bookingCheckInPicker.setValue(null);
        bookingCheckOutPicker.setValue(null);
        bookingGuestsSpinner.getValueFactory().setValue(1);
        bookingStatusField.clear();
        bookingRequestsArea.clear();
        bookingsTable.getSelectionModel().clearSelection();
    }

    private void populateBookingForm(Booking booking) {
        if (booking == null) {
            handleBookingReset(null);
            return;
        }
        bookingPropertyCombo.getSelectionModel().select(booking.getProperty());
        bookingGuestCombo.getSelectionModel().select(booking.getGuest());
        bookingCheckInPicker.setValue(booking.getCheckInDate());
        bookingCheckOutPicker.setValue(booking.getCheckOutDate());
        bookingGuestsSpinner.getValueFactory().setValue(booking.getNumberOfGuests());
        bookingStatusField.setText(booking.getStatus());
        bookingRequestsArea.setText(booking.getSpecialRequests());
    }

    @FXML
    private void handleTripCreate(ActionEvent event) {
        try {
            User user = tripUserCombo.getSelectionModel().getSelectedItem();
            String name = text(tripNameField);
            LocalDate start = tripStartPicker.getValue();
            LocalDate end = tripEndPicker.getValue();
            if (user == null || name.isBlank() || start == null || end == null) {
                showAlert("User, name, and dates are required.");
                return;
            }
            Trip trip = tripService.createTrip(user, name, start, end);
            trip.setStatus(text(tripStatusField, "PLANNED"));
            tripService.updateTrip(trip);
            loadAllData();
            tripsTable.getSelectionModel().select(trip);
            setStatus("Trip created.");
        } catch (Exception ex) {
            showAlert("Trip create failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleTripUpdate(ActionEvent event) {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a trip to update.");
            return;
        }
        try {
            User user = tripUserCombo.getSelectionModel().getSelectedItem();
            if (user != null) {
                selected.setUser(user);
            }
            if (!text(tripNameField).isBlank()) {
                selected.setTripName(text(tripNameField));
            }
            if (tripStartPicker.getValue() != null) {
                selected.setStartDate(tripStartPicker.getValue());
            }
            if (tripEndPicker.getValue() != null) {
                selected.setEndDate(tripEndPicker.getValue());
            }
            if (!text(tripStatusField).isBlank()) {
                selected.setStatus(text(tripStatusField));
            }
            tripService.updateTrip(selected);
            loadAllData();
            tripsTable.getSelectionModel().select(selected);
            setStatus("Trip updated.");
        } catch (Exception ex) {
            showAlert("Trip update failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleTripDelete(ActionEvent event) {
        Trip selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a trip to delete.");
            return;
        }
        try {
            tripService.deleteTrip(selected.getId());
            trips.remove(selected);
            handleTripReset(null);
            setStatus("Trip deleted.");
        } catch (Exception ex) {
            showAlert("Trip delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleTripReset(ActionEvent event) {
        tripUserCombo.getSelectionModel().clearSelection();
        tripNameField.clear();
        tripStartPicker.setValue(null);
        tripEndPicker.setValue(null);
        tripStatusField.clear();
        tripsTable.getSelectionModel().clearSelection();
    }

    private void populateTripForm(Trip trip) {
        if (trip == null) {
            handleTripReset(null);
            return;
        }
        tripUserCombo.getSelectionModel().select(trip.getUser());
        tripNameField.setText(trip.getTripName());
        tripStartPicker.setValue(trip.getStartDate());
        tripEndPicker.setValue(trip.getEndDate());
        tripStatusField.setText(trip.getStatus());
    }

    @FXML
    private void handleActivitySave(ActionEvent event) {
        try {
            Trip trip = activityTripCombo.getSelectionModel().getSelectedItem();
            if (trip == null) {
                showAlert("Trip is required.");
                return;
            }
            Activity selected = activitiesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Activity created = tripService.addActivity(trip.getId(), trip.getUser(),
                        text(activityTitleField), activityDescriptionArea.getText(),
                        activityDatePicker.getValue(), text(activityLocationField),
                        parseDecimal(activityCostField.getText()), activityXpSpinner.getValue());
                loadAllData();
                activitiesTable.getSelectionModel().select(created);
                setStatus("Activity created.");
            } else {
                selected.setTrip(trip);
                selected.setTitle(text(activityTitleField));
                selected.setDescription(activityDescriptionArea.getText());
                selected.setActivityDate(activityDatePicker.getValue());
                selected.setLocation(text(activityLocationField));
                selected.setCost(parseDecimal(activityCostField.getText()));
                selected.setXpReward(activityXpSpinner.getValue());
                tripService.updateActivity(selected);
                loadAllData();
                activitiesTable.getSelectionModel().select(selected);
                setStatus("Activity updated.");
            }
        } catch (Exception ex) {
            showAlert("Activity save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleActivityDelete(ActionEvent event) {
        Activity selected = activitiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select an activity to delete.");
            return;
        }
        try {
            tripService.deleteActivity(selected.getId());
            activities.remove(selected);
            handleActivityReset(null);
            setStatus("Activity deleted.");
        } catch (Exception ex) {
            showAlert("Activity delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleActivityReset(ActionEvent event) {
        activityTripCombo.getSelectionModel().clearSelection();
        activityTitleField.clear();
        activityDatePicker.setValue(null);
        activityLocationField.clear();
        activityCostField.clear();
        activityXpSpinner.getValueFactory().setValue(0);
        activityDescriptionArea.clear();
        activitiesTable.getSelectionModel().clearSelection();
    }

    private void populateActivityForm(Activity activity) {
        if (activity == null) {
            handleActivityReset(null);
            return;
        }
        activityTripCombo.getSelectionModel().select(activity.getTrip());
        activityTitleField.setText(activity.getTitle());
        activityDatePicker.setValue(activity.getActivityDate());
        activityLocationField.setText(activity.getLocation());
        activityCostField.setText(activity.getCost() != null ? activity.getCost().toPlainString() : "");
        activityXpSpinner.getValueFactory().setValue(activity.getXpReward());
        activityDescriptionArea.setText(activity.getDescription());
    }

    @FXML
    private void handleMilestoneSave(ActionEvent event) {
        try {
            Trip trip = milestoneTripCombo.getSelectionModel().getSelectedItem();
            if (trip == null) {
                showAlert("Trip is required.");
                return;
            }
            TripMilestone selected = milestonesTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                TripMilestone created = tripService.addMilestone(trip.getId(), trip.getUser(),
                        text(milestoneTitleField), milestoneDescriptionArea.getText(),
                        milestoneXpSpinner.getValue());
                if (milestoneCompletedCheck.isSelected()) {
                    created.markCompleted();
                    tripService.updateMilestone(created);
                }
                loadAllData();
                milestonesTable.getSelectionModel().select(created);
                setStatus("Milestone created.");
            } else {
                selected.setTrip(trip);
                selected.setTitle(text(milestoneTitleField));
                selected.setDescription(milestoneDescriptionArea.getText());
                selected.setXpReward(milestoneXpSpinner.getValue());
                if (milestoneCompletedCheck.isSelected()) {
                    selected.markCompleted();
                } else {
                    selected.setCompleted(false);
                }
                tripService.updateMilestone(selected);
                loadAllData();
                milestonesTable.getSelectionModel().select(selected);
                setStatus("Milestone updated.");
            }
        } catch (Exception ex) {
            showAlert("Milestone save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleMilestoneDelete(ActionEvent event) {
        TripMilestone selected = milestonesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a milestone to delete.");
            return;
        }
        try {
            tripService.deleteMilestone(selected.getId());
            milestones.remove(selected);
            handleMilestoneReset(null);
            setStatus("Milestone deleted.");
        } catch (Exception ex) {
            showAlert("Milestone delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleMilestoneReset(ActionEvent event) {
        milestoneTripCombo.getSelectionModel().clearSelection();
        milestoneTitleField.clear();
        milestoneXpSpinner.getValueFactory().setValue(0);
        milestoneCompletedCheck.setSelected(false);
        milestoneDescriptionArea.clear();
        milestonesTable.getSelectionModel().clearSelection();
    }

    private void populateMilestoneForm(TripMilestone milestone) {
        if (milestone == null) {
            handleMilestoneReset(null);
            return;
        }
        milestoneTripCombo.getSelectionModel().select(milestone.getTrip());
        milestoneTitleField.setText(milestone.getTitle());
        milestoneXpSpinner.getValueFactory().setValue(milestone.getXpReward());
        milestoneCompletedCheck.setSelected(Boolean.TRUE.equals(milestone.getCompleted()));
        milestoneDescriptionArea.setText(milestone.getDescription());
    }

    @FXML
    private void handleReviewSave(ActionEvent event) {
        try {
            Review selected = reviewsTable.getSelectionModel().getSelectedItem();
            Booking booking = reviewBookingCombo.getSelectionModel().getSelectedItem();
            Integer rating = reviewRatingSpinner.getValue();
            String comment = reviewCommentArea.getText();

            if (booking == null) {
                showAlert("Booking is required.");
                return;
            }

            Property property = booking.getProperty();
            User reviewer = booking.getGuest();

            if (selected == null) {
                Review created = reviewService.createReview(booking, reviewer, property, rating, comment);
                loadAllData();
                reviewsTable.getSelectionModel().select(created);
                updateReviewDerivedFields(booking);
                setStatus("Review created.");
            } else {
                selected.setBooking(booking);
                selected.setProperty(property);
                selected.setReviewer(reviewer);
                selected.setRating(rating);
                selected.setComment(comment);
                reviewService.saveReview(selected);
                loadAllData();
                reviewsTable.getSelectionModel().select(selected);
                updateReviewDerivedFields(booking);
                setStatus("Review updated.");
            }
        } catch (Exception ex) {
            showAlert("Review save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleReviewDelete(ActionEvent event) {
        Review selected = reviewsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a review to delete.");
            return;
        }
        try {
            reviewService.deleteReview(selected.getId());
            reviews.remove(selected);
            handleReviewReset(null);
            setStatus("Review deleted.");
        } catch (Exception ex) {
            showAlert("Review delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleReviewReset(ActionEvent event) {
        reviewBookingCombo.getSelectionModel().clearSelection();
        updateReviewDerivedFields(null);
        reviewRatingSpinner.getValueFactory().setValue(5);
        reviewCommentArea.clear();
        reviewsTable.getSelectionModel().clearSelection();
    }

    private void populateReviewForm(Review review) {
        if (review == null) {
            handleReviewReset(null);
            return;
        }
        reviewBookingCombo.getSelectionModel().select(review.getBooking());
        updateReviewDerivedFields(review.getBooking());
        reviewRatingSpinner.getValueFactory().setValue(review.getRating());
        reviewCommentArea.setText(review.getComment());
    }

    private void updateReviewDerivedFields(Booking booking) {
        if (booking == null) {
            reviewPropertyLabel.setText("");
            reviewUserLabel.setText("");
            return;
        }
        reviewPropertyLabel.setText(booking.getProperty() != null ? booking.getProperty().getTitle() : "");
        reviewUserLabel.setText(booking.getGuest() != null ? booking.getGuest().getUsername() : "");
    }

    @FXML
    private void handleCommentCreate(ActionEvent event) {
        commentsTable.getSelectionModel().clearSelection();
        handleCommentSave(event);
    }

    @FXML
    private void handleCommentSave(ActionEvent event) {
        try {
            ReviewComment selected = commentsTable.getSelectionModel().getSelectedItem();
            Review review = commentReviewCombo.getSelectionModel().getSelectedItem();
            User user = commentUserCombo.getSelectionModel().getSelectedItem();
            String text = commentTextArea.getText();

            if (review == null || user == null) {
                showAlert("Review and user are required for a comment.");
                return;
            }

            if (selected == null) {
                ReviewComment created = reviewCommentService.addComment(review, user, text);
                loadAllData();
                commentsTable.getSelectionModel().select(created);
                setStatus("Comment created.");
            } else {
                selected.setReview(review);
                selected.setCommenter(user);
                selected.setComment(text);
                ReviewComment saved = reviewCommentService.saveExisting(selected);
                loadAllData();
                commentsTable.getSelectionModel().select(saved);
                setStatus("Comment updated.");
            }
        } catch (Exception ex) {
            showAlert("Comment save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCommentDelete(ActionEvent event) {
        ReviewComment selected = commentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a comment to delete.");
            return;
        }
        try {
            reviewCommentService.deleteComment(selected.getId());
            comments.remove(selected);
            handleCommentReset(null);
            setStatus("Comment deleted.");
        } catch (Exception ex) {
            showAlert("Comment delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCommentReset(ActionEvent event) {
        commentReviewCombo.getSelectionModel().clearSelection();
        commentUserCombo.getSelectionModel().clearSelection();
        commentTextArea.clear();
        commentsTable.getSelectionModel().clearSelection();
    }

    private void populateCommentForm(ReviewComment comment) {
        if (comment == null) {
            handleCommentReset(null);
            return;
        }
        commentReviewCombo.getSelectionModel().select(comment.getReview());
        commentUserCombo.getSelectionModel().select(comment.getCommenter());
        commentTextArea.setText(comment.getComment());
    }

    private String snippet(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 80 ? value : value.substring(0, 77) + "...";
    }

    @FXML
    private void handleServiceCreate(ActionEvent event) {
        servicesTable.getSelectionModel().clearSelection();
        handleServiceSave(event);
    }

    @FXML
    private void handleServiceSave(ActionEvent event) {
        try {
            ServiceOffering selected = servicesTable.getSelectionModel().getSelectedItem();
            ServiceOffering service = selected != null ? selected : new ServiceOffering();
            service.setName(text(serviceNameField));
            service.setDescription(serviceDescArea.getText());
            service.setPrice(parseDecimal(servicePriceField.getText()));

            ServiceOffering saved = serviceOfferingService.save(service);
            loadAllData();
            servicesTable.getSelectionModel().select(saved);
            setStatus("Service saved.");
        } catch (Exception ex) {
            showAlert("Service save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleServiceDelete(ActionEvent event) {
        ServiceOffering selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a service to delete.");
            return;
        }
        try {
            serviceOfferingService.delete(selected.getId());
            services.remove(selected);
            handleServiceReset(null);
            setStatus("Service deleted.");
        } catch (Exception ex) {
            showAlert("Service delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleServiceReset(ActionEvent event) {
        serviceNameField.clear();
        servicePriceField.clear();
        serviceDescArea.clear();
        servicesTable.getSelectionModel().clearSelection();
    }

    private void populateServiceForm(ServiceOffering service) {
        if (service == null) {
            handleServiceReset(null);
            return;
        }
        serviceNameField.setText(service.getName());
        servicePriceField.setText(service.getPrice() != null ? service.getPrice().toPlainString() : "");
        serviceDescArea.setText(service.getDescription());
    }

    @FXML
    private void handleBookingServiceCreate(ActionEvent event) {
        bookingServicesTable.getSelectionModel().clearSelection();
        handleBookingServiceSave(event);
    }

    @FXML
    private void handleBookingServiceSave(ActionEvent event) {
        try {
            BookingServiceItem selected = bookingServicesTable.getSelectionModel().getSelectedItem();
            BookingServiceItem item = selected != null ? selected : new BookingServiceItem();
            item.setBooking(bookingServiceBookingCombo.getSelectionModel().getSelectedItem());
            item.setService(bookingServiceServiceCombo.getSelectionModel().getSelectedItem());
            item.setQuantity(bookingServiceQtySpinner.getValue());
            item.setPriceAtBooking(parseDecimal(bookingServicePriceField.getText()));

            BookingServiceItem saved = bookingServiceItemService.save(item);
            loadAllData();
            bookingServicesTable.getSelectionModel().select(saved);
            setStatus("Booking service saved.");
        } catch (Exception ex) {
            showAlert("Booking service save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBookingServiceDelete(ActionEvent event) {
        BookingServiceItem selected = bookingServicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a booking service to delete.");
            return;
        }
        try {
            bookingServiceItemService.delete(selected.getId());
            bookingServices.remove(selected);
            handleBookingServiceReset(null);
            setStatus("Booking service deleted.");
        } catch (Exception ex) {
            showAlert("Booking service delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleBookingServiceReset(ActionEvent event) {
        bookingServiceBookingCombo.getSelectionModel().clearSelection();
        bookingServiceServiceCombo.getSelectionModel().clearSelection();
        bookingServiceQtySpinner.getValueFactory().setValue(1);
        bookingServicePriceField.clear();
        bookingServicesTable.getSelectionModel().clearSelection();
    }

    private void populateBookingServiceForm(BookingServiceItem item) {
        if (item == null) {
            handleBookingServiceReset(null);
            return;
        }
        bookingServiceBookingCombo.getSelectionModel().select(item.getBooking());
        bookingServiceServiceCombo.getSelectionModel().select(item.getService());
        bookingServiceQtySpinner.getValueFactory().setValue(item.getQuantity() != null ? item.getQuantity() : 1);
        bookingServicePriceField.setText(item.getPriceAtBooking() != null ? item.getPriceAtBooking().toPlainString() : "");
    }

    @FXML
    private void handleOfferCreate(ActionEvent event) {
        offersTable.getSelectionModel().clearSelection();
        handleOfferSave(event);
    }

    @FXML
    private void handleOfferSave(ActionEvent event) {
        try {
            Offer selected = offersTable.getSelectionModel().getSelectedItem();
            Offer offer = selected != null ? selected : new Offer();
            offer.setProperty(offerPropertyCombo.getSelectionModel().getSelectedItem());
            offer.setTitle(text(offerTitleField));
            offer.setDescription(offerDescriptionArea.getText());
            offer.setDiscountPercentage(parseDecimal(offerDiscountField.getText()));
            offer.setStartDate(offerStartDatePicker.getValue());
            offer.setEndDate(offerEndDatePicker.getValue());
            offer.setIsActive(offerActiveCheck.isSelected());

            Offer saved = offerService.save(offer);
            loadAllData();
            offersTable.getSelectionModel().select(saved);
            setStatus("Offer saved.");
        } catch (Exception ex) {
            showAlert("Offer save failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleOfferDelete(ActionEvent event) {
        Offer selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select an offer to delete.");
            return;
        }
        try {
            offerService.delete(selected.getId());
            offers.remove(selected);
            handleOfferReset(null);
            setStatus("Offer deleted.");
        } catch (Exception ex) {
            showAlert("Offer delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void handleOfferReset(ActionEvent event) {
        offerPropertyCombo.getSelectionModel().clearSelection();
        offerTitleField.clear();
        offerDiscountField.clear();
        offerStartDatePicker.setValue(null);
        offerEndDatePicker.setValue(null);
        offerActiveCheck.setSelected(false);
        offerDescriptionArea.clear();
        offersTable.getSelectionModel().clearSelection();
    }

    private void populateOfferForm(Offer offer) {
        if (offer == null) {
            handleOfferReset(null);
            return;
        }
        offerPropertyCombo.getSelectionModel().select(offer.getProperty());
        offerTitleField.setText(offer.getTitle());
        offerDiscountField.setText(offer.getDiscountPercentage() != null ? offer.getDiscountPercentage().toPlainString() : "");
        offerStartDatePicker.setValue(offer.getStartDate());
        offerEndDatePicker.setValue(offer.getEndDate());
        offerActiveCheck.setSelected(Boolean.TRUE.equals(offer.getIsActive()));
        offerDescriptionArea.setText(offer.getDescription());
    }

    private String text(TextInputControl control) {
        return control.getText() == null ? "" : control.getText().trim();
    }

    private String text(TextInputControl control, String fallback) {
        String value = text(control);
        return value.isBlank() ? fallback : value;
    }

    private BigDecimal parseDecimal(String input) {
        if (input == null || input.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(input.trim());
    }

    private Integer parseInt(String input, int fallback) {
        try {
            return input == null || input.isBlank() ? fallback : Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Admin");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
