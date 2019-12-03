package com.riverisland.app.automation.framework;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.SystemClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public abstract class AppDriverFunction {
    protected static final int DEFAULT_DRIVER_TIMEOUT = 45;

    protected BiFunction<AppiumDriver, ExpectedCondition<? extends WebElement>, MobileElement> mobileElementFinder =
            ((driver, condition) -> (MobileElement) new FluentWait<>(driver)
                    .ignoreAll(Collections.singleton(WebDriverException.class))
                    .withTimeout(DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS)
                    .until(condition));

    protected BiFunction<AppiumDriver, ExpectedCondition<List<WebElement>>, List<MobileElement>> mobileElementsFinder =
            ((AppiumDriver driver, ExpectedCondition<List<WebElement>> condition) -> {
                List<WebElement> elements =
                        new FluentWait<>(driver)
                                .ignoreAll(Collections.singleton(WebDriverException.class))
                                .withTimeout(DEFAULT_DRIVER_TIMEOUT, TimeUnit.SECONDS)
                                .until(condition);
                final List<MobileElement> mobileElements = new ArrayList<>();
                elements.forEach(element -> mobileElements.add((MobileElement) element));
                return mobileElements;
            });

    protected Boolean wait(WebDriver driver, ExpectedCondition<?> condition, long timeout) {
        final Clock clock = new SystemClock();
        final long end = clock.laterBy(timeout);

        Object result = null;
        do {
            try {
                result = new FluentWait<>(driver)
                        .withTimeout(timeout, TimeUnit.MILLISECONDS)
                        .ignoreAll(Collections.singleton(WebDriverException.class))
                        .until(condition);
            } catch (WebDriverException ignored) {
            }
        }
        while (result == null && clock.isNowBefore(end));
        return result != null;
    }
}
