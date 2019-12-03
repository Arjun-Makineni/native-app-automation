package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.TabBarPage;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Prashant Ramcharan on 22/01/2018
 */
public class AndroidTabBarPage extends AndroidCorePage implements TabBarPage<AndroidTabBarPage> {

    public AndroidTabBarPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidTabBarPage openMyRi() {
        return this;
    }

    @Override
    public AndroidTabBarPage openShop() {
    	
    	final By categoryButtonLocator = By.className("android.widget.Button");
    	
    	if (appDriver.isDisplayed(categoryButtonLocator, 2)) {  		
    		final List<MobileElement> categoryButtons = appDriver.retrieveMobileElements(categoryButtonLocator);

    		//Find the common Y axis value for the category buttons (Women, Men wtc)
    		final List<Integer> yAxisPoints = categoryButtons
                .stream()
                .map(t -> t.getLocation().getY())
                .collect(Collectors.toList());

    		//Find all the category buttons with the same Y Axis value
    		//NB. Android 3.4.4 introduced a button with a 1 pixel difference in value to the other category buttons!
        
    		int categoryYAxis = yAxisPoints.stream().filter(point -> yAxisPoints.stream().filter(t -> t.equals(point)).count() >= 2).findFirst().orElseThrow(() -> new RiverIslandTestError("Unable to find unique category index, please debug. Was the app recently changed?"));
        
    		appDriver.tap(categoryButtons.stream().filter(t -> t.getLocation().getY() == categoryYAxis).findFirst().get());
    	}
    	else {
    		throw new RuntimeException("Failed to find category buttons");
    	}
    	return this;
    }

    @Override
    public AndroidTabBarPage openHome() {
    	
    	final String homeText = "Home"; 
        appDriver.scrollIntoView(homeText);
        MobileElement element = appDriver.retrieveMobileElement(homeText);
        appDriver.click(element);
        return this;
    }

    @Override
    public AndroidTabBarPage openWishlist() {
        appDriver.tap(androidId.apply("action_wishlist"));
        return this;
    }

    @Override
    public AndroidTabBarPage openShoppingBag() {
        appDriver.tap(androidId.apply("action_bag"));
        return this;
    }

	@Override
	public boolean isTabBarVisible() {
		return false;
	}

	@Override
	public boolean isAndroid() {
		return true;
	}
}