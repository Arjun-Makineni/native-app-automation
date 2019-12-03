package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 01/06/2017
 */
public enum DeliveryOption {
    STANDARD_DELIVERY("Standard delivery"),
    EXPRESS_DELIVERY("Express delivery"),
    NOMINATED_DAY_DELIVERY("Nominated Day Delivery"),
    PRECISE_DAY_DELIVERY("Precise Delivery");

    String description;

    DeliveryOption(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionInTitleCase() {
        return description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
    }

    public String getAndroidDescription() {
        switch (this) {
            case NOMINATED_DAY_DELIVERY:
                return "Nominated day delivery";

            case PRECISE_DAY_DELIVERY:
                return "Precise delivery";
        }
        return this.getDescription();
    }
}