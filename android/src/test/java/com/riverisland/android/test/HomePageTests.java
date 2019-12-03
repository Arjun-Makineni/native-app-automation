package com.riverisland.android.test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.domain.Customer;
import com.riverisland.app.automation.domain.CustomerDateOfBirth;
import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.fixtures.HomePageFixture;

/**
 * 
 * @author Simon Johnson
 * Created 19 June 2018
 *
 */

public class HomePageTests extends AndroidTest {
	
	HomePageFixture homePageFixture = new HomePageFixture();
		
	@BeforeMethod(alwaysRun = true)
	public void beforeHomePageScenario() {
        if (GlobalConfig.isAppRunningLocally()) {
        	
            appHelper
            		.openWishlistTab()
            		.clearWishlist()
            		.verifyWishlistSize(0)
            		.goBack()
            		.openShoppingBagTab()
            		.clearShoppingBag()
            		.goBack()
                    .openMenu()
                    .checkAndSignOut();
        }
	}
	
	@Test(description = "HomePage - Neutral not signed in")
	public void homePageScenario_01() {	
		
		homePageFixture.verifyMaleAndFemaleTrendingItemsDisplayed();
	}
	
	@Test(description = "HomePage - Male customer signed in with no order and no wishlist item")
	public void homePageScenario_02() {
		
		appHelper
				.openMenu()
				.openSignIn()
				.signUp(Customer.Builder
									.create()
									.withTitle("Mr.")
									.withFirstName("John")
									.withLastName("Tester")
									.withRandomEmailAddress()
									.withPassword("password")
									.withDOB(new CustomerDateOfBirth("01", "Jan", "2000"))
									.withGender("Male")
									.build());
				
		homePageFixture
				.verifyMaleTrendingItemsDisplayed();
	}
	
	@Test(description = "HomePage - Male customer signed in with order")
	public void homePageScenario_03() {
		
		appHelper
				.openMenu()
				.changeCurrency(Region.GB)
				.addProductAndViewBag(Category.MEN)
				.proceedToCheckout()
				.signUp(Customer.Builder
									.create()
									.withTitle("Mr.")
									.withFirstName("John")
									.withLastName("Tester")
									.withRandomEmailAddress()
									.withPassword("password")
									.withDOB(new CustomerDateOfBirth("01", "Jan", "2000"))
									.withGender("Male")
									.build())
				.selectDeliveryType(DeliveryType.HOME_DELIVERY)
				.addDeliveryHomeAddressAndSelect(Region.GB)
				.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
				.selectPaymentMethod(PaymentMethod.CARD)
				.processPayment(PaymentMethod.CARD, CardType.VISA)
				.payWithCard()
				.verifyOrderSummary()
				.completePayment()
				.verifyOrderCompletion()
				.continueShopping()
				.openMenu()
				.openHomeTab();
				
		homePageFixture
				.verifyMaleTrendingItemsDisplayed()
				.verifyOrderStatusDisplayed();
	}
	
	@Test(description = "HomePage - Male customer signed in with wishlist item")
	public void homePageScenario_04() {
		
		appHelper
				.homepageWelcome()
				.addProductToWishlist(Category.MEN)
				.goBack(4)
				.openMenu()
				.openSignIn()
				.signUp(Customer.Builder
									.create()
									.withTitle("Mr.")
									.withFirstName("John")
									.withLastName("Tester")
									.withRandomEmailAddress()
									.withPassword("password")
									.withDOB(new CustomerDateOfBirth("01", "Jan", "2000"))
									.withGender("Male")
									.build())
				.addProductToWishlist(Category.MEN)
				.goBack(4);
				
		homePageFixture
				.verifyMaleTrendingItemsDisplayed()
				.verifyWishListDisplayed(2);
	}
	
	@Test(description = "HomePage - Female customer signed in")
	public void homePageScenario_05() {
		appHelper
				.openMenu()
				.openSignIn()
				.signUp(Customer.Builder
									.create()
									.withTitle("Mrs.")
									.withFirstName("Jenny")
									.withLastName("Tester")
									.withRandomEmailAddress()
									.withPassword("password")
									.withDOB(new CustomerDateOfBirth("01", "Jan", "2000"))
									.withGender("Female")
									.build());
		homePageFixture
				.verifyFemaleTrendingItemsDisplayed();		
	}
	
	@Test(description = "HomePage - Female customer signed in with no title supplied")
	public void homePageScenario_06() {
		appHelper
				.openMenu()
				.openSignIn()
				.signUp(Customer.Builder
									.create()
									.withFirstName("Jennynotitle")
									.withLastName("Tester")
									.withRandomEmailAddress()
									.withPassword("password")
									.withDOB(new CustomerDateOfBirth("01", "Feb", "2000"))
									.withGender("Female")
									.build());
		
		homePageFixture.verifyMaleAndFemaleTrendingItemsDisplayed();
	}
	
	@Test(description = "HomePage - Search - Predictive and popular")
	public void homePageScenario_07() {
		homePageFixture.verifyPredictiveAndPopularSearch("Top");
	}
}