package com.riverisland.app.automation.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.assertj.core.api.Assertions;
import org.testng.SkipException;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.domain.ProductDetails;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.FilterBy;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.enums.SortBy;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.enums.ToggleType;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.utils.AccountUtils;
import com.riverisland.app.automation.utils.MonetaryAmountUtils;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;
import com.riverisland.automation.utils.ecom.domain.account.AccountRegistrationRequest;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

/**
 * Created by Prashant Ramcharan on 31/05/2017
 */
public class AppHelper extends AppSession {

    public AppHelper() {
    }

    public AppHelper changeCurrency(Region region) {
        myRiverIslandPage.openChangeCurrency();
        myRiverIslandPage.selectCurrency(region);
        selectedRegion = region;
        return this;
    }

    public AppHelper signUp() {
        customer = Customer.Builder.createDefaultBuilder().buildNewCustomer();
        myRiverIslandPage.signUp(customer);
        return this;
    }
    
    public AppHelper signUp(Customer newCustomer) {
    	customer = newCustomer;
    	myRiverIslandPage.signUp(customer);
    	return this;
    }

    public AppHelper verifyMyAccountIsDisplayed() {
        Assertions.assertThat(myRiverIslandPage.isMyAccountDisplayed()).isTrue();
        return this;
    }

    private boolean hasEagerlyLoadedProducts = tcplProducts != null && tcplProducts.entrySet().stream().noneMatch(t -> t.getValue().isEmpty());

    /**
     * If we previously obtained products from the Tcpl API, then we use this to search and the select the product.
     */
    private AppHelper searchAndSelectEagerlyLoadedProduct() {
        final List<Product> productList = tcplProducts.get(category);
        Collections.shuffle(productList);

        return searchAndSelectProduct(productList.subList(0, productList.size() >= 5 ? 5 : productList.size()));
    }

    public AppHelper searchAndVerifyMessage(String criteria, String message) {
        shopPage.search(criteria, true);
        assertThat(searchPage.isSearchMessageDisplayed(message)).isTrue();
        return this;
    }

    public AppHelper searchAndVerifyMessage(String criteria, boolean selectSearchResult, String message) {
        shopPage.search(criteria, selectSearchResult);
        assertThat(searchPage.isSearchMessageDisplayed(message)).isTrue();
        return this;
    }

    public AppHelper searchAndSelectProduct(String productId) {
        shopPage.search(productId, true);
        productLandingPage.selectProduct(productId);
        productDetailsPage.waitForPDPToLoad();
        return this;
    }

    public AppHelper searchAndSelectProduct(List<Product> productList) {
        Product product = shopPage.searchLazilyForProduct(productList);

        if (product == null) {
            throw new RuntimeException("No products from the following list are available in the app -> " + Arrays.toString(productList.toArray()));
        }

        productLandingPage.selectProduct(product.getId());
        productDetailsPage.waitForPDPToLoad();
        return this;
    }

    public AppHelper searchAndSelectProduct(Product product, Category category) {
        if (product != null) {
            shopPage.search(product.getId(), true);
            productLandingPage.selectProduct(product.getId());
        } else {
            openProductSelection(category.name(), category.getRandomSubCategory());
            selectProduct();
        }
        return this;
    }

    public AppHelper verifySingleCategorySearchResult(String search) {
        assertThat(searchPage.isExpectedNumberOfSearchResults(search, 1)).isTrue();
        return this;
    }

    public AppHelper verifyMultiCategorySearchResult(String search) {
        assertThat(searchPage.isExpectedNumberOfSearchResults(search, 4)).isTrue();
        return this;
    }

    public AppHelper openProductSelection(String... categories) {
        category = Category.valueOf(categories[0].toUpperCase());

        if (hasEagerlyLoadedProducts) {
            return this;
        }

        shopPage.searchCategoryAndSubCategory(category.getName(), categories.length > 1 ? categories[1] : category.getRandomSubCategory());
        return this;
    }

    public AppHelper openProductSelectionFromSearch(String... categories) {
        category = Category.valueOf(categories[0].toUpperCase());
        shopPage.searchCategoryAndSubCategory(category.getName(), categories.length > 1 ? categories[1] : category.getRandomSubCategory());
        return this;
    }

    public AppHelper addProductToBag(Category category) {
        openShopTab()
                .openProductSelection(category.getName(), category.getRandomSubCategory())
                .selectProduct()
                .addProductToBag()
                .addInStockSizeToBag()
                .returnToPLP();
        return this;
    }

