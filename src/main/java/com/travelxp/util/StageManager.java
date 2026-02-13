package com.travelxp.util;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class StageManager {

    private final Stage primaryStage;
    private final ApplicationContext applicationContext;
    private static final String APP_STYLESHEET = "/css/styles.css";

    public StageManager(ApplicationContext applicationContext, Stage stage) {
        this.primaryStage = stage;
        this.applicationContext = applicationContext;
    }

    public void switchScene(FXMLView view) {
        Runnable switcher = () -> {
            Parent viewRootNodeHierarchy = loadViewNodeHierarchy(view.getFxmlFile());
            if (viewRootNodeHierarchy == null) {
                throw new IllegalStateException("Unable to load view: " + view.name());
            }
            show(viewRootNodeHierarchy, view.getTitle());
        };

        if (Platform.isFxApplicationThread()) {
            switcher.run();
        } else {
            Platform.runLater(switcher);
        }
    }

    private void show(Parent rootNode, String title) {
        Scene scene = prepareScene(rootNode);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();

        try {
            primaryStage.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Scene prepareScene(Parent rootNode) {
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        attachStylesheet(scene);
        return scene;
    }

    private void attachStylesheet(Scene scene) {
        URL styleUrl = getClass().getResource(APP_STYLESHEET);
        if (styleUrl == null) {
            System.err.println("Stylesheet not found at " + APP_STYLESHEET);
            return;
        }

        String style = styleUrl.toExternalForm();
        if (!scene.getStylesheets().contains(style)) {
            scene.getStylesheets().add(style);
        }
    }

    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            loader.setControllerFactory(applicationContext::getBean);
            return loader.load();
        } catch (Exception exception) {
            // Surface loading problems instead of failing silently so navigation issues are obvious.
            exception.printStackTrace();
            return null;
        }
    }
}
