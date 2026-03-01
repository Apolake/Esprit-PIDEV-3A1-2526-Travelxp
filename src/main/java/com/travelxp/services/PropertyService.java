package com.travelxp.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.travelxp.models.Property;
import com.travelxp.utils.MyDB;

public class PropertyService {

    private final Connection cnx;

    public PropertyService() {
        cnx = MyDB.getInstance().getConnection();
    }

    // CREATE
    public void addProperty(Property property) throws SQLException {
        String sql = "INSERT INTO property (owner_id, title, description, property_type, address, city, country, bedrooms, bathrooms, max_guests, price_per_night, images, is_active, latitude, longitude, rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setLong(1, property.getOwnerId());
            ps.setString(2, property.getTitle());
            ps.setString(3, property.getDescription());
            ps.setString(4, property.getPropertyType());
            ps.setString(5, property.getAddress());
            ps.setString(6, property.getCity());
            ps.setString(7, property.getCountry());
            ps.setInt(8, property.getBedrooms());
            ps.setInt(9, property.getBathrooms());
            ps.setInt(10, property.getMaxGuests());
            ps.setBigDecimal(11, property.getPricePerNight());
            ps.setString(12, property.getImages());
            ps.setBoolean(13, property.getIsActive());
            ps.setObject(14, property.getLatitude());
            ps.setObject(15, property.getLongitude());
            ps.setObject(16, property.getRating());
            ps.executeUpdate();
        }
    }

    // READ
    public List<Property> getAllProperties() throws SQLException {
        List<Property> properties = new ArrayList<>();
        String sql = "SELECT * FROM property";
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Property p = mapResultSetToProperty(rs);
                properties.add(p);
            }
        }
        return properties;
    }

    public Property getPropertyById(Long id) throws SQLException {
        String sql = "SELECT * FROM property WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProperty(rs);
                }
            }
        }
        return null;
    }

    // UPDATE
    public void updateProperty(Property property) throws SQLException {
        String sql = "UPDATE property SET owner_id=?, title=?, description=?, property_type=?, address=?, city=?, country=?, bedrooms=?, bathrooms=?, max_guests=?, price_per_night=?, images=?, is_active=?, latitude=?, longitude=?, rating=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setLong(1, property.getOwnerId());
            ps.setString(2, property.getTitle());
            ps.setString(3, property.getDescription());
            ps.setString(4, property.getPropertyType());
            ps.setString(5, property.getAddress());
            ps.setString(6, property.getCity());
            ps.setString(7, property.getCountry());
            ps.setInt(8, property.getBedrooms());
            ps.setInt(9, property.getBathrooms());
            ps.setInt(10, property.getMaxGuests());
            ps.setBigDecimal(11, property.getPricePerNight());
            ps.setString(12, property.getImages());
            ps.setBoolean(13, property.getIsActive());
            ps.setObject(14, property.getLatitude());
            ps.setObject(15, property.getLongitude());
            ps.setObject(16, property.getRating());
            ps.setLong(17, property.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void deleteProperty(Long id) throws SQLException {
        String sql = "DELETE FROM property WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // additional convenience operations
    
    /**
     * Generic query supporting keyword search, simple filters and sorting.
     *
     * @param keyword   text to match against title/description (SQL LIKE, case-insensitive); null to ignore
     * @param city      filter by city name (exact match); null to ignore
     * @param minPrice  minimum price per night inclusive; null to ignore
     * @param maxPrice  maximum price per night inclusive; null to ignore
     * @param orderBy   column to sort by (must match a database column); null for natural order
     * @param ascending true to sort ASC, false for DESC
     */
    public List<Property> findProperties(
            String keyword,
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isActive,
            String orderBy,
            boolean ascending) throws SQLException {
        List<Property> properties = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT * FROM property");
        List<Object> params = new ArrayList<>();

        boolean whereAdded = false;
        if (keyword != null && !keyword.isBlank()) {
            sb.append(whereAdded ? " AND" : " WHERE");
            sb.append(" (LOWER(title) LIKE ? OR LOWER(description) LIKE ?)");
            String kw = "%" + keyword.toLowerCase().trim() + "%";
            params.add(kw);
            params.add(kw);
            whereAdded = true;
        }
        if (city != null && !city.isBlank()) {
            sb.append(whereAdded ? " AND" : " WHERE");
            sb.append(" city = ?");
            params.add(city.trim());
            whereAdded = true;
        }
        if (minPrice != null) {
            sb.append(whereAdded ? " AND" : " WHERE");
            sb.append(" price_per_night >= ?");
            params.add(minPrice);
            whereAdded = true;
        }
        if (maxPrice != null) {
            sb.append(whereAdded ? " AND" : " WHERE");
            sb.append(" price_per_night <= ?");
            params.add(maxPrice);
            whereAdded = true;
        }
        if (isActive != null) {
            sb.append(whereAdded ? " AND" : " WHERE");
            sb.append(" is_active = ?");
            params.add(isActive);
            whereAdded = true;
        }

        if (orderBy != null && !orderBy.isBlank()) {
            sb.append(" ORDER BY ").append(orderBy);
            sb.append(ascending ? " ASC" : " DESC");
        }

        try (PreparedStatement ps = cnx.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    properties.add(mapResultSetToProperty(rs));
                }
            }
        }
        return properties;
    }

    /**
     * Retrieve a property and eagerly load its offers.
     */
    public Property getPropertyWithOffers(Long id) throws SQLException {
        Property p = getPropertyById(id);
        if (p != null) {
            OfferService offerService = new OfferService();
            p.setOffers(offerService.getOffersByPropertyId(id));
        }
        return p;
    }

    /**
     * Load all properties with their offers attached (N+1 pattern).
     */
    public List<Property> getAllPropertiesWithOffers() throws SQLException {
        List<Property> props = getAllProperties();
        OfferService offerService = new OfferService();
        for (Property p : props) {
            p.setOffers(offerService.getOffersByPropertyId(p.getId()));
        }
        return props;
    }

    /* helper: map result set row into Property object, including coords */
    private Property mapResultSetToProperty(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long ownerId = rs.getLong("owner_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String propertyType = rs.getString("property_type");
        String address = rs.getString("address");
        String city = rs.getString("city");
        String country = rs.getString("country");
        Integer bedrooms = rs.getInt("bedrooms");
        Integer bathrooms = rs.getInt("bathrooms");
        Integer maxGuests = rs.getInt("max_guests");
        BigDecimal pricePerNight = rs.getBigDecimal("price_per_night");
        String images = rs.getString("images");
        Boolean isActive = rs.getBoolean("is_active");
        Double latitude = rs.getObject("latitude") != null ? rs.getDouble("latitude") : null;
        Double longitude = rs.getObject("longitude") != null ? rs.getDouble("longitude") : null;
        Double rating = rs.getObject("rating") != null ? rs.getDouble("rating") : null;
        Property prop = new Property(id, ownerId, title, description, propertyType, address, city, country, bedrooms, bathrooms, maxGuests, pricePerNight, images, isActive);
        prop.setLatitude(latitude);
        prop.setLongitude(longitude);
        prop.setRating(rating);
        return prop;
    }
}
