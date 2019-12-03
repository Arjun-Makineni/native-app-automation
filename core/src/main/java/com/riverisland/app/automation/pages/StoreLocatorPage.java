package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface StoreLocatorPage<T> {
    T viewMap();

    T viewLocalStores();

    T viewAllStores();

    Boolean hasLocalStores();

    T viewStore(String store);

    T viewStore(String postcode, String store);

    String getStoreInfoName();

    String getStoreInfoAddress();

    String getStoreInfoTelephone();

    T getDirections();
}
