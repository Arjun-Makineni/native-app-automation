package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.enums.FilterBy;
import com.riverisland.app.automation.enums.SortBy;
import com.riverisland.app.automation.enums.ToggleType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface ProductLandingPage<T> {
    T waitForLandingPageProducts();

    int getProductItemCount();

    T selectProduct(String productId);

    T selectProduct();

    Boolean selectProductWithSwatch();

    T toggleProductView(ToggleType toggleType);

    int getCountOfDisplayedProducts();

    List<BigDecimal> getProductPrices();

    String filterItem(FilterBy filter);

    List<String> filterItemWithMultipleOptions(FilterBy filter);

    T filterBySearch(String searchKeyword);

    T openFilter();

    T closeFilter();

    T clearSingleFilter(FilterBy filter);

    T clearAllFilters();

    Boolean isFilterApplied(String filterValue);

    Boolean isAllFiltersCleared();

    T sortBy(SortBy sortBy);

    T clearSorting();
    
    int numberOfSwatchesDisplayed();
    
    int excessSwatchValue();
}
