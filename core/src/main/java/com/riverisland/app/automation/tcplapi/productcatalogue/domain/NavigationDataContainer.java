package com.riverisland.app.automation.tcplapi.productcatalogue.domain;

import java.util.List;

import com.riverisland.app.automation.tcplapi.productcatalogue.domain.NavigationItem;

public class NavigationDataContainer {
	
	private List<NavigationItem> navigationItems;

	public List<NavigationItem> getNavigationItems() {
		return navigationItems;
	}

	public void setNavigationItems(List<NavigationItem> navigationItems) {
		this.navigationItems = navigationItems;
	}

}
