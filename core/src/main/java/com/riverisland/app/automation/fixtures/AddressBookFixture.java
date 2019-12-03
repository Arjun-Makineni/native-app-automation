package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;

import com.riverisland.app.automation.domain.Address;

public class AddressBookFixture extends AppFixture {
	
	public AddressBookFixture verifyAddressBook(boolean available) {
		assertTrue(myRiverIslandPage.verifyAddressBookVisible() == available);
		return this;
	}
	
	public AddressBookFixture openAddressBook() {
		myRiverIslandPage.openAddressBook();
		return this;
	}
	
	public AddressBookFixture addAddress(Address address) {
		myRiverIslandPage.addAddress(address);
		assertTrue(myRiverIslandPage.hasAddress(String.format("%s %s %s", address.getRecipientFirstName(), address.getRecipientLastName(), address.getAddressLine1())));
		return this;
	}
	
	public AddressBookFixture verifyMaxAdressesAdded() {
		myRiverIslandPage.clickAddAddress();
		assertTrue(myRiverIslandPage.maxAddressesReached());
		myRiverIslandPage.acknowledgeMaxAddresssReached();
		return this;
	}
}
