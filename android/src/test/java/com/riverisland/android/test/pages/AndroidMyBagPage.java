package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MyBagPage;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.SkipException;

import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidMyBagPage extends AndroidCorePage implements MyBagPage<AndroidMyBagPage> {

    public AndroidMyBagPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidMyBagPage closeBag() {
        appDriver.back();
        return this;
    }

    @Override
    public int getTotalBagQty() {
        final AtomicInteger qty = new AtomicInteger();
        final By bagProductQtyLocator = androidId.apply("bag_product_qty");

        appDriver.swipeElementToElementLocation(androidId.apply("bag_product_image"), 
        		                                androidId.apply("bag_promo_input"));
        appDriver
                .retrieveMobileElements(bagProductQtyLocator)
                .forEach(t -> qty.addAndGet(Integer.parseInt(StringUtils.substringAfter(t.getText(), ": "))));

        return qty.get();
    }

    @Override
    public AndroidMyBagPage proceedToCheckout() {
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(androidText.apply("Add a promo code")))
                .tap(androidId.apply("sticky_summary_button"));
        return this;
    }

    @Override
    public AndroidMyBagPage increaseBagQty(int index) {
        final MobileElement itemQty = appDriver.retrieveMobileElements(androidId.apply("bag_product_qty")).get(index - 1);

        final int currentBagQty = Integer.parseInt((StringUtils.substringAfter(itemQty.getText(), ": ")));

        final String newBagQty = String.valueOf(currentBagQty + 1);

        appDriver.tap(itemQty);

        if (!appDriver.isDisplayed(androidText.apply(newBagQty), 2)) {
            throw new SkipException("This product does not have enough stock to increase the qty!");
        }

        appDriver
                .tap(newBagQty);
        return this;
    }

    @Override
    public AndroidMyBagPage decreaseBagQty(int index) {
        final MobileElement itemQty = appDriver.retrieveMobileElements(androidId.apply("bag_product_qty")).get(index - 1);

        final int currentBagQty = Integer.parseInt((StringUtils.substringAfter(itemQty.getText(), ": ")));

        appDriver
                .tap(itemQty)
                .tap(String.valueOf(currentBagQty - 1));
        return this;
    }

    @Override
    public Boolean isBagEmpty() {
        return appDriver.retrieveMobileElement(androidId.apply("bag_empty_label")) != null;
    }

    @Override
    public Boolean hasNoBagItems() {
        return appDriver.isDisplayed(androidId.apply("bag_empty_label"), 2);
    }

    @Override
    public AndroidMyBagPage editBag() {

        appDriver.tap("Edit bag");
        return this;
    }

    @Override
    public AndroidMyBagPage clearAll() {

        while (!hasNoBagItems()) {
        	appDriver
        		.tap(androidId.apply("bag_product_remove_cross"))
        		.tap("Remove");	
        }

        return this;
    }

    @Override
    public AndroidMyBagPage removeBagItem(String productName) {
    	final By itemSelector = By.xpath(String.format("//android.widget.TextView[@text='%s']/../android.widget.ImageView[contains(@resource-id, '%s')]", productName, "bag_product_remove_cross"));
        appDriver
                .tap(itemSelector)
                .tap("Remove");
        return this;
    }

    @Override
    public AndroidMyBagPage moveAllBagItemsToWishlist() {
        while (!hasNoBagItems()) {
        	appDriver
        		.tap(androidId.apply("bag_product_remove_cross"))
        		.tap("Move to wishlist");	
        }
        return this;
    }

    @Override
    public AndroidMyBagPage moveBagItemToWishlist(String productName) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public AndroidMyBagPage signIn() {
        appDriver.tap(androidId.apply("bag_empty_sign_in"));
        return this;
    }

    @Override
    public AndroidMyBagPage startShopping() {
        appDriver.tap("SHOP WOMEN");
        return this;
    }

    @Override
    public Boolean isPreviousSessionBagMessageDisplayed(int items) {
        return appDriver.retrieveMobileElementText(androidId.apply("bag_migrated_header")).equals("Items below added from a previous session");
    }

	@Override
	public AndroidMyBagPage verifyProgressBar(boolean isDisplayed) {

    	assertTrue(appDriver.isDisplayed(androidId.apply("checkout_progress_one")) == isDisplayed);

		return this;
	}
}
