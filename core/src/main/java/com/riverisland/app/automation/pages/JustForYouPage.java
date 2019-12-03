package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.enums.Category;

public interface JustForYouPage<T> {
	void displayJustForYouModule();
	boolean isSetup();
	T selectPersonaliseFeed();
	void selectPersonalise();
	void chooseCategory(Category category, String... subCategories);
	void startShopping();
	void verifySubCategoriesSelected(Category category, int numberSelected);
	void clearAll();
	void close();
	void failToChooseCategory(Category category, String subCategory);
	void toggleWishList();
}
