package com.riverisland.app.automation.utils;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * Created by Prashant Ramcharan on 11/02/2017
 */
public class RetryUtils {
    private RetryUtils() {
    }

    public static Boolean retryWithTimeout(Predicate<Boolean> condition,
                                           LocalDateTime retryTimeout,
                                           String message) {

        System.out.println("\nRetry attempts will timeout at: " + retryTimeout);

        Boolean isTrue;
        Integer attemptCounter = 1;

        while (LocalDateTime.now().isBefore(retryTimeout)) {
            isTrue = condition.test(Boolean.TRUE);

            if (isTrue) {
                return Boolean.TRUE;
            }

            System.out.println("Attempt " + attemptCounter++ + ": " + message);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ignored) {
            }
        }
        return Boolean.FALSE;
    }
}