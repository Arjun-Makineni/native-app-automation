package com.riverisland.android.test.pages;

import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.collect.Lists;
import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Payment;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.CheckoutPage;
import com.riverisland.app.automation.pojos.GiroPayCredentials;
import com.riverisland.app.automation.pojos.PayPalCredentials;
import com.riverisland.app.automation.pojos.TestCard;
import com.riverisland.automation.utils.core.error.RiverIslandTestError;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidCheckoutPage extends AndroidCorePage implements CheckoutPage<AndroidCheckoutPage> {

    public AndroidCheckoutPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidCheckoutPage selectDeliveryType(DeliveryType deliveryType) {
        appDriver.tap(deliveryType.getDescription());

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Selected delivery type: " + deliveryType.getDescription());
        return this;
    }

    @Override
    public AndroidCheckoutPage openAddNewDeliveryAddress() {
        final By addAddressButton = androidId.apply("address_add_button");

        if (appDriver.isDisplayed(addAddressButton, 2)) {
            appDriver.tap(addAddressButton);
        }
        return this;
    }

    @Override
    public AndroidCheckoutPage addDeliveryHomeAddress(Address address) {
        addAddress(address);
        return this;
    }

    @Override
    public AndroidCheckoutPage addBillingAddress(Address address) {

    	appDriver.swipeElementToElementLocation(
    			By.xpath("//android.widget.EditText[@text='CVV']"), 
    			By.xpath("//android.widget.TextView[@text='Card details']"));
        appDriver.tap("Add new billing address");
        addAddress(address);
        return this;
    }

    @Override
    public Boolean alreadyHasAddress(Address address) {
        int searches = 3;
        while (searches-- > 0) {
            if (appDriver.isDisplayed(androidId.apply("address"), 2)) {
                final List<MobileElement> addresses = appDriver.retrieveMobileElements(androidId.apply("address"));

                if (appDriver.retrieveMobileElements(androidId.apply("address")).stream().anyMatch(t -> t.getText().contains(address.getAddressLine1()))) {
                    return true;
                }

                Point firstAddress = addresses.get(0).getLocation();

                if (addresses.size() > 1) {
                    Point secondAddress = addresses.get(1).getLocation();
                    new TouchAction(appDriver.getWrappedAndroidDriver()).longPress(PointOption.point(secondAddress.getX(), secondAddress.getY()))
                                                                        .moveTo(PointOption.point(firstAddress.getX(), firstAddress.getY()))
                                                                        .release()
                                                                        .perform();
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private Consumer<String> addressSelector = (address) -> appDriver.retrieveMobileElements(androidId.apply("address")).stream().filter(t -> t.getText().contains(address)).findFirst().orElseThrow(() -> new RiverIslandTestError("Address is not present!")).click();

    @Override
    public AndroidCheckoutPage selectAddress(Address address) {
        addressSelector.accept(address.getAddressLine1());
        return this;
    }

    @Override
    public AndroidCheckoutPage selectAddressByPostcode(String postCode) {
        addressSelector.accept(postCode);
        return this;
    }

    @Override
    public AndroidCheckoutPage selectState(String state) {
    	
    	TouchAction action = new TouchAction(appDriver.getWrappedAndroidDriver());
		
        while (true) {
            final List<MobileElement> options = appDriver.retrieveMobileElements(androidId.apply("state_code_state_text"));

            final String lastOption = options.stream().reduce((x, y) -> y).get().getText();

            if (options.stream().anyMatch(t -> t.getText().equals(state))) {
                appDriver.tap(state)
                		.tap("NEXT");
                
                break;
            } else {
            	action.longPress(PointOption.point(options.get(options.size()-1).getLocation().x, options.get(options.size()-1).getLocation().y))
            	      .moveTo(PointOption.point(options.get(0).getLocation().x, options.get(0).getLocation().y))
            	      .release()
            	      .perform();;
            }

            if (lastOption.equals("WY- Wyoming")) {
                throw new RiverIslandTestError("State not found in available options: " + state);
            }
        }
        return this;
    }

    @Override
    public AndroidCheckoutPage selectDeliveryOption(DeliveryOption deliveryOption) {
    	appDriver.waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(
    			By.xpath(String.format("//android.widget.TextView[@text='%s']", deliveryOption.getAndroidDescription()))), 10000);
        appDriver.tap(deliveryOption.getAndroidDescription());
        return this;
    }

    @Override
    public AndroidCheckoutPage selectNominatedDay() {
        return selectPreciseDayAndTime();
    }

    private static final String CHOOSE_DAY = "CHOOSE THIS DAY";
    @Override
    public AndroidCheckoutPage selectPreciseDayAndTime() {
    	appDriver.waitFor(
    			ExpectedConditions.visibilityOfAllElementsLocatedBy(
    					By.xpath(String.format("//android.widget.TextView[@text='%s']", CHOOSE_DAY))), 10000);

    	appDriver.tap(androidId.apply("nominated_day_value")); 	
    	List<MobileElement> timeSlots = appDriver.retrieveMobileElements(androidId.apply("size_picker_radio"));
    	assertTrue(timeSlots != null && !timeSlots.isEmpty(), "No time slots for delivery"); 	
    	timeSlots.get(0).click();
    	
    	appDriver.tap(CHOOSE_DAY);
        return this;
    }

    @Override
    public AndroidCheckoutPage continueToPayment() {
        return this;
    }

    @Override
    public AndroidCheckoutPage searchStore(String store) {
        allowLocationCheck(appDriver);

        appDriver
                .tap(androidId.apply("search_view"))
                .type(androidId.apply("search_view"), store)
                .pressAndroidKey(AndroidKeyCode.ENTER);
        return this;
    }

    @Override
    public AndroidCheckoutPage selectStore(String store) {
        appDriver.scrollIntoViewAndTap(store);
        return this;
    }

    @Override
    public AndroidCheckoutPage changeDeliveryCountry(Region region) {
        throw new NotImplementedException("Method not supported.");
    }

    @Override
    public AndroidCheckoutPage populateTelephoneForDeliveryToStore() {
        appDriver.type(androidId.apply("pickup_collection_details_number"), "7912345678");
        return this;
    }

    @Override
    public AndroidCheckoutPage searchPickupStoreAndSelect(String address) {
        allowLocationCheck(appDriver);

        appDriver
                .tap(androidId.apply("search_view"))
                .type(androidId.apply("search_view"), address)
                .pressAndroidKey(AndroidKeyCode.ENTER)
                .waitFor(ExpectedConditions.numberOfElementsToBeMoreThan(androidId.apply("pickup_store_address"), 1))
                .tap(androidId.apply("pickup_store_address"));
        return this;
    }

    @Override
    public String getPickupStoreAddress() {
        return appDriver
                .waitFor(ExpectedConditions.textMatches(androidId.apply("pickup_collection_details_address"), Pattern.compile("\\w+")))
                .retrieveMobileElementText(androidId.apply("pickup_collection_details_address"));
    }

    @Override
    public AndroidCheckoutPage confirmCollectionDetails() {
    	appDriver.swipeElementToElementLocation(
    			appDriver.retrieveMobileElement(By.xpath("//android.widget.TextView[@text='You must bring with you']")).getLocation(), 
    			appDriver.retrieveMobileElement(By.xpath("//android.widget.TextView[contains(@text, 'email when your order arrives')]")).getLocation());

    	appDriver.tap("NEXT");
        return this;
    }

    private static final By PAY_WITH_CREDIT_DEBIT_CARD_LOCATOR = By.xpath("//*[@text='Pay with Credit/Debit Card']");
    @Override
    public AndroidCheckoutPage selectPaymentMethod(PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CARD:
                appDriver.retrieveMobileElement(PAY_WITH_CREDIT_DEBIT_CARD_LOCATOR).click();
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
    public AndroidCheckoutPage populateCardDetails(CardType cardType, Boolean saveCard) {
        final TestCard card = Payment.Builder.create(PaymentMethod.CARD).withCardType(cardType).build().getTestCard();

        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(androidId.apply("pay_with_card_number")))
                .tap(androidId.apply("pay_with_card_number"))
                .type(androidId.apply("pay_with_card_number"), card.getCardNumber())
                .tap(androidId.apply("pay_with_card_expiry_layout"));

        selectExpiry(card.getExpiryMonth(), card.getExpiryYear());
        
        appDriver.type(androidId.apply("pay_with_card_cvv"), card.getSecurityCode().toString())
                 .hideKeyboard();
        
        if (saveCard) {
            saveCard();
        }

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added payment card: " + card.toString());
        return this;
    }

    private void selectExpiry(String month, String year) {
    	
        final List<MobileElement> cardExpiryElements = appDriver.retrieveMobileElements(By.id("android:id/numberpicker_input"));
        Validate.isTrue(cardExpiryElements.size() == 2);

        final MobileElement monthElement = appDriver.retrieveMobileElement(By.xpath("//android.widget.NumberPicker[@index='0']//android.widget.EditText"));
        final MobileElement yearElement = appDriver.retrieveMobileElement(By.xpath("//android.widget.NumberPicker[@index='1']//android.widget.EditText"));
        
    	TouchAction touchAction = new TouchAction(appDriver.getWrappedAndroidDriver());       
        touchAction.longPress(PointOption.point(yearElement.getCenter().getX(), yearElement.getCenter().getY()))
                   .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                   .release()
                   .perform(); 
        yearElement.clear();
        yearElement.sendKeys(year);
        
        touchAction.longPress(PointOption.point(monthElement.getCenter().getX(), monthElement.getCenter().getY()))
        		   .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
        		   .release()
        		   .perform(); 
        monthElement.clear();
        monthElement.sendKeys(StringUtils.substring(month, 0, 3));
        touchAction.longPress(PointOption.point(yearElement.getCenter().getX(), yearElement.getCenter().getY()))
        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
        .release()
        .perform();
        
        appDriver.retrieveMobileElement(androidId.apply("dialog_positive_button")).click();
    }
    
    @Override
    public AndroidCheckoutPage saveCard() {
        appDriver.tap(androidId.apply("pay_with_card_save"));
        return this;
    }

    @Override
    public AndroidCheckoutPage payWithIdeal() {
        appDriver.tap("Pay with Ideal");
        return this;
    }

    @Override
    public AndroidCheckoutPage processIdealPayTransaction() {
        final By testBankLocator = By.xpath("//android.view.View["
        		+ "contains(@text,'TESTBANK1') "
        		+ "or contains(@content-desc, 'TESTBANK1')"
        		+ "]");
        final By idealPaymentContinueLocator = By.xpath("//android.widget.Button[@text='Continue' or @content-desc='Continue']");
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(testBankLocator))
                 .tap(testBankLocator)
                 .waitFor(ExpectedConditions.visibilityOfElementLocated(idealPaymentContinueLocator))
                 .tap(idealPaymentContinueLocator);
        return this;
    }

    @Override
    public AndroidCheckoutPage payWithGiroPay() {
        appDriver.tap("Pay with Giropay");
        return this;
    }

    @Override
    public AndroidCheckoutPage payWithCard() {
        appDriver.scrollIntoViewAndTap("PROCEED TO PAYMENT");
        return this;
    }

    @Override
    public AndroidCheckoutPage populateCCVForSavedCard(CardType cardType) {
        final TestCard card = Payment.Builder.create(PaymentMethod.CARD).withCardType(cardType).build().getTestCard();
        appDriver.type(androidId.apply("pay_with_card_cvv"), card.getSecurityCode().toString());
        return this;
    }

    @Override
    public AndroidCheckoutPage processGiroPayTransaction() {
        final GiroPayCredentials giroPayCredentials = Payment.Builder.create(PaymentMethod.GIROPAY).build().getGiroPayCredentials();
        final By accountNameLocator = By.xpath("//android.widget.EditText[@resource-id='giropay.accountHolderName']");
        final By bankAccountNumberLocator = By.xpath("//android.widget.EditText[@resource-id='giropay.bankAccountNumber']");
        final By bankleitzahlLocator = By.xpath("//*[@resource-id='giropay.bankLocationId']");

        appDriver
                .type(appDriver.retrieveMobileElement(accountNameLocator), giroPayCredentials.getAccountName())
                .type(appDriver.retrieveMobileElement(bankAccountNumberLocator), giroPayCredentials.getAccountNumber());
        
        MobileElement bankleitzahlElement = appDriver.retrieveMobileElement(bankleitzahlLocator);
        appDriver.touch(bankleitzahlElement);
        
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_4));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_4));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_4));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_4));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.DIGIT_8));
        
        appDriver.getWrappedAndroidDriver().pressKey(new KeyEvent(AndroidKey.ENTER));
      
        final By absendenLocator = By.xpath("//android.widget.Button[@text='Absenden' or @content-desc='Absenden']");
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(absendenLocator))
                .type(By.xpath("(//*[@class='android.widget.EditText'])[1]"), "10")
                .type(By.xpath("(//*[@class='android.widget.EditText'])[2]"), "4000")
                .click(absendenLocator);
        return this;
    }

    @Override
    public AndroidCheckoutPage payWithPayPal() {
        appDriver.tap("Pay with PayPal");
        return this;
    }

    private static final long ONE_MINUTE = 60000;
    private static final long ONE_SECOND = 1000;
    @Override
    public AndroidCheckoutPage processPayPalTransaction() {
        final PayPalCredentials payPalCredentials = Payment.Builder.create(PaymentMethod.PAYPAL).build().getPayPalCredentials();

        final By emailLocator = By.xpath("//*[@resource-id='email']");

        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(emailLocator), ONE_MINUTE)
                .clear(emailLocator)
                .setValue(emailLocator, payPalCredentials.getUserName())
                .setValue(By.xpath("//*[@resource-id='password']"), payPalCredentials.getPassword())
                .tap(By.xpath("//*[@resource-id='btnLogin']"));
        
        final By continueBtn = By.xpath("//android.widget.Button[@text='Continue' or @content-desc='Continue']");
        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@text='Choose a way to pay' or @content-desc='Choose a way to pay']")))
                .pressAndroidKey(AndroidKeyCode.KEYCODE_PAGE_DOWN)
                .tap(continueBtn);
        appDriver
                .pause(ONE_SECOND);
        appDriver
        		.tap(continueBtn);
        
        return this;
    }

    private final String summaryLookupPattern = "//android.widget.TextView[contains(@text, '%s')]//following::android.widget.TextView[1]";

    private Function<String, String> summaryLookup = (label) -> appDriver.retrieveMobileElementText(By.xpath(String.format(summaryLookupPattern, label)));

    @Override
    public String getSummaryCardType() {
    	appDriver.swipeElementToElementLocation(
    			By.xpath("//android.widget.TextView[@text='Delivery address']"), 
    			By.xpath("//android.widget.TextView[@text='Delivery']"));
        appDriver.waitFor(ExpectedConditions.attributeContains(By.xpath(String.format(summaryLookupPattern, "Card details")), "text", "xxxx"));
        return "x" + StringUtils.substringAfter(summaryLookup.apply("Card details"), " x");
    }

    @Override
    public String getSummaryDeliveryAddress() {
        return summaryLookup.apply("Delivery address");
    }

    @Override
    public String getSummaryDeliveryOption(DeliveryOption deliveryOption) {
        moveToOrderSummarySection();
        return appDriver.retrieveMobileElementText(By.xpath(String.format("//android.widget.TextView[contains(@text,'%s')]", deliveryOption.getAndroidDescription())));
    }

    @Override
    public String getSummarySubTotal() {
        return summaryLookup.apply("Sub-total");
    }

    @Override
    public String getSummaryShippingTotal() {
        return summaryLookup.apply("Shipping");
    }

    @Override
    public String getSummaryGrandTotal() {
        return summaryLookup.apply("Total to pay");
    }

    @Override
    public AndroidCheckoutPage completePayment() {
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Completing payment");

        final By completePaymentLocator = androidId.apply("sticky_summary_button");
        appDriver.isDisplayed(completePaymentLocator, 2);
        appDriver.tap(completePaymentLocator);
        return this;
    }

    @Override
    public String getOrderNumber() {
        final String orderNumber = appDriver.retrieveMobileElementText(androidId.apply("order_complete_order_number"));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Order created: " + orderNumber);
        return orderNumber;
    }

    @Override
    public String getOrderCompleteAddress() {
        return StringUtils.substringBetween(appDriver.retrieveMobileElementText(androidId.apply("order_complete_details")), "Your order is estimated to arrive at ", " on").trim();
    }

    @Override
    public AndroidCheckoutPage continueShopping() {
        appDriver.tap(androidId.apply("order_complete_button"));
        return this;
    }

    @Override
    public AndroidCheckoutPage complete3dSecure() {
        appDriver
                .type(By.xpath("//*[@resource-id='username']"), "user")
                .type(By.xpath("//*[@resource-id='password']"), "password")
                .tap(By.xpath("//android.widget.Button[@text='Submit' or @content-desc='Submit']"));
        return this;
    }

    private void selectCountry(Address address) {

        while (true) {
            final List<MobileElement> options = appDriver.retrieveMobileElements(androidId.apply("size_picker_text"));

            final String lastOption = options.stream().reduce((x, y) -> y).get().getText();

            if (options.stream().anyMatch(t -> t.getText().equals(address.getCountry()))) {
                appDriver.tap(address.getCountry());
                break;
            } else {
                appDriver
                        .pressAndroidKey(AndroidKeyCode.KEYCODE_PAGE_DOWN);
            }

            if (lastOption.equals("Vietnam")) {
                throw new RiverIslandTestError("Country not found in available options: " + address.getCountry());
            }
        }
    }
    
    private AndroidCheckoutPage addAddress(Address address) {
        appDriver.tap("Country");
    	selectCountry(address);
    	
        appDriver
                .tap("Title")
                .tap("Mr.");
        appDriver
                .type(By.xpath("//android.widget.EditText[@text='First Name']"), address.getRecipientFirstName())
                .type(By.xpath("//android.widget.EditText[@text='Last Name']"), address.getRecipientLastName())
                .type(By.xpath("//android.widget.EditText[@text='Phone/Mobile']"), address.getTelephone())
                .scrollIntoView(address.getRegion().equals(Region.GB) ? "Lookup address" : "NEXT");

        appDriver
                .type(By.xpath("//android.widget.EditText[@text='Postcode']"), address.getPostCode());

        if (address.getAddressLookup()) {
            appDriver
                    .tap("Lookup address")
                    .retrieveMobileElements(androidId.apply("size_picker_text")).stream().filter(lookupAddress -> lookupAddress.getText().contains(address.getPostCode())).findFirst().orElseThrow(() -> new RuntimeException("Postcode lookup did not return any results!")).click();
        } else {
            if (address.getRegion().equals(Region.GB)) {
                appDriver.tap("Enter manually");
            }

            focusOnAddressElement("Address line 1");
            
            appDriver
            		.type(By.xpath("//android.widget.EditText[@text='Address line 1']"), address.getAddressLine1());
            
            focusOnAddressElement("Town/city");
            appDriver
                    .type(By.xpath("//android.widget.EditText[@text='Town/city']"), address.getTownOrCity());
        }

        appDriver.scrollIntoViewAndTap("NEXT");

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Added new delivery address");
        return this;
    }

    private void moveToOrderSummarySection() {
        Lists.newArrayList("Card details", "Order summary").forEach(label -> {
        	if (appDriver.isDisplayed(By.xpath(String.format("//*[@text='%s']", label)), 1)) {
        		final Point point = appDriver.retrieveMobileElement(label).getLocation();
        		new TouchAction(appDriver.getWrappedAndroidDriver()).longPress(PointOption.point(point.getX(), point.getY()))
            													.moveTo(PointOption.point(point.getX(), 1))
            													.release()
            													.perform();
        	}
        });
    }
    
    private void focusOnAddressElement(String text) {
    	
    	if (!appDriver.isDisplayed(By.xpath(String.format("//*[@text='%s']", text))))
    	{  	
    		WebElement element = appDriver.getWrappedAndroidDriver()
				                      .findElementByAndroidUIAutomator(
				                         "new UiScrollable(new UiSelector().resourceIdMatches(\".*id/form_scroll_view\"))"
				                         + ".scrollIntoView(new UiSelector().text(\"" + text + "\"))");

    		MobileElement phoneElement = appDriver.retrieveMobileElement("Phone/Mobile");

    		Point point = element.getLocation();
    		Point toPoint = phoneElement.getLocation();
		
    		TouchAction action = new TouchAction(appDriver.getWrappedAndroidDriver());
    		action.longPress(PointOption.point(point.x, point.y))
		 	  .moveTo(PointOption.point(point.x, toPoint.y))
		 	  .release()
		 	  .perform();	
    	}
    }
}