package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.WishlistPage;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.function.Function;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidWishlistPage extends AndroidCorePage implements WishlistPage<AndroidWishlistPage> {

    public AndroidWishlistPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    private Function<By, AndroidWishlistPage> wishlistAction = locator -> {
        int size = appDriver.retrieveMobileElements(locator).size();

        while (size-- > 0) {
            final MobileElement item = appDriver.retrieveMobileElement(locator);
            appDriver
            	.tap(item)
            	.waitFor(ExpectedConditions.numberOfElementsToBe(locator, size));
        }
        return this;
    };

    @Override
    public Boolean isWishlistEmpty() {
        return appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//android.widget.TextView[contains(@text,'Wishlist')]")))
                .isDisplayed(androidId.apply("wishlist_empty_text"));
    }

    @Override
    public String getWishlistQty() {
        return StringUtils.substringBetween(appDriver.retrieveMobileElementText(By.xpath("//android.widget.TextView[contains(@text,'Wishlist (')]")), "(", " ").trim();
    }

    @Override
    public AndroidWishlistPage addWishlistItemsToBag() {
        return wishlistAction.apply(androidId.apply("wishlist_add_bag_button"));
    }

    @Override
    public AndroidWishlistPage removeWishlistItem(String productName) {
        openWishlistOptions();

        appDriver
                .tap("Edit")
                .tap(androidText.apply(productName))
                .tap(androidId.apply("action_delete"));
        return this;
    }

    @Override
    public AndroidWishlistPage clearWishlist() {
        if (isWishlistEmpty()) {
            return this;
        }

        openWishlistOptions();

        appDriver
                .tap("Clear All")
                .tap("Yes");
        return this;
    }

    @Override
    public AndroidWishlistPage changeWishlistItemSize() {
        appDriver.tap(androidId.apply("wishlist_size_text"));

        appDriver.tap(androidText.apply(appDriver
                .retrieveMobileElements(androidId.apply("size_picker_text"))
                .stream()
                .map(RemoteWebElement::getText)
                .reduce((x, y) -> y).get()));

        return this;
    }

    @Override
    public Boolean isProductInWishlist(String productName) {
        return appDriver
                .retrieveMobileElements(androidId.apply("wishlist_product_title_text"))
                .stream()
                .anyMatch(t -> StringUtils.containsIgnoreCase(t.getText(), productName));
    }

    private void openWishlistOptions() {
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("wishlist_recycler")))
                .tap(By.xpath("//*[@content-desc='More options']"));
    }
}
