package com.riverisland.app.automation.pojos;

/**
 * Created by Prashant Ramcharan on 22/03/2017
 */
public class GiroPayCredentials {
    private String accountNumber;
    private String bankLoc;
    private String accountName;

    public GiroPayCredentials(String accountNumber, String bankLoc, String accountName) {
        this.accountNumber = accountNumber;
        this.bankLoc = bankLoc;
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankLoc() {
        return bankLoc;
    }

    public String getAccountName() {
        return accountName;
    }
}

