package com.riverisland.app.automation.enums;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public enum AutomatorPropertySelector {
	
	TEXT("text", "text", "name"),
	RESOURCE_ID_MATCHES("resourceIdMatches", "resource-id", "id");
	
	private String name;
	private String androidName;
	private String iosName;
	
	AutomatorPropertySelector(String name, String androidName, String iosName) {
		this.name = name;
		this.androidName = androidName;
		this.iosName = iosName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAndroidName() {
		return androidName;		
	}
	
	public String getIosName() {
		return iosName;
	}
	
	public String getLocator(AppiumDriver driver) {
		
		if (driver instanceof AndroidDriver) {
			return "//*[contains(@" + getAndroidName() + ", '%s')]";
		} 
		else if (driver instanceof IOSDriver) {
			return "//*[contains(@" + getIosName() + ", '%s')]";
		}
		else {
			throw new RuntimeException("Unrogonised appium driver - Should be Android or IOS");
		}
	}
}
