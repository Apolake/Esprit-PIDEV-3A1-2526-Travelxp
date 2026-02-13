package com.travelxp;

import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TravelXPApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private StageManager stageManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        // don't start Spring here
    }

    @Override
    public void start(Stage primaryStage) {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        // register primary JavaFX Stage before the context refresh so it can be injected
        springContext = new SpringApplicationBuilder(TravelXPApplication.class)
            .initializers(ctx -> ctx.getBeanFactory().registerSingleton("primaryStage", primaryStage))
            .run(args);

        stageManager = springContext.getBean(StageManager.class);
        stageManager.switchScene(FXMLView.LOGIN);
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}

