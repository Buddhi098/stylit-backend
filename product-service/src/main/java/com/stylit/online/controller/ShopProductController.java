package com.stylit.online.controller;

import com.stylit.online.dto.DeleteProduct;
import com.stylit.online.dto.ProductDTO;
import com.stylit.online.service.ProductService;
import com.stylit.online.service.ProductUpdateService;
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

    private final ProductUpdateService productUpdateService;

    @PostMapping("/add_new_product")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity addNewProduct(@Valid @RequestBody ProductDTO productDTO){
        return productService.addProduct(productDTO);
    }

    @GetMapping("/get_all_product_by_shop_id")
    public ResponseEntity getAllProductByShopId(@RequestParam String id){
        return productService.getAllProductByShopId(id);
    }

    @PostMapping("/delete_product")
    public ResponseEntity deleteProductByShop(@RequestBody DeleteProduct deleteProduct){
        return productService.deleteProductByShop(deleteProduct);
    }

    @PutMapping("/update_product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return productUpdateService.updateProduct(id, productDTO);
    }

    @GetMapping("/get_product_by_id/{product_id}/{shop_id}")
    public ResponseEntity<?> getProductById(@PathVariable("product_id") Long id, @PathVariable("shop_id") Long shopId) {
        return productUpdateService.getProductById(id, shopId);
    }


}
