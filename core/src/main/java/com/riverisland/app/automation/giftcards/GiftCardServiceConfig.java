package com.riverisland.app.automation.giftcards;

public class GiftCardServiceConfig {
	
	private String tokenRequestServiceUrl;
	private String clientId;
	private String clientSecret;
	private String audience;
	private String grantType;
	private String apiServiceUrl;
	public String getTokenRequestServiceUrl() {
		return tokenRequestServiceUrl;
	}
	public void setTokenRequestServiceUrl(String tokenRequestServiceUrl) {
		this.tokenRequestServiceUrl = tokenRequestServiceUrl;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public String getApiServiceUrl() {
		return apiServiceUrl;
	}
	public void setApiServiceUrl(String apiServiceUrl) {
		this.apiServiceUrl = apiServiceUrl;
	}
	@Override
	public String toString() {
		return "GiftCardServiceConfig [tokenRequestServiceUrl=" + tokenRequestServiceUrl + ", clientId=" + clientId
				+ ", clientSecret=" + clientSecret + ", audience=" + audience + ", grantType=" + grantType
				+ ", apiServiceUrl=" + apiServiceUrl + "]";
	}
}
