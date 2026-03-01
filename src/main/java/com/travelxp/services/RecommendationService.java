package com.travelxp.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.travelxp.models.Offer;
import com.travelxp.models.Property;

/**
 * Service responsible for computing a recommendation score for each property
 * and returning a ranked list.  All business logic lives here; controllers
 * simply call {@link #rankProperties}.
 *
 * The score is a weighted sum of four normalized criteria:
 *   * active offer discount (higher is better)
 *   * price (lower price yields higher score)
 *   * rating (higher is better)
 *   * distance from a given location (closer yields higher score)
 *
 * Normalization maps each raw value into [0,1] based on the min/max seen in
 * the current property list; if all values are equal the normalized value
 * is treated as 0 to avoid division-by-zero.
 */
public class RecommendationService {

    private final PropertyService propertyService;

    public RecommendationService() {
        this.propertyService = new PropertyService();
    }

    /**
     * Compute and rank properties according to the supplied weights.
     *
     * @param userLat        latitude of the user's point of interest
     * @param userLon        longitude of the user's point of interest
     * @param offerWeight    importance of discount (0..1)
     * @param priceWeight    importance of price
     * @param ratingWeight   importance of rating
     * @param distanceWeight importance of distance
     * @return properties sorted by descending score
     * @throws SQLException when database operations fail
     */
    public List<Property> rankProperties(
            double userLat,
            double userLon,
            double offerWeight,
            double priceWeight,
            double ratingWeight,
            double distanceWeight) throws SQLException {

        List<Property> props = propertyService.getAllPropertiesWithOffers();
        if (props.isEmpty()) {
            return props;
        }

        Map<Property, Double> rawDiscount = new HashMap<>();
        Map<Property, Double> rawPrice = new HashMap<>();
        Map<Property, Double> rawRating = new HashMap<>();
        Map<Property, Double> rawDistance = new HashMap<>();

        // gather raw values
        for (Property p : props) {
            double discount = 0.0;
            for (Offer o : p.getOffers()) {
                if (Boolean.TRUE.equals(o.getIsActive()) && o.getDiscountPercentage() != null) {
                    discount = Math.max(discount, o.getDiscountPercentage().doubleValue());
                }
            }
            rawDiscount.put(p, discount);

            double price = p.getPricePerNight() != null ? p.getPricePerNight().doubleValue() : 0.0;
            rawPrice.put(p, price);

            double rating = (p.getRating() != null) ? p.getRating() : 0.0;
            rawRating.put(p, rating);

            double distance = computeDistance(userLat, userLon,
                    (p.getLatitude() != null) ? p.getLatitude() : userLat,
                    (p.getLongitude() != null) ? p.getLongitude() : userLon);
            rawDistance.put(p, distance);
        }

        // find ranges
        double minDisc = Double.MAX_VALUE, maxDisc = Double.MIN_VALUE;
        double minPrice = Double.MAX_VALUE, maxPrice = Double.MIN_VALUE;
        double minRating = Double.MAX_VALUE, maxRating = Double.MIN_VALUE;
        double minDist = Double.MAX_VALUE, maxDist = Double.MIN_VALUE;

        for (Property p : props) {
            double d = rawDiscount.get(p);
            minDisc = Math.min(minDisc, d);
            maxDisc = Math.max(maxDisc, d);

            double pr = rawPrice.get(p);
            minPrice = Math.min(minPrice, pr);
            maxPrice = Math.max(maxPrice, pr);

            double r = rawRating.get(p);
            minRating = Math.min(minRating, r);
            maxRating = Math.max(maxRating, r);

            double dist = rawDistance.get(p);
            minDist = Math.min(minDist, dist);
            maxDist = Math.max(maxDist, dist);
        }

        Map<Property, Double> scoreMap = new HashMap<>();
        for (Property p : props) {
            double nd = normalize(rawDiscount.get(p), minDisc, maxDisc);
            double np = 1.0 - normalize(rawPrice.get(p), minPrice, maxPrice);
            double nr = normalize(rawRating.get(p), minRating, maxRating);
            double ndist = 1.0 - normalize(rawDistance.get(p), minDist, maxDist);

            double score = offerWeight * nd
                         + priceWeight * np
                         + ratingWeight * nr
                         + distanceWeight * ndist;
            scoreMap.put(p, score);
        }

        List<Property> sorted = new ArrayList<>(props);
        sorted.sort((a, b) -> Double.compare(scoreMap.get(b), scoreMap.get(a)));
        return sorted;
    }

    /**
     * simple linear normalization into [0,1]; returns 0 if range is zero.
     */
    private double normalize(double value, double min, double max) {
        if (max <= min) {
            return 0.0;
        }
        return (value - min) / (max - min);
    }

    /**
     * Haversine formula to compute distance in kilometers between two points.
     */
    private double computeDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // earth radius km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}