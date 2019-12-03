package com.riverisland.app.automation.utils;

import com.riverisland.app.automation.enums.Category;
import com.riverisland.app.automation.enums.FemaleCategory;
import com.riverisland.app.automation.enums.MaleCategory;
import com.riverisland.app.automation.pojos.TcplApiCredentials;
import com.riverisland.automation.utils.ecom.domain.product.ProductLookupRequest;
import com.riverisland.automation.utils.ecom.domain.product.ProductLookupResponse;
import com.riverisland.automation.utils.ecom.domain.product.pojos.Product;
import com.riverisland.automation.utils.ecom.service.ProductTestService;
import io.restassured.filter.log.LogDetail;
import org.apache.commons.lang3.EnumUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Prashant Ramcharan on 20/10/2017
 */
public final class ProductUtils {

    private ProductUtils() {
    }

    public static Map<Category, List<Product>> getInStockProductsFromTcplApi(TcplApiCredentials credentials) {
        final Map<Category, List<Product>> productMap = new HashMap<>();

        if (credentials != null) {
            final String endpoint = "/testing/productlookup";

            final ProductTestService productTestService = ProductTestService.create(credentials.getServiceUrl(), credentials.getApiKey(), LogDetail.URI);

            EnumUtils.getEnumList(Category.class).stream().filter(skipCategory -> !skipCategory.getName().equals(Category.KIDS.getName())).forEach(category -> {
                final String categoryAndSubCategory;

                if (category == Category.MEN || category == Category.BOYS) {
                    categoryAndSubCategory = String.format("%s/%s", category.getName(), MaleCategory.randomise().getName()).toLowerCase();
                } else {
                    categoryAndSubCategory = String.format("%s/%s", category.getName(), FemaleCategory.randomise().getName()).toLowerCase();
                }

                try {
                    ProductLookupRequest request = new ProductLookupRequest(categoryAndSubCategory, "InStock");
                    ProductLookupResponse response = productTestService.lookupProduct(endpoint, request);
                    productMap.put(category, response.getProductList());
                } catch (Exception ignored) {
                	String tmp = ignored.getMessage();
                }
            });
        }
        return productMap;
    }
}