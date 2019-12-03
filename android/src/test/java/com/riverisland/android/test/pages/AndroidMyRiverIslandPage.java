package com.riverisland.android.test.pages;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MyRiverIslandPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidKeyCode;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class AndroidMyRiverIslandPage extends AndroidCorePage implements MyRiverIslandPage<AndroidMyRiverIslandPage> {

    public AndroidMyRiverIslandPage(RiverIslandNativeAppDriver appDriver) {
        super(appDriver);
    }

    @Override
    public AndroidMyRiverIslandPage openSignIn() {
        scrollToHome();
        appDriver.tap("Sign in");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage signIn(String email, String password) {
        if (StringUtils.isNotBlank(email)) {
            appDriver.type(androidId.apply("login_email_edit"), email);
        }

        appDriver
                .type(androidId.apply("login_password_edit"), password)
                .tap("SIGN IN")
                .waitFor(ExpectedConditions.invisibilityOfElementLocated(androidId.apply("login_email_edit")));
        
        // Wait for Welcome user to fade out
        welcomeWait();
        return this;
    }
    private static final long WELCOME_WAIT = 3000; // 3 secs
    private void welcomeWait() {
    		appDriver.pause(WELCOME_WAIT);
    }
    
    @Override
    public AndroidMyRiverIslandPage signOut(boolean checkFirst) {
        scrollToHome();

        final By signOutLocator = androidText.apply("Sign out");
        appDriver.scrollIntoView("Recently viewed");
        
        if (!appDriver.isDisplayed(By.xpath("//*[contains(@text, 'Sign')]"), 2)) {
        	appDriver.swipeElementToElementLocation(
    			By.xpath("//*[@text='Recently viewed']"), 
    			By.xpath("//*[@text='Home']"));
        }
        if (checkFirst) {
            if (appDriver.isDisplayed(signOutLocator)) {
                appDriver.tap(signOutLocator);
            } else {
                appDriver.tap("Home");
            }
        } else {
            appDriver.tap(signOutLocator);
        }
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage signUp(Customer customer) {
    	final By firstNameLocator = By.xpath("//android.widget.EditText[@text='First name']");
    	final By lastNameLocator = By.xpath("//android.widget.EditText[@text='Last name']");
    	final By emailAddressLocator = By.xpath("//android.widget.EditText[@text='Email address']");
    	final By confirmEmailAddressLocator = By.xpath("//android.widget.EditText[@text='Confirm email address']");
    	final By passwordLocator = By.xpath("//android.widget.EditText[@text='Password']");
    	final By confirmPasswordLocator = By.xpath("//android.widget.EditText[@text='Confirm password']");
    	
        appDriver.tap(androidId.apply("login_signup_text"))
    			 .tap("Title")    
                 .tap(null != customer.getTitle() ? customer.getTitle() : "----");
        appDriver.type(firstNameLocator, customer.getFirstName())
        		 .type(lastNameLocator, customer.getLastName())
        		 .hideKeyboard()
                 .type(emailAddressLocator, customer.getEmailAddress())
                 .type(confirmEmailAddressLocator, customer.getEmailAddress())      
        		 .waitFor(ExpectedConditions.visibilityOfElementLocated(passwordLocator))
                 .type(passwordLocator, customer.getPassword())
        		 .type(confirmPasswordLocator, customer.getPassword());
        appDriver.scrollIntoView("CREATE ACCOUNT");
        appDriver.tap(androidId.apply("form_submit_button"));

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Signed up customer: " + customer.toString());
        return this;
    }

    @Override
    public Boolean isSignedOut() {
        appDriver.scrollIntoView("Home");
        return appDriver.retrieveMobileElement("Sign in").isDisplayed();
    }

    @Override
    public Boolean isMyAccountDisplayed() {
        return appDriver.retrieveMobileElement(androidId.apply("my_account_menu_item_title")).isDisplayed();
    }

    @Override
    public AndroidMyRiverIslandPage openOrderHistory() {
        appDriver.scrollIntoViewAndTap("Order History");
        return this;
    }

    @Override
    public Boolean isOrderHistoryEmpty() {
        return null;
    }

    @Override
    public AndroidMyRiverIslandPage viewOrder(String orderNumber) {
        appDriver.tap(String.format("Order number - %s", orderNumber));
        return this;
    }

    private Function<String, By> orderHistoryInfo = (label) -> By.xpath(String.format("//android.widget.TextView[@text='%s']//following::android.widget.TextView", label));

    @Override
    public String getOrderDetailsOrderNumber() {
        return appDriver.retrieveMobileElementText(orderHistoryInfo.apply("Order number"));
    }

    @Override
    public String getOrderDetailsStatus() {
        return appDriver.retrieveMobileElementText(orderHistoryInfo.apply("Order Status"));
    }

    @Override
    public String getOrderDetailsPlacedOn() {
        return appDriver.retrieveMobileElementText(orderHistoryInfo.apply("Placed on"));
    }

    @Override
    public String getOrderDetailsDeliveryAddress() {
        return appDriver.retrieveMobileElementText(orderHistoryInfo.apply("Delivery address"));
    }

    @Override
    public String getOrderDetailsPaymentMethod() {
        return appDriver.retrieveMobileElementText(orderHistoryInfo.apply("Card details"));
    }

    @Override
    public AndroidMyRiverIslandPage openFaq() {
        appDriver.tap("FAQ");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewFaqSection(String... section) {
        Arrays.asList(section).forEach(e -> appDriver.tap(e));
        return this;
    }

    @Override
    public Boolean isFaqContentDisplayed() {
        return appDriver.retrieveMobileElement(By.xpath("//android.view.View[@content-desc != '' or @text != '']")) != null;
    }

    @Override
    public AndroidMyRiverIslandPage signUpToNewsletters(String email, String category) {
    	final By categoryLocator = By.xpath(String.format("//android.widget.CheckBox[@text='%s']", category));
        appDriver
                .tap("Sign up to email")
                .type("Email address", email);
        
        appDriver.tap(categoryLocator);
        appDriver.tap(androidId.apply("sign_up_button"));
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewSizeGuides() {
        appDriver.tap("Size guides");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewSizeGuideCategory(String sizeGuide) {
        appDriver.tap(sizeGuide);
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewDelivery() {
        appDriver.tap("Delivery");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewContactUs() {
        appDriver.tap("Contact us");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage viewTermsAndConditions() {
        appDriver.tap("Terms & conditions");
        return this;
    }

    @Override
    public Boolean isTermsAndConditionsDisplayed() {
        return appDriver.retrieveMobileElement(androidTextOrContentDesc.apply("*", "Definitions")).isDisplayed();
    }

    @Override
    public AndroidMyRiverIslandPage openRecentlyViewed() {
        appDriver.scrollIntoViewAndTap("Recently viewed");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage openChangeCurrency() {
        appDriver.scrollIntoViewAndTap("Change currency");
        return this;
    }

    // Note. Using appDriver.scrollIntoViewAndTap() is not full proof here. There's a chance the scroll will move past the element because the elements are lazily loaded.
    @Override
    public AndroidMyRiverIslandPage selectCurrency(Region region) {
        while (true) {
            final List<MobileElement> countries = appDriver.retrieveMobileElements(androidId.apply("currency_country_name_text"));

            if (countries.stream().anyMatch(t -> t.getText().equalsIgnoreCase(region.getDescription()))) {
                break;
            } else {
                final String lastCountry = countries.stream().reduce((x, y) -> y).get().getText();
            	acknowledgeGetMeNow();
                int requiredTabs = countries.size() + 1;
                while (requiredTabs-- > 0) {
                    appDriver.pressAndroidKey(AndroidKeyCode.KEYCODE_TAB);
                }

                while (appDriver.isDisplayed(androidText.apply(lastCountry))) {
                	appDriver.pressAndroidKey(AndroidKeyCode.KEYCODE_TAB);
                }
            }
        }

        appDriver.tap(region.getDescription());

        if (appDriver.isDisplayed(androidText.apply(region.getDescription()))) {
            appDriver.back();
        }

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Changed currency to: " + region.getDescription() + " - " + region.getCurrency().getSymbol());
        return this;
    }

    @Override
    public Boolean isCurrencySelected(Region region) {
        return appDriver.isEnabled(By.xpath(
                String.format(
                        "//android.widget.TextView[@text='%s']//following::android.widget.ImageView[contains(@resource-id,'currency_selected_image')][1]",
                        region.getDescription())));
    }

    @Override
    public AndroidMyRiverIslandPage openStoreLocator() {
        appDriver.scrollIntoViewAndTap("Store locator");
        if (appDriver.isDisplayed(By.xpath("//*[@text=\"Allow River Island QA to access this device's location?\"]"), 5)) {
        	appDriver.tap("ALLOW");
        }
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage openScanInStore() {
        appDriver.scrollIntoViewAndTap("Scan in-store");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage openSettings() {
        appDriver.scrollIntoViewAndTap("Settings");
        return this;
    }

    @Override
    public Boolean isSettingsValid(String... settings) {
        AtomicBoolean settingsResult = new AtomicBoolean();

        Arrays.asList(settings).forEach(setting -> settingsResult.set(appDriver.retrieveMobileElement(androidText.apply(setting)).isDisplayed()));

        return settingsResult.get();
    }

    @Override
    public AndroidMyRiverIslandPage openHelpAndSupport() {
        appDriver.scrollIntoViewAndTap("Help & support");
        return this;
    }

    @Override
    public AndroidMyRiverIslandPage forgotPassword(String email) {
    	final By emailTextLocator = By.xpath("//android.widget.EditText[@resource-id ='ForgottenPasswordEmail']");
    	final String forgotPasswordText = "'just give us your email address and we will send you a link so that you can choose a new one'";
    	
        appDriver
                .tap("Forgot Password?")
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.xpath(String.format("//*[contains(@text, %s) or contains(@content-desc, %s)]", forgotPasswordText, forgotPasswordText))));

        appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(emailTextLocator))
                .type(emailTextLocator, email)
                .scrollIntoViewSilently("", 3000);
        
        closeCookieNotification();
        appDriver.tapUntil(By.xpath("//android.widget.Button[@text='SEND EMAIL' or @content-desc='SEND EMAIL']"), NoSuchElementException.class);
        return this;
    }

    private void closeCookieNotification() {
    	final String cookiePageLocator = "//android.view.View[@resource-id='cookie-notifcation-banner']";
    	final String cookieCloseLocator = cookiePageLocator + "//android.widget.Button";
    	
    	//Check if cookie message is displayed
    	if (appDriver.isDisplayed(By.xpath(cookiePageLocator)) &&
    		appDriver.isDisplayed(By.xpath(cookieCloseLocator))) {
    		
    		appDriver.click(By.xpath(cookieCloseLocator));
    	}    	
    }
    
    private void scrollToHome() {
        appDriver.scrollIntoView("Home");
    }

	@Override
	public String forgottenPasswordEmailConfirmation() {
    	MobileElement element = appDriver.retrieveMobileElement(By.xpath("//android.view.View[contains(@text, 'Thanks') or contains(@content-desc, 'Thanks')]"));
    	if (!element.getText().contains("")) {
    		return element.getText();
    	}
    	else {
    		return element.getAttribute("contentDescription");
    	}
	}

	@Override
	public AndroidMyRiverIslandPage openShop() {
		final By shopsLocator = By.xpath("//android.widget.Button[@index='1']");
		appDriver
			.scrollBackwards();
		List<MobileElement> elements = appDriver.retrieveMobileElements(shopsLocator);
		appDriver
			.tap(elements.get(elements.size()-1));
		return this;
	}
	
	private static final By ADDRESS_BOOK_LOCATOR = By.xpath("//*[@text='Address Book']");
	@Override
	public boolean verifyAddressBookVisible() {
		
		return appDriver.isDisplayed(ADDRESS_BOOK_LOCATOR, 5);
	}

	@Override
	public void openAddressBook() {
		appDriver.tap(ADDRESS_BOOK_LOCATOR);		
	}

	@Override
	public void addAddress(Address address) {
		clickAddAddress();

		appDriver
			.type(By.xpath("//android.widget.EditText[@text='First name']"), address.getRecipientFirstName())
			.type(By.xpath("//android.widget.EditText[@text='Last name']"), address.getRecipientLastName())
			.type(By.xpath("//android.widget.EditText[@text='Phone number']"), address.getTelephone())
			.scrollIntoViewAndTap("Enter manually");
		appDriver.swipeElementToElementLocation(
				By.xpath("//android.widget.EditText[@text='Address line 1']"), 
				By.xpath("//android.widget.EditText[@text='Postcode']"));
		appDriver
			.type(By.xpath("//android.widget.EditText[@text='Title']"), "Mr")
			.type(By.xpath("//android.widget.EditText[@text='Address line 1']"), address.getAddressLine1());
		appDriver.scrollIntoView("Town/city");
		appDriver
			.type(By.xpath("//android.widget.EditText[@text='Town/city']"), address.getTownOrCity())
			.type(By.xpath("//android.widget.EditText[@text='Postcode']"), "EC2A 3AN" );
		
		appDriver.scrollIntoView("SAVE ADDRESS");
		
		selectCountry("United Kingdom");
		
		appDriver.hideKeyboard();
		
		appDriver.tap("SAVE ADDRESS");	
	}
	
	private void selectCountry(String country) {
		appDriver.tap(androidId.apply("form_arrow_image"));
		appDriver.tap(country);
	}

	@Override
	public boolean hasAddress(String address) {
		final By addressLocator = By.xpath(String.format("//android.widget.TextView[contains(@text, '%s')]", address));

		return appDriver.isDisplayed(addressLocator, 2);
	}

	@Override
	public void clickAddAddress() {
		final By addAddressButton = androidId.apply("add_address_button");
		appDriver.tap(addAddressButton);
	}

	@Override
	public boolean maxAddressesReached() {	
		return appDriver.isDisplayed(By.xpath("//android.widget.TextView[@text = 'Maximum addresses reached']"), 2);
	}

	@Override
	public void acknowledgeMaxAddresssReached() {
		appDriver.tap(androidId.apply("dialog_positive_button"));		
	}

	private static final By SAVED_CARDS_LOCATOR = By.xpath("//*[@text='Saved Cards']");
	@Override
	public boolean verifySavedCardsVisible() {
		appDriver.scrollIntoView("Recently viewed");
		return appDriver.isDisplayed(SAVED_CARDS_LOCATOR, 5);
	}

	@Override
	public void openSavedCards() {
		appDriver.tap(SAVED_CARDS_LOCATOR);
		
	}

	@Override
	public int getNumberOfCardsSaved() {

		if (!appDriver.isDisplayed(By.xpath("//*[@text = 'No saved cards']"), 2)) {
			List<MobileElement> cards = appDriver.retrieveMobileElements(androidId.apply("saved_cards_card_no"));
		
			if (null != cards) {
				return cards.size();
			}
		}
		return 0;
	}

	@Override
	public void removeSavedCard(String cardNumber) {
		final By cardSelectorLocator = androidId.apply("saved_cards_checkbox");
		
		appDriver
			.tap(androidId.apply("action_edit"))
			.tap(cardSelectorLocator)
			.tap(androidId.apply("action_delete"));
		
	}
}