package com.travelxp.util;

import com.travelxp.controller.ShellController;
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
    private Parent shellRoot;
    private ShellController shellController;

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

            if (view == FXMLView.LOGIN) {
                // login stays standalone (no shell)
                shellRoot = null;
                shellController = null;
                show(viewRootNodeHierarchy, view.getTitle(), false);
                return;
            }

            ensureShell();
            shellController.setContent(viewRootNodeHierarchy);
            show(shellRoot, view.getTitle(), true);
        };

        if (Platform.isFxApplicationThread()) {
            switcher.run();
        } else {
            Platform.runLater(switcher);
        }
    }

    private void show(Parent rootNode, String title, boolean fullscreen) {
        Scene scene = prepareScene(rootNode);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);

        if (fullscreen) {
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreen(true);
        } else {
            primaryStage.setFullScreen(false);
        }

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

    private void ensureShell() {
        if (shellRoot != null && shellController != null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shell.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            shellRoot = loader.load();
            shellController = loader.getController();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new IllegalStateException("Unable to load shell layout", exception);
        }
    }
}
