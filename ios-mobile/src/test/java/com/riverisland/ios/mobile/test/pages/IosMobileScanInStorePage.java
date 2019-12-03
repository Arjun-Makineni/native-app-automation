package com.riverisland.ios.mobile.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ScanInStorePage;

import io.appium.java_client.MobileElement;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Created by Prashant Ramcharan on 24/01/2018
 */
public class IosMobileScanInStorePage implements ScanInStorePage<IosMobileScanInStorePage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileScanInStorePage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileScanInStorePage openNearestStore() {
        if (appDriver.getAlertText() != null) {
            appDriver.acceptAlert();
        }

        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
                .tap(By.className("XCUIElementTypeCell"), By.className("XCUIElementTypeStaticText"));
        return this;
    }

    @Override
    public IosMobileScanInStorePage scanBarcodeManually(String barcode) {
        if (appDriver.getAlertText() != null) {
            appDriver.acceptAlert();
        }

        appDriver
                .tap("Enter barcode manually");
        MobileElement barcodeElement = appDriver.retrieveMobileElement("Barcode number");
        appDriver
                .type(barcodeElement, barcode);
        barcodeElement.sendKeys(Keys.ENTER);
        return this;
    }
}
