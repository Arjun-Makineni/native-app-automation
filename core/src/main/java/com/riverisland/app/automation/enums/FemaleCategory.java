package com.riverisland.app.automation.enums;

import java.util.Random;

/**
 * Created by Prashant Ramcharan on 07/06/2017
 */
public enum FemaleCategory {
    DRESSES("Dresses"),
    TOPS("Tops"),
    SKIRTS("Skirts"),
    JEANS("Jeans");

    String name;

    FemaleCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FemaleCategory randomise() {
        Random random = new Random();
        return FemaleCategory.values()[random.nextInt(FemaleCategory.values().length)];
    }
}
