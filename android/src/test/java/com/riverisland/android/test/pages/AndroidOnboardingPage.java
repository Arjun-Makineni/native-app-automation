package com.riverisland.android.test.pages;

import org.openqa.selenium.By;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.OnboardingPage;

/**
 * Created by Prashant Ramcharan on 31/01/2018
 */
public class AndroidOnboardingPage extends AndroidCorePage implements OnboardingPage<AndroidOnboardingPage> {

    public AndroidOnboardingPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidOnboardingPage acceptPushNotifications(boolean checkFirst) {
        if (checkFirst) {
            if (!appDriver.isDisplayed(androidId.apply("dialog_title"), 2)) {
                return this;
            }
        }
        if (appDriver.isDisplayed(androidId.apply("dialog_positive_button"), 1)) {
        	appDriver.tap(androidId.apply("dialog_positive_button"));
        }
        return this;
    }

    @Override
    public AndroidOnboardingPage acceptPushNotificationsAndSkip(boolean checkFirst) {
        return skipOnboardingPage().acceptPushNotifications(checkFirst);
    }

    @Override
    public AndroidOnboardingPage declinePushNotifications() {
        return this;
    }

    @Override
    public AndroidOnboardingPage swipeThroughOnboardingPages(SwipeElementDirection direction) {
        final By onboardingImage = androidId.apply("onboarding_background_image");

        switch (direction) {
            case LEFT:
                appDriver.swipeLeft(onboardingImage);
                break;

            case RIGHT:
                appDriver.swipeRight(onboardingImage);
                break;
        }
        return this;
    }

    @Override
    public AndroidOnboardingPage skipOnboardingPage() {
        final By skipElement = androidId.apply("onboarding_skip");

        if (appDriver.isDisplayed(skipElement, 2)) {
            appDriver.tap(skipElement);
        }
        return this;
    }

    @Override
    public AndroidOnboardingPage shopOnboardingPage(Category category) {
        appDriver.tap(androidText.apply("Shop " + category.getName()));
        return this;
    }
}