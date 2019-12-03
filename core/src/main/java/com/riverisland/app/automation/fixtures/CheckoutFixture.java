package com.riverisland.app.automation.fixtures;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.MonetaryAmountUtils;

public class CheckoutFixture extends AppSession {
    public CheckoutFixture verifyOrderSummary() {
        if (null != paymentMethod && paymentMethod.equals(PaymentMethod.CARD)) {
        	String displayedCardNumber = checkoutPage.getSummaryCardType().trim();
            assertThat(displayedCardNumber).containsPattern(Pattern.compile("xxxx xxxx xxxx \\d{4}$"));
        }

        selectedRegion = selectedRegion == null ? Region.GB : selectedRegion; // we apply the default

        assertThat(checkoutPage.getSummaryDeliveryAddress()).containsIgnoringCase(storeAddress != null ? storeAddress.getAddressLine1() : address.getAddressLine1());
        assertThat(stickySummaryPage.getDeliveryType(deliveryOption).contains(deliveryOption.getDescription()));
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), stickySummaryPage.getSubTotal())).isTrue();
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), stickySummaryPage.getShippingTotal())).isTrue();
        assertThat(MonetaryAmountUtils.isValidAmount(selectedRegion.getCurrency(), stickySummaryPage.getTotalToPay())).isTrue();
        return this;
    }

	public CheckoutFixture verifyOrderSummaryForStoreCollection(String deliveryAddress) {
        assertThat(checkoutPage.getSummaryCardType()).isNotBlank();
        assertThat(checkoutPage.getSummaryDeliveryAddress()).containsIgnoringCase(deliveryAddress);
        assertThat(stickySummaryPage.getDeliveryType(deliveryOption)).containsIgnoringCase(deliveryOption.getDescription());
        return this;		
	}
	public CheckoutFixture minimiseSummary() {
		stickySummaryPage.minimise();
		return this;
	}
}
