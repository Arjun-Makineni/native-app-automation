package com.riverisland.ios.mobile.test.pages;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.StickySummaryPage;

public class IosMobileStickySummaryPage implements StickySummaryPage<IosMobileStickySummaryPage> {
    private RiverIslandNativeAppDriver appDriver;
    private IosMobileCheckoutPage checkoutPage;

    public IosMobileStickySummaryPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }
    
    public IosMobileStickySummaryPage(RiverIslandNativeAppDriver appDriver, IosMobileCheckoutPage checkoutPage) {
        this.appDriver = appDriver;
        this.checkoutPage = checkoutPage;
    }
	@Override
	public IosMobileStickySummaryPage expand() {
		if (!isExpanded()) {
			toggleSummary();
		}
		return this;
	}

	@Override
	public IosMobileStickySummaryPage minimise() {
		if (isExpanded()) {
			toggleSummary();
		}
		return this;
	}
	private boolean isExpanded() {
		final By locator = By.xpath("//XCUIElementTypeStaticText[@name='SUBTOTAL_TITLE_LABEL']");
		return appDriver.isDisplayed(locator);
	}

	private void toggleSummary() {
		appDriver.tap("Total to pay");
	}
	
	@Override
	public String getTotalToPay() {
		final By locator = By.xpath("//XCUIElementTypeStaticText[@name='TOTAL_VALUE_LABEL']");
		expand();
		return appDriver.retrieveMobileElementText(locator);
	}

	@Override
	public String getSubTotal() {
		final By locator = By.xpath("//XCUIElementTypeStaticText[@name='SUBTOTAL_VALUE_LABEL']");
		expand();
		return appDriver.retrieveMobileElementText(locator);
	}

	@Override
	public String getShippingTotal() {
		final By locator = By.xpath("//XCUIElementTypeStaticText[@name='DELIVERY_VALUE_LABEL']");
		expand();
		return appDriver.retrieveMobileElementText(locator);
	}

	@Override
	public String getPromoTotal() {
		throw new NotImplementedException("Implementation pending NN4M update");
	}

	@Override
	public String getGiftcardTotal() {
		throw new NotImplementedException("Implementation pending NN4M update");
	}

	@Override
	public String getDeliveryType(DeliveryOption deliveryOption) {
		final By locator = By.xpath(String.format("//XCUIElementTypeStaticText[@name='DELIVERY_TITLE_LABEL' and contains(@value,'%s')]", StringUtils.substringBeforeLast(deliveryOption.getDescription(), " ")));
		expand();
		return appDriver.retrieveMobileElement(locator).getText();
	}

}
