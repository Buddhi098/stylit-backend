package com.stylit.online.controller;

import com.stylit.online.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("public/product")
@RequiredArgsConstructor
@Validated
public class PublicProductController {

    @Autowired
    private final ProductService productService;

    @GetMapping("/get_all_product")
    public ResponseEntity getAllProduct(){
        return productService.getAllProduct();
    }

    @PostMapping("/get_filtered_product")
    public ResponseEntity getFilteredProduct(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false , defaultValue = "") String category,
            @RequestParam(required = false , defaultValue = "") String subCategory,
            @RequestParam(defaultValue = "false" , required = false) boolean isDiscount,
            @RequestParam(defaultValue = "false" , required = false) boolean isNew,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size

    ){
        return productService.getFilteredProduct(gender , category , subCategory , isDiscount , isNew , page , size);
    }

    @GetMapping("/get_product_by_id/{productId}")
    public ResponseEntity getProductById(@PathVariable Long productId){
        return productService.getProductById(productId);
    }
}
