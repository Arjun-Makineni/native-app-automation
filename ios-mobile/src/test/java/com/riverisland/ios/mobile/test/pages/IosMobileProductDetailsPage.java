package com.riverisland.ios.mobile.test.pages;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.domain.ProductDetails;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ProductDetailsPage;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileProductDetailsPage implements ProductDetailsPage<IosMobileProductDetailsPage> {
    private RiverIslandNativeAppDriver appDriver;
    private String selectedSize = "";

    public IosMobileProductDetailsPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    private final By productNumberLocator = By.xpath("//XCUIElementTypeStaticText[contains(@name,'Product no:')]");

    @Override
    public IosMobileProductDetailsPage waitForPDPToLoad() {

        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(productNumberLocator));
        return this;
    }

    @Override
    public IosMobileProductDetailsPage changeProductSwatch(String displayedProductNumber) {
        final List<MobileElement> swatches = appDriver
                .retrievePresentMobileElements(By.xpath("//XCUIElementTypeCollectionView/XCUIElementTypeCell"))
                .stream()
                .filter(t -> t.getLocation().getX() != 0)
                .collect(Collectors.toList());

        final By staleSwatchLocator = By.name("Product no: " + displayedProductNumber);

        swatches.forEach(swatch -> {
            if (appDriver.isDisplayed(staleSwatchLocator)) {
                RiverIslandLogger.getInfoLogger(this.getClass()).info("Changing product swatch");
                swatch.click();
            }
        });
        appDriver.waitFor(ExpectedConditions.invisibilityOfElementLocated(staleSwatchLocator));
        return this;
    }

    private static final String ADD_TO_BAG_TEXT = "Add_To_Bag_Button";
    @Override
    public IosMobileProductDetailsPage addToBag() {
        if (!appDriver.isDisplayed(By.name(ADD_TO_BAG_TEXT), 2)) {
        	appDriver.scrollIntoViewAndTap(ADD_TO_BAG_TEXT);
        }
        else {
        	appDriver.tap(ADD_TO_BAG_TEXT);
        }
        return this;
    }

    private boolean selectInStockSize() {

    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Select size")), 100);
		TouchAction touchAction = new TouchAction(appDriver.getWrappedIOSDriver());
		
		int attempts = 5;
		int y = 484;
		final int sizeListSpace = 40;
		
		while(attempts > 0) {
			
			touchAction.tap(PointOption.point(20, y))
				   .waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)))
				   .release()
				   .perform();
			
			if (appDriver.waitToExist(ExpectedConditions.visibilityOfElementLocated(By.name("Added to bag")), 500)) {
				return true;
			}
			attempts--;
			y = y + sizeListSpace;
		}
			
		return false;
    }
    private String manageSelectedSize(MobileElement size) {
    	selectedSize = size.getText();
    	return selectedSize;
    }
    
    private boolean selectInStockSize(String excludeSize) {
        return appDriver.scrollPickerWheelToExpectedValue(
                By.className("XCUIElementTypePickerWheel"),
                20,
                t -> !StringUtils.containsIgnoreCase(t.getText(), "out of stock")
                	&& !StringUtils.containsIgnoreCase(t.getText(), excludeSize)) != null;
	
    }

    @Override
    public IosMobileProductDetailsPage addInStockSizeToBag() {
        
        final boolean hasStock = selectInStockSize();

        if (!hasStock) {
            throw new RiverIslandTestError("Product does not have stock!");
        }

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to bag");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage viewBag() {
        appDriver.tap("VIEW BAG");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage addToWishlist() {
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
                .scrollDownUntilInView(By.name("Wishlist"))
                .tap("Wishlist");

        selectInStockSize();

        appDriver.tap("Add to Wishlist");

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to wishlist");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage returnToProductListing() {
        appDriver
                .swipe(SwipeElementDirection.DOWN)
                .tap(By.xpath("//XCUIElementTypeNavigationBar/XCUIElementTypeButton"));
        return this;
    }

    @Override
    public IosMobileProductDetailsPage openDetails() {
        appDriver
                .swipe(SwipeElementDirection.UP)
                .tap("Details");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage openFabricAndCare() {
        appDriver
                .swipe(SwipeElementDirection.UP)
                .tap("Fabric & care");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage openDeliveryAndReturns() {
        appDriver
                .swipe(SwipeElementDirection.UP)
                .tap("Delivery & returns");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage openSizeGuide() {
        appDriver.tap("Size Guide");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage openCheckStock() {
        appDriver
                .swipe(SwipeElementDirection.UP)
                .tap("Check Stock");
        return this;
    }

    @Override
    @Deprecated
    public IosMobileProductDetailsPage openWearItWith() {
        return this;
    }

    @Override
    public Boolean hasWearItWithItems() {
        appDriver.swipe(SwipeElementDirection.UP);

        if (!appDriver.isDisplayed(By.name("Wear it with"))) {
            return null;
        }

        // the element within the group is look up differently on real device / simulator
        final By wearItWithPrice = By.xpath(String.format("//XCUIElementTypeButton[@name='Wear it with']//%s::XCUIElementTypeStaticText[1]", GlobalConfig.isAppRunningLocally() ? "preceding" : "following"));

        return appDriver
                .retrievePresentMobileElement(wearItWithPrice)
                .getText().contains(".00");
    }

    @Override
    public ProductDetails getProductDetails() {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Getting PDP details");
        
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")));
        List<MobileElement> pdps = appDriver.getWrappedIOSDriver().findElements(By.xpath("//XCUIElementTypeTable/XCUIElementTypeCell/XCUIElementTypeStaticText"));
        
        final List<String> pdpDetails = pdps
                .stream()
                .map(RemoteWebElement::getText)
                .collect(Collectors.toList());

        if (pdpDetails.isEmpty()) {
            throw new RuntimeException("Unable to get PDP details.");
        }

        final ProductDetails productDetails = new ProductDetails(
                pdpDetails.stream().filter(t -> !StringUtils.containsAny(t, "Product no:", ".00")).findFirst().get(), // description
                pdpDetails.stream().filter(t -> t.contains(".00")).findFirst().get(), // price
                StringUtils.substringAfterLast(pdpDetails.stream().filter(t -> t.startsWith("Product no:")).findFirst().get(), ":").trim()); // product no.

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Product details: " + productDetails.toString());
        return productDetails;
    }

    @Override
    @Deprecated
    public String getProductName() {
        return null;
    }

    @Override
    @Deprecated
    public String getProductPrice() {
        return null;
    }

    @Override
    @Deprecated
    public String getProductDescription() {
        return getProductSpecification();
    }

    @Override
    public String getProductSpecification() {
        return getGenericDetails("Details");
    }

    @Override
    public String getProductNumber() {
     	final int productNumberDisplayTimeout = 2; // Two seconds timeout
    	if (appDriver.isDisplayed(productNumberLocator, productNumberDisplayTimeout)) {
    		return StringUtils.substringAfter(appDriver.retrieveMobileElementText(productNumberLocator), "Product no: ").trim();
    	} else {
    		appDriver.back(); //ProductId from Qubit trending feed is not in the test environment so ignore
    		return null;
    	}
    }

    @Override
    public Boolean isSizeGuideDisplayed(String sizeGuideCategory) {
        return appDriver.retrieveMobileElement(By.name(sizeGuideCategory.toLowerCase())).isDisplayed();
    }

    @Override
    public IosMobileProductDetailsPage openProductImageInFullView() {
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
                .retrieveMobileElements(By.className("XCUIElementTypeTable"), By.className("XCUIElementTypeCell"))
                .stream()
                .findFirst().get()
                .click();
        return this;
    }

    @Override
    public IosMobileProductDetailsPage swipeProductImage(SwipeElementDirection direction) {
        final MobileElement productImage = appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
                .retrieveMobileElement(By.className("XCUIElementTypeTable"), By.className("XCUIElementTypeCell"));

        appDriver.swipe(direction, productImage);
        return this;
    }

    @Override
    public IosMobileProductDetailsPage swipeProductImageInFullView(SwipeElementDirection direction) {
        appDriver.swipe(direction);
        return this;
    }

    @Override
    public int getProductImagesSize() {
        return Integer.parseInt(StringUtils.substringAfter(appDriver.retrievePresentMobileElement(By.className("XCUIElementTypePageIndicator")).getText(), "of ").trim());
    }

    @Override
    public Boolean isAtProductImageIndex(int index) {
        return appDriver.retrievePresentMobileElement(By.className("XCUIElementTypePageIndicator")).getText().startsWith("page " + index);
    }

    @Override
    public IosMobileProductDetailsPage openAllStores() {
        if (appDriver.isDisplayed(By.name("Allow"), 2)) {
            appDriver.tap("Allow");
        }

        appDriver.tap("All Stores");
        return this;
    }

    @Override
    public IosMobileProductDetailsPage selectSizeToCheckStock() {
        selectInStockSize();
        appDriver.tap("Check Stock");
        return null;
    }

    @Override
    public IosMobileProductDetailsPage checkStock() {
        appDriver.tap(By.className("XCUIElementTypeCell"), By.xpath("//XCUIElementTypeButton[2]"));
        return this;
    }

    @Override
    public IosMobileProductDetailsPage searchStockInStore(String store) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public Boolean isStockCheckMessageDisplayed() {
        return appDriver.isDisplayed(By.name("Out of stock")) || appDriver.isDisplayed(By.xpath("//XCUIElementTypeStaticText[contains(@name,'in stock')]"));
    }

    @Override
    public Boolean isStockInStoreDisplayed(String store) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public String getFabricAndCareDetails() {
        return getGenericDetails("Fabric & care");
    }

    @Override
    public String getDeliveryAndReturnInfo() {
        return getGenericDetails("Delivery & returns");
    }

    private String getGenericDetails(String label) {
        return Arrays.toString(appDriver
                .waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name("Back")))
                .waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name(label)))
                .retrievePresentMobileElements(By.className("XCUIElementTypeStaticText"))
                .stream()
                .map(RemoteWebElement::getText)
                .collect(Collectors.toList()).toArray());
    }

	@Override
	public IosMobileProductDetailsPage back() {
		appDriver.back();
		return this;
	}

	@Override
	public Boolean hasSimilarItems() {
        appDriver.swipe(SwipeElementDirection.UP);

        if (!appDriver.isDisplayed(By.name("Similar items"))) {
            return null;
        }

        // the element within the group is look up differently on real device / simulator
        final By similarItemsPrice = By.xpath(String.format("//XCUIElementTypeButton[@name='Similar items']//%s::XCUIElementTypeStaticText[1]", GlobalConfig.isAppRunningLocally() ? "preceding" : "following"));

        return appDriver
                .retrievePresentMobileElement(similarItemsPrice)
                .getText().contains(".00");
	}

	@Override
	public Boolean isDeliveryPromoDisplayed() {
		final By deliveryPromoLocator = By.xpath("//XCUIElementTypeStaticText[contains(@name, 'more for free delivery')]");
	
		if (appDriver.isDisplayed(deliveryPromoLocator, 3)) {
			return Boolean.TRUE;
		} 
		else
		{
			return Boolean.FALSE;
		}
	}

    private static final By SWATCH_LOCATOR = By.xpath("//XCUIElementTypeCell[@name='colour_Swatch_Cell']");
	@Override
	public int numberOfSwatchesDisplayed() {
		appDriver.waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(SWATCH_LOCATOR));
		if (appDriver.isEnabled(SWATCH_LOCATOR)) {
			return appDriver.retrievePresentMobileElements(SWATCH_LOCATOR).size();
		}
		return 0;
	}

	@Override
	public IosMobileProductDetailsPage addToWishlistExcludedSelectedSize() {
        appDriver
        	.waitFor(ExpectedConditions.visibilityOfElementLocated(By.className("XCUIElementTypeTable")))
        	.scrollDownUntilInView(By.name("Wishlist"))
        	.tap("Wishlist");
        selectInStockSize(selectedSize);
        appDriver.tap("Add to Wishlist");
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to wishlist");
        return this;
	}
}