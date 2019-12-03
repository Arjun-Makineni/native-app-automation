package com.riverisland.app.automation.domain;

/**
 * Created by Prashant Ramcharan on 11/05/2017
 */
public class CustomerDateOfBirth {
    private String day;
    private String month;
    private String year;

    public CustomerDateOfBirth(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
}