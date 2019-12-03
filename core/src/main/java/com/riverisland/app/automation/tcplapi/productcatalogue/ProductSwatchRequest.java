package com.riverisland.app.automation.tcplapi.productcatalogue;

public class ProductSwatchRequest {

	private int numberOfSwatches;
	
	public ProductSwatchRequest(int numberOfSwatches) {
		this.numberOfSwatches = numberOfSwatches;
	}

	public int getNumberOfSwatches() {
		return numberOfSwatches;
	}

}
