package com.riverisland.ios.mobile.test.pages;

import static com.riverisland.app.automation.enums.DevicePoint.IPHONEX_SHOP_TOP_Y_POINT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.FilterBy;
import com.riverisland.app.automation.enums.SortBy;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.enums.TapDuration;
import com.riverisland.app.automation.enums.ToggleType;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ProductLandingPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileProductLandingPage implements ProductLandingPage<IosMobileProductLandingPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileProductLandingPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    private static final long LANDING_PAGE_PRODUCTS_TIMEOUT = 5000; //Milliseconds
    @Override
    public IosMobileProductLandingPage waitForLandingPageProducts() {
        appDriver.waitFor(ExpectedConditions.visibilityOfNestedElementsLocatedBy(By.className("XCUIElementTypeCollectionView"), By.className("XCUIElementTypeCell")),
        				  LANDING_PAGE_PRODUCTS_TIMEOUT);
        return this;
    }

    @Override
    public int getProductItemCount() {
        return Integer.parseInt(StringUtils.substringBefore(appDriver.retrieveMobileElementText(By.xpath("//XCUIElementTypeStaticText[contains(@name,' Item(s)')]")), " ").trim());
    }

    @Override
    public IosMobileProductLandingPage selectProduct(String productId) {
       	final By productSelectLocator = By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeStaticText");
        appDriver        
                .waitFor(ExpectedConditions.visibilityOfElementLocated(productSelectLocator), 2000)
                .touch(appDriver.retrieveMobileElement(productSelectLocator));      
        return this;
    }
    
    private static final long PLP_DISPLAY_TIMEOUT = 300000;

    @Override
    public IosMobileProductLandingPage selectProduct() {
        final List<MobileElement> products = 
        		appDriver
        			.waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell")), PLP_DISPLAY_TIMEOUT)
        			.retrieveMobileElements(By.className("XCUIElementTypeCollectionView"), By.className("XCUIElementTypeCell"));
        final List<MobileElement> subListedProducts = new ArrayList<>(products.subList(0, 4));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Choosing a random product");

        final Random random = new Random();
        subListedProducts.get(random.nextInt(subListedProducts.size())).click();
        return this;
    }
    
    private static final By SWATCH_LOCATOR = By.xpath("//XCUIElementTypeTable/XCUIElementTypeCell[2]//XCUIElementTypeImage");

    @Override
    public Boolean selectProductWithSwatch() {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Looking for a product with swatches");

        int attempts = 3;

        AtomicBoolean swatchFound = new AtomicBoolean();

        IntStream.rangeClosed(0, attempts).boxed().forEach(index -> {
            if (!swatchFound.get()) {
                waitForLandingPageProducts();

                final List<MobileElement> products = appDriver.retrieveMobileElements(By.className("XCUIElementTypeCollectionView"), By.xpath("//XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeStaticText"));

                if (products.size() > index) {
                    appDriver
                            .tap(products.get(index))
                            .waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(SWATCH_LOCATOR));

                    if (appDriver.isEnabled(SWATCH_LOCATOR) && appDriver.retrievePresentMobileElements(SWATCH_LOCATOR).size() > 1) {
                        swatchFound.set(true);
                        RiverIslandLogger.getInfoLogger(this.getClass()).info("Product with swatches are available");
                    } else {
                        RiverIslandLogger.getInfoLogger(this.getClass()).info("Product with swatches are not available - retrying another product");
                        appDriver.getWrappedDriver().navigate().back();
                    }
                }
            }
        });

        if (swatchFound.get()) {
            return true;
        } else {
            appDriver.swipe(SwipeElementDirection.UP);
        }
        return swatchFound.get();
    }

    @Override
    public IosMobileProductLandingPage toggleProductView(ToggleType toggleType) {
        switch (toggleType) {
            case GRID_BIG: {
                appDriver.tap("icnGrinBigInactive");
                break;
            }
            case GRID_SMALL: {
                appDriver.tap("icnGridSmallInactive");
                break;
            }
            case GRID_LIST: {
                appDriver.tap("icnListInactive");
                break;
            }
        }
        return this;
    }

    @Override
    public int getCountOfDisplayedProducts() {
        return appDriver.retrieveMobileElements(
        		By.className("XCUIElementTypeCollectionView"), 
        		By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeImage")).size();
    }

    @Override
    public List<BigDecimal> getProductPrices() {
        return appDriver.retrieveMobileElements(By.className("XCUIElementTypeCollectionView"), By.xpath("//XCUIElementTypeCell//XCUIElementTypeOther[2]/XCUIElementTypeStaticText[last()]"))
                .stream().map(e -> new BigDecimal(StringUtils.substring(e.getText(), 1)))
                .collect(Collectors.toList());
    }

    private IosMobileProductLandingPage selectFilter(FilterBy filter) {
        if (!appDriver.isDisplayed(By.name(filter.getDescription()))) {
            appDriver.swipe(SwipeElementDirection.UP);
        }
        appDriver.tap(filter.getDescription());
        return this;
    }

    private IosMobileProductLandingPage clearFilterOrSort() {
        appDriver.tap("Clear");
        return this;
    }

    protected IosMobileProductLandingPage applyFilterOrSort() {
        appDriver.tap("APPLY");
        return this;
    }

    @Override
    public String filterItem(FilterBy filter) {
        selectFilter(filter);
        MobileElement filterElement = appDriver
        		.waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
        		.retrieveMobileElement(By.className("XCUIElementTypeTable"), By.className("XCUIElementTypeCell"), By.className("XCUIElementTypeStaticText"));
        final String filterValue = filterElement.getText();
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product filter: " + filter.getDescription() + "/" + filterValue);
        appDriver.tap(filterElement);
        applyFilterOrSort();

        return filterValue;
    }

    @Override
    public List<String> filterItemWithMultipleOptions(FilterBy filter) {
        selectFilter(filter);

        final List<String> filterValues = appDriver
                .retrievePresentMobileElements(By.xpath("//XCUIElementTypeTable/XCUIElementTypeCell/descendant::XCUIElementTypeStaticText[1]"))
                .subList(0, 2)
                .stream()
                .map(RemoteWebElement::getText)
                .collect(Collectors.toList());

        filterValues.forEach(filterValue -> {
            RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product filter: " + filter.getDescription() + "/" + filterValue);
            appDriver.click(By.name(filterValue));
        });

        applyFilterOrSort();
        return filterValues;
    }

    @Override
    public IosMobileProductLandingPage filterBySearch(String searchKeyword) {
        throw new NotImplementedException("Method is not supported.");
    }

    @Override
    public IosMobileProductLandingPage openFilter() {

    	final By filterLocator = By.xpath("//XCUIElementTypeButton[@name='Filter']");
    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(filterLocator), 2000);
        final MobileElement filterElement = appDriver.retrieveMobileElement(filterLocator);

        filterElement.click();
            
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("APPLY")));
        return this;
    }

    @Override
    public IosMobileProductLandingPage closeFilter() {
        appDriver.tap("Close");
        return this;
    }

    @Override
    public IosMobileProductLandingPage clearSingleFilter(FilterBy filter) {
        return openFilter()
                .selectFilter(filter)
                .clearFilterOrSort()
                .applyFilterOrSort();
    }

    @Override
    public IosMobileProductLandingPage clearAllFilters() {
        return clearFilterOrSort().applyFilterOrSort();
    }

    @Override
    public Boolean isFilterApplied(String filterValue) {
    	final By categoriesLocator = By.name(filterValue);
    	return null != appDriver.getWrappedIOSDriver().findElement(categoriesLocator);    	
    }

    @Override
    public Boolean isAllFiltersCleared() {
        final int availableFilters = appDriver.retrievePresentMobileElements(By.name("chevron")).size();
        return appDriver.retrieveMobileElements(By.xpath("//XCUIElementTypeStaticText[starts-with(@name,'All')]")).size() == availableFilters;
    }

    @Override
    public IosMobileProductLandingPage sortBy(SortBy sortBy) {
        openFilter();

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Applying product sorting: " + sortBy.getDescription());

        appDriver.tap(sortBy.getDescription());
        applyFilterOrSort();
        return this;
    }

    @Override
    public IosMobileProductLandingPage clearSorting() {
        return openFilter().clearFilterOrSort();
    }

	@Override
	public int numberOfSwatchesDisplayed() {
		final By productLandingSwatchLocator = By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeImage");
		appDriver.waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(productLandingSwatchLocator));
		List<MobileElement> swatches = appDriver.retrievePresentMobileElements(productLandingSwatchLocator);

		if (null != swatches) {
			return swatches.size();
		}
		return 0;
	}

	@Override
	public int excessSwatchValue() {
		final By excessSwatchValueLocator = By.xpath("//XCUIElementTypeStaticText[@name='swatch_Count_Label']");
		return Integer.valueOf(appDriver.retrieveMobileElementText(excessSwatchValueLocator));
	}
}