package com.riverisland.android.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.fixtures.JustForYouFixture;

/**
 * Created by Simon Johnson on 12/11/2018
 */
@SuppressWarnings("groupsTestNG")
public class JustForYouTests extends AndroidTest {

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
                    .verifyWishlistSize(0)
                    .goBack();
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTests() {
    }

    @Test(description = "Verify initial JFY is empty and select 5 categories")
    public void justForYouScenario_01() {

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses", "Tops", "Shoes & Boots")
        	.chooseCategory(Category.MEN, "Shirts")
        	.chooseCategory(Category.BOYS, "0-2 years")
        	.failToChooseCategory(Category.GIRLS, "3-5 years")
        	.startShopping();
        
        justForYouFixture
        	.verifyDisplayModule(true)
        	.selectPersonalise()
        	.clearAll()
        	.startShopping()
        	.verifyDisplayModule(false);
    }
    @Test(description = "Verify initial JFY is empty and select 5 categories and then close")
    public void justForYouScenario_02() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses", "Tops", "Shoes & Boots")
        	.chooseCategory(Category.MEN, "Shirts")
        	.chooseCategory(Category.BOYS, "0-2 years")
        	.close()
        	.verifyDisplayModule(false);    
    }   
    @Test(description = "Verify JFY products can be added and removed to/from the wishlist ")
    public void justForYouScenario_03() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
        		.registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());
        
        justForYouFixture
        	.verifyDisplayModule(false)
        	.selectPersonaliseFeed()
        	.chooseCategory(Category.WOMEN, "Dresses")
        	.startShopping()
        	.addProductToWishlist();
        
        appHelper
        	.openWishlistTab()
        	.verifyWishlistSize(1)
        	.goBack();
        
        justForYouFixture
        	.removeProductFromWishList();
        
        appHelper
        	.openWishlistTab()
        	.verifyWishlistSize(0);
    }
}