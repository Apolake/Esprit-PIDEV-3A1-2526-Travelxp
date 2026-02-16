package com.travelxp.controller;

import com.travelxp.model.Review;
import com.travelxp.model.ReviewComment;
import com.travelxp.model.User;
import com.travelxp.service.ReviewCommentService;
import com.travelxp.service.ReviewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ReviewCommentsController implements Initializable {

    @FXML
    private TableView<Review> reviewsTable;

    @FXML
    private TableColumn<Review, String> propertyColumn;

    @FXML
    private TableColumn<Review, String> reviewerColumn;

    @FXML
    private TableColumn<Review, Integer> ratingColumn;

    @FXML
    private ListView<String> commentsList;

    @FXML
    private TextArea commentInput;

    @FXML
    private Button addCommentButton;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewCommentService reviewCommentService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadReviews();
        reviewsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> loadComments(newSel));
    }

    private void setupTable() {
        propertyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getProperty().getTitle()
        ));
        reviewerColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getReviewer().getFullName()
        ));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        ObservableList<Review> data = FXCollections.observableArrayList(reviews);
        reviewsTable.setItems(data);
        if (!data.isEmpty()) {
            reviewsTable.getSelectionModel().selectFirst();
            loadComments(data.get(0));
        }
    }

    private void loadComments(Review review) {
        commentsList.getItems().clear();
        if (review == null) {
            return;
        }
        List<ReviewComment> comments = reviewCommentService.getCommentsForReview(review);
        for (ReviewComment c : comments) {
            String line = String.format("%s (%s): %s",
                c.getCommenter().getFullName(),
                c.getCreatedAt() != null ? formatter.format(c.getCreatedAt()) : "",
                c.getComment());
            commentsList.getItems().add(line);
        }
    }

    @FXML
    private void handleAddComment() {
        Review selected = reviewsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Select a review first.");
            return;
        }
        User currentUser = LoginController.getCurrentUser();
        if (currentUser == null) {
            showWarning("You must be logged in to comment.");
            return;
        }
        String text = commentInput.getText();
        try {
            reviewCommentService.addComment(selected, currentUser, text);
            commentInput.clear();
            loadComments(selected);
        } catch (Exception e) {
            showWarning(e.getMessage());
        }
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Comments");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
