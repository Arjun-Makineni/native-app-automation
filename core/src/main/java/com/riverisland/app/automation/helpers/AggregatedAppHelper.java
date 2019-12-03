package com.riverisland.app.automation.helpers;

import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.fixtures.CheckoutFixture;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
public class AggregatedAppHelper extends AppHelper {

	private CheckoutFixture checkoutFixture = new CheckoutFixture();
	
    public AggregatedAppHelper() {
    }

    public AggregatedAppHelper placeAndroidOrderSignedIn(Region region, CardType cardType) {
        addProductToBag(Category.WOMEN)
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
        this.completePayment()
            .verifyOrderCompletion()
            .continueShopping();
        return this;
    }

    public AggregatedAppHelper placeIOSMobileOrderSignedIn(Region region, CardType cardType) {
        addProductAndViewBag(Category.MEN)
                .proceedToCheckout()
                .selectDeliveryType(DeliveryType.HOME_DELIVERY)
                .addDeliveryHomeAddressAndSelect(region)
                .selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
                .selectPaymentMethod(PaymentMethod.CARD)
                .processPayment(PaymentMethod.CARD, cardType)
                .payWithCard();
        checkoutFixture
                .verifyOrderSummary();
        this.completePayment()
            .verifyOrderCompletion()
            .continueShopping();
        return this;
    }

    public AggregatedAppHelper placeIOSTabletOrderSignedIn(Region region, CardType cardType) {
        addProductToBag(Category.MEN)
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
        this.completePayment()
            .verifyOrderCompletion()
            .continueShopping();
        return this;
    }
}
