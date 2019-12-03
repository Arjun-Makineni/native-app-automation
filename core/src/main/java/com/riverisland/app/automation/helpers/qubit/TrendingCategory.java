package com.riverisland.app.automation.helpers.qubit;

public enum TrendingCategory {
	MEN ("men"), WOMEN ("women"), CHILDREN ("kids");
	
	private final String category;
	
	TrendingCategory(String category) {
		this.category = category;		
	}
	
	public String getCategory() {
		return category;
	}
}
