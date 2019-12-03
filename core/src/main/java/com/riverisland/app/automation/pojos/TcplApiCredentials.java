package com.riverisland.app.automation.pojos;

/**
 * Created by Prashant Ramcharan on 20/10/2017
 */
public class TcplApiCredentials {
    private String environment;
    private String serviceUrl;
    private String apiKey ;

    public TcplApiCredentials() {
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}