package com.riverisland.app.automation.helpers;

import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.pojos.AppSession;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.utils.AccountUtils;
import com.riverisland.automation.utils.core.framework.DriverBuilder;
import com.riverisland.automation.utils.core.framework.DriverProperties;
import com.riverisland.automation.utils.core.framework.RiverIslandWebDriver;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;
import com.riverisland.automation.utils.ecom.domain.account.AccountRegistrationRequest;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Sizes;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static com.riverisland.app.automation.pojos.AppSession.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Prashant Ramcharan on 10/11/2017
 */
public class WebHelper {

    public WebHelper(String webUrl) {
        AppSession.webUrl = webUrl;
    }

    public void createWebDriver() {
        webDriver = RiverIslandWebDriver.newDriver(
                DriverBuilder.createLocalBuilder(BrowserType.CHROME, new DriverProperties()));

        webUrl = StringUtils.substringBeforeLast(webUrl, "/");
    }

    /**
     * Required for Web -> Native App Integration
     */
    public WebHelper registerAndLoginWebCustomer(TcplApiCredentials credentials) {
        customer = Customer.Builder.create().buildNewCustomer();

        // create the account using the TCPL API
        final AccountRegistrationRequest accountRegistrationRequest = new AccountRegistrationRequest(customer.getEmailAddress(), customer.getPassword(), customer.getFirstName(), customer.getLastName());

        final boolean isAccountCreated = AccountUtils.registerAccount(credentials, accountRegistrationRequest);

        if (!isAccountCreated) {
            throw new RuntimeException("Unable to create the customer account using TCPL API. Is the API working?");
        }

        login(customer);
        return this;
    }

    private WebHelper login(Customer customer) {
        createWebDriver();

        webDriver
                .go(webUrl + "/myaccount")
                .type(By.id("signin--email"), customer.getEmailAddress())
                .type(By.id("signin--password"), customer.getPassword())
                .jsClick(By.id("customer-sign-in"))
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("MenuItemMyAccount")));
        return this;
    }

    public WebHelper addInStockProductToBag(Product product) {
        webDriver.go(product.getUrl());

        String sizeKeyVariant = product.getSizes().stream().filter(size -> size.getStockLevel() > 0).findFirst().orElseThrow(() -> new RuntimeException("The product does not have any stock!")).getVariantId();
        String sizeKey = webDriver.retrieveElement(By.xpath(String.format("//*[@data-variant-id='%s']", sizeKeyVariant))).getAttribute("value");

        final String webProductApi = webUrl + "/product/Action";

        final String webProductApiData = String.format("SizeKey=%s&Quantity=1&productId=%s&AddItemToBasket=1", sizeKey, product.getId());

        final String addProductScript = String.format("$.post(\"%s\", \"%s\", function(data){})", webProductApi, webProductApiData);

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Adding web product using script: " + addProductScript);

        ((JavascriptExecutor) webDriver.getDefaultDriver()).executeScript(addProductScript);

        webDriver
                .pause(2000)
                .go(webUrl + "/bag");

        try {
            final String link = StringUtils.substringAfter(product.getUrl(), ".com");
            webDriver.retrieveElement(By.xpath("//a[@href='" + link + "']"));
        } catch (WebDriverException wde) {
            throw new RuntimeException("Unable to add the product to the bag using the web api! Error: " + wde.getMessage());
        }

        return this;
    }

    public WebHelper loginAndVerifyBagHasProduct(Customer customer, Product product) {
        login(customer);

        webDriver.go(webUrl + "/bag");

        final String link = StringUtils.substringAfter(product.getUrl(), ".com");
        assertThat(webDriver.retrieveElement(By.xpath("//a[@href='" + link + "']"))).isNotNull();

        return this;
    }

    public WebHelper addInStockWebWishlistItems(Category category, int numberOfItems) {
        addWebWishlistItems(category, numberOfItems, true);
        return this;
    }

    public WebHelper addOutOfStockWebWishlistItems(Category category, int numberOfItems) {
        addWebWishlistItems(category, numberOfItems, false);
        return this;
    }

    private WebHelper addWebWishlistItems(Category category, int requiredWishlistItems, boolean selectSize) {
        int expectedWishlistItems = requiredWishlistItems;

        Collections.shuffle(tcplProducts.get(category));
        final Iterator<Product> products = tcplProducts.get(category).listIterator();

        final LocalDateTime localTimeout = LocalDateTime.now().plusMinutes(1);

        while (requiredWishlistItems-- > 0 && LocalDateTime.now().isBefore(localTimeout)) {

            // the assumption is that TCPL's API returns in stock products because we trust the provider.
            Product product = products.next();
            Sizes sizeKey = product.getSizes().stream().filter(sizes -> sizes.getStockLevel() > 0).findFirst().get();

            webDriver.go(product.getUrl());

            String webSizeKey;

            try {
                webSizeKey = webDriver.retrieveElement(By.xpath(String.format("//*[@data-variant-id='%s']", sizeKey.getVariantId()))).getAttribute("value");
            } catch (Exception e) {
                requiredWishlistItems++;
                continue;
            }

            // web API call
            final String wishlistApiCall = String.format("%s/Wishlist/AddItemToWishlist?SizeKey=%s&Quantity=1&productId=%s&AddItemToWishlist=1&callback=/", webUrl, selectSize ? webSizeKey : null, product.getId());

            webDriver.go(wishlistApiCall);

            if (!webDriver.getDefaultDriver().getPageSource().contains("\"success\":true")) {
                requiredWishlistItems++;
            }
        }

        webDriver.go(webUrl + "/wishlist");

        assertThat(webDriver.retrieveAllElements(By.className("wishlist__product-image")).size()).as("Incorrect size of web wishlist items").isGreaterThanOrEqualTo(expectedWishlistItems);
        return this;
    }

    public WebHelper verifyWebWishlistItem(String productName) {
        assertThat(
                webDriver
                        .go(webUrl + "/wishlist")
                        .retrieveAllVisibleElements(By.className("product__title")).stream().anyMatch(t -> t.getText().equalsIgnoreCase(productName))).isTrue();
        return this;
    }

    public WebHelper verifyWebWishlistItems(Product... products) {
        webDriver.go(webUrl + "/wishlist");

        Arrays.asList(products).forEach(product -> assertThat(webDriver.retrieveAllVisibleElements(By.className("product__title")).stream().anyMatch(t -> t.getText().equalsIgnoreCase(product.getDescription()))).isTrue());
        return this;
    }

    public WebHelper verifyWebWishlistIsEmpty() {
        assertThat(
                webDriver
                        .pause(1500)
                        .go(webUrl + "/wishlist")
                        .getText(By.className("ui-display")).contains("0")).isTrue();
        return this;
    }
}
