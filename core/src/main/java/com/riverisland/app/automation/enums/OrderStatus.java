package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
public enum OrderStatus {
    AWAITING_PSP_NOTIFICATION("AwaitingPspNotification"),
    NEW_ORDER("New order");

    String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}