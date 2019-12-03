package com.riverisland.app.automation.pages;

import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.enums.Region;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface MyRiverIslandPage<T> {
    T openSignIn();

    T signIn(String email, String password);

    T signOut(boolean checkFirst);

    T signUp(Customer customer);

    Boolean isSignedOut();

    Boolean isMyAccountDisplayed();

    T openOrderHistory();

    Boolean isOrderHistoryEmpty();

    T viewOrder(String orderNumber);

    String getOrderDetailsOrderNumber();

    String getOrderDetailsStatus();

    String getOrderDetailsPlacedOn();

    String getOrderDetailsDeliveryAddress();

    String getOrderDetailsPaymentMethod();

    T openFaq();

    T viewFaqSection(String... section);

    Boolean isFaqContentDisplayed();

    T signUpToNewsletters(String email, String category);

    T viewSizeGuides();

    T viewSizeGuideCategory(String sizeGuide);

    T viewDelivery();

    T viewContactUs();

    T viewTermsAndConditions();

    Boolean isTermsAndConditionsDisplayed();

    T openRecentlyViewed();

    T openChangeCurrency();

    T selectCurrency(Region region);

    Boolean isCurrencySelected(Region region);

    T openStoreLocator();

    T openScanInStore();

    T openSettings();

    Boolean isSettingsValid(String... settings);

    T openHelpAndSupport();

    T forgotPassword(String email);

	String forgottenPasswordEmailConfirmation();

	T openShop();
	
	//AddressBook
	boolean verifyAddressBookVisible();

	void openAddressBook();

	void addAddress(Address address);

	boolean hasAddress(String address);

	void clickAddAddress();

	boolean maxAddressesReached();

	void acknowledgeMaxAddresssReached();

	boolean verifySavedCardsVisible();

	void openSavedCards();

	int getNumberOfCardsSaved();
	
	void removeSavedCard(String cardNumber);
}
