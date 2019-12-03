package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface AppPage<T> {
    T goBack();

    String getAlertMessage();

    T acceptAlert();

    T acceptAlert(String name);

    T closeDialog();
}
