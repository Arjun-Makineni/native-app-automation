package com.riverisland.app.automation.utils;

import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.automation.utils.ecom.domain.account.AccountRegistrationRequest;
import com.riverisland.automation.utils.ecom.service.AccountTestService;
import io.restassured.filter.log.LogDetail;
import org.apache.http.HttpStatus;

/**
 * Created by Prashant Ramcharan on 20/10/2017
 */
public final class AccountUtils {

    private AccountUtils() {
    }

    public static boolean registerAccount(TcplApiCredentials credentials, AccountRegistrationRequest accountRegistrationRequest) {
        if (credentials != null) {
            final String endpoint = "/testing/registeraccount";

            final AccountTestService accountTestService = AccountTestService.create(credentials.getServiceUrl(), credentials.getApiKey(), LogDetail.URI);
            return accountTestService.registerAccount(endpoint, accountRegistrationRequest).getClientResponse().getStatusCode() == HttpStatus.SC_OK;
        }
        return false;
    }
}