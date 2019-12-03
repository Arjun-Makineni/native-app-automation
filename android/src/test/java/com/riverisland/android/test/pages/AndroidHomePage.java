package com.riverisland.android.test.pages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;

import com.riverisland.app.automation.enums.AutomatorPropertySelector;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.HomePage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * 
 * @author Simon Johnson 
 * Created on 19 June 2018
 *
 */

public class AndroidHomePage extends AndroidCorePage implements HomePage<AndroidHomePage>{  
    
	public AndroidHomePage(RiverIslandNativeAppDriver appDriver) {
		super(appDriver);
	}

	@Override
	public AndroidHomePage tapItem(Point point) {
		TouchAction touchAction = new TouchAction(appDriver.getWrappedAndroidDriver());
		
		touchAction.waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)))
		           .tap(PointOption.point(point.x, point.y))
				   .perform();
		return this;
	}
	
	@Override
	public AndroidHomePage tapItem(MobileElement item) {
		try {
			tapItem(item.getLocation());
		}
		catch (Exception e) {
			RiverIslandLogger.getInfoLogger(getClass()).error("Failed getting product id");
		}
		return this;
	}
	private void displayItems(String locator) {

		MobileElement element = appDriver.scrollIntoView(locator);		
		swipeElement(element.getCenter(), element.getCenter().moveBy(0, -200));
	}
	
	@Override
	public List<MobileElement> displayedTrendingProduct(String locator) {
		return displayedProduct(locator);
	}
	
	private List<MobileElement> displayedProduct(String locator) {
		final By trendingItemsLocator = By.xpath(String.format("//android.widget.TextView[@text='%s']/..", locator));

		displayItems(locator);
		// While displayed trending/wishlist images

		MobileElement parent = appDriver.retrieveMobileElement(trendingItemsLocator);
		
		return parent.findElements(By.className("android.widget.ImageView"));
	}
	
	@Override
	public AndroidHomePage wishListDisplayed(int itemsInWishList) {
		List<MobileElement> displayedWishListItems = displayedProduct("Your wishlist");
		assertNotNull(displayedWishListItems);
		assertEquals(displayedWishListItems.size(), itemsInWishList);
		
		return this;
	}
	
	@Override
	public AndroidHomePage orderStatusDisplayed() {
		final String orderStatusLocator = "homescreen_delivery_text";

		if (!appDriver.isDisplayed(androidId.apply(orderStatusLocator), 2)) {
			appDriver.scrollIntoView(AutomatorPropertySelector.RESOURCE_ID_MATCHES, orderStatusLocator);
		}
		MobileElement element = appDriver.retrieveMobileElement(androidId.apply(orderStatusLocator));
		
		assertNotNull(element);
		
		assertTrue(element.getText().equals("Your recent item(s) have been ordered") || 
				   element.getText().equals("Your recent order has been AwaitingPspNotification"));
		
		return this;
	}
	

	@Override
	public AndroidHomePage swipeForNextTrendingItem(Point penultimateDisplayedItemPoint, Point lastDisplayedItemPoint) {
		
		//Swipe the last displayed item to the previous items position
		swipeElement(lastDisplayedItemPoint, penultimateDisplayedItemPoint);		
		return this;
	}

	@Override
	public AndroidHomePage pauseForWelcomeBanner() {
		appDriver.pause(10000);
		return this;
	}
	
	private void swipeElement(Point from, Point to) {
		TouchAction touchAction = new TouchAction(appDriver.getWrappedAndroidDriver());
		
		//Swipe the last displayed item to the previous items position
		touchAction.longPress(PointOption.point(from.x, from.y))
				   .moveTo(PointOption.point(to.x, to.y))
				   .release()
				   .perform();
	}

	@Override
	public AndroidHomePage openSearch() {
		appDriver.click(androidId.apply("search_prompt"));
		return this;
	}
	
	@Override
	public boolean isHomePage() {
		final By homeScreenLocator = androidId.apply("homescreen_recyclerview");

		return appDriver.isDisplayed(homeScreenLocator, 5);
	}
		
	private static final int PRODUCT_ID_SEARCH_WAIT_IN_SECS = 5;
	@Override
	public AndroidHomePage searchForProductsAndSelect(String productId) {
		appDriver.clear(androidId.apply("search_bar_text"))
			.type(androidId.apply("search_bar_text"), productId)
			.pressAndroidKey(AndroidKeyCode.ENTER);

		if (!appDriver.isDisplayed(androidId.apply("search_empty_text"), PRODUCT_ID_SEARCH_WAIT_IN_SECS)) {
			selectSearchResult();
		}
		return this;	
	}
    
    private AndroidHomePage selectSearchResult() {
        appDriver.tap(androidId.apply("search_name"));
        return this;
    }

	@Override
	public AndroidHomePage search(String searchTerm) {
		appDriver
			.clear(androidId.apply("search_bar_text"))
			.type(androidId.apply("search_bar_text"), searchTerm);
		
		return this;
	}
	
	@Override
	public AndroidHomePage closeSearch() {
		if (appDriver.isDisplayed(androidId.apply("search_bar_text"))) {
			appDriver.back();
			appDriver.back();
		}
		
		return this;
	}
}
