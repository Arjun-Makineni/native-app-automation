package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.OnboardingPage;
import com.riverisland.app.automation.pages.SearchPage;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidKeyCode;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidSearchPage extends AndroidCorePage implements SearchPage<AndroidSearchPage> {

    public AndroidSearchPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }
 
    private static final String SEARCH = "I'm looking for...";
    @Override
    public AndroidSearchPage search(String criteria) {
        appDriver
                .tap(SEARCH)
                .type(SEARCH, criteria)
                .pressAndroidKey(AndroidKeyCode.ENTER);
        return this;
    }

    @Override
    public AndroidSearchPage searchHistory(String history) {
    	final By searchLocator = By.xpath(String.format("//*[contains(@text, '%s')]", "looking for"));
    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(searchLocator));
        appDriver
                .tap(SEARCH)
                .tap(history);
        return this;
    }

    @Override
    public AndroidSearchPage cancelSearch() {
        appDriver.tap(androidId.apply("search_back_button"));
        return this;
    }

    @Override
    public Boolean isExpectedNumberOfSearchResults(String criteria, int countOfSearchResults) {
        return appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("search_recycler_view")))
                .retrieveMobileElements(By.xpath(String.format("//android.widget.TextView[@text='%s']", criteria))).size() == countOfSearchResults;
    }

    @Override
    public AndroidSearchPage changeEnvironment(String environment, boolean checkBeforeChanging, OnboardingPage onboardingPage) {
        search("NN4MRI::GOTO_QA");

        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("environments_title")));

        final By environmentTextLocator = androidId.apply("environments_value");

        if (checkBeforeChanging && appDriver.retrieveMobileElementText(environmentTextLocator).equals(environment)) {
            appDriver.tap(androidId.apply("environments_clear_cache_button"));
        } else {
            appDriver
                    .tap("Select an Environment")
                    .scrollIntoViewAndTap(environment)
                    .tap("OK");
        }

        appDriver
                .pause(2500)
                .reLaunchApp();
        
    	onboardingPage.skipOnboardingPage();
        onboardingPage.acceptPushNotificationsAndSkip(false);
        return this;
    }

    @Override
    public Boolean isSearchMessageDisplayed(String message) {
        return appDriver.retrieveMobileElement(By.xpath(String.format("//*[contains(@text,'%s')]", message))).isDisplayed();
    }

    @Override
    public Boolean hasSearchResults() {
        return !appDriver.isDisplayed(By.xpath("//*[contains(@text,'Sorry')]"));
    }

	@Override
	public boolean hasPredictiveDisplayed(String searchTerm) {
		final By predictiveLocator = By.xpath(String.format("//android.widget.TextView[starts-with(@text, '%s')]", searchTerm));
		List<MobileElement> elements = appDriver.retrieveMobileElements(predictiveLocator);
		
		return (null != elements ? elements.size() : 0) > 0;
	}

	@Override
	public boolean hasPopularDisplayed() {
		final By popularLocator = By.xpath("//android.widget.TextView[contains(@text, 'Popular')]");
		List<MobileElement> elements = appDriver.retrieveMobileElements(popularLocator);
		return (null != elements ? elements.size() : 0) > 0;
	}
}