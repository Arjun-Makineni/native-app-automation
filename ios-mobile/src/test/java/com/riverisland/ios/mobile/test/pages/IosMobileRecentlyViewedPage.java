package com.riverisland.ios.mobile.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.RecentlyViewedPage;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileRecentlyViewedPage implements RecentlyViewedPage<IosMobileRecentlyViewedPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileRecentlyViewedPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public Boolean isRecentlyViewedProductDisplayed(String productName) {
    	final By wishlistLocator = By.xpath("//XCUIElementTypeStaticText[@name='Your wishlist']");

    	if (!appDriver.isDisplayed(wishlistLocator, 1)) {
    		appDriver.scrollDownUntilInView(wishlistLocator);
    	}
    	
    	return appDriver.retrieveMobileElement(productName).isDisplayed();
    }

    private By recentlyViewedIsEmpty = By.name("Your recently viewed is empty");

    @Override
    public IosMobileRecentlyViewedPage clearRecentlyViewedList() {
        if (!appDriver.isDisplayed(recentlyViewedIsEmpty, 2)) {
            appDriver
                    .tap("Clear All ")
                    .tap("OK");
        }
        return this;
    }

    @Override
    public Boolean isRecentlyViewedEmpty() {
        return appDriver.retrieveMobileElement(recentlyViewedIsEmpty).isDisplayed();
    }

	@Override
	public IosMobileRecentlyViewedPage selectWishListHeart(String productName) {
		appDriver.tap(By.name("icnWishlist"));
		return this;
	}

	@Override
	public IosMobileRecentlyViewedPage seeAll(int expectedNumberOfProducts) {
		appDriver.scrollDownUntilInView(By.name("Recently viewed"));
		appDriver.retrieveMobileElement("See all").click();
		return this;
	}

	@Override
	public IosMobileRecentlyViewedPage clearAll() {
		appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Clear All")), 2000);
		appDriver.tap(By.name("Clear All "));
		appDriver.tap("OK");
		return this;
	}
}
