package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface MyBagPage<T> {
    T closeBag();

    int getTotalBagQty();

    T proceedToCheckout();

    T increaseBagQty(int index);

    T decreaseBagQty(int index);

    Boolean isBagEmpty();

    Boolean hasNoBagItems();

    T editBag();

    T clearAll();

    T removeBagItem(String productName);

    T moveAllBagItemsToWishlist();

    T moveBagItemToWishlist(String productName);

    T signIn();

    T startShopping();

    Boolean isPreviousSessionBagMessageDisplayed(int items);

	T verifyProgressBar(boolean isDisplayed);
}
