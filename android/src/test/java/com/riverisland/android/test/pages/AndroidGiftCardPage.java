package com.riverisland.android.test.pages;

import org.openqa.selenium.By;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.giftcards.GiftCard;
import com.riverisland.app.automation.pages.GiftCardPage;

public class AndroidGiftCardPage extends AndroidCorePage implements GiftCardPage<AndroidGiftCardPage> {
	private static final By ADD_GIFT_CARD_LOCATOR = By.xpath("//android.widget.TextView[@text='Add Gift Card' or @text='Add or remove Gift Card']");
	
	public AndroidGiftCardPage(RiverIslandNativeAppDriver appDriver) {
		super(appDriver);
	}

	@Override
	public void addGiftCard(GiftCard giftCard) {
		appDriver.tap(ADD_GIFT_CARD_LOCATOR);
		
		appDriver.retrieveMobileElement(androidId.apply("gift_card_number_edit")).sendKeys(giftCard.getNumber());;
		appDriver.retrieveMobileElement(androidId.apply("gift_card_pin_number_edit")).sendKeys(giftCard.getPin());
		
		appDriver.tap("APPLY GIFT CARD");
		
	}

	@Override
	public boolean hasAddGiftCardDisplayed() {
		return appDriver.isDisplayed(ADD_GIFT_CARD_LOCATOR, 1);
	}

	@Override
	public void completeOrder() {
		appDriver.tap("COMPLETE ORDER");		
	}

	@Override
	public void removeGiftCard() {
		appDriver.tap("Remove Gift Card");
		appDriver.tap("Remove");
	}
}
