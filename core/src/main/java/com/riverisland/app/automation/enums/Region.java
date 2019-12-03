package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 22/03/2017
 */
public enum Region {
    GB(Currency.GBP, "United Kingdom"),
    AU(Currency.AUD, "Australia"),
    FR(Currency.EUR, "France"),
    NL(Currency.EUR, "Netherlands"),
    DE(Currency.EUR, "Germany"),
    SE(Currency.SEK, "Sweden"),
    US(Currency.USD, "United States"),
    EU(Currency.EUR, "Spain");

    private Currency currency;
    private String description;

    Region(Currency currency, String description) {
        this.currency = currency;
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteFromRegion(Region region) {
        switch (region) {
            case GB:
                return "UK";
            case EU:
                return "ES";
        }
        return region.name();
    }
}
