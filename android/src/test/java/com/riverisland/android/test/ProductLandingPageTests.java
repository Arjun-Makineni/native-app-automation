package com.riverisland.android.test;

import com.riverisland.app.automation.enums.*;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.testng.annotations.Test;


/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class ProductLandingPageTests extends AndroidTest {

    @Test(description = "Toggle product listing view - Big Grid")
    public void plpScenario_01() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.name(), FemaleCategory.DRESSES.getName());

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_BIG)
                .verifyDisplayedProductCount(numberOfProducts);
    }

    @Test(description = "Toggle product listing view - Small Grid", groups = "smoke")
    public void plpScenario_02() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.name(), MaleCategory.SHIRTS.getName())
                .toggleProductView(ToggleType.GRID_BIG);

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_SMALL)
                .verifyDisplayedProductsEqualsOrExceedCount(numberOfProducts);
    }

    @Test(description = "Toggle product listing view - List Grid")
    public void plpScenario_03() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.BOYS.name(), MaleCategory.SHORTS.getName())
                .toggleProductView(ToggleType.GRID_BIG);

        final int numberOfProducts = appHelper.getCountOfDisplayedProducts();

        appHelper
                .toggleProductView(ToggleType.GRID_LIST)
                .verifyDisplayedProductsEqualsOrLessThanCount(numberOfProducts);
    }

    @Test(description = "Filter products - Single filter", groups = "smoke")
    public void plpScenario_04() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.name(), FemaleCategory.DRESSES.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.COLOUR)
                .closeFilter()
                .verifyProductItemCountIsLessThan(itemCountBeforeFiltering);
    }

    @Test(description = "Filter products - Single filter - Multiple Options")
    public void plpScenario_05() {
        appHelper
				.homepageWelcome()
				.openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.name(), FemaleCategory.DRESSES.getName())
                .applyFilterAndSelectMultipleOptionsAndVerify(FilterBy.COLOUR);
    }

    @Test(description = "Filter products - Multiple filters")
    public void plpScenario_06() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.WOMEN.name(), FemaleCategory.DRESSES.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.SIZE, FilterBy.COLOUR, FilterBy.PRICE)
                .closeFilter()
                .verifyProductItemCountIsLessThan(itemCountBeforeFiltering);
    }

    @Test(description = "Clear single product filter")
    public void plpScenario_07() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.MEN.name(), MaleCategory.SHIRTS.getName())
                .waitForProductsToLoad()
                .applyProductFilterAndVerify(FilterBy.SIZE)
                .clearSingleFilter(FilterBy.SIZE)
                .verifyAllFilteredAreCleared();
    }

    @Test(description = "Clear all product filters", groups = "smoke")
    public void plpScenario_08() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelectionFromSearch(Category.BOYS.name(), MaleCategory.SHIRTS.getName());

        final int itemCountBeforeFiltering = appHelper.getProductCategoryItemCount();

        appHelper
                .applyProductFilterAndVerify(FilterBy.SIZE, FilterBy.COLOUR, FilterBy.PRICE)
                .clearAllFilters()
                .verifyAllFilteredAreCleared()
                .closeFilter()
                .verifyProductItemCountIsEqualTo(itemCountBeforeFiltering);
    }

    @Test(dataProvider = "sorting", description = "Sort products", groups = "smoke")
    public void plpScenario_09(SortBy sortBy, Object ignored) {
        appHelper
        		.homepageWelcome()
        		.openShopTab()
                .openProductSelectionFromSearch(Category.MEN.name(), MaleCategory.randomise().getName())
                .applyProductSorting(sortBy)
                .waitForProductsToLoad();

        if (sortBy.equals(SortBy.PRICE_LOW_TO_HIGH) || sortBy.equals(SortBy.PRICE_HIGH_TO_LOW)) {
            appHelper.verifyDisplayedProductPriceSorting(sortBy);
        } else {
            appHelper.verifyDisplayedProductsEqualsOrExceedCount(1);
        }
    }

    @Test(description = "Search for a product - By description")
    public void plpScenario_011() {
        appHelper
        		.homepageWelcome()
        		.openShopTab()
                .searchForAProductFromShop("Jeans", false)
                .verifyMultiCategorySearchResult("Jeans");
    }

    @Test(description = "Search for a product - By product number")
    public void plpScenario_012() {
        final Product product = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials).get(Category.WOMEN).get(0);

        appHelper
        		.homepageWelcome()
                .openShopTab()
                .searchForAProductFromShop(product.getId(), false)
                .verifySingleCategorySearchResult(product.getId());
    }

    @Test(description = "Search for a product - Invalid product")
    public void plpScenario_013() {
        appHelper
        		.homepageWelcome()
        		.openShopTab()
                .searchAndVerifyMessage("Blah Blah", false, "Sorry");
    }

    @Test(description = "Search from the search history")
    public void plpScenario_014() {
        final String searchTerm = "Jeans";

        appHelper
        		.homepageWelcome()
                .openShopTab()
                .searchForAProductFromShop(searchTerm, false)
                .verifyMultiCategorySearchResult(searchTerm)
                .cancelSearch()
                .goBack()
                .openMenu()
                .searchHistory(searchTerm)
                .verifyMultiCategorySearchResult(searchTerm);
    }
}