package com.riverisland.app.automation.giftcards;

public class GiftCard {
	
	private String description;
	private String number;
	private String pin;
	private String currencyCode;
	private String initialValue;
	
	public GiftCard() {	
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPin() {
		return String.format("%04d", Integer.valueOf(pin));
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	@Override
	public String toString() {
		return "GiftCard [description=" + description + ", number=" + number + ", pin=" + pin + ", currencyCode="
				+ currencyCode + ", initialValue=" + initialValue + "]";
	}
}
