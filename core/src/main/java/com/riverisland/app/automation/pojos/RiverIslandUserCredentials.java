package com.riverisland.app.automation.pojos;

/**
 * Created by Prashant Ramcharan on 22/03/2017
 */
public class RiverIslandUserCredentials {
    private String email;
    private String password;

    public RiverIslandUserCredentials() {
    }

    public RiverIslandUserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
