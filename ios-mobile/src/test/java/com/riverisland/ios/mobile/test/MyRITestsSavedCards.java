package com.riverisland.ios.mobile.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.fixtures.CheckoutFixture;
import com.riverisland.app.automation.fixtures.SavedCardsFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;

public class MyRITestsSavedCards extends IosMobileTest {
	
	private SavedCardsFixture savedCardsFixture = new SavedCardsFixture();
	private CheckoutFixture checkoutFixture = new CheckoutFixture();
	
    @BeforeTest(alwaysRun = true)
    public void beforeTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
    }

    @AfterTest(alwaysRun = true)
    public void afterTests() {
        AppSession.tcplProducts = null;
    }
	
	@BeforeMethod
	public void beforeScenarios() {
        appHelper.openMyRiTab();

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper.checkAndSignOut();
        }	
	}

	@Test(description = "User not signed in - Saved cards not accessible")
	public void MyRiSavedCardsScenario_01() {
        appHelper
        	.openMenu();
        
        savedCardsFixture.verifySavedCards(false);     
	}
	
	@Test(description = "User signed in - Saved Cards accessible")
	public void MyRiSavedCardsScenario_02() {
        appHelper
    	    .registerNewCustomerViaApi(tcplApiCredentials)
    	    .openMenu()
    	    .openSignIn()
    	    .signIn(customer.getEmailAddress(), customer.getPassword())
    	    .openMenu();
    
        savedCardsFixture
        	.verifySavedCards(true)
        	.openSavedCards();
   	}
	
	@Test(dataProvider = "region-and-card", description = "User signed in - Saved cards accessible - Card saved during checkout and removed")
	public void MyRiSavedCardsScenario_03(Region region, CardType cardType) {
        appHelper
    	    .registerNewCustomerViaApi(tcplApiCredentials)
    	    .openMenu()
    	    .openSignIn()
    	    .signIn(customer.getEmailAddress(), customer.getPassword())
    	    .openMenu()
            .changeCurrency(region)
            .addProductToBag(Category.WOMEN)
            .openShoppingBagTab()
            .proceedToCheckout()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(region)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
            .selectPaymentMethod(PaymentMethod.CARD)
            .processPayment(PaymentMethod.CARD, cardType)
            .payWithCard();
        checkoutFixture
            .verifyOrderSummary();
        appHelper
            .completePayment()
            .verifyOrderCompletion()
            .continueShopping()
            .acceptAlert("Not Now")
            .openMyRiTab();
    
        savedCardsFixture
        	.verifySavedCards(true)
        	.openSavedCards()
        	.verifyCountOfSavedCards(1)
        	.removeCard(cardType)
        	.verifyCountOfSavedCards(0);
 	}
}
