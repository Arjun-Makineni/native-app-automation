package com.riverisland.android.test.pages;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ScanInStorePage;
import org.openqa.selenium.By;

/**
 * Created by Prashant Ramcharan on 24/01/2018
 */
public class AndroidScanInStorePage extends AndroidCorePage implements ScanInStorePage<AndroidScanInStorePage> {

    public AndroidScanInStorePage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidScanInStorePage openNearestStore() {
        appDriver.tap(androidId.apply("item_instore_store_name"));
        return this;
    }

    @Override
    public AndroidScanInStorePage scanBarcodeManually(String barcode) {
        if (GlobalConfig.isAppRunningOnRealDevice()) {
            appDriver.tap("ALLOW");
        } else {
            if (appDriver.isDisplayed(By.name("ALLOW"), 2)) {
                appDriver.tap("ALLOW");
            }
        }
        appDriver
                .back()
                .tap("Barcode number")
                .type("Barcode number", barcode);
        return this;
    }
}
