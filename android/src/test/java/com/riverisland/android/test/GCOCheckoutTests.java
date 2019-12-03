package com.riverisland.android.test;

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
import com.riverisland.app.automation.fixtures.GiftCardFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;

/**
 * Created by Simon Johnson on 23/10/2018
 */
@SuppressWarnings("groupsTestNG")
public class GCOCheckoutTests extends AndroidTest {
	
	private GiftCardFixture giftCardFixture = new GiftCardFixture();
	private CheckoutFixture checkoutFixture = new CheckoutFixture();

    @BeforeTest(alwaysRun = true)
    public void beforeCheckoutTests() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
    }

    @AfterTest(alwaysRun = true)
    public void afterCheckoutTests() {
        AppSession.tcplProducts = null;
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeCheckoutScenario() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }
    }
    
    @Test(description = "Checkout as a new customer - add a gift card")
    public void gcoCheckoutScenario_01() {
        appHelper
        	.openMenu()
            .changeCurrency(Region.GB)
            .addProductAndViewBag(Category.WOMEN)
            .proceedToCheckout()
            .signUp()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(Region.GB)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();
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
    public void gcoCheckoutScenario_02() {
        appHelper
        	.openMenu()
            .changeCurrency(Region.GB)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductAndViewBag(Category.WOMEN)
            .proceedToCheckout()
            .signUp()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(Region.GB)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();
        giftCardFixture
			.verifyAddGiftCardIsAvailable(true);
        addGiftCard("UK(GBP)-10");
        addGiftCard("UK(GBP)-20");
        addGiftCard("UK(GBP)-30");
        addGiftCard("UK(GBP)-40");
        addGiftCard("UK(GBP)-50");
        
        checkoutFixture.minimiseSummary();
        giftCardFixture
            .verifyAddGiftCardIsAvailable(false);

        giftCardFixture
        	.removeGiftCard();
        appHelper
        	.goBack();
        checkoutFixture.minimiseSummary();
        giftCardFixture
        	.verifyAddGiftCardIsAvailable(true);
        
        giftCardFixture
        	.addGiftCard("UK(GBP)-60")
        	.verifyAddGiftCardIsAvailable(false);       
    }
    
    private void addGiftCard(String card) {
    	stickySummaryPage.minimise();
    	giftCardFixture.addGiftCard(card);
    }

    @Test(description = "Checkout as a new customer - try to pay with a gift card but not UK or ROI shippping")
    public void gcoCheckoutScenario_03() {
        appHelper
            .openMenu()
            .changeCurrency(Region.GB)
            .addProductAndViewBag(Category.WOMEN)
            .proceedToCheckout()
            .signUp()
            .selectDeliveryType(DeliveryType.HOME_DELIVERY)
            .addDeliveryHomeAddressAndSelect(Region.US)
            .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();
        giftCardFixture
        	.verifyAddGiftCardIsAvailable(false);
    }
    
    @Test(description = "Checkout as a new customer - Pay with gift card and card")
    public void gcoCheckoutScenario_04() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();         
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
    @Test(description = "Checkout as a new customer - Pay with gift card and paypal")
    public void gcoCheckoutScenario_05() {
        appHelper
        	.openMenu()
        	.changeCurrency(Region.GB)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
        	.addProductAndViewBag(Category.MEN)
        	.proceedToCheckout()
        	.signUp()
        	.selectDeliveryType(DeliveryType.HOME_DELIVERY)
        	.lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
        	.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();
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
    public void gcoCheckoutScenario_06() {
        appHelper
        	.openMenu()
        	.changeCurrency(Region.GB)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
            .addProductToBag(Category.WOMEN)
            .goBack(4)
        	.addProductAndViewBag(Category.MEN)
        	.proceedToCheckout()
        	.signUp()
        	.selectDeliveryType(DeliveryType.HOME_DELIVERY)
        	.lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
        	.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();
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
    public void gcoCheckoutScenario_07() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductToBag(Category.WOMEN)
                .goBack(4)
                .addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY);
        checkoutFixture.minimiseSummary();       
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
}