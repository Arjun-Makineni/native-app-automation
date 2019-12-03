package com.riverisland.ios.mobile.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.SalesCountdownPage;

public class IosMobileSalesCountdownPage implements SalesCountdownPage<IosMobileSalesCountdownPage>{

	private RiverIslandNativeAppDriver appDriver;
	
	public IosMobileSalesCountdownPage(RiverIslandNativeAppDriver appDriver) {
		this.appDriver = appDriver;
	}
	
	@Override
	public boolean isCountdownDisplayed() {
		final By countdownSelector = By.name("H");
		WebElement element = null;
		
		try {
			appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(countdownSelector), 5);
			element = appDriver.getWrappedIOSDriver().findElement(countdownSelector);
		} 
		catch(NoSuchElementException re) {}
		
		return null != element;
	}
}