    public AppHelper addProductToBag(Product product, Category category) {
        if (product == null) {
            return addProductToBag(category);
        }
        openShopTab()
                .searchAndSelectProduct(product.getId())
                .addProductToBag()
                .addInStockSizeToBag()
                .returnToPLP();
        return this;
    }

    public AppHelper addProductAndViewBag(Category category) {
        openShopTab()
                .openProductSelection(category.getName(), category.getRandomSubCategory())
                .selectProduct()
                .addProductToBag()
                .addInStockSizeToBag();
        productDetailsPage.viewBag();
        return this;
    }

    public AppHelper addProductToWishlist(Category category) {
        openShopTab()
                .openProductSelection(category.getName(), category.getRandomSubCategory())
                .selectProduct()
                .addProductToWishlist()
                .returnToPLP();
        return this;
    }

    public AppHelper addProductToWishlist(Product product, Category category) {
        if (product == null) {
            return addProductToWishlist(category);
        }
        openShopTab()
                .searchAndSelectProduct(product.getId())
                .addProductToWishlist()
                .returnToPLP();
        return this;
    }
    
    public AppHelper addMultipleSizeProductsToWishlist(Product product, Category category) {
    	return openShopTab()
    		.searchAndSelectProduct(product.getId())
    		.addMultipleSizeProductsToWishlist()
    		.returnToPLP();  	
    }

    public AppHelper selectProduct() {
        if (hasEagerlyLoadedProducts) {
            return searchAndSelectEagerlyLoadedProduct();
        }
        waitForProductsToLoad();
        productLandingPage.selectProduct();
        productDetailsPage.waitForPDPToLoad();
        return this;
    }

    public AppHelper selectProductWithSwatch() {
        if (!productLandingPage.selectProductWithSwatch()) {
            throw new SkipException("Unable to find a product with a swatch!");
        }
        return this;
    }

    public AppHelper changeProductSwatch(String displayedProductNumber) {
        productDetailsPage.changeProductSwatch(displayedProductNumber);
        return this;
    }

    public AppHelper addInStockSizeToBag() {
        productDetailsPage.addInStockSizeToBag();
        return this;
    }

    public AppHelper addProductToBag() {
        productDetailsPage.addToBag();
        return this;
    }

    public AppHelper addProductToWishlist() {
        productDetailsPage.addToWishlist();
        return this;
    }
    
    public AppHelper addMultipleSizeProductsToWishlist() {
    	productDetailsPage.addToWishlist();
    	productDetailsPage.addToWishlistExcludedSelectedSize();
    	return this;
    }

    public AppHelper returnToPLP() {
        productDetailsPage.returnToProductListing();
        return this;
    }

    /**
     * Android Only
     */
    public AppHelper returnToShopFromPDP() {
        goBack()
        .goBack()
        .goBack();
        return this;
    }
    public AppHelper returnToHomeFromPDP() {
        this.returnToShopFromPDP().goBack();
        return this;
    }

    public AppHelper increaseFirstBagItemQty() {
        myBagPage.increaseBagQty(1);
        return this;
    }

    public AppHelper decreaseFirstBagItemQty() {
        myBagPage.decreaseBagQty(1);
        return this;
    }

    public AppHelper removeBagItem(Product product) {
        if (product == null) {
            throw new RiverIslandTestError("Unable to remove bag item because the product data is missing");
        }
        myBagPage.removeBagItem(product.getDescription());
        return this;
    }

    public AppHelper addBagItemToWishlist(Product product) {
        if (product == null) {
            throw new RiverIslandTestError("Unable to add the bag item to the wishlist because the product data is missing");
        }
        myBagPage.moveBagItemToWishlist(product.getDescription());
        return this;
    }

    public AppHelper clearShoppingBag() {
        myBagPage.clearAll();
        return this;
    }

    public AppHelper verifyBagIsEmpty() {
        Assertions.assertThat(myBagPage.isBagEmpty()).isTrue();
        return this;
    }

    public AppHelper verifyTotalBagQty(int qty) {
        Assertions.assertThat(myBagPage.getTotalBagQty()).isEqualTo(qty);
        return this;
    }

    public AppHelper closeBag() {
        myBagPage.closeBag();
        return this;
    }

    public AppHelper signInFromBag() {
        myBagPage.signIn();
        return this;
    }

    public AppHelper startShoppingFromBag() {
        myBagPage.startShopping();
        return this;
    }

    public AppHelper verifyPreviousSessionBagMessageIsDisplayed(int expectedBagItems) {
        assertThat(myBagPage.isPreviousSessionBagMessageDisplayed(expectedBagItems)).isTrue();
        return this;
    }

