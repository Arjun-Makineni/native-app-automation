package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 22/03/2017
 */
public enum Currency {
    GBP("£"),
    SEK("kr"),
    EUR("€"),
    AUD("$"),
    USD("$");

    String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
