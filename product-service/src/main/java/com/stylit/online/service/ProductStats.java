package com.stylit.online.service;

import com.stylit.online.model.Product;
import com.stylit.online.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductStats {

    @Autowired
    private ProductRepo productRepo;

    public ResponseEntity<Map<String, Object>> getProductStatistics(String shopId) {
        long totalProducts = productRepo.countTotalProductsByShopId(shopId);
        long activeProducts = productRepo.countActiveProductsByShopId(shopId);
        long outOfStockProducts = productRepo.countOutOfStockProductsByShopId(shopId);
        long discountedProducts = productRepo.countDiscountedProductsByShopId(shopId);
        List<Object[]> genderRate = productRepo.countProductsByGenderByShopId(shopId);

        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", totalProducts);
        response.put("activeProducts", activeProducts);
        response.put("outOfStockProducts", outOfStockProducts);
        response.put("discountedProducts", discountedProducts);
        response.put("genderRate", genderRate);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getTop6ProductsByQuantity(String shopId) {
        List<Object[]> topProducts = productRepo.findTopProductsByQuantityAndShopId(shopId, PageRequest.of(0, 6));
        List<Map<String, Object>> productDetails = topProducts.stream().map(result -> {
            Product product = (Product) result[0];
            Long totalQuantity = (Long) result[1];
            Map<String, Object> details = new HashMap<>();
            details.put("generalInformation", product.getGeneralInformation());
            details.put("pricing", product.getPricing());
            details.put("totalQuantity", totalQuantity);
            return details;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("topProducts", productDetails);

        return ResponseEntity.ok(response);
    }
}