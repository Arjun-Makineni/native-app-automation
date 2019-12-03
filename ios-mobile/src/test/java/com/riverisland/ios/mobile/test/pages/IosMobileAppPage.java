package com.riverisland.ios.mobile.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.AppPage;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileAppPage implements AppPage<IosMobileAppPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileAppPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileAppPage goBack() {
        appDriver.back().pause(500);
        return this;
    }

    @Override
    public String getAlertMessage() {
        return appDriver.waitFor(ExpectedConditions.alertIsPresent()).getAlertText();
    }

    @Override
    public IosMobileAppPage acceptAlert() {
        appDriver.acceptAlert();
        return this;
    }

    @Override
    public IosMobileAppPage acceptAlert(String name) {
        if (appDriver.isDisplayed(By.name(name), 5)) {
            appDriver.tap(name);
        }
        return this;
    }

    @Override
    public IosMobileAppPage closeDialog() {
        throw new NotImplementedException("Method not supported.");
    }
}
