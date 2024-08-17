package com.stylit.online.controller;

import com.stylit.online.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
