package com.riverisland.app.automation.enums;

import java.util.Random;

/**
 * Created by Prashant Ramcharan on 07/06/2017
 */
public enum MaleCategory {
    JEANS("Jeans"),
    SHIRTS("Shirts"),
    TOPS("Tops"),
    SHORTS("Shorts");

    String name;

    MaleCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MaleCategory randomise() {
        Random random = new Random();
        return MaleCategory.values()[random.nextInt(MaleCategory.values().length)];
    }
}