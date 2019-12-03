package com.riverisland.ios.mobile.test.pages;

import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.OnboardingPage;

/**
 * Created by Prashant Ramcharan on 31/01/2018
 */
public class IosMobileOnboardingPage implements OnboardingPage<IosMobileOnboardingPage> {
    protected RiverIslandNativeAppDriver appDriver;

    public IosMobileOnboardingPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileOnboardingPage acceptPushNotifications(boolean checkFirst) {
        checkAndAllowAccess();

        return this;
    }

    @Override
    public IosMobileOnboardingPage acceptPushNotificationsAndSkip(boolean checkFirst) {

    	if (isOnboardingDisplayed()) {
    		skipOnboardingPage();
    	}
    	return acceptPushNotifications(checkFirst);
    }

    @Override
    public IosMobileOnboardingPage declinePushNotifications() {
        appDriver.dismissAlert();
        return this;
    }

    @Override
    public IosMobileOnboardingPage swipeThroughOnboardingPages(SwipeElementDirection direction) {
        appDriver.swipe(direction);
        return this;
    }

    @Override
    public IosMobileOnboardingPage skipOnboardingPage() {
        if (appDriver.isDisplayed(By.name("Skip"), 5)) {
            appDriver.tap(appDriver.retrieveMobileElement("Skip"));
        }
        return this;
    }

    @Override
    public IosMobileOnboardingPage shopOnboardingPage(Category category) {
        appDriver.tap("Shop " + category.name());
        return this;
    }

    protected void checkAndAllowAccess() {
        if (appDriver.isDisplayed(By.name("Allow"), 2)) {
            appDriver.tap("Allow");
        }
    }
    
    protected boolean isOnboardingDisplayed() {
    	final By skipLocator = By.xpath("//XCUIElementTypeButton[@name = 'Skip']");
    	final By signInLocator = By.xpath("//XCUIElementTypeButton[@name = 'SIGN IN']");
    	
    	try {
    		appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(skipLocator), 5000);
    		return appDriver.isDisplayed(signInLocator) && appDriver.isDisplayed(skipLocator);
    	} catch (NoSuchElementException nse) {
    		return false;
    	}
    }
}