package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 23/06/2017
 */
public enum SortBy {
    RELEVANCE("Relevance"),
    LATEST("Latest"),
    OLDEST("Oldest"),
    PRICE_LOW_TO_HIGH("Price - Low to High"),
    PRICE_HIGH_TO_LOW("Price - High to Low");

    String description;

    SortBy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}