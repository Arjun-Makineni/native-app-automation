package com.riverisland.app.automation.tcplapi.productcatalogue;

import com.riverisland.app.automation.tcplapi.productcatalogue.domain.ProductsDataEntity;

public class ProductListResponse extends Response {
	private ProductsDataEntity productsDataEntity;
	
	public ProductsDataEntity productsContainer() {
		return productsDataEntity;
	}

	public ProductListResponse(int status, ProductsDataEntity productsContainer) {
		super(status);
		this.productsDataEntity = productsContainer;
	}
	
	public boolean hasData() {
		return null != productsDataEntity &&
			   null != productsDataEntity.getData() &&
			   null != productsDataEntity.getData().getBrowse() && 
			   null != productsDataEntity.getData().getBrowse().getProducts();
	}
}
