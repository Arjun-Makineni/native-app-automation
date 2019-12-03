package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 23/06/2017
 */
public enum FilterBy {
    CATEGORIES("Categories"),
    SIZE("Size"),
    COLOUR("Colour"),
    PRICE("Price");

    String description;

    FilterBy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}