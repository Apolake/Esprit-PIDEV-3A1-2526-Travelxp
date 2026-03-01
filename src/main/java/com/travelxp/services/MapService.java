package com.travelxp.services;

/**
 * Utility service that produces an HTML page which displays
 * an interactive map centered on the given coordinates.  The
 * controller is responsible for loading the returned HTML into
 * a JavaFX WebView.
 *
 * Currently uses OpenStreetMap via the Leaflet library (free).
 */
public class MapService {
    private static final String TEMPLATE = """
<!DOCTYPE html>
<html>
<head>
    <meta charset=\"utf-8\"/>
    <title>Property Location</title>
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet/dist/leaflet.css\" />
    <style>#map{height:100vh;width:100vw;}</style>
</head>
<body>
<div id=\"map\"></div>
<script src=\"https://unpkg.com/leaflet/dist/leaflet.js\"></script>
<script>
    var lat = %f;
    var lon = %f;
    var title = "%s";
    var map = L.map('map').setView([lat, lon], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
    L.marker([lat, lon]).addTo(map).bindPopup(title).openPopup();
</script>
</body>
</html>
""";

    /**
     * Build HTML that will show a centered map and marker.
     *
     * @param latitude  latitude of the point
     * @param longitude longitude of the point
     * @param title     text for the marker popup
     * @return HTML string ready to be loaded into a WebView
     */
    public String generateMapHtml(double latitude, double longitude, String title) {
        // escape double quotes in title
        String safe = title == null ? "" : title.replace("\"", "\\\"");
        return String.format(TEMPLATE, latitude, longitude, safe);
    }
}
