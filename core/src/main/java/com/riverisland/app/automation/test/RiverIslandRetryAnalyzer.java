package com.riverisland.app.automation.test;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Created by Prashant Ramcharan on 30/01/2018
 */
public class RiverIslandRetryAnalyzer implements IRetryAnalyzer {
    private int counter = 0;
    private int limit;

    public RiverIslandRetryAnalyzer(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (counter < limit) {
            counter++;
            return true;
        }
        return false;
    }
}