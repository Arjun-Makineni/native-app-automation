package com.riverisland.ios.mobile.test;

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

public class HomePageTests extends IosMobileTest {

	HomePageFixture homePageFixture = new HomePageFixture();

	@BeforeMethod(alwaysRun = true)
	public void beforeHomePageScenario() {
		if (GlobalConfig.isAppRunningLocally()) {

			appHelper.openMyRiTab().checkAndSignOut();

			appHelper.openWishlistTab().clearWishlist().verifyWishlistSize(0).openHomeTab();
		}
	}

	@Test(description = "HomePage - Neutral not signed in")
	public void homePageScenario_01() {

		homePageFixture.verifyMaleAndFemaleTrendingItemsDisplayed();
	}

	@Test(description = "HomePage - Male customer signed in with no order and no wishlist item")
	public void homePageScenario_02() {

		appHelper.openMyRiTab().openSignIn()
				.signUp(Customer.Builder.create().withTitle("Mr.").withFirstName("John").withLastName("Tester")
						.withRandomEmailAddress().withPassword("password")
						.withDOB(new CustomerDateOfBirth("01", "Jan", "2000")).withGender("Male").build())
				.openHomeScreen();

		homePageFixture.verifyMaleTrendingItemsDisplayed();
	}

	@Test(description = "HomePage - Male customer signed in with order")
	public void homePageScenario_03() {

		appHelper.openMyRiTab().changeCurrency(Region.GB).addProductAndViewBag(Category.MEN).proceedToCheckout()
				.signUp(Customer.Builder.create().withTitle("Mr.").withFirstName("John").withLastName("Tester")
						.withRandomEmailAddress().withPassword("password")
						.withDOB(new CustomerDateOfBirth("01", "Jan", "2000")).withGender("Male").build())
				.selectDeliveryType(DeliveryType.HOME_DELIVERY).addDeliveryHomeAddressAndSelect(Region.GB)
				.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY).selectPaymentMethod(PaymentMethod.CARD)
				.processPayment(PaymentMethod.CARD, CardType.VISA).payWithCard().verifyOrderSummary().completePayment()
				.verifyOrderCompletion().continueShopping().acceptAlert("Not Now").openHomeTab();

		homePageFixture.verifyMaleTrendingItemsDisplayed().verifyOrderStatusDisplayed();
	}

	@Test(description = "HomePage - Male customer signed in with wishlist item")
	public void homePageScenario_04() {

		appHelper.addProductToWishlist(Category.MEN).openMyRiTab().openSignIn()
				.signUp(Customer.Builder.create().withTitle("Mr.").withFirstName("John").withLastName("Tester")
						.withRandomEmailAddress().withPassword("password")
						.withDOB(new CustomerDateOfBirth("01", "Jan", "2000")).withGender("Male").build())
				.openShopTab().addProductToWishlist(Category.MEN).openHomeTab();

		homePageFixture.verifyMaleTrendingItemsDisplayed().verifyWishListDisplayed(2);
	}

	@Test(description = "HomePage - Female customer signed in")
	public void homePageScenario_05() {
		appHelper.openMyRiTab().openSignIn()
				.signUp(Customer.Builder.create().withTitle("Mrs.").withFirstName("Jenny").withLastName("Tester")
						.withRandomEmailAddress().withPassword("password")
						.withDOB(new CustomerDateOfBirth("01", "Jan", "2000")).withGender("Female").build())
				.openHomeTab();

		homePageFixture.verifyFemaleTrendingItemsDisplayed();
	}
	
	@Test(description = "HomePage - Female customer signed in with no title supplied")
	public void homePageScenario_06() {
		appHelper.openMyRiTab()
			.openSignIn()
			.signUp(Customer.Builder.create().withFirstName("Jenny").withLastName("Tester")
				.withRandomEmailAddress().withPassword("password")
				.withDOB(new CustomerDateOfBirth("01", "Jan", "2000")).withGender("Female").build())
			.openHomeTab();

		homePageFixture.verifyMaleAndFemaleTrendingItemsDisplayed();
	}
	
	@Test(description = "HomePage - Search - Predictive and popular")
	public void homePageScenario_07() {
		homePageFixture.verifyPredictiveAndPopularSearch("Top");
	}
}
