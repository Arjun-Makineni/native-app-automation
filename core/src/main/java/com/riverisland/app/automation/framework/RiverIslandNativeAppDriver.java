package com.riverisland.app.automation.framework;

import com.riverisland.app.automation.config.AppiumConfig;
import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.DeviceType;
import com.riverisland.app.automation.enums.Platform;
import com.riverisland.app.automation.enums.Provider;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.enums.AutomatorPropertySelector;
import com.riverisland.app.automation.enums.TapDuration;
import com.riverisland.app.automation.pojos.AppiumServer;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class RiverIslandNativeAppDriver extends AppDriverFunction implements NativeAppDriver<RiverIslandNativeAppDriver, MobileElement> {
    private static RiverIslandNativeAppDriver instance;
    private static AppiumDriver driver;
    private static AppiumDriverLocalService driverLocalService;

    private AppiumConfig appiumConfig;
    private String sessionId;

    private RiverIslandNativeAppDriver(AppiumConfig appiumConfig, String sessionId) {
        this.appiumConfig = appiumConfig;
        this.sessionId = sessionId;
    }

    public static RiverIslandNativeAppDriver createAppiumDriver(AppiumConfig appiumConfig, Platform platform) {
        if (instance == null) {
            if (appiumConfig == null || platform == null) {
                throw new RuntimeException("AppiumConfig and Platform are required to create a new Appium driver!");
            }

            instance = new RiverIslandNativeAppDriver(appiumConfig, UUID.randomUUID().toString());

            final Provider provider = appiumConfig.getDeviceType() != DeviceType.REAL_DEVICE ? Provider.LOCAL : Provider.CLOUD;

            final AppiumServer appiumServer = GlobalConfig.instance().getAppiumServer().stream().filter(server -> server.getProvider() == provider).findFirst().get();

            URL appiumServerUrl = appiumServer.getUrl();

            if (appiumServer.getProvider().equals(Provider.LOCAL) && appiumServer.getAutoStart()) {
                try {
                    final AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder();
                    serviceBuilder.usingAnyFreePort();

                    driverLocalService = AppiumDriverLocalService.buildService(serviceBuilder);
                    driverLocalService.start();
                    appiumServerUrl = driverLocalService.getUrl();
                } catch (AppiumServerHasNotBeenStartedLocallyException e) {
                    throw new RuntimeException("Unable to build a new Appium driver service! Reason: " + e.getMessage());
                }
            }

            DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

            final Map<String, Object> capabilities = appiumConfig.getCapabilities();
            capabilities.keySet().forEach(cap -> desiredCapabilities.setCapability(cap, capabilities.get(cap)));

            if (appiumServer.getProvider().equals(Provider.LOCAL)) {
                desiredCapabilities.setCapability(MobileCapabilityType.APP, appiumConfig.getLocalAppName());
            }

            final String sessionName = String.format("RI Native %s App Session: %s", appiumConfig.getCapabilities().get(MobileCapabilityType.PLATFORM_NAME), instance.getSessionId());

            desiredCapabilities.setCapability("sessionName", sessionName);
            Logger.getLogger(RiverIslandNativeAppDriver.class).info(sessionName);

            switch (platform) {
                case ANDROID:
                    driver = new AndroidDriver(appiumServerUrl, desiredCapabilities);
                    break;

                case IOS:
                    driver = new IOSDriver(appiumServerUrl, desiredCapabilities);
                    break;
            }
        }
        return instance;
    }

    public int getDriverTimeout() {
        return DEFAULT_DRIVER_TIMEOUT;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void invalidate() {
        instance = null;
    }

    public AppiumConfig getAppiumConfig() {
        return appiumConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDriverCapability(String name) {
        if (driver != null && !driver.getCapabilities().asMap().isEmpty()) {
            return (T) driver.getCapabilities().getCapability(name);
        }
        return null;
    }

    public <T extends AppiumDriver> T getWrappedDriver() {
        return (T) driver;
    }

    public AndroidDriver getWrappedAndroidDriver() {
        if (driver instanceof AndroidDriver) {
            return getWrappedDriver();
        }
        throw new RuntimeException("This is not an Android driver!");
    }

    public IOSDriver getWrappedIOSDriver() {
        if (driver instanceof IOSDriver) {
            return getWrappedDriver();
        }
        throw new RuntimeException("This is not an IOS driver!");
    }

    public void shutdownAppiumServiceAndQuitDriver() {
        if (driver != null) {
            driver.quit();
        }

        if (driverLocalService != null) {
            driverLocalService.stop();
        }
    }

    @Override
    public String getPageSource() {
        try {
            if (driver != null) {
                return driver.getPageSource();
            }
        } catch (WebDriverException ignored) {
        }
        return null;
    }

    @Override
    public RiverIslandNativeAppDriver reLaunchApp() {
        driver.closeApp();
        driver.launchApp();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver click(By by) {
        retrieveMobileElement(by).click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver click(MobileElement mobileElement) {
        mobileElement.click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tap(By by) {
        tap(by, TapDuration.getShortTapDuration(driver));
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tap(MobileElement mobileElement) {
        waitFor(ExpectedConditions.elementToBeClickable(mobileElement));
        mobileElement.click();
        return this;
    }
    @Override
    public RiverIslandNativeAppDriver touch(MobileElement mobileElement) {
     	mobileElement.click();
    	return this;
    }
    
    @Override
    public RiverIslandNativeAppDriver touchAndType(String usingText, String value) {
    	waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//[@name='%s' or @value='%s']", usingText, usingText))), 2000);
    	MobileElement element = retrieveMobileElement(usingText);
    	touch(element);
    	type(element, value);
    	return this;
    }

    @Override
    public RiverIslandNativeAppDriver tap(String usingText) {
        MobileElement mobileElement = retrieveMobileElement(usingText);

        if (mobileElement == null) {
            throw new RuntimeException("Unable to find mobile element to tap using locator: " + usingText);
        }

        waitFor(ExpectedConditions.elementToBeClickable(mobileElement));
        mobileElement.click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tap(By... by) {
        retrieveMobileElement(by).click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tapPresent(By by) {
        retrievePresentMobileElement(by).click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver doubleTap(By by) {
        final MobileElement element = retrievePresentMobileElement(by);

        JavascriptExecutor js = driver;
        Map<String, Object> params = new HashMap<>();
        params.put("element", element.getId());
        js.executeScript("mobile: doubleTap", params);

        return this;
    }

    /**
     * Android Only
     */
    @Override
    public RiverIslandNativeAppDriver scrollBackwards() {
        try {
            getWrappedAndroidDriver().findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollBackward();");
        } catch (Exception ignored) {
        }
        return this;
    }

    /**
     * iOS only
     */
    @Override
    public RiverIslandNativeAppDriver scrollDown() {
        JavascriptExecutor js = driver;
        HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", "down");
        js.executeScript("mobile: scroll", scrollObject);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver scrollIntoViewAndTap(String usingText) {
        MobileElement mobileElement = scrollIntoView(usingText);

        waitFor(ExpectedConditions.elementToBeClickable(mobileElement));
        mobileElement.click();
        return this;
    }

    @Override
    public MobileElement scrollIntoView(String usingText) {
        return scrollIntoViewInternal(AutomatorPropertySelector.TEXT, usingText, driver instanceof AndroidDriver ? 60000 : 180000, true);
    }
    
    @Override
    public MobileElement scrollIntoView(AutomatorPropertySelector property, String value) {
    	return scrollIntoViewInternal(property, value, driver instanceof AndroidDriver ? 60000 : 180000, true);
    }

    @Override
    public void scrollIntoViewSilently(String usingText, long duration) {
        scrollIntoViewInternal(AutomatorPropertySelector.TEXT, usingText, duration, false);
    }
    private MobileElement scrollIntoViewInternal(AutomatorPropertySelector property, String usingText, long duration, boolean eagerlyScroll) {
        LocalDateTime expiry;

        boolean match = false;

        MobileElement mobileElement = null;

        if (driver instanceof AndroidDriver) {
            expiry = LocalDateTime.now().plus(duration, ChronoUnit.MILLIS);

            final String scrollablePath = String.format("new UiScrollable(new UiSelector()).scrollIntoView(new UiSelector().%s(\"%s\"));",property.getName(), usingText);

            while (!match && LocalDateTime.now().isBefore(expiry)) {
                try {
                    mobileElement = ((AndroidElement) getWrappedAndroidDriver().findElementByAndroidUIAutomator(scrollablePath));
                    match = mobileElement != null;
                } catch (NoSuchElementException ignored) {
                }
            }
        } else if (driver instanceof IOSDriver) {
            expiry = LocalDateTime.now().plus(duration, ChronoUnit.MILLIS);

            if (!isDisplayed(By.name(usingText))) {

                while (!match && LocalDateTime.now().isBefore(expiry)) {
                    try {
                        final HashMap<String, String> scrollObject = new HashMap<>();
                        scrollObject.put(property.getIosName(), usingText);
                        driver.executeScript("mobile: scroll", scrollObject);
                        match = true;
                    } catch (WebDriverException wde) {
                        if (!wde.getMessage().contains("Failed to perform scroll with visible cell due to max scroll count reached")) {
                            throw wde;
                        }
                    }
                }
            }
        }

        if (eagerlyScroll) {
            mobileElement = retrieveMobileElement(By.xpath(String.format(property.getLocator(driver), usingText)));

            if (mobileElement == null) {
                throw new RuntimeException("Unable to find mobile element to tap using locator: " + usingText);
            }
        }
        return mobileElement;
    }

    @Override
    public RiverIslandNativeAppDriver tap(By by, int tapDuration) {
        mobileElementFinder.apply(driver, ExpectedConditions.elementToBeClickable(by)).click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tap(ExpectedCondition<WebElement> condition) {
        mobileElementFinder.apply(driver, condition).click();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tapUntil(By by, Class<? extends RuntimeException> exceptionClass) {
        LocalDateTime exitTime = LocalDateTime.now().plusSeconds(DEFAULT_DRIVER_TIMEOUT);
        boolean exit = false;

        while (!exit && LocalDateTime.now().isBefore(exitTime)) {
            try {
                ((MobileElement) driver.findElement(by)).click();
                pause(1000);
            } catch (WebDriverException wde) {
                if (wde.getClass().equals(exceptionClass)) {
                    exit = true;
                }
            }
        }
        return this;
    }

    private MobileElement retrieveMobileElementInternal(AutomatorPropertySelector property, String usingTextOrId) {
        MobileElement mobileElement = null;

        int triesBeforeTimeout = DEFAULT_DRIVER_TIMEOUT;

        while (triesBeforeTimeout-- > 0) {
            try {
                if (driver instanceof AndroidDriver) {
                    final String androidUIAutomatorText = String.format("%s(\"%s\")", property.getAndroidName(), usingTextOrId);
                    mobileElement = ((AndroidElement) getWrappedAndroidDriver().findElementByAndroidUIAutomator(androidUIAutomatorText));
                } else if (driver instanceof IOSDriver) {
                	waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//*[@value='%s' or @id='%s']", usingTextOrId, usingTextOrId))), 2000);
                    mobileElement = ((IOSElement) getWrappedIOSDriver().findElementByAccessibilityId(usingTextOrId));
                }
                triesBeforeTimeout = 0;
            } catch (WebDriverException ignored) {
                pause(1000);
            }
        }
        if (mobileElement == null) {
            throw new RuntimeException("Unable to find mobile element with text or name matching: '" + usingTextOrId + "'");
        }
        return mobileElement;
    }

    @Override
    public MobileElement retrieveMobileElement(String usingTextOrId) {
        final MobileElement mobileElement = retrieveMobileElementInternal(AutomatorPropertySelector.TEXT, usingTextOrId);
        waitFor(ExpectedConditions.visibilityOf(mobileElement));
        return mobileElement;
    }

    @Override
    public MobileElement retrieveMobileElement(By by) {
        return mobileElementFinder.apply(driver, ExpectedConditions.visibilityOfElementLocated(by));
    }

    @Override
    public MobileElement retrieveMobileElement(By... by) {
        final MobileElement[] element = new MobileElement[1];

        Arrays.asList(by).forEach(locator -> {
            if (element[0] == null) {
                element[0] = retrievePresentMobileElement(locator);
            } else {
                element[0] = element[0].findElement(locator);
            }
        });
        return element[0];
    }

    @Override
    public List<MobileElement> retrieveMobileElements(By... by) {
        final List<MobileElement> elements = new ArrayList<>();

        Arrays.asList(by).forEach(locator -> {
            elements.clear();
            elements.addAll(retrievePresentMobileElements(locator));
        });

        return elements;
    }

    @Override
    public MobileElement retrievePresentMobileElement(By by) {
        return retrieveMobileElements(by, ExpectedConditions.presenceOfAllElementsLocatedBy(by)).stream().findFirst().orElse(null);
    }

    @Override
    public List<MobileElement> retrieveMobileElements(By by) {
        return mobileElementsFinder.apply(driver, ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    @Override
    public List<MobileElement> retrievePresentMobileElements(By by) {
        return retrieveMobileElements(by, ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    @Override
    public List<MobileElement> retrieveMobileElements(By by, ExpectedCondition condition) {
        return mobileElementsFinder.apply(driver, condition);
    }

    @Override
    public List<MobileElement> filterElements(By by, Predicate<MobileElement> filter) {
        return retrievePresentMobileElements(by).stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public List<MobileElement> filterPresentElements(By by, Predicate<MobileElement> filter) {
        return retrievePresentMobileElements(by).stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public RiverIslandNativeAppDriver clear(By by) {
        final MobileElement element = retrieveMobileElement(by);
        try {
            element.clear();
        } catch (WebDriverException ignored) {
        }
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver type(By by, String input) {
        retrieveMobileElement(by).sendKeys(input);
        hideKeyboard();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver typeAndPressKey(String usingText, String input, Keys... keys) {
        retrieveMobileElement(usingText).sendKeys(input);
        Arrays.asList(keys).forEach(key -> {
            driver.getKeyboard().pressKey(key);
            pause(250);
        });
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver setValue(By by, String input) {
        final MobileElement mobileElement = retrieveMobileElement(by);
        mobileElement.clear();
        mobileElement.setValue(input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver type(String usingText, String input) {
        retrieveMobileElement(usingText).sendKeys(input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver typePresent(String usingText, String input) {
        retrieveMobileElementInternal(AutomatorPropertySelector.TEXT, usingText).sendKeys(input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver type(MobileElement mobileElement, String input) {
        if (StringUtils.isNotBlank(mobileElement.getText())) {
            mobileElement.clear();
        }
        mobileElement.sendKeys(input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver keyboardType(String input) {
        driver.getKeyboard().sendKeys(input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver keyboardPress(Keys key) {
        driver.getKeyboard().pressKey(key);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver pressAndroidKey(int keyCode) {
        getWrappedAndroidDriver().pressKeyCode(keyCode);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tapAndType(MobileElement mobileElement, By by, String input) {
        tap(mobileElement).type(by, input);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver tapAndType(MobileElement mobileElement, MobileElement typeElement, String input) {
        tap(mobileElement).type(typeElement, input);
        return this;
    }

    private RiverIslandNativeAppDriver swipe(By by, int offset) {   	
    	TouchAction touch = new TouchAction(driver);
    	MobileElement element = retrieveMobileElement(by);
    	Point from = element.getCenter();
    	touch.longPress(PointOption.point(from.getX(), from.getY()))
    	     .moveTo(PointOption.point(from.getX() + offset, from.getY()))
    	     .release()
    	     .perform();
    	return this;
    }
    
    @Override
    public RiverIslandNativeAppDriver swipeElementToElementLocation(By fromElementLocator, By toElementLocator) {   	
    	swipeElementToElementLocation(retrieveMobileElement(fromElementLocator).getCenter(), retrieveMobileElement(toElementLocator).getCenter());
    	return this;
    }
    
    @Override 
    public RiverIslandNativeAppDriver swipeElementToElementLocation(Point from, Point to) {
    	TouchAction touch = new TouchAction(driver);
    	
    	touch.longPress(PointOption.point(from.getX(), from.getY()))
   	 		 .moveTo(PointOption.point(to.getX(), to.getY()))
   	 		 .release()
   	 		 .perform();    	
	
    	return this;	
    }
    @Override
    public RiverIslandNativeAppDriver swipeLeft(By by) {   	
    	return swipe(by, -50);
    }

    @Override
    public RiverIslandNativeAppDriver swipeRight(By by) {
        return swipe(by, 50);
    }

    @Override
    public String retrieveMobileElementText(By by) {
        return retrieveMobileElement(by).getText();
    }

    @Override
    public String retrieveMobileElementAttribute(By by, String attributeName) {
        return retrieveMobileElement(by).getAttribute(attributeName);
    }

    @Override
    public RiverIslandNativeAppDriver back() {
        driver.navigate().back();
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver pause(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver waitFor(ExpectedCondition condition) {
        wait(driver, condition, DEFAULT_DRIVER_TIMEOUT * 1000);
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver waitFor(ExpectedCondition condition, long timeout) {
        wait(driver, condition, timeout);
        return this;
    }
    
    @Override
    public boolean waitToExist(ExpectedCondition condition, long timeout) {
    	return wait(driver, condition, timeout);
    }

    @Override
    public RiverIslandNativeAppDriver takeScreenshot(String screenshotName) {
        final File screenshot = driver.getScreenshotAs(OutputType.FILE);

        try {
            Files.copy(screenshot.toPath(), Paths.get(screenshotName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private Boolean checkDisplayedCondition(By locator, Integer timeoutSec) {
        while (timeoutSec-- > 0) {
            try {
                WebElement element = driver.findElement(locator);
                if (null != element) {
                	return element.isDisplayed();
                }
            } catch (WebDriverException ignored) {
                pause(1000);
            }
        }
        return false;
    }

    private Boolean checkEnabledCondition(By locator, Integer timeoutSec) {
        while (timeoutSec-- > 0) {
            try {
                return driver.findElement(locator).isEnabled();
            } catch (WebDriverException ignored) {
                pause(1000);
            }
        }
        return false;
    }

    @Override
    public boolean isDisplayed(By locator) {
        return checkDisplayedCondition(locator, 1);
    }

    @Override
    public boolean isEnabled(By locator) {
        return checkEnabledCondition(locator, 1);
    }

    @Override
    public boolean isDisplayed(By locator, Integer timeoutSec) {
        return checkDisplayedCondition(locator, timeoutSec);
    }

    @Override
    public RiverIslandNativeAppDriver acceptAlert() {
        try {
            driver.switchTo().alert().accept();
        } catch (WebDriverException ignored) {
        }
        return this;
    }

    @Override
    public RiverIslandNativeAppDriver dismissAlert() {
        try {
            driver.switchTo().alert().dismiss();
        } catch (WebDriverException ignored) {
        }
        return this;
    }

    @Override
    public String getAlertText() {
        try {
            return driver.switchTo().alert().getText();
        } catch (WebDriverException ignored) {
        }
        return null;
    }

    /**
     * iOS only
     */
    @Override
    public RiverIslandNativeAppDriver scrollPickerWheelToExpectedValue(By by, Integer availableOptions, Predicate<MobileElement> condition) {
        final MobileElement element = retrieveMobileElement(by);

        Boolean exit = condition.test(retrieveMobileElement(by));

        while (!exit && availableOptions-- > 0) {
            try {
                JavascriptExecutor js = driver;
                Map<String, Object> params = new HashMap<>();
                params.put("order", "next");
                params.put("offset", 0.10);
                params.put("element", element.getId());
                js.executeScript("mobile: selectPickerWheelValue", params);
            } catch (WebDriverException ignored) {
            }
            exit = condition.test(retrieveMobileElement(by));
        }

        if (!exit) {
            return null;
        }
        return this;
    }

    /**
     * Use cautiously as this might scroll down passed the desired locator
     */
    @Override
    public RiverIslandNativeAppDriver scrollDownUntilInView(By locator) {
        int maxScrolls = 2;

        while (maxScrolls-- > 0 && !isDisplayed(locator)) {
            scrollDown();
        }

        return this;
    }

    @Override
    @Deprecated
    public RiverIslandNativeAppDriver swipe(SwipeElementDirection direction) {
        final HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", direction.name().toLowerCase());
        driver.executeScript("mobile: swipe", scrollObject);
        return this;
    }

    @Override
    @Deprecated
    public RiverIslandNativeAppDriver swipe(SwipeElementDirection direction, MobileElement mobileElement) {
        final HashMap<String, String> scrollObject = new HashMap<>();
        scrollObject.put("direction", direction.name().toLowerCase());
        scrollObject.put("element", mobileElement.getId());
        driver.executeScript("mobile: swipe", scrollObject);
        return this;
    }
    
    @Override
    public RiverIslandNativeAppDriver swipe(Point point, int xOffset, int yOffset) {
		
		if (null != point) {
			TouchAction touchAction = new TouchAction(driver);
			
			touchAction
				.press(PointOption.point(point.x, point.y))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
				.moveTo(PointOption.point(xOffset < point.x ? point.x + xOffset : 0, yOffset < point.y ? point.y + yOffset : 0))
				.release()
				.perform();
		}    	
    	return this;
	}

    @Override
    public RiverIslandNativeAppDriver hideKeyboard() {
        try {
            driver.hideKeyboard();
        } catch (WebDriverException ignored) {
        }
        return this;
    }
}