package com.travelxp.controllers;

import com.travelxp.services.MapService;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class MapController {

    @FXML
    private WebView mapWebView;

    /**
     * Called by the loader after FXML fields have been injected.
     * Use this method to initialise the map contents.
     */
    public void initialize() {
        // nothing yet
    }

    /**
     * Populate the web view with HTML for the given coordinates.
     */
    public void initLocation(double latitude, double longitude, String title) {
        String html = new MapService().generateMapHtml(latitude, longitude, title);
        mapWebView.getEngine().loadContent(html);
    }
}
