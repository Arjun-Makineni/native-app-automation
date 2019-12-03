package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.fixtures.ShoppingBagFixture;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;


/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class ShoppingBagTests extends IosMobileTest {
	private ShoppingBagFixture shoppingBagFixture;
    private Map<Category, List<Product>> productList = new HashMap<>();

    @BeforeMethod(alwaysRun = true)
    public void beforeShoppingBagTests() {
        productList = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);
        shoppingBagFixture = new ShoppingBagFixture(productList);

        if (GlobalConfig.isAppRunningLocally()) {
            performLocalBagCleanup();
        }
    }

    private Function<Category, Product> productLookup = (category) -> {
        if (productList != null) {
            return productList.get(category).get(new Random().nextInt(productList.get(category).size()));
        }
        return null;
    };

    @Test(description = "Increase and decrease shopping bag", groups = "smoke")
    public void shoppingBagScenario_01() {
        // Note: if the chosen product has only 1 qty in stock - then this scenario will fail - re-run so a new product is chosen. We don't have control on inventory for products.
        appHelper
                .openShoppingBagTab()
                .startShoppingFromBag()
                .addProductToBag(productLookup.apply(Category.WOMEN), Category.WOMEN)
                .openShoppingBagTab()
                .increaseFirstBagItemQty()
                .verifyTotalBagQty(2)
                .decreaseFirstBagItemQty()
                .verifyTotalBagQty(1);
    }

    @Test(description = "Remove shopping bag items", groups = "smoke")
    public void shoppingBagScenario_02() {
        final Product product1 = productLookup.apply(Category.WOMEN);
        final Product product2 = productLookup.apply(Category.MEN);

        appHelper
                .openShoppingBagTab()
                .startShoppingFromBag()
                .addProductToBag(product1, Category.WOMEN)
                .addProductToBag(product2, Category.MEN)
                .openShoppingBagTab()
                .verifyTotalBagQty(2)
                .removeBagItem(product1)
                .removeBagItem(product2)
                .verifyBagIsEmpty();
    }

    @Test(description = "Add bag item to the wishlist", groups = "smoke")
    public void shoppingBagScenario_03() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .openShopTab()
                .addProductToBag(product, Category.WOMEN)
                .openShoppingBagTab()
                .addBagItemToWishlist(product)
                .verifyBagIsEmpty()
                .closeBag()
                .openWishlistTab()
                .verifyProductIsInWishlist(product);
    }

    @Test(description = "Verify that an existing customers web shopping bag is synced to the mobile app")
    public void shoppingBagScenario_04() {
        webHelper
                .registerAndLoginWebCustomer(tcplApiCredentials)
                .addInStockProductToBag(productLookup.apply(Category.WOMEN));

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .openShoppingBagTab()
                .verifyPreviousSessionBagMessageIsDisplayed(1)
                .verifyTotalBagQty(1);
    }

    @Test(description = "Verify that an existing customers mobile app bag is synced to the web app")
    public void shoppingBagScenario_05() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToBag(product, Category.WOMEN)
                .openShoppingBagTab()
                .verifyTotalBagQty(1);

        webHelper.loginAndVerifyBagHasProduct(customer, product);
    }

    @Test(description = "Verify that a previous session bag on the app is retrieved after sign in")
    public void shoppingBagScenario_06() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .addProductToBag(product, Category.WOMEN)
                .openShoppingBagTab()
                .verifyTotalBagQty(1)
                .closeBag()
                .openMyRiTab()
                .signOut()
                .openShoppingBagTab()
                .verifyBagIsEmpty()
                .signInFromBag()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .verifyPreviousSessionBagMessageIsDisplayed(1);
    }

    @Test(description = "Verify that the mobile app bag is empty for a 2nd customer using the same device")
    public void shoppingBagScenario_07() {
        final Product product = productLookup.apply(Category.WOMEN);

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword()) // 1st customer
                .addProductToBag(product, Category.WOMEN)
                .openShoppingBagTab()
                .verifyTotalBagQty(1)
                .closeBag()
                .openMyRiTab()
                .signOut()
                .registerNewCustomerViaApi(tcplApiCredentials) // 2nd customer
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .openShoppingBagTab()
                .verifyBagIsEmpty();
    }

    @Test(description = "Verify that all previous session items can be moved to the wishlist from the mobile app and verify in the web app")
    public void shoppingBagScenario_08() {
        final Product product1 = productLookup.apply(Category.WOMEN);
        final Product product2 = productLookup.apply(Category.MEN);

        webHelper
                .registerAndLoginWebCustomer(tcplApiCredentials)
                .addInStockProductToBag(product1)
                .addInStockProductToBag(product2);

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .openShoppingBagTab()
                .moveAllPreviousSessionBagItemsToWishlist()
                .verifyBagIsEmpty()
                .closeBag()
                .openWishlistTab()
                .verifyProductIsInWishlist(product1)
                .verifyProductIsInWishlist(product2);

        webHelper.verifyWebWishlistItems(product1, product2);
    }

    @Test(description = "Verify that a single previous session item can be moved to the wishlist from the mobile app and verify in the web app")
    public void shoppingBagScenario_09() {
        final Product product = productLookup.apply(Category.WOMEN);

        webHelper
                .registerAndLoginWebCustomer(tcplApiCredentials)
                .addInStockProductToBag(product);

        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .openShoppingBagTab()
                .movePreviousSessionBagItemToWishlist(product)
                .verifyBagIsEmpty()
                .closeBag()
                .openWishlistTab()
                .verifyProductIsInWishlist(product);

        webHelper.verifyWebWishlistItems(product);
    }
    
    @Test(description = "Verify free delivery promo signed in")
    public void shoppingBagScenario_10_01() {
        appHelper
        	.openMenu()
        	.openSignIn()
        	.signUp();

        appHelper.openHomeTab();
        
    	shoppingBagFixture.verifyDeliveryPromoEqualToLimit("65.00");
    }
    
    @Test(description = "Verify free delivery promo less than limit signed in")
    public void shoppingBagScenario_10_02() {
        appHelper
        	.openMenu()
        	.openSignIn()
        	.signUp();
        appHelper.openHomeTab();
    	shoppingBagFixture.verifyDeliveryPromoLessThanLimit("65.00");
    }
    
    @Test(description = "Verify free delivery promo greater than limit signed in")
    public void shoppingBagScenario_10_03() {
        appHelper
        	.openMenu()
        	.openSignIn()
        	.signUp();
        appHelper.openHomeTab();
    	shoppingBagFixture.verifyDeliveryPromoGreaterThanLimit("65.00");
    } 

    // this just ensures the bag is fully cleared to prevent other features from failing
    private void performLocalBagCleanup() {
        appHelper
                .openMyRiTab()
                .checkAndSignOut();

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .signOut();
    }
}