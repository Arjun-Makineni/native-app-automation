package com.riverisland.ios.mobile.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.StoreLocatorPage;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileStoreLocatorPage implements StoreLocatorPage<IosMobileStoreLocatorPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileStoreLocatorPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileStoreLocatorPage viewMap() {
        checkAndAllowAccess();
        appDriver.tap("Map");
        return this;
    }

    @Override
    public IosMobileStoreLocatorPage viewLocalStores() {
        checkAndAllowAccess();
        appDriver.tap("Local Stores");
        return this;
    }

    @Override
    public IosMobileStoreLocatorPage viewAllStores() {
        checkAndAllowAccess();
        appDriver.tap("All Stores");
        return this;
    }

    @Override
    public Boolean hasLocalStores() {
        return appDriver.retrievePresentMobileElements(By.xpath("//XCUIElementTypeTable/XCUIElementTypeCell")).size() > 0;
    }

    @Override
    public IosMobileStoreLocatorPage viewStore(String store) {
        appDriver.tap(store);
        return this;
    }

    @Override
    public IosMobileStoreLocatorPage viewStore(String postcode, String store) {
        throw new NotImplementedException("Method not required.");
    }

    private final String storeInfoLocator = "//XCUIElementTypeImage[@name='icnLocation']//following::XCUIElementTypeStaticText[%s]";

    @Override
    public String getStoreInfoName() {
        return appDriver.retrieveMobileElementText(By.xpath(String.format(storeInfoLocator, 1)));
    }

    @Override
    public String getStoreInfoAddress() {
        return appDriver.retrieveMobileElementText(By.xpath(String.format(storeInfoLocator, 2)));
    }

    @Override
    public String getStoreInfoTelephone() {
        return appDriver.retrievePresentMobileElement(By.xpath("//XCUIElementTypeImage[@name='icnTelephone']//following::XCUIElementTypeStaticText")).getText();
    }

    @Override
    public IosMobileStoreLocatorPage getDirections() {
        appDriver
                .tap("GET DIRECTIONS")
                .tap("Open");
        return this;
    }

    private void checkAndAllowAccess() {
        if (appDriver.isDisplayed(By.name("Allow"), 2)) {
            appDriver.tap("Allow");
        }
    }
}