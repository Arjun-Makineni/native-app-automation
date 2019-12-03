package com.riverisland.android.test.pages;

import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MenuPage;
import org.openqa.selenium.By;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidMenuPage extends AndroidCorePage implements MenuPage<AndroidMenuPage> {

    public AndroidMenuPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidMenuPage openMenu() {
        appDriver
                .tap(By.xpath("//android.widget.LinearLayout/android.view.ViewGroup/android.widget.ImageButton"))
                .pause(500);
        return this;
    }
}