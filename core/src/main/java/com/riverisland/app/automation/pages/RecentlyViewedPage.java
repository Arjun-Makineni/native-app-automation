package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
public interface RecentlyViewedPage<T> {
    Boolean isRecentlyViewedProductDisplayed(String productName);

    T clearRecentlyViewedList();

    Boolean isRecentlyViewedEmpty();

	T selectWishListHeart(String productName);
	
	T seeAll(int expectedNumberOfProducts);

	T clearAll();
}