package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;

import com.riverisland.app.automation.domain.Payment;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.pojos.TestCard;

public class SavedCardsFixture extends AppFixture {
	
	public SavedCardsFixture verifySavedCards(boolean available) {
		assertTrue(myRiverIslandPage.verifySavedCardsVisible() == available);
		return this;
	}
	
	public SavedCardsFixture openSavedCards() {
		myRiverIslandPage.openSavedCards();
		return this;
	}
	
	public boolean hasSavedCards() {
		return myRiverIslandPage.getNumberOfCardsSaved() > 0;
	}
	
	public SavedCardsFixture verifyCountOfSavedCards(int expected) {
		int actualCardsFound = myRiverIslandPage.getNumberOfCardsSaved();
		assertTrue(
				actualCardsFound == expected, 
				String.format(
						"New user : %s expected %s cards but actually had %s", 
						customer.getEmailAddress(),
						expected,
						actualCardsFound));
		return this;
	}
	
	public SavedCardsFixture removeCard(CardType cardtype) {
        final TestCard card = Payment.Builder.create(PaymentMethod.CARD).withCardType(cardType).build().getTestCard();

		myRiverIslandPage.removeSavedCard(card.getCardNumber().substring(card.getCardNumber().length()-4));
		return this;
	}
}
