package com.riverisland.app.automation.tcplapi.productcatalogue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.app.automation.tcplapi.productcatalogue.domain.NavigationDataEntity;
import com.riverisland.app.automation.tcplapi.productcatalogue.domain.NavigationItem;
import com.riverisland.app.automation.tcplapi.productcatalogue.domain.Product;
import com.riverisland.app.automation.tcplapi.productcatalogue.domain.ProductsDataEntity;
import com.riverisland.automation.utils.core.logging.RiverIslandLogger;

public class ProductCatalogueService {
	
	private RestTemplate restTemplate;
	private TcplApiCredentials apiCredentials;
	
	private static final String PRODUCT_LIST = "/productcatalogue/productlisting?productListId=%s&returnSwatchInfo=true";
	private static final String NAVIGATION = "/productcatalogue/getnavigation";
	
	public ProductCatalogueService(TcplApiCredentials apiCredentials) {
		this.apiCredentials = apiCredentials;
		this.restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> apiInterceptors = new ArrayList<>();
		apiInterceptors.add(new HeaderRequestInterceptor("ApiKey", apiCredentials.getApiKey()));
		apiInterceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
 
		this.restTemplate.setInterceptors(apiInterceptors);
	}	
	public String findProductWithRequiredSwatches(String categoryPath, int numberOfSwatches) throws ProductCatalogueServiceException {
		List<Product> products = findProductsWithSwatches(categoryPath);
		
		for (Product product : products) {
			if (product.getSwatchInfo().getSwatchCount() == numberOfSwatches -1) { // count excludes product displayed
				return product.getProductId();
			}
		}
		
		return null;
	}

	public List<Product> findProductsWithSwatches(String categoryPath) throws ProductCatalogueServiceException {
		List<Product> response = null;	
		
		int productListId = findProductListId(categoryPath);
		
		ProductListResponse productListResponse = findProducts(productListId);
		
		if (null != productListResponse && productListResponse.hasData()) {
			response = productListResponse.productsContainer().getData().getBrowse().getProducts();
		}
		
		return response;
	}
	private ProductCatalogueNavigationResponse navigation() throws ProductCatalogueServiceException {
		ProductCatalogueNavigationResponse response = null;
		
		final String url = apiCredentials.getServiceUrl() + NAVIGATION;
		
		try {
			
			ResponseEntity<NavigationDataEntity> responseEntity = restTemplate.getForEntity(url, NavigationDataEntity.class);

			if (null != responseEntity && null != responseEntity.getBody()) {
				response = new ProductCatalogueNavigationResponse(HttpStatus.OK.value(), responseEntity.getBody());
			}
			else {
				response = new ProductCatalogueNavigationResponse(HttpStatus.NOT_FOUND.value(), null);
			}
		}
		catch (RestClientException rce) {
			throw new ProductCatalogueServiceException(
					String.format("Failed to call navigation : %s", rce.getMessage()),
					rce);
		}
		return response;
	}
	private ProductListResponse findProducts(int productListId) throws ProductCatalogueServiceException{
		ProductListResponse response = null;
		final String url = apiCredentials.getServiceUrl() +
				     String.format(PRODUCT_LIST, productListId);
		try {
			ResponseEntity<ProductsDataEntity> responseEntity = restTemplate.getForEntity(
																		String.format(url, productListId),
																		ProductsDataEntity.class);
			
			if (null != responseEntity && null != responseEntity) {
				response = new ProductListResponse(HttpStatus.OK.value(), responseEntity.getBody());
			}
			else {
				response = new ProductListResponse(HttpStatus.NOT_FOUND.value(), null);
			}
			
		}
		catch (RestClientException rce) {
			throw new ProductCatalogueServiceException(
					String.format("Failed to find products : %s", rce.getMessage()),
					rce);
		}
		return response;
	}
	
	private int findProductListId(String categoryPath) {
		List<String> keys = Arrays.asList(categoryPath.split("/"));
		
		ProductCatalogueNavigationResponse navigationResponse = null;
		try {		
			navigationResponse = navigation();
		}
		catch (ProductCatalogueServiceException pcse) {
			RiverIslandLogger.getInfoLogger(this.getClass()).info(pcse.getMessage());
		}
		
		NavigationItem foundItem = null;
		List<NavigationItem> navigationItems = navigationResponse.getNavigationData().getData().getNavigationItems();

		for (String key : keys) {
			foundItem = findProductListId(key, navigationItems);
			if (null != foundItem) {
				navigationItems = foundItem.getChildren();
			}
		}
		
		return null != foundItem ? foundItem.getProductListId() : 0;
	}
	
	private NavigationItem findProductListId(String key, List<NavigationItem> navigationItems) {
		for (NavigationItem item : navigationItems) {
			if (item.getDisplayName().toLowerCase().equals(key.toLowerCase())) {
				return item;
			} else {
				if (item.hasChildren()) {
					NavigationItem foundItem = findProductListId(key, item.getChildren());
					if (null != foundItem) {
						return foundItem;
					}
				}
			}
		}
		return null;
	}

	private class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {
		private final String headerName;
		private final String headerValue;
		
		HeaderRequestInterceptor(String headerName, String headerValue) {
			this.headerName = headerName;
			this.headerValue = headerValue;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			request.getHeaders().set(headerName, headerValue);
			return execution.execute(request, body);
		}		
	}
}
