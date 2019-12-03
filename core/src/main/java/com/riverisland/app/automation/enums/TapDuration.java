package com.riverisland.app.automation.enums;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public enum TapDuration {
    SHORT_TAP_ANDROID(200),
    SHORT_TAP_IOS(500),
    LONG_TAP(1500);

    private int duration;

    TapDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public static int getShortTapDuration(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            return SHORT_TAP_ANDROID.getDuration();
        }
        return SHORT_TAP_IOS.getDuration();
    }
}