    public AppHelper proceedToCheckout() {
        myBagPage.proceedToCheckout();
        return this;
    }

    public AppHelper signInNewlyRegisteredCustomer() {
        Validate.notNull(customer, "Customer object does not exist - did you create the account?");
        myRiverIslandPage.signIn(customer.getEmailAddress(), customer.getPassword());
        return this;
    }

    public AppHelper signIn(String email, String password) {
        myRiverIslandPage.signIn(email, password);
        return this;
    }

    public AppHelper selectDeliveryType(DeliveryType deliveryType) {
        checkoutPage.selectDeliveryType(deliveryType);
        AppSession.deliveryType = deliveryType;
        return this;
    }

    public AppHelper checkOrAddNewDeliveryHomeAddress(Region region) {
        address = Address.Builder.create().buildFromRegion(region);

        if (checkoutPage.alreadyHasAddress(address)) {
            checkoutPage.selectAddress(address);
        } else {
            checkoutPage.openAddNewDeliveryAddress();
            addDeliveryHomeAddress(address).selectDeliveryHomeAddress(address);
        }
        return this;
    }

    public AppHelper addNewDeliveryHomeAddress(Region region) {
        checkoutPage.openAddNewDeliveryAddress();
        return addDeliveryHomeAddressAndSelect(region);
    }

    public AppHelper addDeliveryHomeAddressAndSelect(Region region) {
        address = Address.Builder.create().buildFromRegion(region);
        return addDeliveryHomeAddress(address).selectDeliveryHomeAddress(address);
    }

    public AppHelper addPreciseDayDeliveryHomeAddressAndSelect() {
        address = Address.Builder.create().buildForPreciseDayDelivery();
        return addDeliveryHomeAddress(address).selectDeliveryHomeAddress(address);
    }

    private AppHelper addDeliveryHomeAddress(Address address) {
        checkoutPage.addDeliveryHomeAddress(address);
        return this;
    }

    private AppHelper selectDeliveryHomeAddress(Address address) {
        checkoutPage.selectAddress(address);

        switch (address.getRegion()) {
            case US:
                checkoutPage.selectState("CA - California");
                break;
        }
        return this;
    }

    public AppHelper lookupDeliveryHomeAddressByPostcode(Region region) {
        address = Address.Builder.create().withAddressLookup(true).buildFromRegion(region);
        checkoutPage.addDeliveryHomeAddress(address);
        return this;
    }

    public AppHelper lookupDeliveryHomeAddressByPostcodeAndSelect(Region region) {
        lookupDeliveryHomeAddressByPostcode(region);
        checkoutPage.selectAddressByPostcode(address.getPostCode());
        return this;
    }

    public AppHelper addBillingAddress(Region region) {
        address = Address.Builder.create().buildFromRegion(region);
        checkoutPage.addBillingAddress(address);
        return this;
    }

    public AppHelper addBillingAddressAndSelect(Region region) {
        addBillingAddress(region);
        checkoutPage.selectAddress(address);
        return this;
    }

    public AppHelper searchStore(String store) {
        checkoutPage.searchStore(store);
        return this;
    }

    public AppHelper selectStore(String store) {
        address = new Address(store);
        checkoutPage.selectStore(store);
        return this;
    }

    public AppHelper searchLocalPickupStoreAndConfirm(String pickupAddress) {
        checkoutPage.searchPickupStoreAndSelect(pickupAddress);

        checkoutPage.populateTelephoneForDeliveryToStore();

        final String pickupStoreAddress = checkoutPage.getPickupStoreAddress();
        Validate.notBlank(pickupStoreAddress);

        storeAddress = new Address(pickupStoreAddress, Region.GB);
        address = storeAddress;

        checkoutPage.confirmCollectionDetails();
        return this;
    }

    /**
     * iOS Only
     */
    public AppHelper searchPickupStoreAndConfirm(Region region, String pickupAddress) {
        checkoutPage.changeDeliveryCountry(region);
        checkoutPage.searchPickupStoreAndSelect(pickupAddress);

        final String pickupStoreAddress = checkoutPage.getPickupStoreAddress();
        Validate.notBlank(pickupStoreAddress);

        address = new Address(pickupStoreAddress);

        if (region == Region.GB) {
            checkoutPage.populateTelephoneForDeliveryToStore();
        }

        checkoutPage.confirmCollectionDetails();
        return this;
    }

