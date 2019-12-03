package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 07/06/2017
 */
public enum Category {
    WOMEN("Women", 1),
    MEN("Men", 2),
    GIRLS("Girls", 3),
    BOYS("Boys", 4),
    KIDS("Kids", null);

    String name;
    Integer index;

    Category(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public Integer getIndex() {
        return index;
    }

    public String getPluralName() {
        switch (this) {
            case MEN:
                return "Men's";

            case WOMEN:
                return "Women's";
        }
        return name;
    }

    public String getRandomSubCategory() {
        switch (this) {
            case WOMEN:
            case GIRLS:
                return FemaleCategory.randomise().getName();

            case MEN:
            case BOYS:
                return MaleCategory.randomise().getName();
        }
        return null;
    }
}
