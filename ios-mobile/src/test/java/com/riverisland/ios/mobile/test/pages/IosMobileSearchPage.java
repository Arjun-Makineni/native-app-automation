package com.riverisland.ios.mobile.test.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.OnboardingPage;
import com.riverisland.app.automation.pages.SearchPage;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileSearchPage implements SearchPage<IosMobileSearchPage> {
    private RiverIslandNativeAppDriver appDriver;
    private static final By searchLocator = By.xpath("//XCUIElementTypeTextField");

    public IosMobileSearchPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileSearchPage search(String criteria) {
    	
        appDriver.touch(appDriver.retrieveMobileElement(searchLocator));
        
        MobileElement search = appDriver.retrieveMobileElement(searchLocator);

        appDriver.type(search, criteria);       
        appDriver.retrieveMobileElement("Search").click();
        return this;
    }

    @Override
    public IosMobileSearchPage searchHistory(String history) {
        appDriver
				.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name(history)), 5000)
        		.touch(appDriver.retrieveMobileElement(searchLocator))
                .tap(history);
        return this;
    }

    @Override
    public IosMobileSearchPage cancelSearch() {
        appDriver.tap("Cancel");
        return this;
    }

    @Override
    public Boolean isExpectedNumberOfSearchResults(String criteria, int countOfSearchResults) {

    	//Check if product id search
    	if (null != criteria && 
    			criteria.matches("[0-9]+")) {
    		return appDriver
    				.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name(criteria)), 2000)
                	.retrieveMobileElements(By.xpath(String.format("//XCUIElementTypeNavigationBar/XCUIElementTypeStaticText[@value='%s']", criteria))).size() == countOfSearchResults;
    	} 
    		
    	//general text product search
        return appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name(criteria)), 2000)
        				.retrieveMobileElements(By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name,'%s')]", criteria))).size() == countOfSearchResults;
    }

    @Override
    public IosMobileSearchPage changeEnvironment(String environment, boolean checkBeforeChanging, OnboardingPage onboardingPage) {
    	
    	search("NN4MRI::GOTO_ENVIRONMENTS");

        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Live")));

        if (checkBeforeChanging) {
            try {
                appDriver.getWrappedDriver().findElement(By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']//following::XCUIElementTypeButton[1]", environment)));
                appDriver.tap("Close");
                return this;
            } catch (WebDriverException ignored) {
            }
        }

        appDriver.filterElements(By.name(environment), RemoteWebElement::isDisplayed).get(0).click();

        appDriver
                .waitFor(ExpectedConditions.alertIsPresent())
                .acceptAlert()
                .pause(2500)
                .reLaunchApp();

        onboardingPage.acceptPushNotificationsAndSkip(true);
        return this;
    }

    @Override
    public Boolean isSearchMessageDisplayed(String message) {
        return appDriver.retrieveMobileElement(By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name,'%s')]", message))).isDisplayed();
    }

    @Override
    public Boolean hasSearchResults() {
        return !appDriver.isDisplayed(By.xpath("//XCUIElementTypeStaticText[contains(@name,'Sorry')]"));
    }
    
    @Override
    public boolean hasPredictiveDisplayed(String searchTerm) {
    	final By predictiveLocator = By.xpath(String.format("//XCUIElementTypeTable/XCUIElementTypeCell/XCUIElementTypeStaticText[starts-with(@name,'%s')]", searchTerm));
    	
    	List<MobileElement> elements = appDriver.retrieveMobileElements(predictiveLocator);
    	
    	return (null != elements ? elements.size() : 0) > 0;
    }
    
    @Override
    public boolean hasPopularDisplayed() {
    	final By popularLocator = By.xpath("//XCUIElementTypeTable/XCUIElementTypeOther[starts-with(@name,'Popular')]");
    	List<MobileElement> elements = appDriver.retrieveMobileElements(popularLocator);
    	
    	return (null != elements ? elements.size() : 0) > 0;
    }
}
