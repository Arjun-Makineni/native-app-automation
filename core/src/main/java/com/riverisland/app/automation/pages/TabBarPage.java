package com.riverisland.app.automation.pages;

import org.openqa.selenium.By;

import java.util.function.Function;

/**
 * Created by Prashant Ramcharan on 22/01/2018
 */
public interface TabBarPage<T> {
    T openMyRi();

    T openShop();

    T openHome();

    T openWishlist();

    T openShoppingBag();
    
    boolean isTabBarVisible();
    
    boolean isAndroid();

    Function<TabBar, By> iosTabLocator = (tab) -> {
        Function<Integer, By> locator = (index) -> By.xpath(String.format("//XCUIElementTypeTabBar/XCUIElementTypeButton[%s]", index));

        switch (tab) {
            case MY_RI:
                return locator.apply(1);

            case SHOP:
                return locator.apply(2);

            case HOME:
                return locator.apply(3);

            case WISHLIST:
                return locator.apply(4);

            case SHOPPING_BAG:
                return locator.apply(5);
        }
        return null;
    };

    enum TabBar {
        MY_RI,
        SHOP,
        HOME,
        WISHLIST,
        SHOPPING_BAG
    }
}
