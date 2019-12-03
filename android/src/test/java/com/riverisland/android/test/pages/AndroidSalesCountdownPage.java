package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.SalesCountdownPage;

public class AndroidSalesCountdownPage extends AndroidCorePage implements SalesCountdownPage<AndroidSalesCountdownPage>{

	public AndroidSalesCountdownPage(RiverIslandNativeAppDriver appDriver) {
		super(appDriver);
	}

	@Override
	public boolean isCountdownDisplayed() {
		return appDriver.isDisplayed(androidId.apply("sale_countdown"), 2);
	}
}
