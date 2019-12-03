package com.riverisland.android.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.domain.Address;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.fixtures.AddressBookFixture;

public class MyRITestsAddressBook extends AndroidTest {
	
	private AddressBookFixture addressbookFixture = new AddressBookFixture();
	
	@AfterTest(alwaysRun = true)
	public void afterMyRITests() {
		
	}
	
	@BeforeMethod
	public void beforeAddressBookTests() {
        if (GlobalConfig.isAppRunningLocally()) {
            appHelper
                    .openMenu()
                    .checkAndSignOut();
        }		
	}

	@Test(description = "User not signed in - Address book not accessible")
	public void MyRiAddressBookScenario_01() {
        appHelper
        	.registerNewCustomerViaApi(tcplApiCredentials)
        	.openMenu();
        
        addressbookFixture.verifyAddressBook(false);     
	}
	
	@Test(description = "User signed in - Address book accessible - Add address")
	public void MyRiAddressBookScenario_02() {
        appHelper
    	    .registerNewCustomerViaApi(tcplApiCredentials)
    	    .openMenu()
    	    .openSignIn()
    	    .signIn(customer.getEmailAddress(), customer.getPassword())
    	    .openMenu();
    
        addressbookFixture
        	.verifyAddressBook(true)
        	.openAddressBook();
        
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 1"));       
	}
	
	@Test(description = "User signed in - Address book accessible - Max addresses added")
	public void MyRiAddressBookScenario_03() {
        appHelper
    	    .registerNewCustomerViaApi(tcplApiCredentials)
    	    .openMenu()
    	    .openSignIn()
    	    .signIn(customer.getEmailAddress(), customer.getPassword())
    	    .openMenu();
    
        addressbookFixture
        	.verifyAddressBook(true)
        	.openAddressBook();
        
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 1"));  
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 2"));  
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 3"));  
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 4"));  
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 5"));  
        addressbookFixture.addAddress(createTestAddress("Test", "Tester", "Test Address Line 6")); 

        addressbookFixture.verifyMaxAdressesAdded();
        
	}
	
	private Address createTestAddress(String firstName, String lastName, String addressLine1) {
		return Address.Builder.create()
				.withAddressLine1(addressLine1)
				.withRecipientFirstName(firstName)
				.withRecipientLastName(lastName)
				.withCountry("United Kingdom")
				.withTownOrCity("London")
				.withRegion(Region.GB)
				.withPostCode("EC2A 3AN")
				.withTelephone("01234567890")
				.build();
	}
}
