package com.riverisland.android.test;

import org.testng.annotations.Test;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.FemaleCategory;
import com.riverisland.app.automation.fixtures.SalesCountdownFixture;

@SuppressWarnings("groupsTestNG")
public class SalesCountdownTests extends AndroidTest {
	private SalesCountdownFixture salesCountdownFixture = new SalesCountdownFixture();

    @Test(description = "Sales Countdown on Home screen")
    public void salesCountdownScenario_01() {
    	salesCountdownFixture.verifyCountdownDisplayed();
    }
    
    @Test(description = "Verify Sales Countdown on PLP and PDP")
    public void salesCountdownScenario_02() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.randomise().getName());
        salesCountdownFixture.verifyCountdownDisplayed();
        appHelper
                .selectProduct()
                .verifyLandingProductDetails();
        salesCountdownFixture.verifyCountdownDisplayed();
    }

}
