package com.riverisland.app.automation.tcplapi.productcatalogue.domain;

public class Product {
	
	private String productId;
	private String displayName;
	private SwatchInfo swatchInfo;
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public SwatchInfo getSwatchInfo() {
		return swatchInfo;
	}
	public void setSwatchInfo(SwatchInfo swatchInfo) {
		this.swatchInfo = swatchInfo;
	}
	@Override
	public String toString() {
		return "Product [productId=" + productId + ", displayName=" + displayName + ", swatchInfo=" + swatchInfo + "]";
	}

}
