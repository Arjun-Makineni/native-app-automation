package com.riverisland.app.automation.fixtures;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.riverisland.app.automation.enums.CardType;
import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.DeliveryOption;
import com.riverisland.app.automation.enums.DeliveryType;
import com.riverisland.app.automation.enums.PaymentMethod;
import com.riverisland.app.automation.enums.Region;
import com.riverisland.app.automation.helpers.AppHelper;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;

public class ShoppingBagFixture extends AppFixture {
	private AppHelper appHelper = new AppHelper();
	private CheckoutFixture checkoutFixture = new CheckoutFixture();
	
	private Map<Category, List<Product>> productList;
	private Map<BigDecimal, List<Product>> segmentedProductsByPrice;
	private List<BigDecimal> prices;
	
	public ShoppingBagFixture(Map<Category, List<Product>> productList) {
		this.productList = productList;
		this.segmentedProductsByPrice = segmentProductsByPrice();
		
		this.prices = new ArrayList<>(segmentedProductsByPrice.keySet());
		
	}
	
	public ShoppingBagFixture verifyDeliveryPromoLessThanLimit(String limit) {
		BigDecimal priceLimit = new BigDecimal(limit).subtract(new BigDecimal(5));

		//Add product to value of promo and verify no promo prompt
		assertTrue(searchForProductsAndAddToBag(findProductsForPrice(priceLimit, 0)), "Promo displayed and not expected");
		//Verify free delivery
		completeBagPurchaseAndVerifyShipping("£3.99");
		return this;
	}
	private static final BigDecimal SHOPPING_PROMO_DELIVERY_THRESHOLD = BigDecimal.valueOf(10);
	public ShoppingBagFixture verifyDeliveryPromoEqualToLimit(String limit) {
		BigDecimal priceLimit = new BigDecimal(limit);

		//Find products to take bag value to promo threshold and verify promo prompt
		assertTrue(searchForProductsAndAddToBag(findProductsForPrice(priceLimit.subtract(SHOPPING_PROMO_DELIVERY_THRESHOLD),0)), "Promo not displayed as expected");
		
		//Add product to value of promo and verify no promo prompt
		assertFalse(searchForProductsAndAddToBag(findProductsForPrice(SHOPPING_PROMO_DELIVERY_THRESHOLD, 0)), "Promo displayed and not expected");

		//Verify free delivery
		completeBagPurchaseAndVerifyShipping("£0.00");

		return this;
	}
	public ShoppingBagFixture verifyDeliveryPromoGreaterThanLimit(String limit) {
		BigDecimal priceLimit = new BigDecimal(limit).add(new BigDecimal(5));

		//Add product to value of promo and verify no promo prompt
		assertFalse(searchForProductsAndAddToBag(findProductsForPrice(priceLimit, 0)), "Promo displayed and not expected");
		//Verify free delivery
		completeBagPurchaseAndVerifyShipping("£0.00");
		return this;
	}
	
	private void completeBagPurchaseAndVerifyShipping(String expectedShippingValue) {
		appHelper
			.openShoppingBagTab()
			.proceedToCheckout();
		
		if (null == customer) {
			appHelper.signUp();
		}
		
		appHelper.selectDeliveryType(DeliveryType.HOME_DELIVERY)
			.lookupDeliveryHomeAddressByPostcodeAndSelect(Region.GB)
			.selectDeliveryOption(DeliveryOption.STANDARD_DELIVERY)
			.selectPaymentMethod(PaymentMethod.CARD)
			.processPayment(PaymentMethod.CARD, CardType.VISA).payWithCard();
		checkoutFixture
			.verifyOrderSummary();
		appHelper
			.verifyOrderSummaryShippingTotal(expectedShippingValue)
			.completePayment()
			.verifyOrderCompletion().continueShopping();
	}
	
    public boolean searchForProductsAndAddToBag(List<Product> productList) {
  	
    	boolean deliverPromoDisplayed = false;
		homePage.openSearch();
    	
        for (Product product : productList) {
        	homePage.searchForProductsAndSelect(product.getId());
        	productLandingPage.selectProduct(product.getId());
        	productDetailsPage.addToBag();
        	productDetailsPage.addInStockSizeToBag();

        	productDetailsPage.back();
        	if (!deliverPromoDisplayed) {
        		deliverPromoDisplayed = productDetailsPage.isDeliveryPromoDisplayed();
        	}
        	productDetailsPage.back();
        }

        homePage.closeSearch();
        return deliverPromoDisplayed;
    }
    
	private List<Product> findProductsForPrice(BigDecimal priceLimit, int index) {
		List<Product> retval = new ArrayList<>();
		
		if (segmentedProductsByPrice.containsKey(priceLimit)) {
			retval.add(segmentedProductsByPrice.get(priceLimit).get(0));
		}
		else {
			Product product = segmentedProductsByPrice.get(prices.get(index)).get(0);
			retval.add(product);
			retval.addAll(findProductsForPrice(priceLimit.subtract(prices.get(index)), index++));
		}
		return retval;
	}
	
	private Map<BigDecimal, List<Product>> segmentProductsByPrice() {
		Map<BigDecimal, List<Product>> productsByPrice = new TreeMap<>();
		
		for (Category category : productList.keySet()) {
			for (Product product : productList.get(category)) {
				BigDecimal price = new BigDecimal(product.getPrice());
				if (null == productsByPrice.get(price)) {
					productsByPrice.put(price, new ArrayList<Product>());					
				}
				productsByPrice.get(price).add(product);
			}
		}
		
		return productsByPrice;
	}
}
