package com.riverisland.android.test;

import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.riverisland.android.test.pages.AndroidAppPage;
import com.riverisland.android.test.pages.AndroidCheckoutPage;
import com.riverisland.android.test.pages.AndroidGiftCardPage;
import com.riverisland.android.test.pages.AndroidHomePage;
import com.riverisland.android.test.pages.AndroidJustForYouPage;
import com.riverisland.android.test.pages.AndroidMenuPage;
import com.riverisland.android.test.pages.AndroidMyBagPage;
import com.riverisland.android.test.pages.AndroidMyRiverIslandPage;
import com.riverisland.android.test.pages.AndroidOnboardingPage;
import com.riverisland.android.test.pages.AndroidProductDetailsPage;
import com.riverisland.android.test.pages.AndroidProductLandingPage;
import com.riverisland.android.test.pages.AndroidRecentlyViewedPage;
import com.riverisland.android.test.pages.AndroidSalesCountdownPage;
import com.riverisland.android.test.pages.AndroidScanInStorePage;
import com.riverisland.android.test.pages.AndroidSearchPage;
import com.riverisland.android.test.pages.AndroidShopPage;
import com.riverisland.android.test.pages.AndroidStickySummaryPage;
import com.riverisland.android.test.pages.AndroidStoreLocatorPage;
import com.riverisland.android.test.pages.AndroidTabBarPage;
import com.riverisland.android.test.pages.AndroidWishlistPage;
import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Platform;
import com.riverisland.app.automation.helpers.AggregatedAppHelper;
import com.riverisland.app.automation.helpers.AppHelper;
import com.riverisland.app.automation.helpers.WebHelper;
import com.riverisland.app.automation.test.RiverIslandAppTest;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public abstract class AndroidTest extends RiverIslandAppTest {
    AppHelper appHelper;
    WebHelper webHelper;
    AggregatedAppHelper aggregatedAppHelper;

    private final String CONFIG = "android-config.yml";

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        existingRiverIslandUser = GlobalConfig.instance().getRiverIslandUserCredentials();
        environment = GlobalConfig.instance().getEnvironment();
        tcplApiCredentials = GlobalConfig.instance().getTcplApiCredentials().stream().filter(t -> t.getEnvironment().equalsIgnoreCase(environment.getName())).findFirst().orElse(null);

        createAppDriver(Platform.ANDROID, CONFIG);

        createAppPages();
        createAppHelpers();
        createWebHelpers(tcplApiCredentials.getServiceUrl());

        if (GlobalConfig.isAppRunningLocally()) {
        	onboardingPage.skipOnboardingPage();
        	
            onboardingPage.acceptPushNotifications(true);

            if (GlobalConfig.instance().getEnvironment().forceChange()) {
                menuPage.openMenu();
                searchPage.changeEnvironment(environment.getName(), true, onboardingPage);
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTestMethod() {
        createAppDriver(Platform.ANDROID, CONFIG);
        createAppPages();
        createAppHelpers();
        createWebHelpers(tcplApiCredentials.getServiceUrl());

        if (GlobalConfig.isAppRunningOnRealDevice()) {
            onboardingPage.acceptPushNotificationsAndSkip(false);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterTestMethod(ITestResult result) {
        boolean canRelaunchApp = false;

        try {
            canRelaunchApp = performTestCleanup(result);
        } finally {
            try {
                if (canRelaunchApp) {
                    appDriver.reLaunchApp();
                } else {
                    performKobitonSessionCleanup(new WebDriverException("Not Found"));
                }
            } catch (WebDriverException wde) {
                performKobitonSessionCleanup(wde);
            }
        }
        if (appHelper != null) {
            appHelper.reset();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (appDriver != null) {
            appDriver.shutdownAppiumServiceAndQuitDriver();
        }
        if (appHelper != null) {
            appHelper.resetAll();
        }
    }

    @Override
    protected void createAppPages() {
        appPage = new AndroidAppPage(appDriver);
        menuPage = new AndroidMenuPage(appDriver);
        tabBarPage = new AndroidTabBarPage(appDriver);
        onboardingPage = new AndroidOnboardingPage(appDriver);
        shopPage = new AndroidShopPage(appDriver);
        myRiverIslandPage = new AndroidMyRiverIslandPage(appDriver);
        productDetailsPage = new AndroidProductDetailsPage(appDriver);
        productLandingPage = new AndroidProductLandingPage(appDriver);
        myBagPage = new AndroidMyBagPage(appDriver);
        searchPage = new AndroidSearchPage(appDriver);
        checkoutPage = new AndroidCheckoutPage(appDriver);
        wishlistPage = new AndroidWishlistPage(appDriver);
        storeLocatorPage = new AndroidStoreLocatorPage(appDriver);
        recentlyViewedPage = new AndroidRecentlyViewedPage(appDriver);
        scanInStorePage = new AndroidScanInStorePage(appDriver);
        homePage = new AndroidHomePage(appDriver);
        giftCardPage = new AndroidGiftCardPage(appDriver);
        salesCountdownPage = new AndroidSalesCountdownPage(appDriver);
        justForYouPage = new AndroidJustForYouPage(appDriver);
        stickySummaryPage = new AndroidStickySummaryPage(appDriver);
    }

    @Override
    protected void createAppHelpers() {
        appHelper = new AppHelper();
        aggregatedAppHelper = new AggregatedAppHelper();
    }

    @Override
    protected void createWebHelpers(String serviceUrl) {
        webHelper = new WebHelper(serviceUrl);
    }
}