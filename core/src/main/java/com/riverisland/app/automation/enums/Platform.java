package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public enum Platform {
    IOS("iOS"),
    ANDROID("Android");

    String description;

    Platform(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
