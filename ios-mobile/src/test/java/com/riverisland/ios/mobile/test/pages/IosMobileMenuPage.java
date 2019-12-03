package com.riverisland.ios.mobile.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MenuPage;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
@Deprecated
public class IosMobileMenuPage implements MenuPage<IosMobileMenuPage> {

    public IosMobileMenuPage(RiverIslandNativeAppDriver appDriver) {
    }

    @Override
    public IosMobileMenuPage openMenu() {
        return this;
    }
}
