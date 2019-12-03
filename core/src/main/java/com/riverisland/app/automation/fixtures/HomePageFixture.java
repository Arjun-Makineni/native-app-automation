package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Point;

import com.riverisland.app.automation.helpers.qubit.QubitException;
import com.riverisland.app.automation.helpers.qubit.QubitHelper;
import com.riverisland.app.automation.helpers.qubit.TrendingCategory;

import io.appium.java_client.MobileElement;

public class HomePageFixture extends AppFixture {
	
    private static final int MAX_TRENDING_ITEMS = 10;
    private static final int MAX_DISPLAYED_ITEMS = 3;
    
    private static final String DEFAULT_TRENDING_MEN_ITEMS = "Trending in men";
    private static final String DEFAULT_TRENDING_WOMEN_ITEMS = "Trending in women";
    private static final String TRENDING_ITEMS = "Trending";
	
	public HomePageFixture() {
		
	}
    // Homepage - Trending Items
    
    public HomePageFixture verifyMaleAndFemaleTrendingItemsDisplayed() {   	    	
		QubitHelper qubit = new QubitHelper();
		try {
			verifyDisplayedProductIds(
					DEFAULT_TRENDING_WOMEN_ITEMS,
					qubit.trendingItems(TrendingCategory.WOMEN, MAX_TRENDING_ITEMS).getItems());
			verifyDisplayedProductIds(
					DEFAULT_TRENDING_MEN_ITEMS,
					qubit.trendingItems(TrendingCategory.MEN, MAX_TRENDING_ITEMS).getItems());
		}
		catch (QubitException qe) {
			fail(qe.getMessage());
		}
    	return this;
    }
    
    public HomePageFixture verifyMaleTrendingItemsDisplayed() {
		QubitHelper qubit = new QubitHelper();
		
		try {
			verifyDisplayedProductIds(
					TRENDING_ITEMS,
					qubit.trendingItems(TrendingCategory.MEN, MAX_TRENDING_ITEMS).getItems());
		}
		catch (QubitException qe) {
			fail(qe.getMessage());
		}
		
		return this;
    }
    public HomePageFixture verifyFemaleTrendingItemsDisplayed() {
		QubitHelper qubit = new QubitHelper();
		
		try {
			verifyDisplayedProductIds(
					TRENDING_ITEMS,
					qubit.trendingItems(TrendingCategory.WOMEN, MAX_TRENDING_ITEMS).getItems());
		}
		catch (QubitException qe) {
			fail(qe.getMessage());
		}
		
		return this;
    }
    
	private void verifyDisplayedProductIds(String locator, 
            List<String> expectedProductIds) {

		List<MobileElement> items = homePage.displayedTrendingProduct(locator);
		
		assertTrue(items != null && items.size() >= MAX_DISPLAYED_ITEMS, "Check items are displayed");
		
		List<String> actualDisplayedIds = new ArrayList<String>();		
		String lastProductId = null;

		boolean qubitMatch = false;
		int displayedItemIndex = 0;
		while (items.size() > 0 &&
			   displayedItemIndex < MAX_DISPLAYED_ITEMS) {			
			homePage.tapItem(items.get(displayedItemIndex));
			lastProductId = getProductNumber();
			actualDisplayedIds.add(lastProductId);
			qubitMatch = checkMatch(expectedProductIds, actualDisplayedIds);
			displayedItemIndex++;
		}
		
		Point penultimateDisplayedItemPoint = items.get(MAX_DISPLAYED_ITEMS-2).getLocation();
		Point lastDisplayedItemPoint = items.get(MAX_DISPLAYED_ITEMS-1).getLocation();
		
		// Now swipe through the remaining products

		while (actualDisplayedIds.size() < MAX_TRENDING_ITEMS &&
			   !qubitMatch) {		
			homePage.swipeForNextTrendingItem(penultimateDisplayedItemPoint, lastDisplayedItemPoint);
			
			// Get the product info
			items = homePage.displayedTrendingProduct(locator);
			homePage.tapItem(items.get(MAX_DISPLAYED_ITEMS-1));
			actualDisplayedIds.add(getProductNumber());
			qubitMatch = checkMatch(expectedProductIds, actualDisplayedIds);
		}
		
		assertTrue(qubitMatch, "No trending items displayed which are in the Qubit feed");
	} 
	
	private boolean checkMatch(List<String> expectedItems, List<String> actualItems) {
		
		//Cannot definitively check these products as the Qubit api gets a list of trending products from live
		// and there is no guarantee that the products will also exist in the various UAT and PrProd environments.
		// So basically just check that at least one product is displayed from the list of Qubit trending products
			
		for (String actualItem : actualItems) {
			for (String expectedItem : expectedItems) {
				if (null != expectedItem && expectedItem.equals(actualItem)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getProductNumber() {
		String retval = null;
		
		retval = productDetailsPage.getProductNumber();		
		if (null != retval) {
			productDetailsPage.back();
		}
		return retval;
	}
    
    // Homepage - Wishlist
    public HomePageFixture verifyWishListDisplayed(int itemsInWishList) {
    	homePage.wishListDisplayed(itemsInWishList);
    	return this;
    }
    //Homepage - Order status
    public HomePageFixture verifyOrderStatusDisplayed() {
    	homePage.orderStatusDisplayed();	
    	return this;
    }
    
    public HomePageFixture openSearch() {
    	homePage.openSearch();
    	return this;
    }
    
    public boolean verifyPredictiveAndPopularSearch(String searchTerm) {
    	openSearch();
    	homePage.search(searchTerm);   	
    	return searchPage.hasPredictiveDisplayed("Top") && searchPage.hasPopularDisplayed();	
    }
}
