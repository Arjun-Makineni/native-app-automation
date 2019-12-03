package com.riverisland.app.automation.test;

import com.riverisland.app.automation.config.AppiumConfig;
import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.pojos.Environment;
import com.riverisland.app.automation.pojos.RiverIslandUserCredentials;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.utils.YamlLoaderUtils;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;
import io.restassured.builder.RequestSpecBuilder;
import org.assertj.core.util.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.given;

/**
 * Created by Prashant Ramcharan on 12/06/2017
 */
public abstract class RiverIslandAppTest extends AppSession {
    protected static RiverIslandNativeAppDriver appDriver;
    protected static RiverIslandUserCredentials existingRiverIslandUser;
    protected static Environment environment;
    protected static TcplApiCredentials tcplApiCredentials;

    private final String kobitonApiEndpoint = "https://api.kobiton.com/v1";
    private int kobitonSessionId;
    private boolean kobitonDeviceBooked;

    protected AppiumConfig appiumConfig;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteCore(ITestContext context) {
        for (ITestNGMethod method : context.getAllTestMethods()) {
            method.setRetryAnalyzer(new RiverIslandRetryAnalyzer(GlobalConfig.instance().getRetryTestFailureLimit()));
        }
    }

    protected void createAppDriver(Platform platform, String config) {
        if (appDriver == null) {
            RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Creating a new RiverIsland native app driver");

            this.appiumConfig =
                    (YamlLoaderUtils.loadYamlDataCollection(config, AppiumConfig.class))
                            .stream()
                            .filter(GlobalConfig.instance().getProvider().equals(Provider.LOCAL) ?
                                    t -> t.getDeviceType().equals(DeviceType.EMULATOR) || t.getDeviceType().equals(DeviceType.SIMULATOR) :
                                    t -> t.getDeviceType().equals(DeviceType.REAL_DEVICE))
                            .findFirst().get();

            if (GlobalConfig.isAppRunningOnRealDevice()) {
                final int deviceId = (int) appiumConfig.getCapabilities().getOrDefault("deviceId", 0);

                if (deviceId > 0) {
                    waitForAvailableKobitonDevice(deviceId);
                }
            }

            try {
                appDriver = RiverIslandNativeAppDriver.createAppiumDriver(this.appiumConfig, platform);

                if (appDriver == null) {
                    throw new RiverIslandTestError("Appium driver not created. Refer to console logs");
                }

                if (GlobalConfig.isAppRunningOnRealDevice()) {
                    Object sessionId = appDriver.getDriverCapability("kobitonSessionId");

                    if (sessionId != null) {
                        kobitonSessionId = ((Long) sessionId).intValue();
                        RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Kobiton session created: " + kobitonSessionId);
                    } else {
                        throw new SkipException("The device is not available for testing or there is an issue with the cloud provider platform!");
                    }
                }
            } catch (WebDriverException ex) {
                if (ex instanceof SessionNotCreatedException) {
                    throw new SkipException("The device is not available for testing. Reason: " + ex.getMessage());
                }
                throw new SkipException(ex.getMessage());
            }
        }
    }

    protected abstract void createAppPages();

    protected abstract void createAppHelpers();

    protected abstract void createWebHelpers(String serviceUrl);

    @DataProvider(name = "region-and-card")
    protected static Object[][] regionAndCard() {
        return new Object[][]{
                {Region.GB, CardType.VISA},
                {Region.AU, CardType.MASTERCARD},
                {Region.SE, CardType.VISA},
                {Region.US, CardType.AMEX},
                {Region.NL, CardType.MAESTRO}
        };
    }

    @DataProvider(name = "region-and-third-party-payment")
    protected static Object[][] regionAndThirdPartyPayment() {
        return new Object[][]{
                {Region.GB, PaymentMethod.PAYPAL},
                {Region.NL, PaymentMethod.IDEAL},
                {Region.DE, PaymentMethod.GIROPAY}
        };
    }

    @DataProvider(name = "categories")
    protected static Object[][] categories() {
        return new Object[][]{
                {Category.WOMEN, FemaleCategory.randomise().getName()},
                {Category.MEN, MaleCategory.randomise().getName()},
                {Category.GIRLS, FemaleCategory.randomise().getName()},
                {Category.BOYS, MaleCategory.randomise().getName()},
        };
    }

