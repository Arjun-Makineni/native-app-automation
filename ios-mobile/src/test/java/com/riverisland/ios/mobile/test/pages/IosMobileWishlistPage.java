package com.riverisland.ios.mobile.test.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.WishlistPage;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileWishlistPage implements WishlistPage<IosMobileWishlistPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileWishlistPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public Boolean isWishlistEmpty() {
        return appDriver.retrieveMobileElement(By.name("Oh no, your wishlist is empty!")).isDisplayed();
    }

    @Override
    public String getWishlistQty() {
        return StringUtils.substringBefore(appDriver.retrieveMobileElementText(By.xpath("//XCUIElementTypeStaticText[contains(@name,'Items')]")), " ").trim();
    }

    @Override
    public IosMobileWishlistPage addWishlistItemsToBag() {
        waitForWishlistToLoad();

        final By addToBagLocator = By.name("ADD TO BAG");

        int wishlistItems = appDriver.retrieveMobileElements(addToBagLocator).size();

    	appDriver.pause(5000);	//animation must complete
        while (wishlistItems-- > 0) {
            appDriver
                    .tap("ADD TO BAG")
                    .waitFor(ExpectedConditions.numberOfElementsToBe(addToBagLocator, wishlistItems));
        }
        return this;
    }

    @Override
    public IosMobileWishlistPage removeWishlistItem(String productName) {
    	appDriver.pause(5000);	//animation must complete
        appDriver
                .swipe(SwipeElementDirection.LEFT, appDriver.retrieveMobileElement(By.name(productName)))
                .tap("Remove")
                .waitFor(ExpectedConditions.invisibilityOfElementLocated(By.name(productName)));
        return null;
    }

    @Override
    public IosMobileWishlistPage clearWishlist() {
        if (appDriver.isDisplayed(By.name("Clear All"), 2)) {
            appDriver
                    .tap("Clear All")
                    .tap("OK");
        }
        return this;
    }

    @Override
    public IosMobileWishlistPage changeWishlistItemSize() {
    	final By sizeLocator = By.xpath("//XCUIElementTypeButton[contains(@name,'Size')]");
    	
    	appDriver.pause(5000);	//animation must complete
        appDriver
                .tap(sizeLocator)
                .tap("Select");
        return this;
    }

    @Override
    public Boolean isProductInWishlist(String productName) {
        return appDriver.retrieveMobileElement(productName).isDisplayed();
    }

    private void waitForWishlistToLoad() {
        appDriver.waitFor(ExpectedConditions.visibilityOfNestedElementsLocatedBy(By.className("XCUIElementTypeNavigationBar"), By.name("Wishlist")));
    }
}
