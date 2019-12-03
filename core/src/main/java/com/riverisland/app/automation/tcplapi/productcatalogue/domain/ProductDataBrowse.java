package com.riverisland.app.automation.tcplapi.productcatalogue.domain;

import java.util.List;

public class ProductDataBrowse {
	
	private String displayName;
	private int totalCount;
	private List<Product> products;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}

}
