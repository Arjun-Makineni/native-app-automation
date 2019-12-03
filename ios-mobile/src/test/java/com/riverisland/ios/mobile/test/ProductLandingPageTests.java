package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.testng.annotations.Test;


/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class ProductLandingPageTests extends IosMobileTest {

    @Test(description = "Toggle product listing view - Big Grid")
    public void plpScenario_01() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.getName(), FemaleCategory.DRESSES.getName());

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_BIG)
                .verifyDisplayedProductsEqualsOrExceedCount(numberOfProducts);
    }

    @Test(description = "Toggle product listing view - Small Grid", groups = "smoke")
    public void plpScenario_02() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.getName(), MaleCategory.SHIRTS.getName())
                .toggleProductView(ToggleType.GRID_BIG);

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_SMALL)
                .verifyDisplayedProductsEqualsOrExceedCount(numberOfProducts);
    }

    @Test(description = "Toggle product listing view - List Grid")
    public void plpScenario_03() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.BOYS.getName(), MaleCategory.SHORTS.getName())
                .toggleProductView(ToggleType.GRID_BIG);

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_LIST)
                .verifyDisplayedProductsEqualsOrLessThanCount(numberOfProducts);
    }

    @Test(description = "Filter products - Single filter", groups = "smoke")
    public void plpScenario_04() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.getName(), FemaleCategory.DRESSES.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.CATEGORIES)
                .closeFilter()
                .verifyProductItemCountIsLessThan(itemCountBeforeFiltering);
    }

    @Test(description = "Filter products - Single filter - Multiple Options")
    public void plpScenario_05() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.getName(), FemaleCategory.DRESSES.getName())
                .applyFilterAndSelectMultipleOptionsAndVerify(FilterBy.COLOUR);
    }

    @Test(description = "Filter products - Multiple filters")
    public void plpScenario_06() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.getName(), FemaleCategory.DRESSES.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.SIZE, FilterBy.COLOUR, FilterBy.PRICE)
                .closeFilter()
                .verifyProductItemCountIsLessThan(itemCountBeforeFiltering);
    }

    @Test(description = "Clear single product filter")
    public void plpScenario_07() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.getName(), MaleCategory.SHIRTS.getName())
                .waitForProductsToLoad()
                .applyProductFilterAndVerify(FilterBy.SIZE)
                .closeFilter()
                .clearSingleFilter(FilterBy.SIZE)
                .verifyAllFilteredAreCleared();
    }

    @Test(description = "Clear all product filters", groups = "smoke")
    public void plpScenario_08() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.BOYS.getName(), MaleCategory.SHIRTS.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.SIZE, FilterBy.COLOUR, FilterBy.PRICE)
                .clearAllFilters()
                .verifyProductItemCountIsEqualTo(itemCountBeforeFiltering)
                .verifyAllFilteredAreCleared();
    }

    @Test(dataProvider = "sorting", description = "Sort products", groups = "smoke")
    public void plpScenario_09(SortBy sortBy, Object ignored) {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.getName(), MaleCategory.randomise().getName())
                .applyProductSorting(sortBy);

        if (sortBy.equals(SortBy.PRICE_LOW_TO_HIGH) || sortBy.equals(SortBy.PRICE_HIGH_TO_LOW)) {
            appHelper.verifyDisplayedProductPriceSorting(sortBy);
        } else {
            appHelper.verifyDisplayedProductsEqualsOrExceedCount(1);
        }
    }

    @Test(description = "Clear product sorting")
    public void plpScenario_10() {
        appHelper
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.getName(), MaleCategory.randomise().getName())
                .applyProductSorting(SortBy.LATEST)
                .clearProductSorting()
                .verifyDisplayedProductsEqualsOrExceedCount(1);
    }

    @Test(description = "Search for a product - By description")
    public void plpScenario_11() {
        appHelper
                .openShopTab()
                .searchForAProductFromShop("Jeans")
                .verifyMultiCategorySearchResult("jeans");
    }

    @Test(description = "Search for a product - By product number")
    public void plpScenario_12() {
        final Product product = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials).get(Category.WOMEN).get(0);

        appHelper
                .openShopTab()
                .searchForAProductFromShop(product.getId())
                .verifySingleCategorySearchResult(product.getId());
    }

    @Test(description = "Search for a product - Invalid product")
    public void plpScenario_13() {
        appHelper
                .openShopTab()
                .searchAndVerifyMessage("Blah Blah", "Sorry");
    }

    @Test(description = "Search from the search history")
    public void plpScenario_14() {
        final String searchTerm = "Jeans";

        appHelper
                .openShopTab()
                .searchForAProductFromShop(searchTerm)
                .goBack()
                .searchHistory(searchTerm)
                .verifyMultiCategorySearchResult(searchTerm);
    }

    @Test(description = "Verify store - Store locator - PLP landing")
    public void plpScenario_15() {
        final String store = "ABERDEEN ACCORD";

        appHelper
                .openShopTab()
                .openStoreLocatorFromShop()
                .viewAllStores()
                .findAndOpenStore(store)
                .verifyStoreDetails(store);
    }

    @Test(description = "Scan in store - Invalid Barcode - PLP landing")
    public void plpScenario_16() {
        appHelper
                .openShopTab()
                .openScanInStoreFromShop()
                .openNearestStore()
                .scanBarcodeManually("00000123")
                .verifyMessageAndAccept("Could not find product");
    }
}