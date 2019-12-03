package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 01/06/2017
 */
public enum DeliveryType {
    HOME_DELIVERY("Home or business address"),
    COLLECT_FROM_STORE("Nearest River Island store"),
    DELIVER_TO_STORE("Local collection point");

    String description;

    DeliveryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
