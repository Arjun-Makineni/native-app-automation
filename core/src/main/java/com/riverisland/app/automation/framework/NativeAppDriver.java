package com.riverisland.app.automation.framework;

import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.riverisland.app.automation.enums.AutomatorPropertySelector;
import com.riverisland.app.automation.enums.SwipeElementDirection;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface NativeAppDriver<T, E extends MobileElement> {
    String getPageSource();

    T reLaunchApp();

    T click(By by);

    T click(E mobileElement);

    T tap(By by);

    T tap(E mobileElement);
    
    T touch(E mobileElement);
    
    T touchAndType(String usingText, String value);

    T tap(String usingText);

    T tap(By... by);

    T tapPresent(By by);

    T doubleTap(By by);

    T scrollBackwards();

    T scrollDown();

    T scrollIntoViewAndTap(String usingText);

    E scrollIntoView(String usingText);
    
    E scrollIntoView(AutomatorPropertySelector property, String value);

    void scrollIntoViewSilently(String usingText, long duration);

    T tap(By by, int tapDuration);

    T tap(ExpectedCondition<WebElement> condition);

    T tapUntil(By by, Class<? extends RuntimeException> exception);

    E retrieveMobileElement(String usingText);

    E retrieveMobileElement(By by);

    E retrieveMobileElement(By... by);

    List<E> retrieveMobileElements(By... by);

    E retrievePresentMobileElement(By by);

    List<E> retrieveMobileElements(By by);

    List<E> retrievePresentMobileElements(By by);

    List<E> retrieveMobileElements(By by, ExpectedCondition condition);

    List<E> filterElements(By by, Predicate<E> filter);

    List<E> filterPresentElements(By by, Predicate<E> filter);

    T clear(By by);

    T type(By by, String input);

    T typeAndPressKey(String usingText, String input, Keys... keys);

    T setValue(By by, String input);

    T type(String usingText, String input);

    T typePresent(String usingText, String input);

    T type(E mobileElement, String input);

    T keyboardType(String input);

    T keyboardPress(Keys key);

    T pressAndroidKey(int keyCode);

    T tapAndType(E mobileElement, By by, String input);

    T tapAndType(E mobileElement, E typeElement, String input);

    T swipeLeft(By by);

    T swipeRight(By by);

    String retrieveMobileElementText(By by);

    String retrieveMobileElementAttribute(By by, String attributeName);

    T back();

    T pause(long time);

    T waitFor(ExpectedCondition condition);

    T waitFor(ExpectedCondition condition, long timeout);
    
    boolean waitToExist(ExpectedCondition condition, long timeout);

    T takeScreenshot(String path);

    boolean isDisplayed(By locator);

    boolean isEnabled(By locator);

    boolean isDisplayed(By locator, Integer timeoutSec);

    T acceptAlert();

    T dismissAlert();

    String getAlertText();

    T scrollPickerWheelToExpectedValue(By by, Integer availableOptions, Predicate<E> condition);

    T scrollDownUntilInView(By locator);

    T swipe(SwipeElementDirection direction);

    T swipe(SwipeElementDirection direction, E mobileElement);
    
    T swipe(Point point, int xOffSet, int yOffSet);
    
    T swipeElementToElementLocation(By fromElementLocator, By toElementLocator);
    
    T swipeElementToElementLocation(Point from, Point to);

    T hideKeyboard();
}