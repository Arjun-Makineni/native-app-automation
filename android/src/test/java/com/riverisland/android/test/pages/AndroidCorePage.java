package com.riverisland.android.test.pages;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;

/**
 * Created by Prashant Ramcharan on 03/07/2017
 */
public class AndroidCorePage {
    protected RiverIslandNativeAppDriver appDriver;

    public AndroidCorePage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    protected Function<String, By> androidId = (id) -> By.id(String.format("%s:id/%s", appDriver.getAppiumConfig().getBundleId(), id));

    protected Function<String, By> androidText = (text) -> By.xpath(String.format("//*[@text='%s']", text));
    
    protected BiFunction<String, String, By> androidTextOrContentDesc = (className, text) -> By.xpath(String.format("//%s[@text='%s' or @content-desc='%s']", className, text, text));

    protected void allowLocationCheck(RiverIslandNativeAppDriver appDriver) {
        final By allowButton = By.id("com.android.packageinstaller:id/permission_allow_button");

        if (appDriver.isDisplayed(allowButton, 5)) {
            appDriver.tap(allowButton);
        }
    }

    protected void acceptAndroidDialog(RiverIslandNativeAppDriver appDriver) {
        appDriver.tap(androidId.apply("dialog_positive_button")).waitFor(ExpectedConditions.invisibilityOfElementLocated(androidId.apply("dialog_positive_button")));
    }
    
    private static final int GET_ME_NOW_TIMEOUT = 1;
    
    protected void acknowledgeGetMeNow() {
    	if (appDriver.isDisplayed(androidId.apply("inactive_social_proof_title"), GET_ME_NOW_TIMEOUT)) {
    		appDriver.tap("GO TO BAG")
    				 .back();
    	}
    }
}