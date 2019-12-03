package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.fixtures.RecentlyViewedFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.function.Function;

/**
 * Created by Prashant Ramcharan on 15/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class WishlistTests extends IosMobileTest {

	private RecentlyViewedFixture recentlyViewedFixture = new RecentlyViewedFixture();

    @BeforeTest(alwaysRun = true)
    public void beforeWishlistTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeWishlistScenario() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openWishlistTab()
                    .clearWishlist()
                    .verifyWishlistSize(0);
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterWishlistTests() {
        AppSession.tcplProducts = null;
    }

    private Function<Category, Product> productLookup = (category) -> {
        if (AppSession.tcplProducts != null) {
            return AppSession.tcplProducts.get(category).get(new Random().nextInt(AppSession.tcplProducts.get(category).size()));
        }
        return null;
    };

    @Test(dataProvider = "categories", description = "Add item to wishlist", groups = "smoke")
    public void wishlistScenario_01(Category category, Object ignored) {
        final Product product = productLookup.apply(category);

        appHelper
                .addProductToWishlist(product, category)
                .openWishlistTab()
                .verifyWishlistSize(1)
                .verifyProductIsInWishlist(product);
    }

    @Test(description = "Add all wishlist items to the bag")
    public void wishlistScenario_02() {
        final Product product1 = productLookup.apply(Category.WOMEN);
        final Product product2 = productLookup.apply(Category.MEN);

        if (GlobalConfig.isAppRunningLocally()) {
            performLocalBagCleanup();
        }

        appHelper
                .addProductToWishlist(product1, Category.WOMEN)
                .addProductToWishlist(product2, Category.MEN)
                .openWishlistTab()
                .verifyWishlistSize(2)
                .addWishlistItemsToBag()
                .verifyWishlistSize(0)
                .openShoppingBagTab()
                .verifyTotalBagQty(2);
    }

    @Test(description = "Remove item from the wishlist", groups = "smoke")
    public void wishlistScenario_03() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .addProductToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(1)
                .removeWishlistItem(product)
                .verifyWishlistSize(0);
    }

    @Test(description = "Clear wishlist", groups = "smoke")
    public void wishlistScenario_04() {
        final Product product1 = productLookup.apply(Category.WOMEN);
        final Product product2 = productLookup.apply(Category.MEN);

        appHelper
                .addProductToWishlist(product1, Category.WOMEN)
                .addProductToWishlist(product2, Category.MEN)
                .openWishlistTab()
                .verifyWishlistSize(2)
                .clearWishlist()
                .verifyWishlistSize(0);
    }

    @Test(description = "Ensure that a size can be selected from a wishlist item")
    public void wishlistScenario_05() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .addProductToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .changeWishlistItemSize()
                .verifyWishlistSize(1);
    }

    @Test(description = "Verify web wishlist items are correctly synced to the app")
    public void wishlistScenario_06() {
        webHelper
                .registerAndLoginWebCustomer(tcplApiCredentials)
                .addInStockWebWishlistItems(Category.WOMEN, 3);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .checkAndSignOut();
        }

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signInNewlyRegisteredCustomer()
                .openHomeTab()
                .openWishlistTab()
                .verifyWishlistSize(3);
    }

    @Test(description = "Verify that only in stock web wishlist items are synced to the app")
    public void wishlistScenario_07() {
        webHelper
                .registerAndLoginWebCustomer(tcplApiCredentials)
                .addInStockWebWishlistItems(Category.WOMEN, 2)
                .addOutOfStockWebWishlistItems(Category.WOMEN, 1);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .checkAndSignOut();
        }

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signInNewlyRegisteredCustomer()
                .openHomeTab()
                .openWishlistTab()
                .verifyWishlistSize(3);
    }

    @Test(description = "Verify that the app wishlist is correctly synced to the web")
    public void wishlistScenario_08() {
        final Product product = productLookup.apply(Category.WOMEN);

        webHelper.registerAndLoginWebCustomer(tcplApiCredentials);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .checkAndSignOut();
        }

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signInNewlyRegisteredCustomer()
                .addProductToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(1);

        webHelper.verifyWebWishlistItem(product.getDescription());

        appHelper.removeWishlistItem(product);

        webHelper.verifyWebWishlistIsEmpty();
    }

    // this just ensures the bag is fully cleared to prevent other features from failing
    private void performLocalBagCleanup() {
        appHelper
                .openMyRiTab()
                .checkAndSignOut();

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .signOut();
    }
}