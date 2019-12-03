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
 * Created by Prashant Ramcharan on 30/05/2017
 */
@SuppressWarnings("groupsTestNG")
public class CheckoutTests extends AndroidTest {
	
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
    	appHelper
                .openMenu()
                .checkAndSignOut();
    }

    @Test(dataProvider = "region-and-card", description = "Checkout as a new customer - Home Delivery")
    public void checkoutScenario_01(Region region, CardType cardType) {
        appHelper
                .openMenu()
                .changeCurrency(region)
                .addProductAndViewBag(Category.WOMEN)
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
                .openMenu()
                .changeCurrency(region)
                .addProductAndViewBag(Category.WOMEN)
                .proceedToCheckout()
                .signIn(existingRiverIslandUser.getEmail(), existingRiverIslandUser.getPassword())
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .checkOrAddNewDeliveryHomeAddress(region)
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY);
        checkoutFixture.minimiseSummary();
        appHelper
                .processPayment(paymentMethod, null)
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Postcode Lookup", groups = {"smoke","regression"})
    public void checkoutScenario_03() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.MEN)
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
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.COLLECT_FROM_STORE)
                .searchStore("W2 5DR")
                .selectStore("WHITE CITY WESTFIELD")
                .selectDeliveryOption(DeliveryOption.EXPRESS_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .addBillingAddress(Region.GB)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard();
        checkoutFixture
                .verifyOrderSummaryForStoreCollection("Westfield Shopping Centre");
        appHelper
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as a new customer - Deliver to UK Store", groups = "smoke")
    public void checkoutScenario_05() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.BOYS)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.DELIVER_TO_STORE)
                .searchLocalPickupStoreAndConfirm("W2 5RA")
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .addBillingAddress(Region.GB)
                .processPayment(PaymentMethod.CARD, CardType.VISA)
                .payWithCard()
                .completePayment()
                .verifyOrderCompletion()
                .continueShopping();
    }

    @Test(description = "Checkout as an existing customer - Signed in", groups = "smoke")
    public void checkoutScenario_06() {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .changeCurrency(Region.GB)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToBag(Category.GIRLS)
                .returnToHomeFromPDP()
                .addProductAndViewBag(Category.BOYS)
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
    public void checkoutScenario_07() {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductToBag(Category.GIRLS)
                .returnToHomeFromPDP()
                .addProductToBag(Category.BOYS)
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
    public void checkoutScenario_08() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.WOMEN)
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
    public void checkoutScenario_09() {
        appHelper
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductAndViewBag(Category.WOMEN)
                .proceedToCheckout()
                .signUp()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addPreciseDayDeliveryHomeAddressAndSelect()
                .selectDeliveryOption(DeliveryOption.PRECISE_DAY_DELIVERY)
                .selectPreciseDayAndTime()
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

    @Test(description = "Checkout as a new customer - Add a new address and pay with saved card")
    public void checkoutScenario_10() {
        final Region region = Region.GB;
        final CardType savedCardType = CardType.VISA;

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());

        aggregatedAppHelper.placeAndroidOrderSignedIn(region, savedCardType);

        appHelper.goBack(2)
                .addProductAndViewBag(Category.WOMEN)
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
    @Test(description = "Checkout as an returning customer - Sign in during checkout")
    public void checkoutScenario_11() {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .changeCurrency(Region.GB)
                .addProductToBag(Category.GIRLS)
                .returnToHomeFromPDP()
                .addProductToBag(Category.BOYS)
                .openShoppingBagTab()
        		.verifyProgressBar(true)
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
                .continueShopping()
                .addProductToBag(Category.GIRLS)
                .openShoppingBagTab()
                .verifyProgressBar(false);
    }
}
