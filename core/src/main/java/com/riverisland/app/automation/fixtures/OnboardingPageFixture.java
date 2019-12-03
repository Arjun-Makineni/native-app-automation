package com.riverisland.app.automation.fixtures;

public class OnboardingPageFixture extends AppFixture {
	
	public OnboardingPageFixture skipAndAcceptPushNotifications(boolean checkFirst) {
		onboardingPage.acceptPushNotificationsAndSkip(checkFirst);
		return this;
	}

}
