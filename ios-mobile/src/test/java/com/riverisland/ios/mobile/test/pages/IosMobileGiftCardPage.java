package com.riverisland.ios.mobile.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.giftcards.GiftCard;
import com.riverisland.app.automation.pages.GiftCardPage;

public class IosMobileGiftCardPage implements GiftCardPage<IosMobileGiftCardPage> {
    private RiverIslandNativeAppDriver appDriver;
	private static final By ADD_GIFT_CARD_LOCATOR = By.xpath("//XCUIElementTypeButton[@name='Add Gift Card' or @name='Add or remove Gift Card']");
	private static final By REMOVE_GIFT_CARD_LOCATOR = By.xpath("//XCUIElementTypeButton[@name='Remove Gift Card'");
	private static final int GIFTCARD_LINK_TIMEOUT_IN_SECS = 2;
	
    public IosMobileGiftCardPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }
	@Override
	public void addGiftCard(GiftCard giftCard) {

		appDriver.retrieveMobileElement(ADD_GIFT_CARD_LOCATOR).click();
		appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeTextField[@value='16 digit Gift Card number']")).sendKeys(giftCard.getNumber());
		appDriver.tap("Done");
		appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeTextField[@value='4 digit PIN']")).sendKeys(giftCard.getPin());
		appDriver.tap("Done");		
		appDriver.tap("APPLY GIFT CARD");
		
	}

	@Override
	public boolean hasAddGiftCardDisplayed() {
		//Cater for lag in IOS update to add gift card status
		appDriver.waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(REMOVE_GIFT_CARD_LOCATOR), GIFTCARD_LINK_TIMEOUT_IN_SECS);
		return appDriver.isDisplayed(ADD_GIFT_CARD_LOCATOR, GIFTCARD_LINK_TIMEOUT_IN_SECS);
	}

	@Override
	public void completeOrder() {
		appDriver.tap("COMPLETE ORDER");	
	}

	@Override
	public void removeGiftCard() {
		appDriver.retrieveMobileElement("Remove Gift Card").click();
		appDriver.retrieveMobileElement("Remove").click();
	}

}
