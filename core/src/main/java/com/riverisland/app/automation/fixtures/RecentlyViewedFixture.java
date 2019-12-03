package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;

import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

public class RecentlyViewedFixture extends AppFixture {

	public RecentlyViewedFixture verifyRecentlyViewed(Product product) {
		assertTrue(recentlyViewedPage.isRecentlyViewedProductDisplayed(product.getDescription()));
		return this;
	}
	
	public boolean isRecentlyViewedInWishList(String productName) {
		return false;
	}
	
	public RecentlyViewedFixture toggleProductInWishList(String productName) {
		recentlyViewedPage.selectWishListHeart(productName);
		return this;
	}
	
	public RecentlyViewedFixture displayRecentlyViewed(int expectedProductsDisplayed) {
		recentlyViewedPage.seeAll(expectedProductsDisplayed);
		return this;
	}

	public RecentlyViewedFixture clearRecentlyViewed() {
		recentlyViewedPage.clearAll();
		return this;		
	}
}
