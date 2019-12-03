package com.riverisland.app.automation.config;

import com.riverisland.app.automation.enums.Provider;
import com.riverisland.app.automation.pojos.AppiumServer;
import com.riverisland.app.automation.pojos.Environment;
import com.riverisland.app.automation.pojos.RiverIslandUserCredentials;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.utils.YamlLoaderUtils;

import java.util.List;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class GlobalConfig {
    private Provider provider;
    private RiverIslandUserCredentials riverIslandUserCredentials;
    private Environment environment;
    private int retryTestFailureLimit;
    private List<TcplApiCredentials> tcplApiCredentials;
    private List<AppiumServer> appiumServer;

    private static GlobalConfig instance;
    private final static String GLOBAL_CONFIG_YAML = "global-config.yml";

    public GlobalConfig() {
    }

    public Provider getProvider() {
        return Provider.valueOf(System.getProperty("provider", provider.name()).toUpperCase());
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public RiverIslandUserCredentials getRiverIslandUserCredentials() {
        return riverIslandUserCredentials;
    }

    public void setRiverIslandUserCredentials(RiverIslandUserCredentials riverIslandUserCredentials) {
        this.riverIslandUserCredentials = riverIslandUserCredentials;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getRetryTestFailureLimit() {
        return retryTestFailureLimit;
    }

    public void setRetryTestFailureLimit(int retryTestFailureLimit) {
        try {
            this.retryTestFailureLimit = Integer.parseInt(System.getProperty("retryTestFailureLimit", String.valueOf(retryTestFailureLimit)));
        } catch (NumberFormatException nfe) {
            this.retryTestFailureLimit = retryTestFailureLimit;
        }
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public List<TcplApiCredentials> getTcplApiCredentials() {
        return tcplApiCredentials;
    }

    public void setTcplApiCredentials(List<TcplApiCredentials> tcplApiCredentials) {
        this.tcplApiCredentials = tcplApiCredentials;
    }

    public List<AppiumServer> getAppiumServer() {
        return appiumServer;
    }

    public void setAppiumServer(List<AppiumServer> appiumServer) {
        this.appiumServer = appiumServer;
    }

    public static GlobalConfig instance() {
        if (instance == null) {
            instance = (YamlLoaderUtils.loadYamlDataCollection(GLOBAL_CONFIG_YAML, GlobalConfig.class)).stream().findFirst().orElseThrow(() -> new RuntimeException("Missing global config yaml!"));
        }
        return instance;
    }

    public static boolean isAppRunningLocally() {
        return instance().getProvider().equals(Provider.LOCAL);
    }

    public static boolean isAppRunningOnRealDevice() {
        return instance().getProvider().equals(Provider.CLOUD);
    }

    public static String getApiAuthForCloudProvider() {
        return instance().getAppiumServer().stream().filter(t -> t.getProvider().equals(Provider.CLOUD)).findFirst().get().getApiAuth();
    }
}
