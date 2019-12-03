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
 * Created by Prashant Ramcharan on 30/05/2017
 */
@SuppressWarnings("groupsTestNG")
public class CheckoutTests extends IosMobileTest {
	
	GiftCardFixture giftCardFixture = new GiftCardFixture();
	CheckoutFixture checkoutFixture = new CheckoutFixture();
	
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
        appHelper.openMyRiTab();

        appHelper.checkAndSignOut();
    }

    @Test(dataProvider = "region-and-card", description = "Checkout as a new customer - Home Delivery")
    public void checkoutScenario_01(Region region, CardType cardType) {
        appHelper
                .changeCurrency(region)
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signUp()
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
                .continueShopping();
    }

    @Test(dataProvider = "region-and-third-party-payment", description = "Checkout as an existing customer - 3rd Party Payment", groups = "smoke")
    public void checkoutScenario_02(Region region, PaymentMethod paymentMethod) {
        appHelper
                .changeCurrency(region)
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signIn(existingRiverIslandUser.getEmail(), existingRiverIslandUser.getPassword())
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .checkOrAddNewDeliveryHomeAddress(region)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .processPayment(paymentMethod, null)
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Postcode Lookup", groups = {"smoke","regression"})
    public void checkoutScenario_03() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.MEN)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
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

    @Test(description = "Checkout as a new customer - Collect from River Island Store", groups = "smoke")
    public void checkoutScenario_04() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.MEN)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.COLLECT_FROM_STORE)
                .searchStore("W2 5DR")
                .selectStore("WHITE CITY WESTFIELD")
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .addBillingAddressAndSelect(Region.GB)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard();
        checkoutFixture
                .verifyOrderSummaryForStoreCollection("WHITE CITY WESTFIELD");
        appHelper
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Deliver to UK Store", groups = "smoke")
    public void checkoutScenario_05() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.BOYS)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.DELIVER_TO_STORE)
                .searchLocalPickupStoreAndConfirm("W2 5RA")
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .addBillingAddressAndSelect(Region.GB)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard()
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Deliver to EU Store")
    public void checkoutScenario_06() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.DELIVER_TO_STORE)
                .searchPickupStoreAndConfirm(Region.DE, "Berlin")
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .addBillingAddressAndSelect(Region.GB)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard()
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as an existing customer - Signed in", groups = "smoke")
    public void checkoutScenario_07() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToBag(Category.GIRLS)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY)
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

    @Test(description = "Checkout as an existing customer - Sign in during checkout")
    public void checkoutScenario_08() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .addProductToBag(Category.WOMEN)
                .addProductToBag(Category.MEN)
                .openShoppingBagTab()
                .proceedToCheckout()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY)
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

    @Test(description = "Checkout as a new customer - Nominated Day Home Delivery")
    public void checkoutScenario_09() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.NOMINATED_DAY_DELIVERY)
                .selectNominatedDay()
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

    @Test(description = "Checkout as a new customer - Precise Day Home Delivery")
    public void checkoutScenario_10() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addPreciseDayDeliveryHomeAddressAndSelect()
                .selectDeliveryOption(DeliveryOption.PRECISE_DAY_DELIVERY)
                .selectPreciseDayAndTime()
                .selectPaymentMethod(PaymentMethod.CARD)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard()
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Add a new address and pay with saved card")
    public void checkoutScenario_11() {
        changeToDefaultCurrencyIfRunningLocally();

        final Region region = Region.GB;
        final CardType savedCardType = CardType.VISA;

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());

        aggregatedAppHelper.placeIOSMobileOrderSignedIn(region, savedCardType);

        appHelper
        		.acceptAlert("Not Now")
                .addProductToBag(Category.WOMEN)
                .openShoppingBagTab()
                .proceedToCheckout()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addNewDeliveryHomeAddress(Region.EU) // this will verify we can add a new address
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .payWithSavedCard(savedCardType)
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }
    @Test(description = "Checkout as an returning customer - Signed in", groups = "smoke")
    public void checkoutScenario_12() {
        changeToDefaultCurrencyIfRunningLocally();

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToBag(Category.GIRLS)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
                .proceedToCheckout()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(Region.GB)
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard();
        checkoutFixture
                .verifyOrderSummary();
        appHelper
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping()
        		.acceptAlert("Not Now")
                .addProductToBag(Category.GIRLS)
                .openShoppingBagTab()
        		.verifyProgressBar(false);
    }
    private void changeToDefaultCurrencyIfRunningLocally() {
        // when running locally, the currency could be leaked from a previous test and therefore become stale which means the default is not Region.GB
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper.changeCurrency(Region.GB);
        }
    }
}
