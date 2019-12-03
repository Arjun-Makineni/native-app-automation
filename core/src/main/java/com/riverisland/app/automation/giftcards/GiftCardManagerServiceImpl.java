package com.riverisland.app.automation.giftcards;

import java.math.BigDecimal;

import com.riverisland.automation.utils.ecom.domain.giftcard.GiftCardTokenRequest;
import com.riverisland.automation.utils.ecom.domain.giftcard.GiftCardTokenResponse;
import com.riverisland.automation.utils.ecom.domain.giftcard.GiftCardTransactionRequest;
import com.riverisland.automation.utils.ecom.domain.giftcard.GiftCardTransactionResponse;
import com.riverisland.automation.utils.ecom.service.GiftCardTokenRequestService;
import com.riverisland.automation.utils.ecom.service.GiftCardTransactionRequestService;

import io.restassured.filter.log.LogDetail;

public class GiftCardManagerServiceImpl implements GiftCardManagerService {

	private static final String TRANSACTION_ENDPOINT = "transaction";
	
	private GiftCardServiceConfig config;
	private String token;
	private GiftCardTransactionRequestService transactionRequestService;
	
	public GiftCardManagerServiceImpl(GiftCardServiceConfig config) {
		this.config = config;
		transactionRequestService = GiftCardTransactionRequestService.create(
				config.getApiServiceUrl(), 
				LogDetail.URI, 
				getToken());
	}
	
	@Override
	public BigDecimal getBalance(GiftCard giftCard) {
		GiftCardTransactionRequest request = new GiftCardTransactionRequest(
				giftCard.getNumber(), 
				giftCard.getPin(), 
				giftCard.getCurrencyCode());
		
		GiftCardTransactionResponse response = transactionRequestService.getBalance(
				TRANSACTION_ENDPOINT, 
				request);
		return new BigDecimal(response.getTranactionResponse().getCards().get(0).getBalanceAmount().getAmount());
	}

	@Override
	public void setBalance(GiftCard giftCard, BigDecimal value) {
		BigDecimal currentBalance = getBalance(giftCard);
		GiftCardTransactionResponse response = null;
		
		if (value.compareTo(currentBalance) < 0) {
			response = redeemBalance(giftCard, currentBalance.subtract(value));
		} else if (value.compareTo(currentBalance) > 0) {
			response = addBalance(giftCard, value.subtract(currentBalance));
		}
		
		if (null != response) {
//			System.out.println("ClientResponseStatus: " + response.getClientResponse().getStatusCode());
//			System.out.println("ClientResponseBody: " + response.getClientResponse().getResponseBody());
			System.out.println("TestCard name: " + giftCard.getDescription() + " - Card No: "+giftCard.getNumber() + " - PIN: " + giftCard.getPin() + " - " + response.getTranactionResponse().getResponse().getMessage());
		}
	}
	
	@Override
	public void resetBalance(GiftCard giftCard) {
		setBalance(giftCard, new BigDecimal(giftCard.getInitialValue()));
	}
	
	private String getToken() {
		if (token == null) {
			final GiftCardTokenRequestService tokenRequestService = GiftCardTokenRequestService.create(
					config.getTokenRequestServiceUrl(), LogDetail.URI);

			GiftCardTokenRequest request = new GiftCardTokenRequest(
					config.getClientId(), 
					config.getClientSecret(), 
					config.getAudience(), 
					config.getGrantType());
			
			GiftCardTokenResponse response = tokenRequestService.getTokenResponse("oauth/token", request);
			token = response.getGiftCardAuth().getAccess_token();
		}
		return token;
	}
	
	private GiftCardTransactionResponse addBalance(GiftCard giftCard, BigDecimal value) {
		GiftCardTransactionRequest request = new GiftCardTransactionRequest(
				giftCard.getNumber(), 
				giftCard.getPin(), 
				giftCard.getCurrencyCode());
		
		return transactionRequestService.addBalance(
				TRANSACTION_ENDPOINT, 
				request, 
				value.toString(), 
				giftCard.getCurrencyCode());
	}
	
	private GiftCardTransactionResponse redeemBalance(GiftCard giftCard, BigDecimal value) {
		GiftCardTransactionRequest request = new GiftCardTransactionRequest(
				giftCard.getNumber(), 
				giftCard.getPin(), 
				giftCard.getCurrencyCode());
		
		return transactionRequestService.redeemBalance(
				TRANSACTION_ENDPOINT, 
				request, 
				value.toString(), 
				giftCard.getCurrencyCode());	
	}
}
