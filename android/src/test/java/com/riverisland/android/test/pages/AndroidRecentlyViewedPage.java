package com.riverisland.android.test.pages;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.RecentlyViewedPage;

import io.appium.java_client.MobileElement;


/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
public class AndroidRecentlyViewedPage extends AndroidCorePage implements RecentlyViewedPage<AndroidRecentlyViewedPage> {

    public AndroidRecentlyViewedPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public Boolean isRecentlyViewedProductDisplayed(String productName) {
    	final By recentItemsLocator = androidId.apply("recent_title_text");
	
    	appDriver.scrollIntoView(productName);
    	
        return appDriver
                .retrieveMobileElements(recentItemsLocator)
                .stream()
                .anyMatch(t -> StringUtils.containsIgnoreCase(t.getText(), productName));
    }

    @Override
    public AndroidRecentlyViewedPage clearRecentlyViewedList() {
        if (appDriver.isDisplayed(androidText.apply("Clear all"), 2)) {
            appDriver.tap(androidText.apply("Clear all"));
            acceptAndroidDialog(appDriver);
        }
        return this;
    }

    @Override
    public Boolean isRecentlyViewedEmpty() {
        return appDriver.retrieveMobileElement(androidId.apply("recent_empty_text")).isDisplayed();
    }
	
	@Override 
	public AndroidRecentlyViewedPage selectWishListHeart(String productName) {
		//Processing to navigate sticky search bar interfering with recently viewed
		List<MobileElement> recentViewedImages = appDriver.retrieveMobileElements(androidId.apply("item_recent_image"));
		if (null != recentViewedImages && !recentViewedImages.isEmpty()) {
			appDriver.swipeElementToElementLocation(
					recentViewedImages.get(recentViewedImages.size()-1).getCenter(), 
					appDriver.retrieveMobileElement("Your wishlist").getCenter());
		}
		appDriver.tap(androidId.apply("item_recent_wishlist_icon"));	
		return this;
	}

	@Override
	public AndroidRecentlyViewedPage seeAll(int expectedNumberOfProducts) {
		selectSeeAll();
		List<MobileElement> recentlyViewedImages = appDriver.retrieveMobileElements(androidId.apply("recent_main_image"));
		assertTrue( recentlyViewedImages.size() == expectedNumberOfProducts, String.format("Should have %s products displayed", expectedNumberOfProducts));
		return this;
	}

	@Override
	public AndroidRecentlyViewedPage clearAll() {
		selectSeeAll();
		appDriver.tap("Clear all");
		appDriver.tap(androidId.apply("dialog_positive_button"));
		assertTrue(appDriver.isDisplayed(androidId.apply("recent_empty_text"), 2));
		return this;
	}
	
	private void selectSeeAll() {
		appDriver.scrollIntoView("See all");
		
		// Make sure sticky search is not hiding see all
		List<MobileElement> prices = appDriver.retrieveMobileElements(androidId.apply("item_recent_price"));
		if (prices.size() > 1) {
			Point from = prices.get(0).getCenter();
			Point to = prices.get(1).getCenter();
			appDriver.swipeElementToElementLocation(from, to);
		}
		
		appDriver.tap("See all");
	}
}