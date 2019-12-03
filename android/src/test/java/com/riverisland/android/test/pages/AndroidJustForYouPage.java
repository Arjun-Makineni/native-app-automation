package com.riverisland.android.test.pages;

import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.JustForYouPage;

public class AndroidJustForYouPage extends AndroidCorePage implements JustForYouPage<AndroidJustForYouPage> {

	private boolean isSetup = false;
	
	public AndroidJustForYouPage(RiverIslandNativeAppDriver appDriver) {
		super(appDriver);
	}

	@Override
	public void displayJustForYouModule() {
		if (!isSetup) {
			appDriver.scrollIntoView("PERSONALISE MY FEED");
		} else {
			appDriver.scrollIntoView("Trending in women");
		}
	}

	@Override
	public boolean isSetup() {
		return isSetup;
	}

	@Override
	public AndroidJustForYouPage selectPersonaliseFeed() {
		appDriver.tap("PERSONALISE MY FEED");
		return this;
	}

	@Override
	public void selectPersonalise() {
		//Sticky search obscures display
		appDriver.swipeElementToElementLocation(
				androidId.apply("just_for_you_price"), 
				androidId.apply("trending_title"));
		appDriver.scrollIntoViewAndTap("Personalise");
	}

	@Override
	public void chooseCategory(Category category, String... subCategories) {
		appDriver.tap(category.getName());
		for (String subcat : subCategories) {
			appDriver.tap(subcat);
		}
		appDriver.tap(category.getName());
		isSetup = true;
	}
	
	@Override
	public void startShopping() {
		appDriver.tap("START SHOPPING");
	}

	@Override
	public void verifySubCategoriesSelected(Category category, int numberSelected) {
		if (numberSelected > 0) {
			assertTrue(null != appDriver.retrieveMobileElement(
					By.xpath(String.format("//android.widget.TextView[@text='%s']/../android.widget.TextView[@text='%s selected']", 
							category.getName(), 
							numberSelected))));
		} else {
			assertTrue(!appDriver.isDisplayed(
					By.xpath(String.format("//android.widget.TextView[@text='%s']/../android.widget.TextView[contains(@text,'selected')]", 
							category.getName())), 2));
		}
	}

	@Override
	public void clearAll() {
		appDriver.tap("Clear all");
		isSetup = false;
	}

	@Override
	public void close() {
		appDriver.back();
		isSetup = false;
	}

	@Override
	public void failToChooseCategory(Category category, String subCategory) {
		chooseCategory(category, subCategory);
		verifySubCategoriesSelected(category, 0);
	}

	@Override
	public void toggleWishList() {
		displayJustForYouModule();
		appDriver.tap(androidId.apply("just_for_you_wishlist_icon"));
	}
}
