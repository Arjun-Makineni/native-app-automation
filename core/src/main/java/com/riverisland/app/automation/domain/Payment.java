package com.riverisland.app.automation.domain;

import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.giftcards.GiftCard;
import com.riverisland.app.automation.pojos.GiroPayCredentials;
import com.riverisland.app.automation.pojos.PayPalCredentials;
import com.riverisland.app.automation.pojos.TestCard;
import com.riverisland.app.automation.utils.YamlLoaderUtils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Prashant Ramcharan on 21/03/2017
 */
public class Payment {
    private PaymentMethod paymentMethod;
    private TestCard testCard;
    private GiftCard testGiftCard;
    private PayPalCredentials payPalCredentials;
    private GiroPayCredentials giroPayCredentials;

    public Payment(PaymentMethod paymentMethod, TestCard testCard) {
        this.paymentMethod = paymentMethod;
        this.testCard = testCard;
    }
    
    public Payment(PaymentMethod paymentMethod, GiftCard testGiftCard) {
        this.paymentMethod = paymentMethod;
        this.testGiftCard = testGiftCard;
    }    

    public Payment(PaymentMethod paymentMethod, PayPalCredentials payPalCredentials) {
        this.paymentMethod = paymentMethod;
        this.payPalCredentials = payPalCredentials;
    }

    public Payment(PaymentMethod paymentMethod, GiroPayCredentials giroPayCredentials) {
        this.paymentMethod = paymentMethod;
        this.giroPayCredentials = giroPayCredentials;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public TestCard getTestCard() {
        return testCard;
    }
    
    public GiftCard getTestGiftCard() {
    	return testGiftCard;
    }

    public PayPalCredentials getPayPalCredentials() {
        return payPalCredentials;
    }

    public GiroPayCredentials getGiroPayCredentials() {
        return giroPayCredentials;
    }

    public static class Builder {
        private PaymentMethod paymentMethod;
        private CardType cardType;
        private String giftCardDescription;
        private PayPalCredentials payPalCredentials;
        private GiroPayCredentials giroPayCredentials;

        private List<TestCard> availableTestCards;
        private List<GiftCard> availableTestGiftCards;

        private Builder(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;

            switch (paymentMethod) {
                case CARD: {
                    availableTestCards = YamlLoaderUtils.loadYamlDataCollection("testcards.yml", TestCard.class);
                    if (availableTestCards.isEmpty()) {
                        throw new RuntimeException("Unable to build payment details because there are no test cards!");
                    }
                    break;
                }

                case PAYPAL:
                    payPalCredentials = new PayPalCredentials("ritest_smi@river-island.com", "riverisland123");
                    break;

                case GIROPAY:
                    giroPayCredentials = new GiroPayCredentials("1234567890", "44448888", "AUTO TESTER");
                    break;
                    
                case GIFTCARD:
                	availableTestGiftCards = YamlLoaderUtils.loadYamlDataCollection("giftcards.yml", GiftCard.class);
                    if (availableTestGiftCards.isEmpty()) {
                        throw new RuntimeException("Unable to build payment details because there are no test gift cards!");
                    }
                    break;
            }
        }

        public static Builder create(PaymentMethod paymentMethod) {
            return new Builder(paymentMethod);
        }

        public Builder withCardType(CardType cardType) {
            this.cardType = cardType;
            return this;
        }
        
        public Builder withGiftCardDescription(String description) {
        	this.giftCardDescription = description;
        	return this;
        }

        public Payment build() {
            switch (paymentMethod) {
                case CARD:
                    TestCard testCard = null;
                    if (PaymentMethod.CARD.equals(paymentMethod)) {
                        testCard = availableTestCards
                                .stream()
                                .filter(t -> t.getCardType().equalsIgnoreCase(cardType.getDescription()))
                                .findFirst().orElseThrow(() -> new RuntimeException(cardType.getDescription() + " is not a configured test card!"));
                    }
                    return new Payment(paymentMethod, testCard);

                case PAYPAL:
                    return new Payment(paymentMethod, payPalCredentials);

                case GIROPAY:
                    return new Payment(paymentMethod, giroPayCredentials);
                    
                case GIFTCARD:
                	GiftCard testGiftCard = null;
                	if (PaymentMethod.GIFTCARD.equals(paymentMethod)) {
                		if (StringUtils.isEmpty(giftCardDescription)) {
                			testGiftCard = availableTestGiftCards
                					.stream()
                					.findFirst().orElseThrow(() -> new RuntimeException("No giftcards configured"));
                		} else {
                			testGiftCard = availableTestGiftCards
                				.stream()
                				.filter(gc -> gc.getDescription().equalsIgnoreCase(giftCardDescription))
								.findFirst().orElseThrow(() -> new RuntimeException(giftCardDescription + " is not a configured test gift card!"));
                		}
                	}
                	return new Payment(paymentMethod, testGiftCard);
            }
            throw new RuntimeException("Invalid or missing payment type!");
        }
    }
}
