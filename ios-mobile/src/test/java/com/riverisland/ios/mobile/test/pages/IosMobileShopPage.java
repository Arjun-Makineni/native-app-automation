package com.riverisland.ios.mobile.test.pages;

import static com.riverisland.app.automation.enums.DevicePoint.IPHONEX_SHOP_TOP_Y_POINT;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.enums.TapDuration;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ShopPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 25/01/2018
 */
public class IosMobileShopPage implements ShopPage<IosMobileShopPage> {
	private static final int PRODUCT_ID_SEARCH_WAIT_IN_SECS = 2; 
    private RiverIslandNativeAppDriver appDriver;
    
    private static final By SEARCH_LOCATOR = By.xpath("//XCUIElementTypeTextField");

    public IosMobileShopPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileShopPage openCategory(String... categories) {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Opening main category: " + categories[0]);

        appDriver.tap(categories[0]);

        // hack to force the view to load the full XML source
        appDriver
                .swipe(SwipeElementDirection.UP)
                .swipe(SwipeElementDirection.DOWN)
                .swipe(SwipeElementDirection.DOWN);

        Arrays.stream(categories).skip(1).forEach((String category) ->
                {
                    int scrollDownTries = 10;

                    while (scrollDownTries-- > 0) {
                        if (appDriver.isDisplayed(By.name(category), 2)) {
                            RiverIslandLogger.getInfoLogger(this.getClass()).info("Opening sub category: " + category);
                            appDriver.tap(category);
                            break;
                        } else {
                            final String lastDisplayedCategory = appDriver.retrievePresentMobileElement(By.xpath("(//XCUIElementTypeCollectionView//descendant::XCUIElementTypeOther/XCUIElementTypeStaticText)[last()]")).getText();

                            RiverIslandLogger.getInfoLogger(this.getClass()).info("Scrolling to last visible sub category: " + lastDisplayedCategory);

                            appDriver.scrollIntoView(lastDisplayedCategory);
                        }
                    }

                    if (scrollDownTries == 0) {
                        throw new RuntimeException("Looks like the '" + category + "' category is not displayed in the app!");
                    }
                }
        );
        return this;
    }

    @Override
    public IosMobileShopPage search(String criteria, boolean selectSearchResult) {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Performing search using criteria: " + criteria);
        MobileElement search = appDriver.retrieveMobileElement(SEARCH_LOCATOR);
        appDriver
                .swipe(SwipeElementDirection.DOWN)
                .type(search, criteria);

        if (selectSearchResult) {
            selectSearchResult(criteria);
        }
        return this;
    }

    @Override
    public Product searchLazilyForProduct(List<Product> productList) {

        Product searchedProduct = null;

        for (Product product : productList) {
            appDriver.clear(SEARCH_LOCATOR);
            search(product.getId(), true);

            if (!appDriver.isDisplayed(By.xpath("//XCUIElementTypeStaticText[contains(@name,'Sorry')]"), PRODUCT_ID_SEARCH_WAIT_IN_SECS)) {
                searchedProduct = product;
                break;
            }
        }
        return searchedProduct;
    }

    @Override
    public IosMobileShopPage selectSearchResult() {
        appDriver.retrieveMobileElement("Search").click();
        return this;
    }
    
    private void selectSearchResult(String productId) {
    	selectSearchResult();

    	final By locator = By.xpath(String.format("//XCUIElementTypeCell/XCUIElementTypeStaticText[@name='%s']", productId));
    	
    	if (appDriver.isDisplayed(locator, 5)) {
    		appDriver
    			.tap(appDriver.retrieveMobileElement(locator))
    			.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name(productId)), 10000);  
    	}
    }

    @Override
    public IosMobileShopPage searchCategoryAndSubCategory(String category, String subCategory) {
        search(subCategory, true);
        return this;
    }

    @Override
    public IosMobileShopPage openStoreLocator() {
        final MobileElement storeLocatorElement = appDriver.retrieveMobileElement("icnNavLocation");
        final Point storeLocatorLocation = storeLocatorElement.getLocation();

        appDriver.tap(storeLocatorElement);
        return this;
    }

    @Override
    public IosMobileShopPage openScanInStore() {
        final MobileElement barcodeElement = appDriver.retrieveMobileElement("icnNavBarcode");
        final Point barcodeLocation = barcodeElement.getLocation();

        appDriver.tap(barcodeElement);

        return this;
    }

	@Override
	public boolean isShopPage() {
		throw new NotImplementedException("isShopPage is not currently used by IOS");
	}
}
