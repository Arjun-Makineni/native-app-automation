package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import com.riverisland.app.automation.domain.Payment;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.giftcards.GiftCard;
import com.riverisland.app.automation.giftcards.GiftCardManagerService;
import com.riverisland.app.automation.giftcards.GiftCardManagerServiceImpl;
import com.riverisland.app.automation.giftcards.GiftCardServiceConfig;
import com.riverisland.app.automation.helpers.AppHelper;
import com.riverisland.app.automation.utils.YamlLoaderUtils;

public class GiftCardFixture extends AppFixture {
	
	private GiftCardManagerService giftCardManagerService;
	private AppHelper appHelper = new AppHelper();
	
	public GiftCardFixture() {
		this.giftCardManagerService = new GiftCardManagerServiceImpl(
				YamlLoaderUtils.loadYamlDataCollection(
						"giftcard_config.yml", 
						GiftCardServiceConfig.class).get(0));
	}
	
	public GiftCardFixture addGiftCard(String giftCardDescription) {		
		GiftCard giftCard = Payment.Builder
				.create(PaymentMethod.GIFTCARD)
				.withGiftCardDescription(giftCardDescription)
				.build().getTestGiftCard();
		
		BigDecimal initialBalance = giftCardManagerService.getBalance(giftCard);
		giftCardManagerService.resetBalance(giftCard);
		BigDecimal balance = giftCardManagerService.getBalance(giftCard);
		
		giftCardPage.addGiftCard(giftCard);
		return this;
	}
	
	public GiftCardFixture addGiftCardForProductValue(String description)
	{
		GiftCard giftCard = Payment.Builder
				.create(PaymentMethod.GIFTCARD)
				.withGiftCardDescription(description)
				.build().getTestGiftCard();
		
		BigDecimal basketValue = new BigDecimal(checkoutPage.getSummaryGrandTotal());
		giftCardManagerService.setBalance(giftCard, basketValue);
		giftCardPage.addGiftCard(giftCard);
		
		return this;
	}

	public GiftCardFixture completeGiftCardOrder() {
		giftCardPage.completeOrder();
		return this;
	}

	public GiftCardFixture verifyAddGiftCardIsAvailable(boolean isAvailable) {
		assertEquals(giftCardPage.hasAddGiftCardDisplayed(), isAvailable);
		return this;
	}
	
	public GiftCardFixture removeGiftCard() {
		giftCardPage.removeGiftCard();
		return this;
	}
}
