package com.riverisland.ios.mobile.test;

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
public class ProductDetailsPageTests extends IosMobileTest {
	
	private ProductLandingFixture productLandingFixture = new ProductLandingFixture();

    @Test(description = "Verify PDP landing details", groups = "smoke")
    public void pdpScenario_01() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.WOMEN.getName(), FemaleCategory.randomise().getName())
                .selectProduct()
                .verifyLandingProductDetails();
    }

    @Test(description = "Verify PDP details")
    public void pdpScenario_02() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.MEN.getName(), MaleCategory.randomise().getName())
                .selectProduct()
                .verifyProductDetails();
    }

    @Test(description = "Swipe through product images", groups = "smoke")
    public void pdpScenario_03() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.MEN.getName(), MaleCategory.SHIRTS.getName())
                .selectProduct()
                .swipeProductImage(SwipeElementDirection.LEFT)
                .verifyProductImageIndex(2)
                .swipeProductImage(SwipeElementDirection.RIGHT)
                .verifyProductImageIndex(1);
    }

    @Test(dataProvider = "categories", description = "Open Size Guide from PDP", groups = "smoke")
    public void pdpScenario_04(Category category, String subCategory) {
        appHelper
                .openShopTab()
                .openProductSelection(category.getName(), subCategory)
                .selectProduct()
                .openSizeGuideFromProductDetails()
                .viewSizeGuideCategory(category)
                .verifySizeGuideIsDisplayed(category);
    }

    @Test(description = "Check Store from PDP")
    public void pdpScenario_05() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.WOMEN.getName(), FemaleCategory.randomise().getName())
                .selectProduct()
                .openCheckStockFromProductDetails()
                .selectSizeToCheckStock()
                .checkStockFromAllStores()
                .verifyCheckStoreMessageIsDisplayed();
    }

    @Test(description = "Ensure wear it with items are displayed from PDP")
    public void pdpScenario_06() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.WOMEN.getName(), FemaleCategory.TOPS.getName())
                .selectProduct()
                .verifyHasWearItWithItems();
    }

    @Test(description = "Verify fabric and care details")
    public void pdpScenario_07() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.MEN.getName(), MaleCategory.randomise().getName())
                .selectProduct()
                .verifyFabricAndCare();
    }

    @Test(description = "Verify delivery & returns")
    public void pdpScenario_08() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.MEN.getName(), MaleCategory.randomise().getName())
                .selectProduct()
                .verifyDeliveryAndReturns();
    }

    @Test(description = "Swipe through all product images in full view - forwards and backwards")
    public void pdpScenario_09() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.MEN.getName(), MaleCategory.SHIRTS.getName())
                .selectProduct()
                .openProductImageInFullView();

        final int imageCount = appHelper.getCountOfProductImages();

        // swipe forward through all images
        IntStream.rangeClosed(1, imageCount).boxed().forEach(imageIndex -> {
            appHelper.swipeProductImageInFullView(SwipeElementDirection.LEFT);

            if (imageIndex == imageCount) {
                appHelper.verifyProductImageIndex(imageIndex);
            } else {
                appHelper.verifyProductImageIndex(imageIndex + 1);
            }
        });

        // swipe back through all images
        IntStream.rangeClosed(1, imageCount).boxed().forEach(imageIndex -> {
            appHelper.swipeProductImageInFullView(SwipeElementDirection.RIGHT);

            if (imageIndex == imageCount) {
                appHelper.verifyProductImageIndex(1);
            } else {
                appHelper.verifyProductImageIndex(imageCount - imageIndex);
            }
        });
    }

    @Test(description = "Verify product swatch")
    public void pdpScenario_10() {
        appHelper
                .openShopTab()
                .openProductSelection(Category.WOMEN.getName(), FemaleCategory.DRESSES.getName())
                .selectProductWithSwatch()
                .saveProductNumber()
                .changeProductSwatch(productNumber)
                .verifyDisplayedProductNumberIsDifferent(productNumber);
    }
    
    @Test(description = "Verify display of less than the maximum number of swatches ")
    public void pdpScenario_10_1() {
        productLandingFixture
    		.selectProductWithSwatches(2, Category.WOMEN.getName(), FemaleCategory.TOPS.getName())
    		.verifyProductSwatchesDetails(2);
    }
    
    @Test(description = "Verify swatches to maximum number of swatces displayed (4)")
    public void pdpScenario_10_2() {
        productLandingFixture
        	.selectProductWithSwatches(4, Category.WOMEN.getName(), FemaleCategory.TOPS.getName())
        	.verifyProductSwatchesDetails(4);      
    }
    
    @Test(description = "Verify swatches with extra swatches displayed as a number")
    public void pdpScenario_10_3() {
        productLandingFixture
        .selectProductWithSwatches(7, Category.WOMEN.getName(), FemaleCategory.TOPS.getName())
        .verifyProductSwatchesDetails(7);
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