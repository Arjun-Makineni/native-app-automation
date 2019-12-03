package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.giftcards.GiftCard;

public interface GiftCardPage<T> {
	
	public void addGiftCard(GiftCard giftCard);

	public boolean hasAddGiftCardDisplayed();

	public void completeOrder();

	public void removeGiftCard();

}
