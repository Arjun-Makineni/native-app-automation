package com.riverisland.app.automation.giftcards;

import java.math.BigDecimal;

public interface GiftCardManagerService {
	
	BigDecimal getBalance(GiftCard giftCard);
	
	void setBalance(GiftCard giftCard, BigDecimal value);
	
	void resetBalance(GiftCard giftCard);

}
