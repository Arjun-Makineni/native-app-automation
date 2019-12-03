package com.riverisland.app.automation.utils;

import com.riverisland.app.automation.enums.Currency;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import java.util.regex.Pattern;

/**
 * Created by Prashant Ramcharan on 07/02/2018
 */
public final class MonetaryAmountUtils {
    private final static Pattern AMOUNT_PATTERN = Pattern.compile("^\\d{0,10}(\\.\\d{1,2})?$");

    public static boolean isValidAmount(Currency currency, String amount) {
        RiverIslandLogger.getInfoLogger(MonetaryAmountUtils.class).info(String.format("Validating that %s is a valid monetary value", amount));

        if (currency == Currency.SEK) {
            return Pattern.compile("\\d+(\\s?kr)").matcher(amount).matches();
        } else {
            return amount.startsWith(currency.getSymbol()) && AMOUNT_PATTERN.matcher(amount.substring(1)).matches();
        }
    }
}