    public AppHelper selectDeliveryOption(DeliveryOption deliveryOption) {
        AppSession.deliveryOption = deliveryOption;
        checkoutPage.selectDeliveryOption(deliveryOption);
        return this;
    }

    public AppHelper selectNominatedDay() {
        checkoutPage.selectNominatedDay();
        return this;
    }

    public AppHelper selectPreciseDayAndTime() {
        checkoutPage.selectPreciseDayAndTime();
        return this;
    }

    public AppHelper selectPaymentMethod(PaymentMethod paymentMethod) {
        checkoutPage.selectPaymentMethod(paymentMethod);
        return this;
    }

    public AppHelper processPayment(PaymentMethod paymentMethod, CardType cardType) {
        AppSession.paymentMethod = paymentMethod;

        if (paymentMethod.equals(PaymentMethod.CARD)) {
            AppSession.cardType = cardType;
            checkoutPage.populateCardDetails(cardType, Boolean.TRUE);
        } else {
            switch (paymentMethod) {
                case IDEAL:
                    checkoutPage.payWithIdeal();

                    if (address.getRegion() == Region.NL) {
                        checkoutPage.selectState("LI - Limburg");
                    }
                    completePayment();
                    checkoutPage.processIdealPayTransaction();
                    break;

                case GIROPAY:
                    checkoutPage.payWithGiroPay();
                    completePayment();
                    checkoutPage.processGiroPayTransaction();
                    break;

                case PAYPAL:
                    checkoutPage.payWithPayPal();
                    completePayment();
                    checkoutPage.processPayPalTransaction();
                    break;
                    
                case GIFTCARD:
                	completePayment();
                	break;
            }
            RiverIslandLogger.getInfoLogger(this.getClass()).info("Completed 3rd party payment using: " + paymentMethod.name());
        }
        return this;
    }

    public AppHelper payWithCard() {
        checkoutPage.payWithCard();
        return this;
    }

    public AppHelper payWithSavedCard(CardType cardType) {
        checkoutPage.populateCCVForSavedCard(cardType);
        payWithCard();
        return this;
    }

