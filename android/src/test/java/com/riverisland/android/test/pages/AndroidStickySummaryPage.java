package com.riverisland.android.test.pages;

import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.StickySummaryPage;

public class AndroidStickySummaryPage extends AndroidCorePage implements StickySummaryPage<AndroidStickySummaryPage> {

	public AndroidStickySummaryPage(RiverIslandNativeAppDriver appDriver) {
		super(appDriver);
	}

	@Override
	public AndroidStickySummaryPage expand() {
		if (isMinimised()) {
			appDriver.tap(androidId.apply("sticky_total_icon"));
		}
		return this;
	}

	@Override
	public AndroidStickySummaryPage minimise() {
		appDriver.tap("Total to pay");
		return this;
	}

	@Override
	public String getTotalToPay() {
		return isMinimised() ? 
				appDriver.retrieveMobileElementText(androidId.apply("sticky_total_value_collapsed")) :
				appDriver.retrieveMobileElementText(androidId.apply("sticky_total_value"));
	}

	@Override
	public String getSubTotal() {
		expand();
		return appDriver.retrieveMobileElement(
				androidId.apply("sticky_subtotal_row"),
				androidId.apply("sticky_value")).getText();
	}

	@Override
	public String getShippingTotal() {
		expand();
		return appDriver.retrieveMobileElement(
				androidId.apply("sticky_delivery_row"), 
				androidId.apply("sticky_value")).getText();
	}

	@Override
	public String getPromoTotal() {
		expand();
		return appDriver.retrieveMobileElement(
				androidId.apply("sticky_promo_value_row"), 
				androidId.apply("sticky_value")).getText();
	}

	@Override
	public String getGiftcardTotal() {
		expand();
		return appDriver.retrieveMobileElement(
				androidId.apply("sticky_giftcard_value_row"), 
				androidId.apply("sticky_value")).getText();
	}

	@Override
	public String getDeliveryType(DeliveryOption deliveryOption) {
		expand();
		return appDriver.retrieveMobileElement(
				androidId.apply("sticky_delivery_row"), 
				androidId.apply("sticky_label")).getText();
	}

	private boolean isMinimised() {
		return appDriver.isDisplayed(androidId.apply("sticky_total_icon"), 1);
	}
}
