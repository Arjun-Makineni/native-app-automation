package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.SwipeElementDirection;

/**
 * Created by Prashant Ramcharan on 31/01/2018
 */
public interface OnboardingPage<T> {
    T acceptPushNotifications(boolean checkFirst);

    T acceptPushNotificationsAndSkip(boolean checkFirst);

    T declinePushNotifications();

    T swipeThroughOnboardingPages(SwipeElementDirection direction);

    T skipOnboardingPage();

    T shopOnboardingPage(Category category);
}