package com.riverisland.android.test;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.FemaleCategory;
import com.riverisland.app.automation.enums.MaleCategory;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.fixtures.ProductLandingFixture;

import org.testng.annotations.Test;

import java.util.stream.IntStream;

/**
 * Created by Prashant Ramcharan on 15/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class ProductDetailsPageTests extends AndroidTest {
	
	private ProductLandingFixture productLandingFixture = new ProductLandingFixture();

    @Test(description = "Verify PDP landing details", groups = "smoke")
    public void pdpScenario_01() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.randomise().getName())
                .selectProduct()
                .verifyLandingProductDetails();
    }

    @Test(description = "Verify PDP details")
    public void pdpScenario_02() {
        appHelper
				.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.MEN.name(), MaleCategory.randomise().getName())
                .selectProduct()
                .openProductDetails()
                .verifyProductDetails();
    }

    @Test(description = "Swipe through product images", groups = "smoke")
    public void pdpScenario_03() {
        appHelper
				.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.MEN.name(), MaleCategory.SHIRTS.getName())
                .selectProduct()
                .swipeProductImage(SwipeElementDirection.LEFT)
                .swipeProductImage(SwipeElementDirection.RIGHT);
    }

    @Test(dataProvider = "categories", description = "Open Size Guide from PDP", groups = "smoke")
    public void pdpScenario_04(Category category, String subCategory) {
        appHelper
				.homepageWelcome()
				.openShopTab()
                .openProductSelection(category.name(), subCategory)
                .selectProduct()
                .openSizeGuideFromProductDetails()
                .viewSizeGuideCategory(category)
                .verifySizeGuideIsDisplayed(category);
    }

    @Test(description = "Check Store from PDP")
    public void pdpScenario_05() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.GIRLS.name(), FemaleCategory.randomise().getName())
                .selectProduct()
                .openCheckStockFromProductDetails()
                .selectSizeToCheckStock()
                .checkStockFromAllStores()
                .verifyCheckStoreMessageIsDisplayed();
    }

    @Test(description = "Ensure wear it with items are displayed from PDP")
    public void pdpScenario_06() {
        appHelper
				.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.TOPS.getName())
                .selectProduct()
                .verifyHasWearItWithItems();
    }

    @Test(description = "Verify fabric and care details")
    public void pdpScenario_07() {
        appHelper
				.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.MEN.name(), MaleCategory.randomise().getName())
                .selectProduct()
                .verifyFabricAndCare();
    }

    @Test(description = "Verify delivery & returns")
    public void pdpScenario_08() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.MEN.name(), MaleCategory.randomise().getName())
                .selectProduct()
                .verifyDeliveryAndReturns();
    }

    @Test(description = "Swipe through all product images in full view - forwards and backwards")
    public void pdpScenario_09() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.MEN.name(), MaleCategory.SHIRTS.getName())
                .selectProduct()
                .openProductImageInFullView();

        final int imageCount = appHelper.getCountOfProductImages();

        // swipe forward through all images
        IntStream.rangeClosed(1, imageCount).boxed().forEach(imageIndex -> appHelper.swipeProductImageInFullView(SwipeElementDirection.LEFT));

        // swipe back through all images
        IntStream.rangeClosed(1, imageCount).boxed().forEach(imageIndex -> appHelper.swipeProductImageInFullView(SwipeElementDirection.RIGHT));
    }

    @Test(description = "Verify product swatch")
    public void pdpScenario_10() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.DRESSES.getName())
                .selectProductWithSwatch()
                .saveProductNumber()
                .changeProductSwatch(productNumber)
                .verifyDisplayedProductNumberIsDifferent(productNumber);
    }
    
    @Test(description = "Verify two swatches")
    public void pdpScenario_10_1() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.DRESSES.getName());
        productLandingFixture
                .selectProductWithSwatches(2, Category.WOMEN.name(), FemaleCategory.DRESSES.getName())
                .verifyProductSwatchesDetails(2);
    }
    
    @Test(description = "Verify four swatches")
    public void pdpScenario_10_2() {
        appHelper
        		.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.TOPS.getName());
        productLandingFixture
                .selectProductWithSwatches(4, Category.WOMEN.name(), FemaleCategory.DRESSES.getName())
                .verifyProductSwatchesDetails(4);
    }
    
    @Test(description = "Verify six swatches and extra swatch is displayed as a number")
    public void pdpScenario_10_3() {
        appHelper
        		.homepageWelcome()
                .openShopTab();
        productLandingFixture
                .selectProductWithSwatches(10, Category.WOMEN.name(), FemaleCategory.DRESSES.getName())
                .verifyProductSwatchesDetails(10);
    }
    
    @Test(description = "Ensure similar items are displayed from PDP")
    public void pdpScenario_11() {
        appHelper
				.homepageWelcome()
                .openShopTab()
                .openProductSelection(Category.WOMEN.name(), FemaleCategory.TOPS.getName())
                .selectProduct()
                .verifyHasSimilarItems();
    }    
}