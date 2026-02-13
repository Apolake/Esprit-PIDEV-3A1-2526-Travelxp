package com.travelxp.util;

public enum FXMLView {

    LOGIN {
        @Override
        public String getTitle() {
            return "TravelXP - Login";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/login.fxml";
        }
    },

    DASHBOARD {
        @Override
        public String getTitle() {
            return "TravelXP - Dashboard";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/dashboard.fxml";
        }
    },

    PROPERTY_LIST {
        @Override
        public String getTitle() {
            return "TravelXP - Browse Properties";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/property-list.fxml";
        }
    },

    PROPERTY_DETAIL {
        @Override
        public String getTitle() {
            return "TravelXP - Property Details";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/property-detail.fxml";
        }
    },

    BOOKING {
        @Override
        public String getTitle() {
            return "TravelXP - Make a Booking";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/booking.fxml";
        }
    },

    PROFILE {
        @Override
        public String getTitle() {
            return "TravelXP - Profile";
        }

        @Override
        public String getFxmlFile() {
            return "/fxml/profile.fxml";
        }
    };

    public abstract String getTitle();
    public abstract String getFxmlFile();
}
