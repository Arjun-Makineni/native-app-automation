package com.riverisland.app.automation.fixtures;

import org.testng.SkipException;

import com.riverisland.app.automation.pojos.AppSession;

public abstract class AppFixture extends AppSession {
    public AppFixture goBack() {
        appPage.goBack();
        return this;
    }

    public AppFixture goBack(int times) {
        while (times-- > 0) {
            appPage.goBack();
        }
        return this;
    }
    
	protected void goHome() {	
		int attempts = 10;
		
		while (!homePage.isHomePage() && attempts-- > 0) {
			if (tabBarPage.isTabBarVisible()) {
				tabBarPage.openHome();
			} else {
				appPage.goBack();
			}
		}
		if (!homePage.isHomePage()) {
			throw new SkipException("Could not navigate to home ");
		}
 	}
}
