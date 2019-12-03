package com.riverisland.ios.mobile.test.pages;

import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MyBagPage;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileMyBagPage implements MyBagPage<IosMobileMyBagPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileMyBagPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    private final Function<Integer, By> bagQtyLocator = (index) -> index == 0 ? By.xpath("//XCUIElementTypeButton[contains(@name, 'Qty:')]") : By.xpath(String.format("//XCUIElementTypeButton[contains(@name, 'Qty:')][%s]", index));

    @Override
    public IosMobileMyBagPage closeBag() {
        appDriver.tap("Close");
        return this;
    }

    @Override
    public int getTotalBagQty() {
        final AtomicInteger qty = new AtomicInteger();

        waitForBagToLoad();

        appDriver
                .retrievePresentMobileElements(bagQtyLocator.apply(0))
                .stream()
                .filter(t -> !StringUtils.isBlank(t.getText()))
                .forEach(t -> qty.addAndGet(Integer.parseInt(StringUtils.substringAfter(t.getText(), "Qty: "))));

        return qty.get();
    }

    @Override
    public IosMobileMyBagPage proceedToCheckout() {
        appDriver.tap("PROCEED TO CHECKOUT");
        return this;
    }

    private Consumer<String> qtyPicker = (direction) -> {
        try {
            JavascriptExecutor js = appDriver.getWrappedIOSDriver();
            Map<String, Object> params = new HashMap<>();
            params.put("order", direction);
            params.put("offset", 0.10);
            params.put("element", appDriver.retrieveMobileElement(By.className("XCUIElementTypePickerWheel")).getId());
            js.executeScript("mobile: selectPickerWheelValue", params);
        } catch (WebDriverException ignored) {
        }

        appDriver
                .tap("Done")
                .pause(500);
    };

    @Override
    public IosMobileMyBagPage increaseBagQty(int index) {
        appDriver.tap(bagQtyLocator.apply(index));
        qtyPicker.accept("next");
        return this;
    }

    @Override
    public IosMobileMyBagPage decreaseBagQty(int index) {
        appDriver.tap(bagQtyLocator.apply(index));
        qtyPicker.accept("previous");
        return this;
    }

    private static final By EMPTY_BAG_TEXT_LOCATOR = By.xpath("//XCUIElementTypeStaticText[contains(@name,'Your bag is empty')]");
    @Override
    public Boolean isBagEmpty() {
        return appDriver.retrieveMobileElement(EMPTY_BAG_TEXT_LOCATOR).isDisplayed();
    }

    public Boolean hasNoBagItems() {
        return appDriver.isDisplayed(EMPTY_BAG_TEXT_LOCATOR);
    }

    @Override
    public IosMobileMyBagPage editBag() {
        appDriver.tap("Edit Bag");
        return this;
    }

    @Override
    public IosMobileMyBagPage clearAll() {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public IosMobileMyBagPage removeBagItem(String productName) {
        waitForBagToLoad();

        appDriver
                .swipe(SwipeElementDirection.LEFT, appDriver.retrievePresentMobileElement(By.name(productName)))
                .tap("Remove");
        return this;
    }

    @Override
    public IosMobileMyBagPage moveAllBagItemsToWishlist() {
        waitForBagToLoad();
        appDriver.tap(By.xpath("//XCUIElementTypeButton[contains(@name,'items to wishlist')]"));
        return this;
    }

    @Override
    public IosMobileMyBagPage moveBagItemToWishlist(String productName) {
        waitForBagToLoad();

        appDriver
                .swipe(SwipeElementDirection.RIGHT, appDriver.retrievePresentMobileElement(By.name(productName)))
                .tap("Add to wishlist");
        return this;
    }

    @Override
    public IosMobileMyBagPage signIn() {
        appDriver.tap(By.xpath("//XCUIElementTypeStaticText[starts-with(@name,'Sign in')]"));
        return this;
    }

    @Override
    public IosMobileMyBagPage startShopping() {
        appDriver.tap("SHOP WOMEN");
        return this;
    }

    @Override
    public Boolean isPreviousSessionBagMessageDisplayed(int itemCount) {
        waitForBagToLoad();
        return appDriver.retrieveMobileElement(By.name("Items below added from a previous session")).isDisplayed();
    }

    private void waitForBagToLoad() {
        appDriver.waitFor(ExpectedConditions.presenceOfElementLocated(By.name("PROCEED TO CHECKOUT")));
    }
    
    @Override
    public IosMobileMyBagPage verifyProgressBar(boolean isDisplayed) {
    	final By progressBarLocator = By.xpath("//XCUIElementTypeStaticText[@name='Bag']");
    	
    	assertTrue(appDriver.isDisplayed(progressBarLocator) == isDisplayed);
    	return this;
    	
    }
}
