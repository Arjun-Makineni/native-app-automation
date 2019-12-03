package com.riverisland.ios.mobile.test.pages;

import org.openqa.selenium.By;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.TabBarPage;

/**
 * Created by Prashant Ramcharan on 22/01/2018
 */
public class IosMobileTabBarPage implements TabBarPage<IosMobileTabBarPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileTabBarPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileTabBarPage openMyRi() {
        appDriver.tap(iosTabLocator.apply(TabBar.MY_RI));
        return this;
    }

    @Override
    public IosMobileTabBarPage openShop() {
    	acknowledgeGetMeBeforImGone();
        appDriver.tap(iosTabLocator.apply(TabBar.SHOP));
        return this;
    }

    @Override
    public IosMobileTabBarPage openHome() {
        appDriver.tap(iosTabLocator.apply(TabBar.HOME));
        return this;
    }

    @Override
    public IosMobileTabBarPage openWishlist() {
        appDriver.tap(iosTabLocator.apply(TabBar.WISHLIST));
        return this;
    }

    @Override
    public IosMobileTabBarPage openShoppingBag() {
        appDriver.tap(iosTabLocator.apply(TabBar.SHOPPING_BAG));
        waitForBagAnimation();
        return this;
    }
    
    @Override
	public boolean isTabBarVisible() {
		return appDriver.isDisplayed(By.xpath("//XCUIElementTypeTabBar"));
	}
    
    private static final long BAG_ANIMATION_DISPLAY_TIME = 5000;
    private void waitForBagAnimation() {
    	appDriver.pause(BAG_ANIMATION_DISPLAY_TIME);
    }
    private void acknowledgeGetMeBeforImGone() {
    	if (appDriver.isDisplayed(By.xpath("//XCUIElementTypeStaticText[contains(@value,'Get me before')]"), 1)) {
    		appDriver.tap("GO TO BAG")
    				 .back();
    	}
    }

	@Override
	public boolean isAndroid() {
		return false;
	}
}
