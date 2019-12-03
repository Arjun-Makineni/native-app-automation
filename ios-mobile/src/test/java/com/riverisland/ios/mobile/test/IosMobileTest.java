package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Platform;
import com.riverisland.app.automation.helpers.AggregatedAppHelper;
import com.riverisland.app.automation.helpers.AppHelper;
import com.riverisland.app.automation.helpers.WebHelper;
import com.riverisland.app.automation.test.RiverIslandAppTest;
import com.riverisland.ios.mobile.test.pages.*;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileTest extends RiverIslandAppTest {
    AppHelper appHelper;
    WebHelper webHelper;
    AggregatedAppHelper aggregatedAppHelper;

    private final String CONFIG = "ios-mobile-config.yml";

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        existingRiverIslandUser = GlobalConfig.instance().getRiverIslandUserCredentials();
        environment = GlobalConfig.instance().getEnvironment();
        tcplApiCredentials = GlobalConfig.instance().getTcplApiCredentials().stream().filter(t -> t.getEnvironment().equalsIgnoreCase(environment.getName())).findFirst().orElse(null);

        createAppDriver(Platform.IOS, CONFIG);

        createAppPages();
        createAppHelpers();
        createWebHelpers(tcplApiCredentials.getServiceUrl());

        onboardingPage.acceptPushNotificationsAndSkip(true);
        if (GlobalConfig.isAppRunningLocally()) {

            if (GlobalConfig.instance().getEnvironment().forceChange()) {
                tabBarPage.openShop();
                searchPage.changeEnvironment(environment.getName(), false, onboardingPage);
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTestMethod() {
        createAppDriver(Platform.IOS, CONFIG);
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
        appPage = new IosMobileAppPage(appDriver);
        tabBarPage = new IosMobileTabBarPage(appDriver);
        menuPage = new IosMobileMenuPage(appDriver);
        searchPage = new IosMobileSearchPage(appDriver);
        productDetailsPage = new IosMobileProductDetailsPage(appDriver);
        productLandingPage = new IosMobileProductLandingPage(appDriver);
        myBagPage = new IosMobileMyBagPage(appDriver);
        myRiverIslandPage = new IosMobileMyRiverIslandPage(appDriver);
        checkoutPage = new IosMobileCheckoutPage(appDriver);
        storeLocatorPage = new IosMobileStoreLocatorPage(appDriver);
        recentlyViewedPage = new IosMobileRecentlyViewedPage(appDriver);
        wishlistPage = new IosMobileWishlistPage(appDriver);
        scanInStorePage = new IosMobileScanInStorePage(appDriver);
        shopPage = new IosMobileShopPage(appDriver);
        onboardingPage = new IosMobileOnboardingPage(appDriver);
        homePage = new IosMobileHomePage(appDriver);
        giftCardPage = new IosMobileGiftCardPage(appDriver);
        stickySummaryPage = new IosMobileStickySummaryPage(appDriver, (IosMobileCheckoutPage)checkoutPage);
        salesCountdownPage = new IosMobileSalesCountdownPage(appDriver);
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