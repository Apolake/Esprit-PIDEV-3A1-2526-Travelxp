package com.travelxp.utils;

import javafx.scene.Scene;

public class ThemeManager {
    private static boolean isDark = true;

    public static void toggleTheme(Scene scene) {
        isDark = !isDark;
        applyTheme(scene);
    }

    public static void applyTheme(Scene scene) {
        if (isDark) {
            scene.getRoot().getStyleClass().remove("light-theme");
        } else {
            if (!scene.getRoot().getStyleClass().contains("light-theme")) {
                scene.getRoot().getStyleClass().add("light-theme");
            }
        }
    }

    public static boolean isDark() {
        return isDark;
    }
}