    @DataProvider(name = "newsletter-categories")
    protected static Object[][] newsLetterCategories() {
        return new Object[][]{
                {Category.WOMEN, null},
                {Category.MEN, null},
                {Category.KIDS, null},
        };
    }

    @DataProvider(name = "sorting")
    protected static Object[][] sorting() {
        return new Object[][]{
                {SortBy.RELEVANCE, null},
                {SortBy.LATEST, null},
                {SortBy.PRICE_LOW_TO_HIGH, null},
                {SortBy.PRICE_HIGH_TO_LOW, null},
        };
    }

    protected boolean performTestCleanup(ITestResult result) {
        if (!result.isSuccess()) {
            try {
                final File screenshotDir = new File("screenshots");

                if (!screenshotDir.exists()) {
                    screenshotDir.mkdir();
                }

                // no point continuing - the remote driver is dead or the device screen is locked!
                if (appDriver != null && (appDriver.getPageSource() == null || appDriver.isDisplayed(By.name("SBCoverSheetWindow")))) {
                    RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("The remote driver is dead or the remote device screen is locked!");
                    return false;
                }

                final String screenshotName = String.format("%s/%s_%s_%s.png", screenshotDir, result.getName(), result.getEndMillis(), appDriver.getSessionId());
                RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Capturing screenshot -> " + screenshotName);

                appDriver.takeScreenshot(screenshotName);
            } catch (WebDriverException wde) {
                RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Unable to capture screenshot -> Error: " + wde.getMessage());
                return false;
            }
        }

        if (AppSession.webDriver != null) {
            AppSession.webDriver.quit();
        }
        return true;
    }

    protected void performKobitonSessionCleanup(WebDriverException wde) {
        final List<String> knownErrors = Lists.newArrayList("Not Found", "502 Bad Gateway", "An unknown server-side error occurred");

        AtomicBoolean hasError = new AtomicBoolean();

        knownErrors.forEach(error -> {
            if (hasError.get()) {
                return;
            }
            hasError.set(wde.getMessage().contains(error));
        });

        if (GlobalConfig.isAppRunningOnRealDevice() && hasError.get()) {
            if (appDriver != null) {
                if (kobitonSessionId > 0) {
                    boolean terminated = given(new RequestSpecBuilder()
                            .addHeader("Authorization", "Basic " + GlobalConfig.getApiAuthForCloudProvider())
                            .build())
                            .delete(String.format("%s/sessions/%s/terminate", kobitonApiEndpoint, kobitonSessionId)).getBody().asString().contains("OK");

                    if (terminated) {
                        kobitonDeviceBooked = false;
                        RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Kobiton session '%s' is now terminated!");
                    } else {
                        appDriver.getWrappedDriver().quit();
                        RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("A driver quit signal was sent to Kobiton!");
                    }
                    appDriver.invalidate();
                }
            }
        }
    }

    private void waitForAvailableKobitonDevice(int deviceId) {
        if (!kobitonDeviceBooked) {
            RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Checking if Kobiton device is available for testing - will wait up to 5 minutes.");

            boolean isBooked = true;

            final Date timeout = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));

            while (Date.from(Instant.now()).before(timeout) && isBooked) {
                isBooked = given(new RequestSpecBuilder()
                        .addHeader("Authorization", "Basic " + GlobalConfig.getApiAuthForCloudProvider())
                        .build())
                        .get(String.format("%s/devices/%s/status", kobitonApiEndpoint, deviceId)).getBody().jsonPath().get("isBooked");

                if (isBooked) {
                    RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Kobiton device is not available for testing, will check again in 5 seconds.");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            if (isBooked) {
                throw new RiverIslandTestError(String.format("Waited for 5 minutes for Kobiton device %s to become available but never did!", deviceId));
            }

            RiverIslandLogger.getInfoLogger(RiverIslandAppTest.class).info("Kobiton device is available, booking it for testing.");
            kobitonDeviceBooked = true;
        }
    }
}
