package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.domain.ProductDetails;
import com.riverisland.app.automation.enums.SwipeElementDirection;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface ProductDetailsPage<T> {
    T waitForPDPToLoad();

    T changeProductSwatch(String displayedProductNumber);

    T addToBag();

    T addInStockSizeToBag();

    T viewBag();

    T addToWishlist();

    T returnToProductListing();

    T openDetails();

    T openFabricAndCare();

    T openDeliveryAndReturns();

    T openSizeGuide();

    T openCheckStock();

    T openWearItWith();

    Boolean hasWearItWithItems();
    
    Boolean hasSimilarItems();

    ProductDetails getProductDetails();

    String getProductName();

    String getProductPrice();

    String getProductDescription();

    String getProductSpecification();

    String getProductNumber();

    Boolean isSizeGuideDisplayed(String sizeGuideCategory);

    T openProductImageInFullView();

    T swipeProductImage(SwipeElementDirection direction);

    T swipeProductImageInFullView(SwipeElementDirection direction);

    int getProductImagesSize();

    Boolean isAtProductImageIndex(int index);

    T openAllStores();

    T selectSizeToCheckStock();

    T checkStock();

    T searchStockInStore(String store);

    Boolean isStockCheckMessageDisplayed();

    Boolean isStockInStoreDisplayed(String store);

    String getFabricAndCareDetails();

    String getDeliveryAndReturnInfo();
    
    T back();
    
    Boolean isDeliveryPromoDisplayed();
    
    int numberOfSwatchesDisplayed();

	T addToWishlistExcludedSelectedSize();
}