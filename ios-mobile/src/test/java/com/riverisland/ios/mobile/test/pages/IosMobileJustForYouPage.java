package com.riverisland.ios.mobile.test.pages;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.JustForYouPage;

/**
 * Created by Simon Johnson on 21/11/2018
 */
public class IosMobileJustForYouPage implements JustForYouPage<IosMobileJustForYouPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileJustForYouPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

	@Override
	public void displayJustForYouModule() {
		appDriver.scrollDownUntilInView(By.name("PERSONALISE MY FEED"));		
	}

	private static final By IM_EMPTY_LOCATOR = By.name("I'm empty!");
	@Override
	public boolean isSetup() {
		appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(IM_EMPTY_LOCATOR), 2000);
		return !appDriver.isDisplayed(IM_EMPTY_LOCATOR);
	}
	
	@Override
	public IosMobileJustForYouPage selectPersonaliseFeed() {
		appDriver.retrieveMobileElement(By.name("PERSONALISE MY FEED")).click();
		return this;
	}
	@Override
	public void selectPersonalise() {
		appDriver.scrollDownUntilInView(By.name("Personalise"));
		appDriver.tap("Personalise");		
	}
	@Override
	public void chooseCategory(Category category, String... subCategories) {
		appDriver.tap(category.getName());
		for (String subCat : subCategories) {
			appDriver.tap(subCat);
			verifySubCategorySelected(subCat);
		}	
		appDriver.tap(category.getName());
	}
	
	private void verifySubCategorySelected(String subCategory) {
		final By categorySelectedLocator = By.xpath(String.format("//XCUIElementTypeImage[@name = 'icnSmallTick']/../XCUIElementTypeStaticText[@name='%s']", subCategory));	
		assertTrue(null != appDriver.retrieveMobileElement(categorySelectedLocator));
	}
	
	@Override
	public void startShopping() {
		appDriver.tap("START SHOPPING");		
	}

	@Override
	public void verifySubCategoriesSelected(Category category, int numberSelected) {
		final By numberSelectedLocator = By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s selected']/../XCUIElementTypeStaticText[@name='%s']", numberSelected, category.getName()));
		assertTrue(null != appDriver.retrieveMobileElement(numberSelectedLocator));
	}

	@Override
	public void clearAll() {
		appDriver.tap("Clear all");	
	}

	@Override
	public void close() {
		appDriver.tap("Close");		
	}

	@Override
	public void failToChooseCategory(Category category, String subCategory) {
		try {
			chooseCategory(category, subCategory);
		}
		catch (TimeoutException te) {
			// Expected not to find selected category
			return;
		}
		fail(String.format("Not expected to find category %s and sub category %s set", category.getName(), subCategory));
	}

	@Override
	public void toggleWishList() {	
		appDriver.tap(By.name("icnWishlist"));
	}
}
