package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.AppPage;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidAppPage extends AndroidCorePage implements AppPage<AndroidAppPage> {

    public AndroidAppPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidAppPage goBack() {
        appDriver.back().pause(500);
        return this;
    }

    @Override
    public String getAlertMessage() {
        return appDriver.retrieveMobileElementText(androidId.apply("dialog_message"));
    }

    @Override
    public AndroidAppPage acceptAlert() {
        appDriver.tap("OK");
        return this;
    }

    @Override
    public AndroidAppPage acceptAlert(String name) {
        appDriver.tap(name);
        return this;
    }

    @Override
    public AndroidAppPage closeDialog() {
        throw new NotImplementedException("Method not supported.");
    }
}
