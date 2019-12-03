package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 20/03/2017
 */
public enum CardType {
    AMEX("American Express"),
    MASTERCARD("Mastercard"),
    VISA("Visa"),
    MAESTRO("Maestro"),
    NONE(null);

    String description;

    CardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
