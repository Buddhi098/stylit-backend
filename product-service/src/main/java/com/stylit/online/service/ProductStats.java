package com.stylit.online.service;

import com.stylit.online.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}