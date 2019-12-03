package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 24/01/2018
 */
public interface ScanInStorePage<T> {
    T openNearestStore();

    T scanBarcodeManually(String barcode);
}
