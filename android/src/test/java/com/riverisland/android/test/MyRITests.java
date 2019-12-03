package com.riverisland.android.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.OrderStatus;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.assertj.core.util.Lists;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class MyRITests extends AndroidTest {

    @AfterTest(alwaysRun = true)
    public void afterMyRITests() {
        AppSession.tcplProducts = null;
    }

    @Test(description = "Verify order history")
    public void myRiScenario_01() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .changeCurrency(Region.GB)
                .openMenu()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword());

        aggregatedAppHelper.placeAndroidOrderSignedIn(Region.GB, CardType.VISA);

        appHelper
                .openMenu()
                .openOrderHistory()
                .viewOrder(orderNumber)
                .verifyOrderDetails(orderNumber, address, paymentMethod, OrderStatus.AWAITING_PSP_NOTIFICATION.getDescription(), OrderStatus.NEW_ORDER.getDescription());
    }

    @Test(dataProvider = "newsletter-categories", description = "Sign up to newsletters")
    public void myRiScenario_02(Category category, Object ignored) {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openHelpAndSupport()
                .signUpToNewsletters(customer.getEmailAddress(), category)
                .verifyMessageAndAccept("You are now signed up to receive our newsletter");
    }

    @Test(description = "Verify FAQ sections")
    public void myRiScenario_03() {
        final Map<String, String> faqSections = new LinkedHashMap<>();

        faqSections.put("Delivery", "Standard delivery");
        faqSections.put("How to place your order", "How can I pay on your site?");
        faqSections.put("International", "International Delivery");
        faqSections.put("Payment", "How secure is your site?");
        faqSections.put("Product and general info", "The item I want is not available, can I check if it's in a store?");
        faqSections.put("Promotion codes", "Where do I enter my promotion code ?");
        faqSections.put("Returns", "Exchanging Goods");
        faqSections.put("Store information", "How do I find out a store's opening hours?");

        appHelper
                .openMenu()
                .openHelpAndSupport()
                .viewFaq();

        faqSections.keySet().forEach(section ->
                appHelper
                        .viewFaqSection(section, faqSections.get(section))
                        .verifyFaqHasContent());
    }

    @Test(description = "Verify size guides", groups = "smoke")
    public void myRiScenario_04() {
        final List<Category> categoryList = Lists.newArrayList(Category.WOMEN, Category.MEN, Category.GIRLS, Category.BOYS);

        appHelper
                .openMenu()
                .openHelpAndSupport();

        categoryList.forEach(category ->
                appHelper
                        .viewSizeGuides()
                        .viewSizeGuideCategory(category)
                        .verifySizeGuideIsDisplayed(category)
                        .goBack());
    }

    @Test(description = "Sign out", groups = "smoke")
    public void myRiScenario_05() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
                .openMenu()
                .openSignIn()
                .signIn(existingRiverIslandUser.getEmail(), existingRiverIslandUser.getPassword())
                .openMenu()
                .signOut()
                .openMenu()
                .verifyIsSignedOut();
    }

    @Test(description = "Verify store - Store locator", groups = "smoke")
    public void myRiScenario_06() {
        final String store = "ABERDEEN ACCORD";

        appHelper
                .openMenu()
                .openStoreLocator()
                .viewAllStores()
                .findAndOpenStore(store)
                .verifyStoreDetails(store);
    }

    @Test(description = "Verify local stores exist")
    public void myRiScenario_07() {
        appHelper
                .openMenu()
                .openStoreLocator()
                .viewLocalStores()
                .verifyLocalStoresExist();
    }

    @Test(description = "Verify recently viewed products", groups = "smoke")
    public void myRiScenario_08() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);

        final Product product = tcplProducts.isEmpty() ? null : tcplProducts.get(Category.WOMEN).stream().findFirst().orElse(null);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .openRecentlyViewed()
                    .clearRecentlyViewedList()
                    .goBack();
        }

        appHelper
                .openShopTab()
                .searchAndSelectProduct(product, Category.WOMEN);

        if (product == null) {
            appHelper.saveProductName();
        }

        appHelper
                .goBack(5)
                .openMenu()
                .openRecentlyViewed()
                .verifyRecentlyViewedProduct(product != null ? product.getDescription() : productName)
                .clearRecentlyViewedList()
                .verifyRecentlyViewedIsEmpty();
    }

    @Test(description = "Verify Terms and Conditions")
    public void myRiScenario_09() {
        appHelper
                .openMenu()
                .openHelpAndSupport()
                .viewTermsAndConditions()
                .verifyTermsAndConditionsAreDisplayed();
    }

    @Test(description = "Verify Settings")
    public void myRiScenario_10() {
        appHelper
                .openMenu()
                .openSettings()
                .verifySettings("Unique identifier", "Version");
    }

    @Test(description = "Verify Forgot Password", groups = "smoke")
    public void myRiScenario_11() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }

        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMenu()
                .openSignIn()
                .forgotPassword(customer.getEmailAddress())
                .verifyPasswordResetMessage("Thanks - an email is on its way to you now with a link to create a new password.");
    }
}