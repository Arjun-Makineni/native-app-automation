package com.riverisland.ios.mobile.test.pages;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.framework.RiverIslandNativeAppDriver;
import com.riverisland.app.automation.pages.MyRiverIslandPage;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

import io.appium.java_client.MobileElement;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public class IosMobileMyRiverIslandPage implements MyRiverIslandPage<IosMobileMyRiverIslandPage> {
    private RiverIslandNativeAppDriver appDriver;

    public IosMobileMyRiverIslandPage(RiverIslandNativeAppDriver appDriver) {
        this.appDriver = appDriver;
    }

    private boolean isSignedIn() {
        return appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Recently Viewed"))).isDisplayed(By.name("Sign Out"));
    }

    @Override
    public IosMobileMyRiverIslandPage openSignIn() {
        if (isSignedIn()) { // ensure that you're not already signed in
            signOut(false);
        }

        appDriver.tap("Sign In");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage signIn(String email, String password) {
        if (StringUtils.isNotBlank(email)) {
            appDriver
                    .clear(By.name("Email Address"))
                    .type("Email Address", email);
        }

        appDriver.type("Password", password);
        appDriver.tap("SIGN IN");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage signOut(boolean checkFirst) {
        if (checkFirst) {
            if (!isSignedIn()) {
                return this;
            }
        }
        appDriver.tap("Sign Out").acceptAlert();
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage signUp(Customer customer) {
        appDriver
                .tap("Sign Up")
                .waitFor(ExpectedConditions.visibilityOfNestedElementsLocatedBy(By.className("XCUIElementTypeTable"), By.className("XCUIElementTypeCell")));

        appDriver
                .click(By.className("XCUIElementTypeCell"))
                .tap(null != customer.getTitle() ? customer.getTitle() : "----")
                .touchAndType("First Name", customer.getFirstName())
                .touchAndType("Last Name", customer.getLastName())
                .touchAndType("Email Address", customer.getEmailAddress())
                .touchAndType("Confirm Email Address", customer.getEmailAddress())
                .touchAndType("Password", customer.getPassword())
                .touch(appDriver.retrieveMobileElement("Re-Type Password"))
                .typePresent("Re-Type Password", customer.getPassword())
                .tap("CREATE ACCOUNT");

        if (appDriver.isDisplayed(By.name("Registration Complete"), 5)) {
        	appDriver.tap("Registration Complete").acceptAlert();
        }
        
        RiverIslandLogger.getInfoLogger(this.getClass()).info("Signed up customer: " + customer.toString());
        return this;
    }

    @Override
    public Boolean isSignedOut() {
        return appDriver.retrieveMobileElement(By.name("Sign In")).isDisplayed();
    }

    @Override
    public Boolean isMyAccountDisplayed() {
        return appDriver.retrieveMobileElement(By.name("Order History")).isDisplayed();
    }

    @Override
    public IosMobileMyRiverIslandPage openOrderHistory() {
        appDriver.tap("Order History");
        return this;
    }

    @Override
    public Boolean isOrderHistoryEmpty() {
        return appDriver.retrieveMobileElement(By.name("Your order history is empty")).isDisplayed();
    }

    @Override
    public IosMobileMyRiverIslandPage viewOrder(String orderNumber) {
        appDriver.tap("Order Number - " + orderNumber);
        return this;
    }

    private Function<String, By> orderDetailsLocator = (label) -> By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']//following::XCUIElementTypeStaticText[1]", label));

    @Override
    public String getOrderDetailsOrderNumber() {
        return appDriver.retrieveMobileElementText(orderDetailsLocator.apply("Order number"));
    }

    @Override
    public String getOrderDetailsStatus() {
        return appDriver.retrieveMobileElementText(orderDetailsLocator.apply("Order Status"));
    }

    @Override
    public String getOrderDetailsPlacedOn() {
        return appDriver.retrieveMobileElementText(orderDetailsLocator.apply("Placed on"));
    }

    @Override
    public String getOrderDetailsDeliveryAddress() {
        return appDriver.retrieveMobileElementText(orderDetailsLocator.apply("Delivery address"));
    }

    @Override
    public String getOrderDetailsPaymentMethod() {
        return appDriver.retrieveMobileElementText(orderDetailsLocator.apply("Card details"));
    }

    @Override
    public IosMobileMyRiverIslandPage openFaq() {
        appDriver.tap("FAQ");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewFaqSection(String... section) {
    	Arrays.asList(section).forEach(faqSection -> appDriver.tap(faqSection));
        return this;
    }

    @Override
    public Boolean isFaqContentDisplayed() {
        return StringUtils.isNoneBlank(appDriver.retrieveMobileElementText(By.xpath("//XCUIElementTypeStaticText[1]")));
    }

    @Override
    public IosMobileMyRiverIslandPage signUpToNewsletters(String email, String category) {
        appDriver
                .tap("Sign up to email")
                .type(By.className("XCUIElementTypeTextField"), email)
                .tap(By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']//preceding::XCUIElementTypeButton[1]", category)))
                .tap("SIGN UP");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewSizeGuides() {
        appDriver.tap("Size guides");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewSizeGuideCategory(String sizeGuide) {
        appDriver.tap(sizeGuide);
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewDelivery() {
        appDriver.tap("Delivery");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewContactUs() {
        appDriver.tap("Contact us");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage viewTermsAndConditions() {
        appDriver.tap("Terms & Conditions");
        return this;
    }

    @Override
    public Boolean isTermsAndConditionsDisplayed() {
        return appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeStaticText[@name='Definitions']")) != null;
    }

    @Override
    public IosMobileMyRiverIslandPage openRecentlyViewed() {
        appDriver.tap("Recently Viewed");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage openChangeCurrency() {
        appDriver.tap("Change Currency");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage selectCurrency(Region region) {
        appDriver.waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("table index")));
    	
        displayAndSelectCurrency(region.getDescription());
        appDriver.back();

        RiverIslandLogger.getInfoLogger(this.getClass()).info("Changed currency to: " + region.getDescription() + " - " + region.getCurrency().getSymbol());
        return this;
    }
    
    private void displayAndSelectCurrency(String country) {
    	final By countryLocator = By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']", country));
    	final Point toPoint = appDriver.retrieveMobileElement(By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']", "United Kingdom"))).getCenter();
    	final Point fromPoint = appDriver.retrieveMobileElement(By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']", "Aruba"))).getCenter();
    	
    	int tries = 20;
    	while (tries-- > 0) {
        	MobileElement countryElement = appDriver.retrievePresentMobileElement(countryLocator);

        	if (countryElement.isDisplayed()) {
        		countryElement.click();
        		return;
        	} else {
        		appDriver.swipeElementToElementLocation(fromPoint, toPoint);
        	}
    	}
    }
    
    @Override
    public Boolean isCurrencySelected(Region region) {
        Boolean result = appDriver
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("table index")))
                .retrievePresentMobileElement(By.xpath(String.format("(//XCUIElementTypeStaticText[@name='%s']//following::XCUIElementTypeImage)[1]", region.getDescription()))) != null;

        appDriver.back();
        return result;
    }

    @Override
    public IosMobileMyRiverIslandPage openStoreLocator() {
        appDriver.tap("Store Locator");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage openScanInStore() {
        appDriver.tap("Scan in-store");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage openSettings() {
        appDriver.tap("Settings");
        return this;
    }

    @Override
    public Boolean isSettingsValid(String... settings) {
        AtomicBoolean result = new AtomicBoolean();

        Arrays.asList(settings).forEach(setting -> result.set(appDriver.retrieveMobileElement(By.xpath(String.format("//XCUIElementTypeStaticText[@name='%s']", setting))) != null));

        return result.get();
    }

    @Override
    public IosMobileMyRiverIslandPage openHelpAndSupport() {
        appDriver.tap("Help & Support");
        return this;
    }

    @Override
    public IosMobileMyRiverIslandPage forgotPassword(String email) {
        appDriver
                .tap("Forgot password?")
                .waitFor(ExpectedConditions.visibilityOfElementLocated(By.name("Don't worry - just give us your email address and we will send you a link so that you can choose a new one.")));
        MobileElement emailElement = appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeStaticText[@name='Email']//following::XCUIElementTypeTextField"));
        
        appDriver.type(emailElement, email);
        emailElement.sendKeys(Keys.ENTER);
        appDriver.tapUntil(By.name("Send"), NoSuchElementException.class);
        return this;
    }
    
    @Override
    public String forgottenPasswordEmailConfirmation() {
    	return appDriver.retrieveMobileElement(By.xpath("//XCUIElementTypeStaticText[contains(@name, 'Thanks')]")).getText();
    }

	@Override
	public IosMobileMyRiverIslandPage openShop() {
		throw new NotImplementedException("Not implemented for IOS");
	}

	@Override
	public boolean verifyAddressBookVisible() {
		throw new NotImplementedException("IOS not implemented");
//		return false;
	}

	@Override
	public void openAddressBook() {
		throw new NotImplementedException("IOS not implemented");
		
	}

	@Override
	public void addAddress(Address address) {
		throw new NotImplementedException("IOS not implemented");
		
	}

	@Override
	public boolean hasAddress(String address) {
		throw new NotImplementedException("IOS not implemented");
//		return false;
	}

	@Override
	public void clickAddAddress() {
		throw new NotImplementedException("IOS not implemented");	
	}

	@Override
	public boolean maxAddressesReached() {
		throw new NotImplementedException("IOS not implemented");
//		return false;
	}

	@Override
	public void acknowledgeMaxAddresssReached() {
		throw new NotImplementedException("IOS not implemented");
		
	}

	private static final By SAVED_CARDS_LOCATOR = By.xpath("//*[@name='Saved Cards']");
	@Override
	public boolean verifySavedCardsVisible() {
		return appDriver.isDisplayed(SAVED_CARDS_LOCATOR, 5);
	}

	@Override
	public void openSavedCards() {
		appDriver.tap(SAVED_CARDS_LOCATOR);	
	}

	@Override
	public int getNumberOfCardsSaved() {
		if (!appDriver.isDisplayed(By.xpath("//XCUIElementTypeStaticText[@name = 'Saved_cards_empty_title_label']"), 2)) {
			List<MobileElement> cards = appDriver.retrieveMobileElements(By.xpath("//XCUIElementTypeTable[@name='Cards_table_view']/XCUIElementTypeCell"));
		
			if (null != cards) {
				return cards.size();
			}
		}
		return 0;
	}

	@Override
	public void removeSavedCard(String cardNumber) {
		final By cardLocator = By.xpath(String.format("//XCUIElementTypeTable[@name='Cards_table_view']/XCUIElementTypeCell/XCUIElementTypeStaticText[@name = 'Card_last_digits_label' and @value = '%s']",cardNumber));
		appDriver
			.swipeLeft(cardLocator)
			.tap("Remove");
		
	}
}
