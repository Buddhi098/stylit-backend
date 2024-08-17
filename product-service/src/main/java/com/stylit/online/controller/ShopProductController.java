package com.stylit.online.controller;

import com.stylit.online.dto.ProductDTO;
import com.stylit.online.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shop/product")
@RequiredArgsConstructor
@Validated
public class ShopProductController {

    @Autowired
    private final ProductService productService;

    @PostMapping("/add_new_product")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity addNewProduct(@Valid @RequestBody ProductDTO productDTO){
        return productService.addProduct(productDTO);
    }

    @GetMapping("/get_all_product_by_shop_id")
    public ResponseEntity getAllProductByShopId(@RequestParam String id){
        return productService.getAllProductByShopId(id);
    }
}
