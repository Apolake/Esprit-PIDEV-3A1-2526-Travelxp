package com.travelxp.config;

import com.travelxp.util.StageManager;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class JavaFXConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Lazy
    @Scope("prototype")
    public StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(applicationContext, stage);
    }
}
