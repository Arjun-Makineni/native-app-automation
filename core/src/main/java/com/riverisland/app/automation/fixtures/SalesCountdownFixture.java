package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertTrue;

public class SalesCountdownFixture extends AppFixture {

	public SalesCountdownFixture verifyCountdownDisplayed() {
		assertTrue(salesCountdownPage.isCountdownDisplayed());
		return this;
	}
}
