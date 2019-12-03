package com.riverisland.app.automation.fixtures;


import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.SkipException;

import com.riverisland.app.automation.config.GlobalConfig;
import com.riverisland.app.automation.pojos.Environment;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.tcplapi.productcatalogue.ProductCatalogueService;
import com.riverisland.app.automation.tcplapi.productcatalogue.ProductCatalogueServiceException;
import com.riverisland.app.automation.tcplapi.productcatalogue.domain.Product;


public class ProductLandingFixture extends AppFixture {
	private String productNumber = null;
	private ProductCatalogueService productCatalogueService;
	
    private Environment environment = GlobalConfig.instance().getEnvironment();

	private TcplApiCredentials tcplApiCredentials = GlobalConfig.instance().getTcplApiCredentials().stream().filter(t -> t.getEnvironment().equalsIgnoreCase(environment.getName())).findFirst().orElse(null);

	
	public ProductLandingFixture() {
		productCatalogueService = new ProductCatalogueService(tcplApiCredentials);
	}

	public ProductLandingFixture selectProductWithSwatches(int numberOfSwatches, String category, String subcategory) {
		
		try {
			this.productNumber = productCatalogueService.findProductWithRequiredSwatches(String.format("%s/%s", category, subcategory), numberOfSwatches);
			
			if (null == this.productNumber) {
				throw new SkipException(String.format("Unable to find product with %s swatches", numberOfSwatches));				
			}
		} catch (ProductCatalogueServiceException pcse) {
			throw new RuntimeException("Failed to get product data from apis : " + pcse.getMessage());
		}
		return this;
	}
		
	public ProductLandingFixture verifyProductSwatchesDetails(int numberOfSwatches) {	
		goHome();
		homePage.openSearch();
		homePage.searchForProductsAndSelect(productNumber);
		
		assertTrue(verifySwatchDetails(numberOfSwatches) && verifyProductDetailSwatches(numberOfSwatches), 
				   String.format("Swatch details displayed as expected for %s swatches", numberOfSwatches));
		return this;
	}
	
	private static final int MAX_SWATCH_DISPLAY = 3;
	private boolean verifySwatchDetails(int numberOfSwatches) {

		int actualSwatchesDisplayed = productLandingPage.numberOfSwatchesDisplayed();
		int actualExcessSwatchCount = 0;
		if (numberOfSwatches > MAX_SWATCH_DISPLAY ) {
			actualExcessSwatchCount = productLandingPage.excessSwatchValue();
		}
		
		if (numberOfSwatches <= MAX_SWATCH_DISPLAY) {
			return actualSwatchesDisplayed == numberOfSwatches &&
				   actualExcessSwatchCount == 0;
		} 
		
		return actualSwatchesDisplayed == MAX_SWATCH_DISPLAY &&
				   actualExcessSwatchCount == numberOfSwatches - (MAX_SWATCH_DISPLAY);
	}
	
	private boolean verifyProductDetailSwatches(int numberOfSwatches) {
		productLandingPage.selectProduct(productNumber);
		return numberOfSwatches == productDetailsPage.numberOfSwatchesDisplayed();	
	}
}
