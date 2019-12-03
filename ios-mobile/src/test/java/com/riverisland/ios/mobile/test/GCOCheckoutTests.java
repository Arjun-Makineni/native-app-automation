package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.fixtures.CheckoutFixture;
import com.riverisland.app.automation.fixtures.GiftCardFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Created by Simon Johnson on 18/10/2018
 */
@SuppressWarnings("groupsTestNG")
public class GCOCheckoutTests extends IosMobileTest {
	
	GiftCardFixture giftCardFixture = new GiftCardFixture();
	CheckoutFixture checkoutFixture = new CheckoutFixture();

    @BeforeTest(alwaysRun = true)
    public void beforeCheckoutTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
        if (GlobalConfig.isAppRunningLocally()) {
            performLocalBagCleanup();
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterCheckoutTests() {
        AppSession.tcplProducts = null;
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeCheckoutScenario() {
        appHelper.openMyRiTab();

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper.checkAndSignOut();
        }
    }
    
    @Test(description = "Checkout as a new customer - Pay with giftcard")
    public void checkoutScenario_01() {
        appHelper
                .changeCurrency(Region.GB)
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        
        giftCardFixture
			.verifyAddGiftCardIsAvailable(true)
			.addGiftCard("UK(GBP)-60")
			.completeGiftCardOrder();
        
        checkoutFixture
                .verifyOrderSummary();
        appHelper
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }
    
    @Test(description = "Checkout as a new customer - add multiple gift cards to exceed limit of 5")
    public void checkoutScenario_02() {
        appHelper
            .changeCurrency(Region.GB)
            .addProductToBag(Category.MEN)
            .addProductToBag(Category.MEN)
            .addProductToBag(Category.MEN)
            .addProductToBag(Category.MEN)
            .addProductToBag(Category.MEN)
            .addProductAndViewBag(Category.MEN)
            .proceedToCheckout()
            .signUp()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(Region.GB)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        giftCardFixture
			.verifyAddGiftCardIsAvailable(true)
            .addGiftCard("UK(GBP)-10")
            .addGiftCard("UK(GBP)-20")
            .addGiftCard("UK(GBP)-30")
            .addGiftCard("UK(GBP)-40")
            .addGiftCard("UK(GBP)-50")
            .verifyAddGiftCardIsAvailable(false);

        giftCardFixture
        	.removeGiftCard();
        appHelper
        	.goBack();
        giftCardFixture
        	.verifyAddGiftCardIsAvailable(true);
        
        giftCardFixture
        	.addGiftCard("UK(GBP)-60")
        	.verifyAddGiftCardIsAvailable(false);
        
    }
    @Test(description = "Checkout as a new customer - try to pay with a gift card but not UK or ROI shippping")
    public void checkoutScenario_03() {
        appHelper
            .changeCurrency(Region.GB)
            .addProductAndViewBag(Category.WOMEN)
            .proceedToCheckout()
            .signUp()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(Region.US)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        giftCardFixture
        	.verifyAddGiftCardIsAvailable(false);
    }
    
    @Test(description = "Checkout as a new customer - Pay with gift card and card")
    public void checkoutScenario_04() {
        appHelper
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
                
        giftCardFixture
    			.verifyAddGiftCardIsAvailable(true)
                .addGiftCard("UK(GBP)-10");
        appHelper
                .selectPaymentMethod(PaymentMethod.CARD)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard();
        checkoutFixture
        		.verifyOrderSummary();
        appHelper
                .verifyOrderSummary()
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }
    @Test(description = "Checkout as a new customer - Pay with gift card and paypal")
    public void checkoutScenario_05() {
        appHelper
        	.changeCurrency(Region.GB)
            .addProductToBag(Category.WOMEN)
            .addProductToBag(Category.WOMEN)
        	.addProductAndViewBag(Category.MEN)
        	.proceedToCheckout()
        	.signUp()
        	.selectDeliveryType(DeliveryType.HOME_DELIVERY)
        	.lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
        	.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        
        giftCardFixture
			.verifyAddGiftCardIsAvailable(true)
			.addGiftCard("UK(GBP)-10");
        appHelper
        	.selectPaymentMethod(PaymentMethod.CARD)
        	.processPayment(PaymentMethod.CARD, CardType.VISA)
        	.payWithCard();
        checkoutFixture
        	.verifyOrderSummary();
        appHelper
        	.completePayment()
        	.verifyOrderCompletion()
        	.continueShopping();
    }
    @Test(description = "Checkout as a new customer - Pay with ROI gift card and paypal")
    public void checkoutScenario_06() {
        appHelper
        	.changeCurrency(Region.GB)
            .addProductToBag(Category.WOMEN)
            .addProductToBag(Category.WOMEN)
        	.addProductAndViewBag(Category.MEN)
        	.proceedToCheckout()
        	.signUp()
        	.selectDeliveryType(DeliveryType.HOME_DELIVERY)
        	.lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
        	.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        
        giftCardFixture
			.verifyAddGiftCardIsAvailable(true)
			.addGiftCard("ROI(EUR)-30");
        appHelper
        	.selectPaymentMethod(PaymentMethod.CARD)
        	.processPayment(PaymentMethod.CARD, CardType.VISA)
        	.payWithCard();
        checkoutFixture
        	.verifyOrderSummary();
        appHelper
        	.completePayment()
        	.verifyOrderCompletion()
        	.continueShopping();
    }
    @Test(description = "Checkout as a new customer - Pay with ROI gift card and card")
    public void checkoutScenario_07() {
        appHelper
                .changeCurrency(Region.GB)
                .addProductToBag(Category.WOMEN)
                .addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
                
        giftCardFixture
    			.verifyAddGiftCardIsAvailable(true)
                .addGiftCard("ROI(EUR)-30");
        appHelper
                .selectPaymentMethod(PaymentMethod.CARD)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard();
        checkoutFixture
            	.verifyOrderSummary();
        appHelper
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }
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