    public AppHelper verifyOrderSummary() {
        if (null != paymentMethod && paymentMethod.equals(PaymentMethod.CARD)) {
        	String displayedCardNumber = checkoutPage.getSummaryCardType().trim();
            assertThat(displayedCardNumber).containsPattern(Pattern.compile("xxxx xxxx xxxx \\d{4}$"));
        }

        selectedRegion = selectedRegion == null ? Region.GB : selectedRegion; // we apply the default

        assertThat(checkoutPage.getSummaryDeliveryAddress()).containsIgnoringCase(storeAddress != null ? storeAddress.getAddressLine1() : address.getAddressLine1());
        assertThat(checkoutPage.getSummaryDeliveryOption(deliveryOption)).containsIgnoringCase(deliveryOption.getDescription());
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), checkoutPage.getSummarySubTotal())).isTrue();
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), checkoutPage.getSummaryShippingTotal())).isTrue();
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), checkoutPage.getSummaryGrandTotal())).isTrue();        return this;
    }
    
    public AppHelper verifyOrderSummaryShippingTotal(String value) {
    	String actualShippingvalue = checkoutPage.getSummaryShippingTotal();
    	assertTrue(value.equals(actualShippingvalue), String.format("Check shipping total is as expected : %s but was : %s", value, actualShippingvalue));
    	return this;
    }

    public AppHelper verifyOrderSummaryForStoreCollection(String deliveryAddress) {
        assertThat(checkoutPage.getSummaryCardType()).isNotBlank();
        assertThat(checkoutPage.getSummaryDeliveryAddress()).containsIgnoringCase(deliveryAddress);
        assertThat(checkoutPage.getSummaryDeliveryOption(deliveryOption)).containsIgnoringCase(deliveryOption.getDescription());
        return this;
    }

    public AppHelper completePayment() {
        checkoutPage.completePayment();

        if (cardType != null && cardType.equals(CardType.MAESTRO)) {
            checkoutPage.complete3dSecure();
        }
        return this;
    }

    public AppHelper verifyOrderCompletion() {
        String address = checkoutPage.getOrderCompleteAddress();

        if (deliveryType == DeliveryType.HOME_DELIVERY) {
            assertThat(address).containsIgnoringCase(AppSession.address.getAddressLine1());
        } else {
            assertThat(address).isNotBlank();
        }

        orderNumber = checkoutPage.getOrderNumber();
        assertThat(orderNumber).isNotEmpty();
        return this;
    }

    public AppHelper continueShopping() {
        checkoutPage.continueShopping();
        return this;
    }

    public AppHelper signOut() {
        myRiverIslandPage.signOut(false);
        return this;
    }

    public AppHelper checkAndSignOut() {
        myRiverIslandPage.signOut(true);
        return this;
    }

    public AppHelper verifyIsSignedOut() {
        Assertions.assertThat(myRiverIslandPage.isSignedOut());
        return this;
    }

    public AppHelper goBack() {
        appPage.goBack();
        return this;
    }

    public AppHelper goBack(int times) {
        while (times-- > 0) {
            appPage.goBack();
        }
        return this;
    }

    public AppHelper openProductDetails() {
        productDetailsPage.openDetails();
        return this;
    }

    public AppHelper openSizeGuideFromProductDetails() {
        productDetailsPage.openSizeGuide();
        return this;
    }

    public AppHelper openCheckStockFromProductDetails() {
        productDetailsPage.openCheckStock();
        return this;
    }

    public AppHelper saveProductName() {
        productName = productDetailsPage.getProductName();
        return this;
    }

    public AppHelper saveProductNumber() {
        productNumber = productDetailsPage.getProductNumber();
        return this;
    }

    public AppHelper verifyHasWearItWithItems() {
        Boolean hasWearItWithItems = productDetailsPage.hasWearItWithItems();

        if (hasWearItWithItems == null) {
            throw new SkipException("There are no wear it with items for this product! Unable to continue verification.");
        }

        Assertions.assertThat(productDetailsPage.hasWearItWithItems()).isTrue();
        return this;
    }

    public AppHelper acceptAlert(String message) {
        appPage.acceptAlert(message);
        return this;
    }

    public AppHelper verifyMessageAndAccept(String message) {
        Assertions.assertThat(appPage.getAlertMessage()).containsIgnoringCase(message);
        appPage.acceptAlert();
        return this;
    }
    
    public AppHelper verifyPasswordResetMessage(String message) {
    	Assertions.assertThat(myRiverIslandPage.forgottenPasswordEmailConfirmation()).containsIgnoringCase(message);
    	return this;
    }

    public AppHelper verifyLandingProductDetails() {
        final ProductDetails productDetails = productDetailsPage.getProductDetails();

        Assertions.assertThat(productDetails.getProductName()).isNotEmpty();
        Assertions.assertThat(productDetails.getProductPrice()).isNotEmpty();
        Assertions.assertThat(productDetails.getProductId()).isNotEmpty();
        return this;
    }

    public AppHelper verifyProductDetails() {
        productDetailsPage.openDetails();
        Assertions.assertThat(productDetailsPage.getProductSpecification()).isNotBlank();
        return this;
    }

    public AppHelper verifyDisplayedProductNumberIsDifferent(String productNumber) {
        Assertions.assertThat(productDetailsPage.getProductNumber()).isNotEqualToIgnoringCase(productNumber);
        return this;
    }

    public AppHelper verifyFabricAndCare() {
        productDetailsPage.openFabricAndCare();
        Assertions.assertThat(productDetailsPage.getFabricAndCareDetails()).isNotBlank();
        return this;
    }

    public AppHelper verifySizeGuideIsDisplayed(Category category) {
        Assertions.assertThat(productDetailsPage.isSizeGuideDisplayed(category.getPluralName() + " size guides")).isTrue();
        return this;
    }

    public AppHelper openProductImageInFullView() {
        productDetailsPage.openProductImageInFullView();
        return this;
    }

    public AppHelper swipeProductImage(SwipeElementDirection direction) {
        productDetailsPage.swipeProductImage(direction);
        return this;
    }

    public AppHelper swipeProductImageInFullView(SwipeElementDirection direction) {
        productDetailsPage.swipeProductImageInFullView(direction);
        return this;
    }

    public int getCountOfProductImages() {
        return productDetailsPage.getProductImagesSize();
    }

    public AppHelper verifyProductImageIndex(int index) {
        Assertions.assertThat(productDetailsPage.isAtProductImageIndex(index)).isTrue();
        return this;
    }

    public AppHelper selectSizeToCheckStock() {
        productDetailsPage.selectSizeToCheckStock();
        return this;
    }

    public AppHelper checkStockFromAllStores() {
        productDetailsPage.openAllStores();
        productDetailsPage.checkStock();
        return this;
    }

    public AppHelper verifyCheckStoreMessageIsDisplayed() {
        Assertions.assertThat(productDetailsPage.isStockCheckMessageDisplayed()).isTrue();
        return this;
    }

    public AppHelper verifyWishlistSize(int size) {
        if (size == 0) {
            Assertions.assertThat(wishlistPage.isWishlistEmpty()).isTrue();
        } else {
            Assertions.assertThat(wishlistPage.getWishlistQty()).startsWith(String.valueOf(size));
        }
        return this;
    }

    public AppHelper verifyProductIsInWishlist(Product product) {
        Assertions.assertThat(wishlistPage.isProductInWishlist(product.getDescription())).isTrue();
        return this;
    }

    public AppHelper addWishlistItemsToBag() {
        wishlistPage.addWishlistItemsToBag();
        return this;
    }

    public AppHelper removeWishlistItem(Product product) {
        wishlistPage.removeWishlistItem(product.getDescription());
        return this;
    }

    public AppHelper changeWishlistItemSize() {
        wishlistPage.changeWishlistItemSize();
        return this;
    }

    public AppHelper clearWishlist() {
        wishlistPage.clearWishlist();
        return this;
    }

    public AppHelper openOrderHistory() {
        myRiverIslandPage.openOrderHistory();
        return this;
    }

    public AppHelper verifyOrdeHistoryIsEmpty() {
        Assertions.assertThat(myRiverIslandPage.isOrderHistoryEmpty()).isTrue();
        return this;
    }

    public AppHelper viewOrder(String orderNumber) {
        myRiverIslandPage.viewOrder(orderNumber);
        return this;
    }

    public AppHelper verifyOrderDetails(String orderNumber, Address orderAddress, PaymentMethod paymentMethod, Object... expectedStatus) {
        Assertions.assertThat(myRiverIslandPage.getOrderDetailsOrderNumber()).isEqualTo(orderNumber);
        Assertions.assertThat(myRiverIslandPage.getOrderDetailsPlacedOn()).isNotEmpty();
        Assertions.assertThat(myRiverIslandPage.getOrderDetailsDeliveryAddress()).contains(orderAddress.getAddressLine1());
        Assertions.assertThat(myRiverIslandPage.getOrderDetailsPaymentMethod()).startsWith(paymentMethod.equals(PaymentMethod.CARD) ? cardType.getDescription() : paymentMethod.name());
        Assertions.assertThat(myRiverIslandPage.getOrderDetailsStatus()).isIn(expectedStatus);
        return this;
    }

    public AppHelper viewFaq() {
        myRiverIslandPage.openFaq();
        return this;
    }

    public AppHelper viewFaqSection(String... section) {
        myRiverIslandPage.viewFaqSection(section);
        return this;
    }

    public AppHelper verifyFaqHasContent() {
        Assertions.assertThat(myRiverIslandPage.isFaqContentDisplayed()).isTrue();
        appPage.goBack();
        appPage.goBack();
        return this;
    }

    public AppHelper signUpToNewsletters(String email, Category category) {
        myRiverIslandPage.signUpToNewsletters(email, category.getName());
        return this;
    }

    public AppHelper viewSizeGuides() {
        myRiverIslandPage.viewSizeGuides();
        return this;
    }

    public AppHelper viewSizeGuideCategory(Category category) {
        myRiverIslandPage.viewSizeGuideCategory(category.getName());
        return this;
    }

    public AppHelper openStoreLocator() {
        myRiverIslandPage.openStoreLocator();
        return this;
    }

    public AppHelper openStoreLocatorFromShop() {
        shopPage.openStoreLocator();
        return this;
    }

    public AppHelper viewLocalStores() {
        storeLocatorPage.viewLocalStores();
        return this;
    }

    public AppHelper verifyLocalStoresExist() {
        Assertions.assertThat(storeLocatorPage.hasLocalStores()).isTrue();
        return this;
    }

    public AppHelper viewAllStores() {
        storeLocatorPage.viewAllStores();
        return this;
    }

    public AppHelper findAndOpenStore(String store) {
        storeLocatorPage.viewStore(store);
        return this;
    }

    public AppHelper verifyStoreDetails(String store) {
        Assertions.assertThat(storeLocatorPage.getStoreInfoName()).isEqualToIgnoringCase(store);
        Assertions.assertThat(storeLocatorPage.getStoreInfoAddress()).isNotEmpty();
        Assertions.assertThat(storeLocatorPage.getStoreInfoTelephone()).isNotEmpty();
        return this;
    }

    public AppHelper openStoreDirections() {
        storeLocatorPage.getDirections();
        return this;
    }

    public AppHelper openRecentlyViewed() {
        myRiverIslandPage.openRecentlyViewed();
        return this;
    }

    public AppHelper verifyRecentlyViewedProduct(String productName) {
        Assertions.assertThat(recentlyViewedPage.isRecentlyViewedProductDisplayed(productName)).as("Verifying product '" + productName + "' is displayed on the recently viewed screen").isTrue();
        return this;
    }

    public AppHelper clearRecentlyViewedList() {
        recentlyViewedPage.clearRecentlyViewedList();
        return this;
    }

    public AppHelper verifyRecentlyViewedIsEmpty() {
        Assertions.assertThat(recentlyViewedPage.isRecentlyViewedEmpty()).isTrue();
        return this;
    }

    public AppHelper toggleProductView(ToggleType toggleType) {
        productLandingPage.toggleProductView(toggleType);
        return this;
    }

    public int getProductCategoryItemCount() {
        return productLandingPage.getProductItemCount(); // displayed label with 'X Items'
    }

    public int getCountOfDisplayedProducts() {
        return productLandingPage.getCountOfDisplayedProducts(); // count of product images cells
    }

    public AppHelper verifyDisplayedProductPriceSorting(SortBy sortBy) {
        List<BigDecimal> productPrices = productLandingPage.getProductPrices();

        if (sortBy.equals(SortBy.PRICE_LOW_TO_HIGH)) {
            assertThat(productPrices.subList(1, productPrices.size())).allMatch(t -> productPrices.get(0).compareTo(t) <= 0);
        } else if (sortBy.equals(SortBy.PRICE_HIGH_TO_LOW)) {
            assertThat(productPrices.subList(1, productPrices.size())).allMatch(t -> productPrices.get(0).compareTo(t) >= 0);
        }
        return this;
    }

    public AppHelper verifyDisplayedProductCount(int count) {
        Assertions.assertThat(productLandingPage.getCountOfDisplayedProducts()).isEqualTo(count);
        return this;
    }

    public AppHelper verifyDisplayedProductsEqualsOrExceedCount(int count) {
        Assertions.assertThat(productLandingPage.getCountOfDisplayedProducts()).isGreaterThanOrEqualTo(count);
        return this;
    }

    public AppHelper verifyDisplayedProductsEqualsOrLessThanCount(int count) {
        Assertions.assertThat(productLandingPage.getCountOfDisplayedProducts()).isLessThanOrEqualTo(count);
        return this;
    }

    public AppHelper verifyProductItemCountIsEqualTo(int count) {
        Assertions.assertThat(productLandingPage.getProductItemCount()).isEqualTo(count);
        return this;
    }

    public AppHelper verifyProductItemCountIsLessThan(int count) {
        Assertions.assertThat(productLandingPage.getProductItemCount()).isLessThanOrEqualTo(count);
        return this;
    }

    public AppHelper applyProductFilterAndVerify(FilterBy... filters) {
        productLandingPage.openFilter();

        Arrays.asList(filters).forEach(filter -> {
            final String filterValue = productLandingPage.filterItem(filter);
            productLandingPage.openFilter();
            Assertions.assertThat(productLandingPage.isFilterApplied(filterValue)).isTrue();
        });
        return this;
    }

    public AppHelper applyFilterAndSelectMultipleOptionsAndVerify(FilterBy filterBy) {
        productLandingPage.openFilter();
        final List filterValues = productLandingPage.filterItemWithMultipleOptions(filterBy);
        productLandingPage.openFilter();
        Assertions.assertThat(productLandingPage.isFilterApplied(filterValues.toString().replace("[", "").replace("]", ""))).isTrue();
        return this;
    }

    public AppHelper closeFilter() {
        productLandingPage.closeFilter();
        return this;
    }

    public AppHelper clearSingleFilter(FilterBy filter) {
        productLandingPage.clearSingleFilter(filter);
        return this;
    }

    public AppHelper clearAllFilters() {
        productLandingPage.clearAllFilters();
        return this;
    }

    public AppHelper verifyAllFilteredAreCleared() {
        productLandingPage.openFilter();
        Assertions.assertThat(productLandingPage.isAllFiltersCleared()).isTrue();
        return this;
    }

    public AppHelper applyProductSorting(SortBy sortBy) {
        productLandingPage.sortBy(sortBy);
        return this;
    }

    public AppHelper clearProductSorting() {
        productLandingPage.clearSorting();
        return this;
    }

    public AppHelper searchForAProductFromShop(String searchKeyword) {
        shopPage.search(searchKeyword, true);
        return this;
    }

    public AppHelper searchForAProductFromShop(String searchKeyword, boolean selectSearchResult) {
        shopPage.search(searchKeyword, selectSearchResult);
        return this;
    }

    public AppHelper searchHistory(String searchKeyword) {
        searchPage.searchHistory(searchKeyword);
        return this;
    }

    public AppHelper cancelSearch() {
        searchPage.cancelSearch();
        return this;
    }

    public AppHelper openHelpAndSupport() {
        myRiverIslandPage.openHelpAndSupport();
        return this;
    }

    public AppHelper registerNewCustomerViaApi(TcplApiCredentials credentials) {
        customer = Customer.Builder.createDefaultBuilder().build();

        final AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest(customer.getEmailAddress(), customer.getPassword(), customer.getFirstName(), customer.getLastName());

        final boolean isAccountCreated = AccountUtils.registerAccount(credentials, accountRegistrationRequest);

        if (!isAccountCreated) {
            throw new RuntimeException("Unable to create customer account using the TCPL API!");
        }
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Registered customer (via API): " + customer.toString());
        return this;
    }

    public AppHelper waitForProductsToLoad() {
        productLandingPage.waitForLandingPageProducts();
        return this;
    }

    public AppHelper openMenu() {
        menuPage.openMenu();
        return this;
    }

    public AppHelper openMyRiTab() {
        tabBarPage.openMyRi();
        return this;
    }

    public AppHelper openShopTab() {
    	if (tabBarPage.isAndroid()) {
    		menuPage.openMenu();
    		myRiverIslandPage.openShop();
    	}
    	else {
    		tabBarPage.openShop();
    	}
        return this;
    }

    public AppHelper openHomeTab() {
        tabBarPage.openHome();
        return this;
    }

    public AppHelper openHomeScreen() {
        return openMenu().openHomeTab();
    }

    public AppHelper openWishlistTab() {
        tabBarPage.openWishlist();
        return this;
    }

    public AppHelper openShoppingBagTab() {
        tabBarPage.openShoppingBag();
        return this;
    }
    
    public AppHelper openSignIn() {
        myRiverIslandPage.openSignIn();
        return this;
    }

    public AppHelper openScanInStore() {
        myRiverIslandPage.openScanInStore();
        return this;
    }

    public AppHelper openScanInStoreFromShop() {
        shopPage.openScanInStore();
        return this;
    }

    public AppHelper openNearestStore() {
        scanInStorePage.openNearestStore();
        return this;
    }
    
    public AppHelper homepageWelcome() {
    	homePage.pauseForWelcomeBanner();
    	return this;
    }

    public AppHelper scanBarcodeManually(String barcode) {
        scanInStorePage.scanBarcodeManually(barcode);
        return this;
    }

    public AppHelper viewTermsAndConditions() {
        myRiverIslandPage.viewTermsAndConditions();
        return this;
    }

    public AppHelper verifyTermsAndConditionsAreDisplayed() {
        assertThat(myRiverIslandPage.isTermsAndConditionsDisplayed()).isTrue();
        return this;
    }

    public AppHelper openSettings() {
        myRiverIslandPage.openSettings();
        return this;
    }

    public AppHelper verifySettings(String... settings) {
        assertThat(myRiverIslandPage.isSettingsValid(settings)).isTrue();
        return this;
    }

    public AppHelper verifyDeliveryAndReturns() {
        productDetailsPage.openDeliveryAndReturns();
        Assertions.assertThat(productDetailsPage.getDeliveryAndReturnInfo()).isNotBlank();
        return this;
    }

    public AppHelper forgotPassword(String email) {
        myRiverIslandPage.forgotPassword(email);
        return this;
    }

    public AppHelper movePreviousSessionBagItemToWishlist(Product product) {
        myBagPage.moveBagItemToWishlist(product.getDescription());
        return this;
    }
    
    public AppHelper moveAllPreviousSessionBagItemsToWishlist() {
        myBagPage.moveAllBagItemsToWishlist();
        return this;
    }
    
    // Similar Items
    
    public AppHelper verifyHasSimilarItems() {
        Boolean hasItems = productDetailsPage.hasSimilarItems();

        if (hasItems == null) {
            throw new SkipException("There are no similar items for this product! Unable to continue verification.");
        }

        Assertions.assertThat(hasItems).isTrue();
        return this;
    }
    
	public AppHelper verifyProgressBar(boolean isDisplayed) {
		myBagPage.verifyProgressBar(isDisplayed);		
		return this;
	}
}

