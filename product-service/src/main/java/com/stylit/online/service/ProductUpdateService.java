package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.repository.ProductRepo;
import com.stylit.online.model.*; // Assuming this contains your models like Product, AdditionalInfo, etc.
import com.stylit.online.dto.*; // Assuming this contains your DTOs like ProductDTO, AdditionalInfoDTO, etc.
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductUpdateService {

    private final ProductRepo productRepo;

    public ResponseEntity<?> updateProduct(Long productId, ProductDTO productDTO) {
        try {
            // Validate the input data
            if (productDTO == null) {
                return ResponseEntity.badRequest().body("Product data is missing");
            }

            // Retrieve the existing product by ID
            Optional<Product> existingProductOpt = productRepo.findById(productId);
            if (existingProductOpt.isEmpty())  {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            Product existingProduct = existingProductOpt.get();

            // Update AdditionalInfo
            AdditionalInfoDTO additionalInfoDTO = productDTO.getAdditionalInfo();
            if (additionalInfoDTO != null) {
                AdditionalInfo additionalInfo = existingProduct.getAdditionalInfo();
                additionalInfo.setAgeGroup(additionalInfoDTO.getAgeGroup());
                additionalInfo.setSeason(additionalInfoDTO.getSeason());
                additionalInfo.setOccasions(new ArrayList<>(additionalInfoDTO.getOccasions()));
            }

            // Update GeneralInformation
            GeneralInformationDTO generalInformationDTO = productDTO.getGeneralInformation();
            if (generalInformationDTO != null) {
                GeneralInformation generalInformation = existingProduct.getGeneralInformation();
                generalInformation.setSku(generalInformationDTO.getSku());
                generalInformation.setBrand(generalInformationDTO.getBrand());
                generalInformation.setProductName(generalInformationDTO.getProductName());
                generalInformation.setDescription(generalInformationDTO.getDescription());
                generalInformation.setGender(generalInformationDTO.getGender());
                generalInformation.setSubcategory(generalInformationDTO.getSubcategory());
                generalInformation.setCategory(generalInformationDTO.getCategory());
            }

            // Update MaterialCare
            MaterialCareDTO materialCareDTO = productDTO.getMaterialCare();
            if (materialCareDTO != null) {
                MaterialCare materialCare = existingProduct.getMaterialCare();
                materialCare.setMaterial(materialCareDTO.getMaterial());
                materialCare.setCareInstructions(materialCareDTO.getCareInstructions());
                materialCare.setPattern(materialCareDTO.getPattern());
            }

            // Update Pricing
            PricingDTO pricingDTO = productDTO.getPricing();
            if (pricingDTO != null) {
                Pricing pricing = existingProduct.getPricing();
                pricing.setBasePrice(pricingDTO.getBasePrice());
                pricing.setDiscount(pricingDTO.getDiscount());
                pricing.setDiscountType(pricingDTO.getDiscountType());
            }

            // Update VariantBox
            List<VariantBoxDTO> variantBoxDTOS = productDTO.getVariantBoxes();
            if (variantBoxDTOS != null && !variantBoxDTOS.isEmpty()) {
                List<VariantBox> variantBoxes = variantBoxDTOS.stream()
                        .map(variantBoxDTO -> VariantBox.builder()
                                .colorVariant(variantBoxDTO.getColorVariant())
                                .status(variantBoxDTO.getStatus())
                                .sizeQuantityChart(
                                        variantBoxDTO.getSizeQuantityChart().stream()
                                                .map(sizeQuantity -> SizeQuantityChart.builder()
                                                        .size(sizeQuantity.getSize())
                                                        .quantity(sizeQuantity.getQuantity())
                                                        .build())
                                                .collect(Collectors.toList())
                                )
                                .build())
                        .collect(Collectors.toList());
                existingProduct.getVariantBoxes().clear();
                existingProduct.setVariantBoxes(variantBoxes);
            }

            // Update the timestamps
            existingProduct.setUpdatedAt(LocalDateTime.now());

            // Save the updated product to the database
            Product updatedProduct = productRepo.save(existingProduct);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("ProductId", updatedProduct.getId());

            return ResponseEntity.ok(new ApiSuccessResponse("Product Updated Successfully", responseData));


        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Data integrity violation: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the product: " + ex.getMessage());
        }
    }

    public ResponseEntity getProductById(Long id, Long shopId) {
        try{
            Optional<Product> product = productRepo.findByIdAndShopId( id , String.valueOf(shopId) );
            Product product1 = product.get();

            return ResponseEntity.status(HttpStatus.OK).body(product1);
        }catch (Exception e){
            Map<String , String> response= new HashMap<>();

            response.put("error" , e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
