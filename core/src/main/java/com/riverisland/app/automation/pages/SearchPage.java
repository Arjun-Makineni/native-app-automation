package com.riverisland.app.automation.pages;

/**
 * Created by Prashant Ramcharan on 30/05/2017
 */
public interface SearchPage<T> {
    T search(String criteria);

    T searchHistory(String history);

    T cancelSearch();

    Boolean isExpectedNumberOfSearchResults(String criteria, int countOfSearchResults);

    T changeEnvironment(String environment, boolean checkBeforeChanging, OnboardingPage onboardingPage);

    Boolean isSearchMessageDisplayed(String message);

    Boolean hasSearchResults();
    
    boolean hasPredictiveDisplayed(String searchTerm);
    
    public boolean hasPopularDisplayed();
}