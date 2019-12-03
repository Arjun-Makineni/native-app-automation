package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ShopPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Prashant Ramcharan on 25/01/2018
 */
public class AndroidShopPage extends AndroidCorePage implements ShopPage<AndroidShopPage> {
	
	private static final int PRODUCT_ID_SEARCH_WAIT_IN_SECS = 2; 

    public AndroidShopPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidShopPage openCategory(String... categories) {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Opening main category: " + categories[0]);

        appDriver.tap(categories[0].toUpperCase()).pause(500);

        if (categories.length == 1) {
            return this;
        }

        try {
            appDriver.getWrappedAndroidDriver().findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true).instance(1)).scrollForward();)");
        } catch (WebDriverException ignore) {
        }

        Arrays.stream(categories).skip(1).forEach((String category) -> appDriver.scrollIntoViewAndTap(category));
        return this;
    }

    private void searchOnly(String criteria) {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Performing search using criteria: " + criteria);

        appDriver
                .tap(androidId.apply("action_search"))
                .type(androidId.apply("search_bar_text"), criteria)
                .pressAndroidKey(AndroidKeyCode.ENTER);
    }

    @Override
    public AndroidShopPage search(String criteria, boolean selectSearchResult) {
        searchOnly(criteria);

        if (selectSearchResult) {
            return selectSearchResult();
        }
        return this;
    }

    @Override
    public Product searchLazilyForProduct(List<Product> productList) {
        Product searchedProduct = null;

        appDriver.tap(androidId.apply("action_search"));

        for (Product product : productList) {
            appDriver
                    .clear(androidId.apply("search_bar_text"))
                    .type(androidId.apply("search_bar_text"), product.getId())
                    .pressAndroidKey(AndroidKeyCode.ENTER);

            if (!appDriver.isDisplayed(androidId.apply("search_empty_text"), PRODUCT_ID_SEARCH_WAIT_IN_SECS)) {
                searchedProduct = product;
                break;
            }
        }

        if (searchedProduct != null) {
            selectSearchResult();
        }
        return searchedProduct;
    }

    @Override
    public AndroidShopPage selectSearchResult() {
        appDriver.tap(androidId.apply("search_name"));
        return this;
    }

    @Override
    public AndroidShopPage searchCategoryAndSubCategory(String category, String subCategory) {
        searchOnly(subCategory);

        appDriver.tap(By.xpath(String.format(
                "//android.widget.TextView[@text='%s']//following::android.widget.TextView[@text='%s']",
                subCategory, category)));
        return this;
    }

    @Override
    public AndroidShopPage openStoreLocator() {
        return this;
    }

    @Override
    public AndroidShopPage openScanInStore() {
        return this;
    }

	@Override
	public boolean isShopPage() {
		MobileElement element = appDriver.retrieveMobileElement(androidId.apply("toolbar_title"));		
		return element.getText().equals("Shop");		
	}
}
