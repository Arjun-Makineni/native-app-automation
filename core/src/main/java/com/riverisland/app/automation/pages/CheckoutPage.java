package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.enums.*;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface CheckoutPage<T> {
    T selectDeliveryType(DeliveryType deliveryType);

    T openAddNewDeliveryAddress();

    T addDeliveryHomeAddress(Address address);

    T addBillingAddress(Address address);

    Boolean alreadyHasAddress(Address address);

    T selectAddress(Address address);

    T selectAddressByPostcode(String postCode);

    T selectState(String state);

    T selectDeliveryOption(DeliveryOption deliveryOption);

    T selectNominatedDay();

    T selectPreciseDayAndTime();

    T continueToPayment();

    T searchStore(String store);

    T selectStore(String store);

    T changeDeliveryCountry(Region region);

    T populateTelephoneForDeliveryToStore();

    T searchPickupStoreAndSelect(String address);

    String getPickupStoreAddress();

    T confirmCollectionDetails();

    T selectPaymentMethod(PaymentMethod paymentMethod);

    T populateCardDetails(CardType cardType, Boolean saveCard);

    T saveCard();

    T payWithCard();

    T populateCCVForSavedCard(CardType cardType);

    T payWithIdeal();

    T processIdealPayTransaction();

    T payWithGiroPay();

    T processGiroPayTransaction();

    T payWithPayPal();

    T processPayPalTransaction();

    String getSummaryCardType();

    String getSummaryDeliveryAddress();

    String getSummaryDeliveryOption(DeliveryOption deliveryOption);

    String getSummarySubTotal();

    String getSummaryShippingTotal();

    String getSummaryGrandTotal();

    T completePayment();

    String getOrderNumber();

    String getOrderCompleteAddress();

    T continueShopping();

    T complete3dSecure();
}
