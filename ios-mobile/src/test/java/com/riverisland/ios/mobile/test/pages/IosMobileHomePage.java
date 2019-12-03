package com.riverisland.ios.mobile.test.pages;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.HomePage;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * 
 * @author Simon Johnson
 * Created on 20 June 2018
 *
 */

public class IosMobileHomePage implements HomePage<IosMobileHomePage>{
    private RiverIslandNativeAppDriver appDriver;
    private IosMobileTabBarPage tabBarPage;
	
	public IosMobileHomePage(RiverIslandNativeAppDriver appDriver) {
		this.appDriver = appDriver;
		this.tabBarPage = new IosMobileTabBarPage(appDriver);
	}

	@Override
	public IosMobileHomePage wishListDisplayed(int itemsInWishlist) {
		appDriver.scrollIntoView("Your wishlist");
		
		List<MobileElement> items = displayedProduct("Your wishlist");
		
		assertNotNull(items);
		assertEquals(items.size(), itemsInWishlist);
		
		return this;
	}

	@Override
	public IosMobileHomePage orderStatusDisplayed() {
		final By orderStatusLocator = By.name("Your recent order has been New order");

        if (!appDriver.isDisplayed(orderStatusLocator, 2)) {
        	appDriver.scrollDown();
        }
        
		MobileElement element = appDriver.retrieveMobileElement(orderStatusLocator);
		
		assertNotNull(element);
		
		assertEquals(element.getText(), "Your recent order has been New order");
		
		return this;
	}

	@Override
	public IosMobileHomePage tapItem(MobileElement item) {
		Point elementLocation = item.getLocation();
		TouchAction touchAction = new TouchAction(appDriver.getWrappedIOSDriver());
		touchAction.tap(PointOption.point(elementLocation.getX(), elementLocation.getY()))
				   .release()
				   .perform();
		return this;
	}

	@Override
	public IosMobileHomePage tapItem(Point point) {
		TouchAction touchAction = new TouchAction(appDriver.getWrappedIOSDriver());
		
		touchAction.press(PointOption.point(point.x, point.y))
		           .waitAction(WaitOptions.waitOptions(Duration.ofMillis(200)))
				   .release()
				   .perform();
		return this;
	}

	@Override
	public List<MobileElement> displayedTrendingProduct(String locator) {
		return displayedProduct(locator);
	}

	@Override
	public IosMobileHomePage swipeForNextTrendingItem(Point penultimateDisplayedItemPoint, Point lastDisplayedItemPoint) {
		TouchAction touchAction = new TouchAction(appDriver.getWrappedIOSDriver());
		
		//Swipe the last displayed item to the previous items position
		touchAction.press(PointOption.point(lastDisplayedItemPoint.x, lastDisplayedItemPoint.y))
                   .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
				   .moveTo(PointOption.point(penultimateDisplayedItemPoint.x-20, penultimateDisplayedItemPoint.y))
				   .release()
				   .perform();
		
		return this;
	}

	@Override
	public IosMobileHomePage pauseForWelcomeBanner() {
		appDriver.pause(10000);
		return this;
	}
	
	private List<MobileElement> displayedProduct(String locator) {
		final By trendingItemsLocator = By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']/..", locator));
		
		if (!appDriver.isDisplayed(trendingItemsLocator, 1)) {
			appDriver.scrollDownUntilInView(trendingItemsLocator);
			appDriver.scrollDown();
		}
		MobileElement parent = appDriver.retrieveMobileElement(trendingItemsLocator);
		return parent.findElements(By.xpath(".//XCUIElementTypeImage"));
	}

	@Override
	public IosMobileHomePage openSearch() {
		MobileElement search = (MobileElement)appDriver.getWrappedIOSDriver().findElement(By.xpath("//XCUIElementTypeTextField[contains(@value, 'looking for')]"));
		appDriver.touch(search);
		return this;
	}

	@Override
	public IosMobileHomePage searchForProductsAndSelect(String productId) {
		final By productIdSearchResultLocator = By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']", productId));

		MobileElement searchEntry = appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeOther/XCUIElementTypeTextField[contains(@value, 'looking for')]"));
        appDriver.type(searchEntry, productId);

        searchEntry.sendKeys(Keys.ENTER);

        if (appDriver.isDisplayed(productIdSearchResultLocator, 2)) {
        	appDriver.tap(productIdSearchResultLocator);
        }
		return this;
	}

	@Override
	public IosMobileHomePage search(String searchTerm) {		
		appDriver.type(By.name("Search"), searchTerm);		
		return this;
	}
	
	@Override
	public boolean isHomePage() {
		return appDriver.isDisplayed(By.xpath("//XCUIElementTypeNavigationBar[contains(@name,'NRIHomeScreenView')]"));
	}

	@Override
	public IosMobileHomePage closeSearch() {
		return this;
	}
}
