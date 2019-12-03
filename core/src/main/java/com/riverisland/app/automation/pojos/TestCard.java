package com.riverisland.app.automation.pojos;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Prashant Ramcharan on 20/03/2017
 */
public class TestCard {
    private String cardType;
    private String cardNumber;
    private Integer securityCode;
    private String expiryMonth;
    private String expiryYear;
    private Boolean is3DSecure;

    public TestCard() {
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(Integer securityCode) {
        this.securityCode = securityCode;
    }

    public String getExpiryMonth() {
        return expiryMonth.length() == 1 ? "0" + expiryMonth : expiryMonth;
    }

    public String getExpiryMonthAndYear() {
        try {
            int month = new SimpleDateFormat("MMMM").parse(expiryMonth).getMonth() + 1;
            return (month < 10 ? "0" + month : String.valueOf(month)) + "/" + expiryYear;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public Boolean getIs3DSecure() {
        return is3DSecure;
    }

    public void setIs3DSecure(Boolean is3DSecure) {
        this.is3DSecure = is3DSecure;
    }

    @Override
    public String toString() {
        return "{" +
                "cardType='" + cardType + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", securityCode=" + securityCode +
                ", expiryMonth='" + expiryMonth + '\'' +
                ", expiryYear='" + expiryYear + '\'' +
                ", is3DSecure=" + is3DSecure +
                '}';
    }
}
