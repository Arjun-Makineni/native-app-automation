package com.riverisland.app.automation.giftcards;

import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.riverisland.app.automation.domain.Payment;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.utils.YamlLoaderUtils;
import com.riverisland.automation.utils.ecom.domain.giftcard.GiftCardTransactionRequest;

public class GiftCardManagerServiceTest {
	
	private GiftCardManagerService giftCardManagerService;
	private GiftCardServiceConfig config;
	
	@BeforeClass
	void setupBeforeClass() {
		List<GiftCardServiceConfig> configs = YamlLoaderUtils.loadYamlDataCollection("giftcard_config.yml", GiftCardServiceConfig.class);
		this.config = configs.get(0);
		this.giftCardManagerService = new GiftCardManagerServiceImpl(config);
	}
	
	@Test
	public void checkBalance() {
		
		Payment testPayment = Payment.Builder
				.create(PaymentMethod.GIFTCARD)
				.withGiftCardDescription("UK(GBP)-10")
				.build();
		
		BigDecimal balance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());
		assertTrue(balance != null, "No balance returned");		
	}
	
	@Test
	public void setBalance() {
		
		Payment testPayment = Payment.Builder
				.create(PaymentMethod.GIFTCARD)
				.withGiftCardDescription("UK(GBP)-10")
				.build();
		
		BigDecimal currentBalance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
		assertTrue(currentBalance != null, "No balance returned");
		
		BigDecimal value = new BigDecimal("100.00");
		
		giftCardManagerService.setBalance(testPayment.getTestGiftCard(), value);

		BigDecimal balance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
		assertTrue(balance != null, "No balance returned");
		assertTrue(balance.compareTo(value) == 0, "Value shoud be set to " + value);	
	}
	
	@Test
	public void resetBalance() {		
		Payment testPayment = Payment.Builder
				.create(PaymentMethod.GIFTCARD)
				.withGiftCardDescription("UK(GBP)-60")
				.build();
		
		BigDecimal currentBalance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
		assertTrue(currentBalance != null, "No balance returned");
		
		BigDecimal value = new BigDecimal(testPayment.getTestGiftCard().getInitialValue());
		
		giftCardManagerService.resetBalance(testPayment.getTestGiftCard());

		BigDecimal balance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
		assertTrue(balance != null, "No balance returned");
		assertTrue(balance.compareTo(value) == 0, "Value shoud be set to " + value);
	}
	
	@Test
	public void initialiseCards() {
		String [] cards = {
				"UK(GBP)-10",
				"UK(GBP)-20",
				"UK(GBP)-30",
				"UK(GBP)-40",
				"UK(GBP)-50",
				"UK(GBP)-60",
				"ROI(EUR)-10",
				"ROI(EUR)-20",
				"ROI(EUR)-30",
				"ROI(EUR)-40",
				"ROI(EUR)-50",
				"ROI(EUR)-60"};
		
		for (String card : cards) {			
			Payment testPayment = Payment.Builder
					.create(PaymentMethod.GIFTCARD)
					.withGiftCardDescription(card)
					.build();
			
			BigDecimal currentBalance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
			assertTrue(currentBalance != null, "No balance returned");
			
			BigDecimal value = new BigDecimal(testPayment.getTestGiftCard().getInitialValue());
			
			giftCardManagerService.resetBalance(testPayment.getTestGiftCard());

			BigDecimal balance = giftCardManagerService.getBalance(testPayment.getTestGiftCard());		
			assertTrue(balance != null, "No balance returned");
//			System.out.println("Initialise balance: " +balance);
//			assertTrue(balance.compareTo(value) == 0, "Value should be set to " + value);
		}
	}
	@Test 
	public void addTen() {
		String [] cards = {
				"UK(GBP)-10",
				"UK(GBP)-20",
				"UK(GBP)-30",
				"UK(GBP)-40",
				"UK(GBP)-50",
				"UK(GBP)-60",
				"ROI(EUR)-10",
				"ROI(EUR)-20",
				"ROI(EUR)-30",
				"ROI(EUR)-40",
				"ROI(EUR)-50",
				"ROI(EUR)-60"
				};
		System.out.println("Setting balance to initial value + 10.00");
		BigDecimal increment = new BigDecimal("10.00");
		
		for (String card : cards) {
			System.out.println("==========================");
			Payment testPayment = Payment.Builder
					.create(PaymentMethod.GIFTCARD)
					.withGiftCardDescription(card)
					.build();
			System.out.println("Card No: " + testPayment.getTestGiftCard().getNumber());
			
			BigDecimal initialValue = giftCardManagerService.getBalance(testPayment.getTestGiftCard());
			System.out.println("Initial value: " + initialValue);
			BigDecimal balanceToSet = initialValue.add(increment);
			
			System.out.println("Setting balance to: " + balanceToSet);
			
			giftCardManagerService.setBalance(testPayment.getTestGiftCard(), balanceToSet);
			
			System.out.println("Incremented balance : " + giftCardManagerService.getBalance(testPayment.getTestGiftCard()));
			
			System.out.println("==========================");
		}
	}
}
