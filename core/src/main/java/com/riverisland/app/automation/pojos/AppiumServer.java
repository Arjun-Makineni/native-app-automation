package com.riverisland.app.automation.pojos;

import com.riverisland.app.automation.enums.Provider;

import java.net.URL;

/**
 * Created by Prashant Ramcharan on 03/10/2017
 */
public class AppiumServer {
    private Provider provider;
    private URL url;
    private Boolean autoStart;
    private String apiAuth;

    public AppiumServer() {
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Boolean getAutoStart() {
        return autoStart;
    }

    public void setAutoStart(Boolean autoStart) {
        this.autoStart = autoStart;
    }

    public String getApiAuth() {
        return apiAuth;
    }

    public void setApiAuth(String apiAuth) {
        this.apiAuth = apiAuth;
    }
}