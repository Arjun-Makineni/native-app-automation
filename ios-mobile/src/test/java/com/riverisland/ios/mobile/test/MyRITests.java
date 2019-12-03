package com.riverisland.ios.mobile.test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.OrderStatus;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.fixtures.OnboardingPageFixture;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.utils.ProductUtils;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import org.assertj.core.util.Lists;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Prashant Ramcharan on 22/06/2017
 */
@SuppressWarnings("groupsTestNG")
public class MyRITests extends IosMobileTest {
	
	OnboardingPageFixture onboardingPageFixture = new OnboardingPageFixture();

    @Test(description = "Verify order history")
    public void myRiScenario_01() {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials);
        onboardingPageFixture.skipAndAcceptPushNotifications(true);
        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(customer.getEmailAddress(), customer.getPassword())
                .openOrderHistory()
                .verifyOrdeHistoryIsEmpty();

        aggregatedAppHelper.placeIOSMobileOrderSignedIn(Region.GB, CardType.VISA);

        appHelper
        		.acceptAlert("Not Now")
                .openMyRiTab()
                .openOrderHistory()
                .viewOrder(orderNumber)
                .verifyOrderDetails(orderNumber, address, paymentMethod, OrderStatus.AWAITING_PSP_NOTIFICATION.getDescription(), OrderStatus.NEW_ORDER.getDescription());
    }

    @Test(dataProvider = "newsletter-categories", description = "Sign up to newsletters")
    public void myRiScenario_02(Category category, Object ignored) {
        appHelper
                .openMyRiTab()
                .openHelpAndSupport()
                .signUpToNewsletters(existingRiverIslandUser.getEmail(), category)
                .verifyMessageAndAccept("You are now signed up to receive our newsletter");
    }

    @Test(description = "Sign up to newsletters - Invalid email")
    public void myRiScenario_03() {
        appHelper
                .openMyRiTab()
                .openHelpAndSupport()
                .signUpToNewsletters("a+@gmail.com", Category.WOMEN)
                .verifyMessageAndAccept("Please try again");
    }

    @Test(description = "Verify FAQ sections")
    public void myRiScenario_04() {
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
                .openMyRiTab()
                .openHelpAndSupport()
                .viewFaq();

        faqSections.keySet().forEach(section ->
                appHelper
                        .viewFaqSection(section, faqSections.get(section))
                        .verifyFaqHasContent());
    }

    @Test(description = "Verify size guides", groups = "smoke")
    public void myRiScenario_05() {
        final List<Category> categoryList = Lists.newArrayList(Category.WOMEN, Category.MEN, Category.GIRLS, Category.BOYS);

        appHelper
                .openMyRiTab()
                .openHelpAndSupport()
                .viewSizeGuides();

        categoryList.forEach(category ->
                appHelper
                        .viewSizeGuideCategory(category)
                        .verifySizeGuideIsDisplayed(category)
                        .goBack());
    }

    @Test(description = "Sign out", groups = "smoke")
    public void myRiScenario_06() {
        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(existingRiverIslandUser.getEmail(), existingRiverIslandUser.getPassword())
                .verifyMyAccountIsDisplayed()
                .signOut()
                .openMyRiTab()
                .verifyIsSignedOut();
    }

    @Test(description = "Sign in - Wrong username and password")
    public void myRiScenario_07() {
        appHelper
                .openMyRiTab()
                .openSignIn()
                .signIn(UUID.randomUUID().toString() + "@gmail.com", "password")
                .verifyMessageAndAccept("Unable to find a match for that email address or password");
    }

    @Test(description = "Verify store - Store locator", groups = "smoke")
    public void myRiScenario_08() {
        final String store = "ABERDEEN ACCORD";

        appHelper
                .openMyRiTab()
                .openStoreLocator()
                .viewAllStores()
                .findAndOpenStore(store)
                .verifyStoreDetails(store);
    }

    @Test(description = "Verify store - Get Directions")
    public void myRiScenario_09() {
        final String store = "ABERDEEN ACCORD";

        appHelper
                .openMyRiTab()
                .openStoreLocator()
                .viewAllStores()
                .findAndOpenStore(store)
                .openStoreDirections();
    }

    @Test(description = "Verify local stores exist")
    public void myRiScenario_10() {
        appHelper
                .openMyRiTab()
                .openStoreLocator()
                .viewLocalStores()
                .verifyLocalStoresExist();
    }

    @Test(description = "Scan in store - Invalid barcode")
    public void myRiScenario_011() {
        appHelper
                .openMyRiTab()
                .openScanInStore()
                .openNearestStore()
                .scanBarcodeManually("00000123")
                .verifyMessageAndAccept("Could not find product");
    }

    @Test(description = "Verify recently viewed products", groups = "smoke")
    public void myRiScenario_12() {
        AppSession.tcplProducts = ProductUtils.getInStockProductsFromTcplApi(tcplApiCredentials);

        final Product product = tcplProducts.isEmpty() ? null : tcplProducts.get(Category.WOMEN).stream().findFirst().orElse(null);

        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMyRiTab()
                    .openRecentlyViewed()
                    .clearRecentlyViewedList()
                    .openMyRiTab();
        }

        appHelper
                .openShopTab()
                .searchAndSelectProduct(product, Category.WOMEN);

        if (product == null) {
            appHelper.saveProductName();
        }

        appHelper
                .goBack()
                .goBack()
                .openMyRiTab()
                .openRecentlyViewed()
                .verifyRecentlyViewedProduct(product != null ? product.getDescription() : productName)
                .clearRecentlyViewedList()
                .verifyRecentlyViewedIsEmpty();
    }

    @Test(description = "Verify Terms and Conditions")
    public void myRiScenario_13() {
        appHelper
                .openMyRiTab()
                .openHelpAndSupport()
                .viewTermsAndConditions()
                .verifyTermsAndConditionsAreDisplayed();
    }

    @Test(description = "Verify Settings")
    public void myRiScenario_14() {
        appHelper
                .openMyRiTab()
                .openSettings()
                .verifySettings("Display in miles", "Last updated", "No updates available");
    }

    @Test(description = "Verify Forgot Password", groups = "smoke")
    public void myRiScenario_15() {
        appHelper
                .registerNewCustomerViaApi(tcplApiCredentials)
                .openMyRiTab()
                .openSignIn()
                .forgotPassword(customer.getEmailAddress())
                .verifyPasswordResetMessage("Thanks - an email is on its way to you now with a link to create a new password.");
    }
}