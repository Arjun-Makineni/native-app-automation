package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.enums.DeliveryOption;

public interface StickySummaryPage<T> {

	T expand();
	T minimise();
	String getTotalToPay();
	String getSubTotal();
	String getShippingTotal();
	String getPromoTotal();
	String getGiftcardTotal();
	String getDeliveryType(DeliveryOption deliveryOption);
	
}
