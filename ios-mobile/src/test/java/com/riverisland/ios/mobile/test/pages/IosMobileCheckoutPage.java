package com.riverisland.ios.mobile.test.pages;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.SkipException;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Payment;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.enums.SwipeElementDirection;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.CheckoutPage;
import com.riverisland.app.automation.pojos.GiroPayCredentials;
import com.riverisland.app.automation.pojos.PayPalCredentials;
import com.riverisland.app.automation.pojos.TestCard;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileCheckoutPage implements CheckoutPage<IosMobileCheckoutPage> {
    protected RiverIslandNativeAppDriver appDriver;

    public IosMobileCheckoutPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    @Override
    public IosMobileCheckoutPage selectDeliveryType(DeliveryType deliveryType) {
        appDriver.tap(deliveryType.getDescription());

        if (deliveryType.equals(DeliveryType.COLLECT_FROM_STORE) || deliveryType.equals(DeliveryType.DELIVER_TO_STORE)) {
            checkAndAllowAccess();
        }
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Selected delivery type: " + deliveryType.getDescription());
        return this;
    }

    @Override
    public IosMobileCheckoutPage openAddNewDeliveryAddress() {
        appDriver
                .swipe(SwipeElementDirection.UP)
                .tapPresent(By.name("Add new delivery address"));
        return this;
    }

    private IosMobileCheckoutPage addAddress(Address address) {
    	
    	final By titleLocator = By.xpath("//[@name='Title']");
    			
        appDriver
        		.waitFor(ExpectedConditions.visibilityOfElementLocated(titleLocator), 10)
                .tap("Title")
                .tap("Mr.")
                .touchAndType("First Name", address.getRecipientFirstName())
                .touchAndType("Last Name", address.getRecipientLastName())
                .touchAndType("Phone/Mobile", address.getTelephone())
        		.touch(appDriver.retrieveMobileElement("Country"));
        
        selectCountry(address.getCountry());

        if (address.getAddressLookup()) {
            appDriver
                    .touchAndType("Post Code", address.getPostCode());
            selectLookupAddress();
            appDriver
                    .tap(By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name,'%s')]", address.getPostCode())));
        } else {
            if (address.getRegion().equals(Region.GB)) {
            	appDriver.swipeElementToElementLocation(By.xpath("//*[@name='New address']"), By.xpath("//*[@name='Country']"));
                appDriver.retrieveMobileElement("Enter Manually").click();
            }
            appDriver
                    .touchAndType("Address Line 1", address.getAddressLine1())
                    .touch(appDriver.retrieveMobileElement("Address Line 2"))
                    .hideKeyboard()
                    .touch(appDriver.retrieveMobileElement("Address Line 3"))
                    .hideKeyboard()
                    .touchAndType("Town Or City", address.getTownOrCity())
                    .hideKeyboard()
                    .touchAndType("Post Code", address.getPostCode())
                    .swipeElementToElementLocation(By.xpath("//*[@value='Address Line 3']"), By.xpath("//*[@value='New address']"));
        }
        appDriver.scrollIntoViewAndTap("NEXT");

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added new delivery address");
        return this;
    }
    
    protected void selectLookupAddress() {
    	appDriver
        	.scrollIntoViewAndTap("Lookup Address");
    }
    
    private void selectCountry(String country) {
    	final Point toPoint = appDriver.retrieveMobileElement(By.name("United Kingdom")).getCenter();
    	final Point fromPoint = appDriver.retrieveMobileElement(By.name("Aruba")).getCenter();
 
        MobileElement countryElement = null;     
        boolean displayed = false;
        int tries=10;
        while (!displayed && tries-- > 0) {
        	countryElement = appDriver.retrievePresentMobileElement(By.name(country));
        	displayed = countryElement.isDisplayed();
        	
        	if (!displayed) {
        		appDriver.swipeElementToElementLocation(fromPoint, toPoint);
        	}     
        } 
        if (countryElement != null) {

        	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name(country)));      	
        	appDriver.retrievePresentMobileElement(By.name(country)).click(); 
        }
    }

    @Override
    public IosMobileCheckoutPage addDeliveryHomeAddress(Address address) {
        return addAddress(address);
    }

    @Override
    public IosMobileCheckoutPage addBillingAddress(Address address) {
        appDriver.scrollIntoViewAndTap("Change");
        return addAddress(address);
    }

    final Function<Address, By> existingAddressLocator = (address) -> By.xpath(String.format("//XCUIElementTypeStaticText[starts-with(@value,'%s')]", address.toIosFormattedAddress()));

    @Override
    public Boolean alreadyHasAddress(Address address) {
        appDriver.waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name("Add new delivery address")));

        if (!appDriver.isDisplayed(existingAddressLocator.apply(address))) {
            appDriver.swipe(SwipeElementDirection.UP);
        }
        return appDriver.isEnabled(existingAddressLocator.apply(address));
    }

    @Override
    public IosMobileCheckoutPage selectAddress(Address address) {
        appDriver.waitFor(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name("Add new delivery address")));

        if (!appDriver.isDisplayed(existingAddressLocator.apply(address), 3)) {
            appDriver.swipe(SwipeElementDirection.UP);
        }
        appDriver.tap(existingAddressLocator.apply(address));
        return this;
    }

    @Override
    public IosMobileCheckoutPage selectAddressByPostcode(String postCode) {
        appDriver.tap(By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name, '%s')]", postCode)));
        return this;
    }

    @Override
    public IosMobileCheckoutPage selectState(String state) {
        appDriver
                .tap(state)
                .tap("NEXT");
        return this;
    }

    private int getPickerWheelOptionSize() {
        return appDriver.retrievePresentMobileElements(By.xpath("//XCUIElementTypePickerWheel//following::XCUIElementTypeOther")).size();
    }

    private Function<IosMobileCheckoutPage, RiverIslandNativeAppDriver> nddDateTimePicker = (option) -> appDriver.scrollPickerWheelToExpectedValue(
            By.className("XCUIElementTypePickerWheel"),
            getPickerWheelOptionSize(),
            t -> !StringUtils.containsIgnoreCase(t.getText(), "unavailable"));

    @Override
    public IosMobileCheckoutPage selectDeliveryOption(DeliveryOption deliveryOption) {
        appDriver.tap(By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name, '%s')]", deliveryOption.getDescription())));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Selected delivery option: " + deliveryOption.getDescription());
        return this;
    }

    @Override
    public IosMobileCheckoutPage selectNominatedDay() {
        appDriver.tap("Day");

        if (nddDateTimePicker.apply(this) == null) {
            throw new RuntimeException("There are no available days for this nominated day delivery!");
        }

        appDriver
                .tap("Select Day")
        		.tap("Time")
        		.tap("Select Time of Day")
                .tap("CHOOSE THIS DAY");
        return this;
    }

    @Override
    public IosMobileCheckoutPage selectPreciseDayAndTime() {
        if (!appDriver.isDisplayed(By.name("Time"), 3)) {
            final String notAvailableMessage = appDriver.getAlertText();

            if (notAvailableMessage != null && notAvailableMessage.contains("Precise Delivery is not currently available for this area")) {
                throw new SkipException("There are no available times for this precise day delivery!");
            }
        }

        appDriver.tap("Day");

        if (nddDateTimePicker.apply(this) == null) {
            throw new RuntimeException("There are no available days for this nominated day delivery!");
        }

        appDriver.tap("Select Day");

        appDriver.tap("Time");

        if (nddDateTimePicker.apply(this) == null) {
            throw new RuntimeException("There are no available times for this precise day delivery!");
        }

        appDriver
                .tap("Select Time")
                .tap("CHOOSE THIS DAY AND TIME");
        return this;
    }

    @Override
    public IosMobileCheckoutPage continueToPayment() {
        appDriver.tap("CONTINUE TO PAYMENT");
        return this;
    }

    private IosMobileCheckoutPage searchLocation(String locatorText, String location) {
        checkAndAllowAccess();

        appDriver
                .touchAndType(locatorText, location)
                .retrieveMobileElement("Search").click();
        return this;
    }

    @Override
    public IosMobileCheckoutPage searchStore(String store) {
        return searchLocation("Town, place or UK postcode", store);
    }

    @Override
    public IosMobileCheckoutPage selectStore(String store) {
        appDriver.tap(store);
        return this;
    }

    @Override
    public IosMobileCheckoutPage changeDeliveryCountry(Region region) {
        appDriver
                .tap("Country")
                .tap(region.getDescription());
        return this;
    }

    private final By choosenStoreAddressLocator = By.xpath("//XCUIElementTypeButton[@name='icnInfo']//following::XCUIElementTypeStaticText[2]");

    @Override
    public IosMobileCheckoutPage populateTelephoneForDeliveryToStore() {
        appDriver
                .type("Mobile number", "7912345678")
                .tap(choosenStoreAddressLocator); // currently, we cant hide the keyboard - this is the hack!
        return this;
    }

    @Override
    public IosMobileCheckoutPage searchPickupStoreAndSelect(String address) {
        searchLocation("Town, place or UK postcode", address);
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("icnInfo")))
                .retrievePresentMobileElement(By.xpath("//XCUIElementTypeButton[@name='icnInfo']//following::XCUIElementTypeButton[1]")).click();
        return this;
    }

    @Override
    public String getPickupStoreAddress() {
        return appDriver.retrieveMobileElementText(choosenStoreAddressLocator).trim();
    }

    @Override
    public IosMobileCheckoutPage confirmCollectionDetails() {
        appDriver.scrollIntoViewAndTap("NEXT");
        return this;
    }

    @Override
    public IosMobileCheckoutPage selectPaymentMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CARD:
                appDriver.tap("Pay with Credit/Debit Card");
                break;

            case IDEAL:
                payWithIdeal();
                break;

            case GIROPAY:
                payWithGiroPay();
                break;

            case PAYPAL:
                payWithPayPal();
                break;
        }
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Selected payment method: " + paymentMethod.name());
        return this;
    }

    @Override
    public IosMobileCheckoutPage populateCardDetails(CardType cardType, Boolean saveCard) {
        final TestCard card = Payment.Builder.create(PaymentMethod.CARD).withCardType(cardType).build().getTestCard();

        appDriver
                .touchAndType("Card number", card.getCardNumber())
                .tap("Card expiry");

        final List<MobileElement> cardExpiryElements = appDriver.retrievePresentMobileElements(By.className("XCUIElementTypePickerWheel"));
        Validate.noNullElements(cardExpiryElements);

        cardExpiryElements.get(1).setValue(card.getExpiryYear());
        cardExpiryElements.get(0).setValue(card.getExpiryMonth());

        appDriver
                .click(appDriver.retrieveMobileElement(card.getExpiryMonthAndYear()))
                .type("CVV", card.getSecurityCode().toString());

        if (saveCard) {
            saveCard();
        }
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added payment card: " + card.toString());
        return this;
    }

    @Override
    public IosMobileCheckoutPage saveCard() {
        appDriver.tap("icnCheckBoxInactive");
        return this;
    }

    @Override
    public IosMobileCheckoutPage payWithCard() {
        appDriver.tap("PAY WITH CARD");
        return this;
    }

    @Override
    public IosMobileCheckoutPage populateCCVForSavedCard(CardType cardType) {
        final TestCard card = Payment.Builder.create(PaymentMethod.CARD).withCardType(cardType).build().getTestCard();

        appDriver
                .type("Please enter your CVV", String.valueOf(card.getSecurityCode()))
                .tap("Done");
        return this;
    }

    @Override
    public IosMobileCheckoutPage payWithIdeal() {
        appDriver.tap("Pay with Ideal");
        return this;
    }

    @Override
    public IosMobileCheckoutPage processIdealPayTransaction() {
        appDriver
                .tap(By.xpath("(//*[contains(@value,'Enter your Payment Details')]//following::XCUIElementTypeLink)[1]"))
                .click(By.name("Continue"));
        return this;
    }

    @Override
    public IosMobileCheckoutPage payWithGiroPay() {
        appDriver.tap("Pay with Giropay");
        return this;
    }

    @Override
    public IosMobileCheckoutPage processGiroPayTransaction() {
    	final By scLocator = By.xpath("//XCUIElementTypeStaticText[@name='sc:']/../XCUIElementTypeTextField");
    	final By extensionScLocator = By.xpath("//XCUIElementTypeStaticText[@name='extensionSc:']/../XCUIElementTypeTextField");
        final GiroPayCredentials giroPayCredentials = Payment.Builder.create(PaymentMethod.GIROPAY).build().getGiroPayCredentials();

        appDriver
                .touchAndType("Bank account holder name", giroPayCredentials.getAccountName())
                .touchAndType("Bank account number", giroPayCredentials.getAccountNumber())
                .touchAndType("Type the Bankleitzahl", giroPayCredentials.getBankLoc());

        performGiroPay();
        
		appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(scLocator), 10000);
        populateGiroElements(scLocator, "10");
        populateGiroElements(extensionScLocator, "4000");
        appDriver.tap("Absenden");
        return this;
    }
    
    protected void performGiroPay() {
        appDriver.tap("Done")
        		 .tap("pay");
    }
    
    private void populateGiroElements(By locator, String value) {
        MobileElement scElement = appDriver.retrieveMobileElement(locator);
        appDriver.touch(scElement);
        scElement.sendKeys(value);
    }

    @Override
    public IosMobileCheckoutPage payWithPayPal() {
        appDriver.tap("Pay with PayPal");
        return this;
    }

    @Override
    public IosMobileCheckoutPage processPayPalTransaction() {
    	final PayPalCredentials payPalCredentials = Payment.Builder.create(PaymentMethod.PAYPAL).build().getPayPalCredentials();

        final By emailLocator = By.className("XCUIElementTypeTextField");
        final By passwordLocator = By.xpath("//XCUIElementTypeSecureTextField[@value='Password']");

        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Log in to PayPal")))
                .clear(emailLocator)
                .doubleTap(emailLocator);

        final By selectAllLocator = By.name("Select All");

        // needed for smaller screens (iPhone 5 etc)
        if (appDriver.isDisplayed(selectAllLocator, 2)) {
            appDriver
                    .click(selectAllLocator)
                    .click(By.name("Cut"));
        }

        appDriver.retrieveMobileElement(emailLocator).sendKeys(payPalCredentials.getUserName());
        MobileElement passwordElement = appDriver.retrieveMobileElement(passwordLocator);
        appDriver.touch(passwordElement);
        passwordElement.sendKeys(payPalCredentials.getPassword());
        appDriver.tap("Log In");
        
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Choose a way to pay")))
                .pause(1000);

        appDriver
                .swipe(SwipeElementDirection.UP)
                .tap("Continue");
        
        appDriver
        		.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Pay with")))
        		.pause(1000)
        		.tap("Continue");
        
        return this;
    }

    private void waitForOrderDetailsDisplayed() {
    	final By orderDetailsLocator = By.xpath("//XCUIElementTypeNavigationBar[@name='Order details']");

    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(orderDetailsLocator));
    	
    }
    private Function<String, By> orderSummaryLocator = (label) -> By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']//following::XCUIElementTypeStaticText[1]", label));

    @Override
    public String getSummaryCardType() {
    	waitForOrderDetailsDisplayed();
        return appDriver.retrieveMobileElementText(orderSummaryLocator.apply("Card details")).trim();
    }

    @Override
    public String getSummaryDeliveryAddress() {
    	waitForOrderDetailsDisplayed();
        return appDriver.retrievePresentMobileElement(orderSummaryLocator.apply("Delivery address")).getText();
    }

    @Override
    public String getSummaryDeliveryOption(DeliveryOption deliveryOption) {
        final By deliveryOptionLocator = By.xpath(String.format("//XCUIElementTypeStaticText[contains(@name,'%s')]", StringUtils.substringBeforeLast(deliveryOption.getDescription(), " ")));

        return appDriver
                .swipe(SwipeElementDirection.UP)
                .retrievePresentMobileElement(deliveryOptionLocator)
                .getText();
    }

    @Override
    public String getSummarySubTotal() {
        return appDriver.retrievePresentMobileElement(orderSummaryLocator.apply("Sub-total")).getText();
    }

    @Override
    public String getSummaryShippingTotal() {
        return appDriver.retrievePresentMobileElement(By.xpath("//XCUIElementTypeStaticText[@name='Shipping']//following::XCUIElementTypeStaticText[not(contains(translate(@name,'Delivery','delivery'), 'delivery'))][1]")).getText();
    }

    @Override
    public String getSummaryGrandTotal() {
        return appDriver.retrievePresentMobileElement(orderSummaryLocator.apply("Total to pay")).getText();
    }

    @Override
    public IosMobileCheckoutPage completePayment() {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Completing payment");

        appDriver
                .waitFor(ExpectedConditions.presenceOfElementLocated(orderSummaryLocator.apply("Delivery address")))
                .scrollIntoView("By clicking continue you agree to our Terms & Conditions");

        appDriver.tap(By.xpath("//XCUIElementTypeButton[contains(@name,'CONFIRM') or contains(@name,'CONTINUE') or contains(@name, 'CHECKOUT')]"));
        return this;
    }

    @Override
    public String getOrderNumber() {
        final String orderNumber = StringUtils.substringAfter(appDriver.retrieveMobileElementText(By.xpath("//XCUIElementTypeStaticText[contains(@name,'Your order number is')]")), "is ").trim();
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Order created: " + orderNumber);
        return orderNumber;
    }

    @Override
    public String getOrderCompleteAddress() {
    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Thank you!")));
        final String orderCompletionAddress = StringUtils.substringBetween(
        		appDriver
                        .retrieveMobileElementText(By.xpath("//XCUIElementTypeStaticText[starts-with(@name,'Your order is estimated to arrive at')]")),
                "Your order is estimated to arrive at\n",
                "\non the");

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Order will arrive at: " + orderCompletionAddress);
        return orderCompletionAddress;
    }

    @Override
    public IosMobileCheckoutPage continueShopping() {
        appDriver.tap("CONTINUE SHOPPING");
        return this;
    }

    @Override
    public IosMobileCheckoutPage complete3dSecure() {
    	appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Submit")));
        appDriver
                .touchAndType("User Name:", "user")
                .touchAndType("Password:", "password")
                .tap("Submit");
        return this;
    }

    private void checkAndAllowAccess() {
        final String allowAccess = "Allow";

        if (appDriver.isDisplayed(By.name(allowAccess), 2)) {
            appDriver.tap(allowAccess);
        }
    }
}