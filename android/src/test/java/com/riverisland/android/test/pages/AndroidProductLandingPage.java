package com.riverisland.android.test.pages;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.FilterBy;
import com.riverisland.app.automation.enums.SortBy;
import com.riverisland.app.automation.enums.ToggleType;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ProductLandingPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidProductLandingPage extends AndroidCorePage implements ProductLandingPage<AndroidProductLandingPage> {

    public AndroidProductLandingPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidProductLandingPage waitForLandingPageProducts() {
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("product_list_image")));
        return this;
    }

    @Override
    public int getProductItemCount() {
        return Integer.parseInt(StringUtils.substringBefore(appDriver.retrieveMobileElementText(androidId.apply("product_list_item_count")), " "));
    }

    @Override
    public AndroidProductLandingPage selectProduct(String productId) { //param not needed for method but required for interface
        appDriver.tap(androidId.apply("product_list_image"));
        return this;
    }

    @Override
    public AndroidProductLandingPage selectProduct() {
        final List<MobileElement> products = appDriver.retrieveMobileElements(androidId.apply("product_list_image"));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Choosing a random product");

        final Random random = new Random();
        appDriver.tap(products.get(random.nextInt(products.size())));
        return this;
    }

    @Override
    public Boolean selectProductWithSwatch() {
        int attempts = 4;

        while (attempts-- > 0) {
            selectProduct();

            appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("product_details_id")));

            if (appDriver.isDisplayed(androidId.apply("product_details_swatch_container"))) {
                return true;
            } else {
                appDriver.back();
            }
        }
        return false;
    }

    @Override
    public AndroidProductLandingPage toggleProductView(ToggleType toggleType) {
        switch (toggleType) {
            case GRID_BIG:
                appDriver.tap(androidId.apply("product_list_grid_detail_view")).pause(500);
                break;

            case GRID_SMALL:
                appDriver.tap(androidId.apply("product_list_grid_view")).pause(500);
                break;

            case GRID_LIST:
                appDriver.tap(androidId.apply("product_list_list_view")).pause(500);
                break;
        }
        return this;
    }

    @Override
    public int getCountOfDisplayedProducts() {
        return appDriver.retrieveMobileElements(androidId.apply("product_list_image")).size();
    }

    @Override
    public List<BigDecimal> getProductPrices() {
        return appDriver.retrieveMobileElements(androidId.apply("product_list_price"))
                .stream().map(e -> new BigDecimal(StringUtils.substring(e.getText(), 1)))
                .collect(Collectors.toList());
    }

    private final By filterValueRowLocator = By.xpath("//android.widget.TextView[contains(@resource-id,'fragment_title')]");

    @Override
    public String filterItem(FilterBy filter) {
        appDriver.tap(filter.getDescription());

        final String filterValue = appDriver.retrieveMobileElementText(filterValueRowLocator);

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product filter: " + filter.getDescription() + "/" + filterValue);

        appDriver
                .tap(filterValue)
                .tap("APPLY");

        return filterValue;
    }

    @Override
    public List<String> filterItemWithMultipleOptions(FilterBy filter) {
        appDriver.tap(filter.getDescription());

        final List<String> filterValues = appDriver.retrieveMobileElements(filterValueRowLocator).subList(0, 2).stream().map(RemoteWebElement::getText).collect(Collectors.toList());

        filterValues.forEach(filterValue -> {
            RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product filter: " + filter.getDescription() + "/" + filterValue);
            appDriver.tap(filterValue);
        });
        appDriver.tap("APPLY");

        return filterValues;
    }

    @Override
    public AndroidProductLandingPage filterBySearch(String searchKeyword) {
        return null;
    }

    @Override
    public AndroidProductLandingPage openFilter() {
        appDriver.waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(androidId.apply("product_list_image")))
                 .tap("Refine");
        return this;
    }

    @Override
    public AndroidProductLandingPage closeFilter() {
        appDriver.tap(By.className("android.widget.ImageButton"));
        return this;
    }

    @Override
    public AndroidProductLandingPage clearSingleFilter(FilterBy filter) {
        appDriver
                .tap(filter.getDescription())
                .tap(filterValueRowLocator)
                .tap("APPLY");
        return this;
    }

    @Override
    public AndroidProductLandingPage clearAllFilters() {
        appDriver.tap("Clear all");
        return this;
    }

    @Override
    public Boolean isFilterApplied(String filterValue) {
        return appDriver.retrieveMobileElements(androidId.apply("filter_selected_options_textview")).stream().anyMatch(t -> t.getText().equalsIgnoreCase(filterValue));
    }

    @Override
    public Boolean isAllFiltersCleared() {
        return appDriver.retrieveMobileElements(androidId.apply("filter_selected_options_textview")).stream().allMatch(t -> t.getText().startsWith("All"));
    }

    @Override
    public AndroidProductLandingPage sortBy(SortBy sortBy) {
        waitForLandingPageProducts();

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product sorting: " + sortBy.getDescription());

        appDriver
                .tap("Sort")
                .tap(sortBy.getDescription())
                .pause(1000);
        return this;
    }

    @Override
    public AndroidProductLandingPage clearSorting() {
        throw new NotImplementedException("Method not supported");
    }

	@Override
	public int numberOfSwatchesDisplayed() {
		final By swatchesLocator = androidId.apply("product_list_swatch");

		List<MobileElement> swatches = appDriver.retrieveMobileElements(swatchesLocator);

		if (null != swatches) {
			return swatches.size();
		}
		
		return 0;
	}
	
	@Override
	public int excessSwatchValue() {
		final By swatchExcessLocator = androidId.apply("product_list_swatch_excess");
		
		if (appDriver.isDisplayed(swatchExcessLocator, 2)) {
			String excess = appDriver.retrieveMobileElementText(swatchExcessLocator);
		
			if (null != excess) {
				return Integer.valueOf(excess);
			}
		}
		return 0;
	}
}