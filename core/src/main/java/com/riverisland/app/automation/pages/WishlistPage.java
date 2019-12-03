package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface WishlistPage<T> {
    Boolean isWishlistEmpty();

    String getWishlistQty();

    T addWishlistItemsToBag();

    T removeWishlistItem(String productName);

    T clearWishlist();

    T changeWishlistItemSize();

    Boolean isProductInWishlist(String productName);
}
