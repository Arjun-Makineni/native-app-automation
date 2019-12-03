package com.riverisland.android.test.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.domain.ProductDetails;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.ProductDetailsPage;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidKeyCode;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidProductDetailsPage extends AndroidCorePage implements ProductDetailsPage<AndroidProductDetailsPage> {

	private String selectedSize = "";
	
    public AndroidProductDetailsPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidProductDetailsPage waitForPDPToLoad() {
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("product_details_price")));
        return this;
    }

    @Override
    public AndroidProductDetailsPage changeProductSwatch(String displayedProductNumber) {
        appDriver.retrieveMobileElements(androidId.apply("product_details_swatch"))
                .forEach(swatch -> {
                    if (appDriver.retrieveMobileElementText(androidId.apply("product_details_id")).contains(displayedProductNumber)) {
                        swatch.click();
                    }
                });
        return this;
    }

    @Override
    public AndroidProductDetailsPage addToBag() {
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("product_details_price")))
                .scrollIntoView("ADD TO BAG");

        appDriver.tap(androidId.apply("product_details_add_to_bag"));
        return this;
    }

    private void selectInStockSize(Predicate<String> filter) {

        String sizeInStock = findInStockSize();
        		
        int moveDown = 20;

        while (moveDown-- > 0) {
            if (sizeInStock != null) {
                appDriver
                        .tap(sizeInStock);
                break;
            } else {
                appDriver.pressAndroidKey(AndroidKeyCode.KEYCODE_PAGE_DOWN);
                sizeInStock = findInStockSize();
            }
        }
        if (moveDown == 0) {
            throw new RiverIslandTestError("Product does not have stock!");
        }
    }
    
    private String findInStockSize() {
        List<String> sizes = appDriver.retrievePresentMobileElements(androidId.apply("bottomsheet_item_one"))
                .stream().map(RemoteWebElement::getText)
                .collect(Collectors.toList());

        if (!appDriver.isDisplayed(androidId.apply("bottomsheet_item_two"), 2)) {
        	return sizes.get(0);
        }
        else {
        	List<String> stockStatus = getStockStatusValues();
        	int index = 0;
        	for (String status : stockStatus) {
        		if (!status.contains("Out of stock")) {
        			return sizes.get(index); 
        		}
        		++index;
        	}
        }
        return null;
    }
    
    private List<String> getStockStatusValues() {
    	List<String> retval = new ArrayList<>();
    	
    	List<MobileElement> elements = appDriver.retrievePresentMobileElements(By.xpath("//android.widget.TextView[contains(@resource-id, 'bottomsheet_item_one')]/.."));
    	
    	for (MobileElement element : elements) {
    	
    		MobileElement statusElement = null;
    		try {
    			statusElement = element.findElement(By.xpath("//android.widget.TextView[contains(@resource-id, 'bottomsheet_item_two')]"));
    		}
    		catch (NoSuchElementException nse) {}
    		
    		if (null != statusElement) {
    			retval.add(statusElement.getText());
    		}
    		else {
    			retval.add("");
    		}
    	}
    	
    	return retval;
    			
    }
    private void selectInStockSize() {
        final Predicate<String> sizeFilter = size -> !size.contains("out of stock");
        selectInStockSize(sizeFilter);
    }

    private void selectInStockSizeAndExcluded(String excludeSize) {
    	final Predicate<String> filter = size -> !size.contains("out of stock") && !size.contains(excludeSize);
    	selectInStockSize(filter);
    }
    
    @Override
    public AndroidProductDetailsPage addInStockSizeToBag() {
        appDriver.pressAndroidKey(AndroidKeyCode.KEYCODE_PAGE_DOWN);

        final String confirmMessage = "Added to bag";
        selectInStockSize();
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidText.apply("Added to bag")));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to bag");
        return this;
    }

    @Override
    public AndroidProductDetailsPage viewBag() {
    	RiverIslandLogger.getInfoLogger(this.getClass()).info("viewing bag");
        appDriver
        	.back()
        	.tap(androidId.apply("action_bag"));
        return this;
    }

    @Override
    public AndroidProductDetailsPage addToWishlist() {
        appDriver.scrollIntoView("Wishlist");
        appDriver.tap(androidId.apply("product_details_wishlist_heart"));
        final String confirmMessage = "Added to wishlist";
        selectInStockSize();
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidText.apply(confirmMessage)));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to wishlist");
        return this;
    }

    private AndroidProductDetailsPage addToWishlistInStock(String exclude) {
        appDriver.scrollIntoView("Wishlist");
        appDriver.tap(androidId.apply("product_details_wishlist_heart"));
        final String confirmMessage = "Added to wishlist";
        if (null == exclude) {
        	selectInStockSize();
        } else {
        	selectInStockSizeAndExcluded(exclude);
        }
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidText.apply(confirmMessage)));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added product to wishlist");   	
    	return this;
    }
    
    @Override
    public AndroidProductDetailsPage addToWishlistExcludedSelectedSize() {
		return addToWishlistInStock(selectedSize);
	}
    @Override
    public AndroidProductDetailsPage back() {
    	appDriver.back();
    	return this;
    }
    @Override
    public AndroidProductDetailsPage returnToProductListing() {
        appDriver.back();

        if (appDriver.isDisplayed(androidId.apply("search_bar_text"), 2)) {
            appDriver.back();
        }
        return this;
    }

    @Override
    public AndroidProductDetailsPage openDetails() {
        appDriver.scrollIntoViewAndTap("Details");
        return this;
    }

    @Override
    public AndroidProductDetailsPage openFabricAndCare() {
        appDriver.scrollIntoViewAndTap("Fabric & care");
        return this;
    }

    @Override
    public AndroidProductDetailsPage openDeliveryAndReturns() {
        appDriver.scrollIntoViewAndTap("Delivery & returns");
        return this;
    }

    @Override
    public AndroidProductDetailsPage openSizeGuide() {
        appDriver.scrollIntoViewAndTap("Size Guide");
        return this;
    }

    @Override
    public AndroidProductDetailsPage openCheckStock() {
        appDriver.scrollIntoViewAndTap("Check Stock");
        return this;
    }

    @Override
    @Deprecated
    public AndroidProductDetailsPage openWearItWith() {
        return this;
    }

    @Override
    public Boolean hasWearItWithItems() {
        appDriver.scrollIntoView("Wear it with");
        return appDriver.retrieveMobileElements(androidId.apply("wear_with_image")).size() > 0;
    }
    
    @Override
    public Boolean hasSimilarItems() {
    	appDriver.scrollIntoViewAndTap("Similar items");
    	return appDriver.retrieveMobileElements(androidId.apply("wear_with_image")).size() > 0;
    }

    @Override
    public ProductDetails getProductDetails() {
        final ProductDetails productDetails = new ProductDetails(
                appDriver.retrieveMobileElementText(androidId.apply("product_details_name")),
                appDriver.retrieveMobileElementText(androidId.apply("product_details_price")),
                StringUtils.substringAfterLast(appDriver.retrieveMobileElementText(androidId.apply("product_details_id")), ": "));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Product details: " + productDetails.toString());

        return productDetails;
    }

    @Override
    public String getProductName() {
        return appDriver.retrieveMobileElementText(androidId.apply("product_details_name"));
    }

    @Override
    public String getProductPrice() {
        return appDriver.retrieveMobileElementText(androidId.apply("product_details_price"));
    }

    @Override
    @Deprecated
    public String getProductDescription() {
        return null;
    }

    @Override
    public String getProductSpecification() {
        return getGenericWebViewData("Details");
    }

    @Override
    public String getProductNumber() {
        String productNumber = null;
        final By productIdLocator = androidId.apply("product_details_id");
        
        if (appDriver.isDisplayed(productIdLocator, 2)) {
        	productNumber = appDriver.retrieveMobileElementText(productIdLocator);
        }
        return productNumber != null ? StringUtils.substringAfter(productNumber, ":").trim() : productNumber;
    }

    @Override
    public Boolean isSizeGuideDisplayed(String sizeGuideCategory) {
        return StringUtils.equalsIgnoreCase(getSizeGuideTitle(), sizeGuideCategory);
    }

    @Override
    public AndroidProductDetailsPage openProductImageInFullView() {
        appDriver.tap(androidId.apply("carousel_image"));
        return this;
    }

    @Override
    public AndroidProductDetailsPage swipeProductImage(SwipeElementDirection direction) {
        switch (direction) {
            case LEFT:
                appDriver.swipeLeft(androidId.apply("carousel_image"));
                break;

            case RIGHT:
                appDriver.swipeRight(androidId.apply("carousel_image"));
                break;
        }
        appDriver.pause(200);
        return this;
    }

    @Override
    public AndroidProductDetailsPage swipeProductImageInFullView(SwipeElementDirection direction) {
        swipeProductImage(direction);
        return this;
    }

    @Override
    public int getProductImagesSize() {
        return appDriver.retrieveMobileElements(By.className("android.widget.LinearLayout"), By.className("android.widget.ImageView")).size();
    }

    @Override
    public Boolean isAtProductImageIndex(int index) {
        throw new NotImplementedException("Its not possible to retrieve the index for the currently displayed image. This is not set in the app source.");
    }

    @Override
    public AndroidProductDetailsPage openAllStores() {
        if (appDriver.isDisplayed(androidText.apply("ALLOW"), 2)) {
            appDriver.tap("ALLOW");
        }
        appDriver.tap("All Stores");
        return this;
    }

    @Override
    public AndroidProductDetailsPage selectSizeToCheckStock() {
        appDriver
                .tap(androidId.apply("size_picker_text"));
        return this;
    }

    @Override
    public AndroidProductDetailsPage checkStock() {
        appDriver.tap(androidId.apply("check_stock_button"));
        return this;
    }

    @Override
    public AndroidProductDetailsPage searchStockInStore(String store) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public Boolean isStockCheckMessageDisplayed() {
        return StringUtils.isNotBlank(appDriver.retrieveMobileElementText(androidId.apply("check_stock_store_quantity")));
    }

    @Override
    public Boolean isStockInStoreDisplayed(String store) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public String getFabricAndCareDetails() {
        return getGenericWebViewData("Fabric & care");
    }

    @Override
    public String getDeliveryAndReturnInfo() {
        return getGenericWebViewData("Delivery & returns");
    }

    private String getSizeGuideTitle() {
    	String displayedTitle = appDriver.retrieveMobileElement(By.xpath("//*[contains(@resource-id,'sizeGuideHeader')]")).getAttribute("text");
    	if (null == displayedTitle || displayedTitle.isEmpty()) {
    		displayedTitle = appDriver.retrieveMobileElement(By.xpath("//*[contains(@resource-id,'sizeGuideHeader')]")).getAttribute("contentDescription");
    	}    		
    	return displayedTitle;
    }
    private String getGenericWebViewData(String label) {
        final String attr = "text";

        return Arrays.toString(
                appDriver
                        .retrieveMobileElements(By.xpath(String.format("//android.widget.TextView[@text='%s']//following::*[@%s != '']", label, attr)))
                        .stream()
                        .map(t -> t.getAttribute(attr))
                        .collect(Collectors.toList())
                        .stream()
                        .filter(t -> !t.contains("•"))
                        .collect(Collectors.toList()).toArray()).replace("[", "").replace("]", "");
    }

	@Override
	public Boolean isDeliveryPromoDisplayed() {
		final By deliveryMessageLocator = androidId.apply("delivery_prompt_message");
		if (appDriver.isDisplayed(deliveryMessageLocator, 2)) {
			String displayedPromo = appDriver.retrieveMobileElementText(deliveryMessageLocator); 
			return (null != displayedPromo && 
					displayedPromo.contains("Spend") &&
					displayedPromo.contains("more for free delivery"));
		}
		return Boolean.FALSE;
	}

	@Override
	public int numberOfSwatchesDisplayed() {
		final By swatchesLocator = androidId.apply("product_details_swatch");
		//Make sure swatches are in focus
		appDriver.swipe(new Point(200, 400), 0, -100);
		if (appDriver.isDisplayed(swatchesLocator, 2)) {
			List<MobileElement> swatches = appDriver.retrieveMobileElements(swatchesLocator);
			if (null != swatches) {
				return swatches.size();
			}
		}
		return 0;
	}
}