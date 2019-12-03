package com.riverisland.app.automation.tcplapi.productcatalogue;

import org.springframework.web.client.RestClientException;

public abstract class Response {
	
	private int status;
	private String errorText;
	
	public Response(int status) {
		this.status = status;
	}
	
	public Response(RestClientException rce) {
		this.errorText = String.format("Failed request: %s", rce.getMessage());
	}

	public int getStatus() {
		return status;
	}

	public String getErrorText() {
		return errorText;
	}
	
	public abstract boolean hasData();
}
