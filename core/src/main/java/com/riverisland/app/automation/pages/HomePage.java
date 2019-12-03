package com.riverisland.app.automation.pages;

import java.util.List;

import org.openqa.selenium.Point;

import io.appium.java_client.MobileElement;

public interface HomePage<T> {
		
	T wishListDisplayed(int itemsInWishlist);
		
	T orderStatusDisplayed();
	
	T tapItem(MobileElement item);
	
	T tapItem(Point point);
	
	List<MobileElement> displayedTrendingProduct(String locator);
	
	T swipeForNextTrendingItem(Point penultimateDisplayedItemPoint, Point lastDisplayedItemPoint);
	
	T pauseForWelcomeBanner();
	
	T openSearch();
	
	T search(String searchTerm);
	
	T searchForProductsAndSelect(String productId);
	
	boolean isHomePage();
	
	T closeSearch();

}
