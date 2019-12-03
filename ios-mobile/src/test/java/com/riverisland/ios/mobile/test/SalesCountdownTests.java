package com.riverisland.ios.mobile.test;

import org.testng.annotations.Test;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.FemaleCategory;
import com.riverisland.app.automation.fixtures.SalesCountdownFixture;

/**
 * Created by Simon Johnson on 20/11/2018
 */
@SuppressWarnings("groupsTestNG")
public class SalesCountdownTests extends IosMobileTest {
	
	private SalesCountdownFixture salesCountdownFixture = new SalesCountdownFixture();

    @Test(description = "Sales Countdown on Home screen")
    public void salesCountdownScenario_01() {
    	appHelper.homepageWelcome();
    	salesCountdownFixture.verifyCountdownDisplayed();
    }
    
    @Test(description = "Verify Sales Countdown on PLP and PDP")
    public void salesCountdownScenario_02() {
        appHelper
                .openHomeTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.randomise().getName());
        salesCountdownFixture.verifyCountdownDisplayed();
        appHelper
                .selectProduct()
                .verifyLandingProductDetails();
        salesCountdownFixture.verifyCountdownDisplayed();
    }
}
