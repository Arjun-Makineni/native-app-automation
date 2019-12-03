package com.riverisland.ios.mobile.test;

import java.util.Random;
import java.util.function.Function;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.fixtures.JustForYouFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

/**
 * Created by Simon Johnson on 13/11/2018
 */
@SuppressWarnings("groupsTestNG")
public class JustForYouTests extends IosMobileTest {

	private JustForYouFixture justForYouFixture = new JustForYouFixture();

    @BeforeTest(alwaysRun = true)
    public void beforeTests() {
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
    }

    @Test(description = "Verify initial JFY is empty and select 5 categories")
    public void justForYouScenario_01() {

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
                .openHomeTab();
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses", "Tops", "Skirts")
        	.chooseCategory(Category.MEN, "Shirts")
        	.chooseCategory(Category.BOYS, "Shorts")
        	.failToChooseCategory(Category.GIRLS, "Dresses")
        	.startShopping();

        appHelper.openHomeTab();
        
        justForYouFixture
        	.verifyDisplayModule(true);
        
        appHelper.openHomeTab();
        
        justForYouFixture
        	.selectPersonalise()
        	.clearAll()
        	.startShopping()
        	.verifyDisplayModule(false);
    }
    @Test(description = "Verify initial JFY is empty and select 5 categories and then close")
    public void justForYouScenario_02() {

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
                .openHomeTab();
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses", "Tops", "Skirts")
        	.chooseCategory(Category.MEN, "Shirts")
        	.chooseCategory(Category.BOYS, "Shorts")
        	.close()
        	.verifyDisplayModule(false);    
    }    
    @Test(description = "Verify JFY products can be added and removed to/from the wishlist ")
    public void justForYouScenario_03() {
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
                .openHomeTab();
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses")
        	.startShopping()
        	.addProductToWishlist();
        
        appHelper
        	.openWishlistTab()
        	.verifyWishlistSize(1)
        	.openHomeTab();
        
        justForYouFixture
        	.removeProductFromWishList();
        
        appHelper
        	.openWishlistTab()
        	.verifyWishlistSize(0);      
    }
}