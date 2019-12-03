package com.riverisland.android.test;

import java.util.Random;
import java.util.function.Function;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.fixtures.RecentlyViewedFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

/**
 * Created by Simon Johnson on 12/11/2018
 */
@SuppressWarnings("groupsTestNG")
public class RecentlyViewedTests extends AndroidTest {

	private RecentlyViewedFixture recentlyViewedFixture = new RecentlyViewedFixture();
    @BeforeTest(alwaysRun = true)
    public void beforeTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeScenario() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openWishlistTab()
                    .clearWishlist()
                    .verifyWishlistSize(0)
                    .goBack();
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
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(1)
                .goBack(5);
        
        recentlyViewedFixture
        		.verifyRecentlyViewed(product)
        		.toggleProductInWishList(product.getDescription());
        appHelper.openWishlistTab()
        		 .verifyWishlistSize(0)
        		 .goBack();
        recentlyViewedFixture.clearRecentlyViewed();
        
    }

    @Test(description = "Verify that recently viewed multiple size product are added and removed from wishlist")
    public void recentlyViewedScenario_02() {
        final Product product = productLookup.apply(Category.WOMEN);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToWishlist(Category.MEN)
                .goBack(4)
                .addMultipleSizeProductsToWishlist(product, Category.WOMEN)
                .openWishlistTab()
                .verifyWishlistSize(3)
                .goBack(5);
        
        recentlyViewedFixture
        		.verifyRecentlyViewed(product)
        		.toggleProductInWishList(product.getDescription());
        appHelper.openWishlistTab()
        		 .verifyWishlistSize(1)
        		 .goBack();
        recentlyViewedFixture.clearRecentlyViewed();
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
			.openMenu()
			.openSignIn()
			.signIn(customer.getEmailAddress(), customer.getPassword())
			.addProductToWishlist(Category.MEN)
			.goBack(4)
			.addProductToWishlist(Category.WOMEN)
			.goBack(4)
			.addMultipleSizeProductsToWishlist(product, Category.WOMEN)
			.openWishlistTab()
			.verifyWishlistSize(4)
			.goBack(5);
        
        recentlyViewedFixture
        	.displayRecentlyViewed(3);
        appHelper.goBack();
        recentlyViewedFixture.clearRecentlyViewed();
    }    
}