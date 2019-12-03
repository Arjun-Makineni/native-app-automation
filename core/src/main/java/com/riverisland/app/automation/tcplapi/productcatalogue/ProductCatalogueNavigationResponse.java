package com.riverisland.app.automation.tcplapi.productcatalogue;

import com.riverisland.app.automation.tcplapi.productcatalogue.domain.NavigationDataEntity;

public class ProductCatalogueNavigationResponse extends Response{

	private NavigationDataEntity navigationData;
	
	public ProductCatalogueNavigationResponse(int status, NavigationDataEntity navigationData) {
		super(status);
		this.navigationData = navigationData;
	}

	public NavigationDataEntity getNavigationData() {
		return navigationData;
	}
	
	public boolean hasData() {
		return null != navigationData && 
			   null != navigationData.getData() && 
			   null != navigationData.getData().getNavigationItems();
	}

}
