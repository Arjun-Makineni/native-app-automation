package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.openqa.selenium.TimeoutException;

import com.riverisland.app.automation.enums.Category;


public class JustForYouFixture extends AppFixture {

	public JustForYouFixture verifyDisplayModule(boolean categoriesSelected) {
		displayJustForYouModule();
		assertTrue(justForYouPage.isSetup() == categoriesSelected);
		return this;
	}
	
	private void displayJustForYouModule() {
		justForYouPage.displayJustForYouModule();
	}

	public JustForYouFixture selectPersonaliseFeed() {
		justForYouPage.selectPersonaliseFeed();
		return this;
	}
	
	public JustForYouFixture chooseCategory(Category category, String... subCategories) {
		justForYouPage.chooseCategory(category, subCategories);	
		justForYouPage.verifySubCategoriesSelected(category, subCategories.length);
		return this;
	}

	public JustForYouFixture failToChooseCategory(Category category, String subCategory) {
		justForYouPage.failToChooseCategory(category, subCategory);
		return this;
	}

	public JustForYouFixture startShopping() {
		justForYouPage.startShopping();
		return this;
	}

	public JustForYouFixture clearAll() {
		justForYouPage.clearAll();
		return this;
	}

	public JustForYouFixture selectPersonalise() {
		justForYouPage.selectPersonalise();
		return this;
	}

	public JustForYouFixture close() {
		justForYouPage.close();
		return this;
	}

	public JustForYouFixture addProductToWishlist() {
		justForYouPage.toggleWishList();
		return this;
	}

	public JustForYouFixture removeProductFromWishList() {
		justForYouPage.toggleWishList();
		return this;		
	}
}
