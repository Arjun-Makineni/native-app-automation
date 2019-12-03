package com.riverisland.app.automation.pojos;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.domain.ProductDetails;
import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.pages.*;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.framework.RiverIslandWebDriver;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
public class AppSession {
    protected static MenuPage menuPage;
    protected static TabBarPage tabBarPage;
    protected static AppPage appPage;
    protected static MyRiverIslandPage myRiverIslandPage;
    protected static ProductDetailsPage productDetailsPage;
    protected static ProductLandingPage productLandingPage;
    protected static MyBagPage myBagPage;
    protected static SearchPage searchPage;
    protected static CheckoutPage checkoutPage;
    protected static WishlistPage wishlistPage;
    protected static StoreLocatorPage storeLocatorPage;
    protected static RecentlyViewedPage recentlyViewedPage;
    protected static ScanInStorePage scanInStorePage;
    protected static ShopPage shopPage;
    protected static OnboardingPage onboardingPage;
    protected static HomePage homePage;
    protected static GiftCardPage giftCardPage;
    protected static SalesCountdownPage salesCountdownPage;
    protected static JustForYouPage justForYouPage;
    protected static StickySummaryPage stickySummaryPage;

    public static Region selectedRegion;
    public static Customer customer;
    public static Address address;
    public static Address storeAddress;
    public static PaymentMethod paymentMethod;
    public static CardType cardType;
    public static DeliveryType deliveryType;
    public static DeliveryOption deliveryOption;
    public static String productName;
    public static String productNumber;
    public static String orderNumber;
    public static Category category;
    public static ProductDetails productDetails;

    public static Map<Category, List<Product>> tcplProducts;

    public static Object singletonVar;

    public static RiverIslandWebDriver webDriver;
    public static String webUrl;

    protected String getRandomTcplProduct(Category category) {
        final Random random = new Random();
        final List<Product> categoryProducts = new ArrayList<>(tcplProducts.getOrDefault(category, new ArrayList<>()));

        if (categoryProducts.isEmpty()) {
            throw new RiverIslandTestError(String.format("No products from the '%s' category could be retrieved from the Tcpl API!", category.getName()));
        }

        return categoryProducts.get(random.nextInt(categoryProducts.size())).getId();
    }

    public void reset() {
        selectedRegion = null;
        customer = null;
        address = null;
        storeAddress = null;
        paymentMethod = null;
        cardType = null;
        deliveryType = null;
        deliveryOption = null;
        productName = null;
        productNumber = null;
        orderNumber = null;
        category = null;
        singletonVar = null;
        webDriver = null;
        webUrl = null;
        productDetails = null;
    }

    public void resetAll() {
        reset();
        tcplProducts = null;
    }
}
