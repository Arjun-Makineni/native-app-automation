package com.riverisland.app.automation.pages;

import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

import java.util.List;

/**
 * Created by Prashant Ramcharan on 25/01/2018
 */
public interface ShopPage<T> {
    T openCategory(String... categories);

    T search(String criteria, boolean selectSearchResult);

    Product searchLazilyForProduct(List<Product> productList);

    T selectSearchResult();

    T searchCategoryAndSubCategory(String category, String subCategory);

    T openStoreLocator();

    T openScanInStore();
    
    boolean isShopPage();
}
