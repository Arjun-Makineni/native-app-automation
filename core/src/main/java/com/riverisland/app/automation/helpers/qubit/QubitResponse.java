package com.riverisland.app.automation.helpers.qubit;

import java.util.List;

import org.springframework.web.client.RestClientException;

public class QubitResponse {
	
	private int status;
	private String errorText;
	private List<String> productIds;

	public QubitResponse () {
		
	}
	
	public QubitResponse (int status, List<String> items) {
		setStatus(status);
		setItems(items);
	}
	
	public QubitResponse(RestClientException rce) {
		setErrorText(String.format("Failed to obtain Qubit trending information : %s", 
				      rce.getMessage()));
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public List<String> getItems() {
		return productIds;
	}

	public void setItems(List<String> items) {
		this.productIds = items;
	}
}
