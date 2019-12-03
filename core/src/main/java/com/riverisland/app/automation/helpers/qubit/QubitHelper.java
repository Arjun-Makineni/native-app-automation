package com.riverisland.app.automation.helpers.qubit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class QubitHelper {
	
	private static final String QBIT_URL = "https://tally-1.qubitproducts.com/tally/riverisland/topk/riverIsland-socialProofTopKAddToBags-%s?k=%s";
	
	private RestTemplate restTemplate;
	
	public QubitHelper() {
		this.restTemplate = new RestTemplate();
	}

	public QubitResponse trendingItems(TrendingCategory category, int itemCount) throws QubitException {
		
		QubitResponse response = null;

		try {
			 
			 Map<String, Object> payload = restTemplate.getForObject(
					 String.format(QBIT_URL, category.getCategory(), itemCount),
					 Map.class);
			
			 response = new QubitResponse(
					 HttpStatus.OK.value(), 
					 extractProductIds((List<List<String>>) payload.get("data")));			 		
		}
		catch (RestClientException rce) {
			throw new QubitException(
					String.format("Failed to obtain Qbit trending information : %s", rce.getMessage()), 
					rce);
		}		
		return response;
	}
	
	private List<String> extractProductIds (List<List<String>> data) {
		List<String> productIds = new ArrayList<>();
		
		for (List<String> product : data) {		
			String [] productId = product.get(0).split("::");
			productIds.add(productId.length == 4 ? productId[productId.length-1] : "");
		}
		return productIds;
	}
}
