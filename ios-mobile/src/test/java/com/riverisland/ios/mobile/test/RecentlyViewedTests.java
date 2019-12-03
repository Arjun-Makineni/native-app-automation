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
 * Created by Simon Johnson on 13/11/2018
 */
@SuppressWarnings("groupsTestNG")
public class RecentlyViewedTests extends IosMobileTest {

	private RecentlyViewedFixture recentlyViewedFixture = new RecentlyViewedFixture();

    @BeforeTest(alwaysRun = true)
    public void beforeWishlistTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeScenario() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openWishlistTab()
                    .clearWishlist()
                    .verifyWishlistSize(0);
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTests() {
        AppSession.tcplProducts = null;
    }

    private Function<Category, Product> productLookup = (category) -> {
        if (AppSession.tcplProducts != null) {
            return AppSession.tcplProducts.get(category).get(new Random().nextInt(AppSession.tcplProducts.get(category).size()));
        }
        return null;
    };

    @Test(description = "Verify that recently viewed product is added and removed from wishlist")
    public void recentlyViewedScenario_01() {
        final Product product = productLookup.apply(Category.WOMEN);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(1)
                .openHomeTab();
        
        recentlyViewedFixture
        		.verifyRecentlyViewed(product)
        		.toggleProductInWishList(product.getDescription());
        appHelper.openWishlistTab()
        		 .verifyWishlistSize(0);
    }

    @Test(description = "Verify that recently viewed multiple size product are added and removed from wishlist")
    public void recentlyViewedScenario_02() {
        final Product product = productLookup.apply(Category.WOMEN);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToWishlist(Category.MEN)
                .goBack()
                .addMultipleSizeProductsToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(3)
                .openHomeTab();
        
        recentlyViewedFixture
        		.verifyRecentlyViewed(product)
        		.toggleProductInWishList(product.getDescription());
        appHelper.openWishlistTab()
        		 .verifyWishlistSize(1);
    }
    @Test(description = "Verify that recently viewed See All")
    public void recentlyViewedScenario_03() {
        final Product product = productLookup.apply(Category.WOMEN);
    	
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }
        
        appHelper
			.registerNewCustomerViaApi(tcplApiCredentials)
			.openMyRiTab()
			.openSignIn()
			.signIn(customer.getEmailAddress(), customer.getPassword())
			.addProductToWishlist(Category.MEN)
			.goBack()
			.addProductToWishlist(Category.MEN)
			.goBack()
			.addMultipleSizeProductsToWishlist(product, Category.WOMEN)
			.openWishlistTab()
			.verifyWishlistSize(4)
			.openHomeTab();
        
        recentlyViewedFixture
        	.displayRecentlyViewed(3);
        recentlyViewedFixture.clearRecentlyViewed();
    }    
}