package com.riverisland.app.automation.config;

import com.google.common.collect.Lists;
import com.riverisland.app.automation.enums.DeviceType;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AppiumConfig {
    private DeviceType deviceType;
    private String bundleId;
    private Map<String, Object> capabilities;

    public AppiumConfig() {
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public Map<String, Object> getCapabilities() {
        applySystemProvidedCapabilities();
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }

    public String getLocalAppName() {
        final String appName = (String) capabilities.get("app");

        if (deviceType != DeviceType.REAL_DEVICE) {
            final URL app = Thread.currentThread().getContextClassLoader().getResource(appName);
            if (app != null) {
                return new File(app.getFile()).getAbsolutePath();
            }
        }
        return appName;
    }

    public boolean shouldResetApp() {
        return !((boolean) getCapabilities().getOrDefault("noReset", false));
    }

    private void applySystemProvidedCapabilities() {
        Lists.newArrayList("app", "platformVersion", "deviceName").forEach(cap -> capabilities.put(cap, System.getProperty(cap, String.valueOf(capabilities.get(cap)))));
    }
}