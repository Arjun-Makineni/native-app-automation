package com.riverisland.app.automation.pojos;

/**
 * Created by Prashant Ramcharan on 22/03/2017
 */
public class PayPalCredentials {
    private String userName;
    private String password;

    public PayPalCredentials(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
