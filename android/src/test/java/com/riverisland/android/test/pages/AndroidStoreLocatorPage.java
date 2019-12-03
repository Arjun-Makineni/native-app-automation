package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.StoreLocatorPage;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidStoreLocatorPage extends AndroidCorePage implements StoreLocatorPage<AndroidStoreLocatorPage> {

    public AndroidStoreLocatorPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidStoreLocatorPage viewMap() {
        appDriver.tap("Map");
        return this;
    }

    @Override
    public AndroidStoreLocatorPage viewLocalStores() {
        appDriver.tap("Local Stores");
        return this;
    }

    @Override
    public AndroidStoreLocatorPage viewAllStores() {
        appDriver.tap("All Stores");
        return this;
    }

    @Override
    public Boolean hasLocalStores() {
        return appDriver.retrieveMobileElements(androidId.apply("stores_title_text")).size() > 0;
    }

    @Override
    public AndroidStoreLocatorPage viewStore(String store) {
        appDriver.scrollIntoViewAndTap(store);
        return this;
    }

    @Override
    public AndroidStoreLocatorPage viewStore(String postcode, String store) {
        throw new NotImplementedException("Method not required.");
    }

    @Override
    public String getStoreInfoName() {
        return appDriver.retrieveMobileElementText(androidId.apply("selected_store_name_text"));
    }

    @Override
    public String getStoreInfoAddress() {
        return appDriver.retrieveMobileElementText(androidId.apply("selected_store_address_text"));
    }

    @Override
    public String getStoreInfoTelephone() {
        return appDriver.retrieveMobileElementText(androidId.apply("selected_stores_phone_number_text"));
    }

    @Override
    public AndroidStoreLocatorPage getDirections() {
        throw new NotImplementedException("Method not used.");
    }
}
