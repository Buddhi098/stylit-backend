package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.*;
import com.stylit.online.model.*;
import com.stylit.online.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepo productRepo;

    public ResponseEntity<?> addProduct(ProductDTO productDTO) {
        try {
            // Validate the input data
            if (productDTO == null) {
                return ResponseEntity.badRequest().body("Product data is missing");
            }

            // Map AdditionalInfoDTO to AdditionalInfo
            AdditionalInfoDTO additionalInfoDTO = productDTO.getAdditionalInfo();
            if (additionalInfoDTO == null) {
                return ResponseEntity.badRequest().body("Additional info is missing");
            }
            AdditionalInfo additionalInfo = AdditionalInfo.builder()
                    .ageGroup(additionalInfoDTO.getAgeGroup())
                    .season(additionalInfoDTO.getSeason())
                    .occasions(new ArrayList<>(additionalInfoDTO.getOccasions()))
                    .build();

            // Map GeneralInformationDTO to GeneralInformation
            GeneralInformationDTO generalInformationDTO = productDTO.getGeneralInformation();
            if (generalInformationDTO == null) {
                return ResponseEntity.badRequest().body("General information is missing");
            }
            GeneralInformation generalInformation = GeneralInformation.builder()
                    .sku(generalInformationDTO.getSku())
                    .brand(generalInformationDTO.getBrand())
                    .productName(generalInformationDTO.getProductName())
                    .description(generalInformationDTO.getDescription())
                    .gender(generalInformationDTO.getGender())
                    .subcategory(generalInformationDTO.getSubcategory())
                    .category(generalInformationDTO.getCategory())
                    .build();

            // Map MaterialCareDTO to MaterialCare
            MaterialCareDTO materialCareDTO = productDTO.getMaterialCare();
            if (materialCareDTO == null) {
                return ResponseEntity.badRequest().body("Material care information is missing");
            }
            MaterialCare materialCare = MaterialCare.builder()
                    .material(materialCareDTO.getMaterial())
                    .careInstructions(materialCareDTO.getCareInstructions())
                    .pattern(materialCareDTO.getPattern())
                    .build();

            // Map PricingDTO to Pricing
            PricingDTO pricingDTO = productDTO.getPricing();
            if (pricingDTO == null) {
                return ResponseEntity.badRequest().body("Pricing information is missing");
            }
            Pricing pricing = Pricing.builder()
                    .basePrice(pricingDTO.getBasePrice())
                    .discount(pricingDTO.getDiscount())
                    .discountType(pricingDTO.getDiscountType())
                    .build();

            // Map VariantBoxDTO to VariantBox
            List<VariantBoxDTO> variantBoxDTOS = productDTO.getVariantBoxes();
            if (variantBoxDTOS == null || variantBoxDTOS.isEmpty()) {
                return ResponseEntity.badRequest().body("Variant boxes are missing");
            }
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

            // Build the Product entity
            Product product = Product.builder()
                    .shopId(productDTO.getShopId())
                    .additionalInfo(additionalInfo)
                    .generalInformation(generalInformation)
                    .pricing(pricing)
                    .materialCare(materialCare)
                    .variantBoxes(variantBoxes)
                    .isRemove(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save the product to the database (Assuming productRepository is available)
            Product createdProduct = productRepo.save(product);
            Map<String , Object> responseData = new HashMap<>();
            responseData.put("ProductId" , createdProduct.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Product Created Successfully" , responseData));

        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Data integrity violation: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the product: " + ex.getMessage());
        }
    }

    public ResponseEntity getAllProduct(){
        try{
            List<Product> products = productRepo.findAllActiveProducts();
            Map<String , Object> response = new HashMap<>();
            response.put("products" , products);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Here you can see All the products of Stylit System" , response));

        }catch (DataAccessException e){
            Map<String , Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("Fail" , response));
        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("Fail" , response));
        }
    }

    public ResponseEntity getAllProductByShopId(String id){
        try{
            List<Product> products = productRepo.findAllActiveProductsByShopId(id);
            Map<String , Object> response = new HashMap<>();
            response.put("products" , products);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Here you can see product according shop" , response));

        }catch (DataAccessException e){
            Map<String , Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("Fail" , response));
        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("Fail" , response));
        }
    }


    public ResponseEntity deleteProductByShop(DeleteProduct deleteProduct) {
        try{
          int response =   productRepo.markProductAsRemoved(deleteProduct.getProductId() , deleteProduct.getShopId());

          if(response == 0){
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("product Can't find" , new HashMap<>()));
          }

            return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("product Delete Successfully" , new HashMap<>()));
        }catch(DataAccessException e){
            Map<String , Object> response = new HashMap<>();
            response.put("Database_error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail" , response));
        }catch (Exception e){
            Map<String , Object> response = new HashMap<>();
            response.put("Database_error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail" , response));
        }
    }

    public ResponseEntity getFilteredProduct(String gender, String category, String subCategory, boolean isDiscount, boolean isNew, int page, int size) {
        try {
            LocalDateTime newItemDate = isNew ? LocalDateTime.now().minus(1, ChronoUnit.MONTHS) : LocalDateTime.MIN;
            Pageable pageable = PageRequest.of(page, size);
            List<Product> products = productRepo.findAllActiveProductsByGender(gender, pageable);

            List<Product> filteredProducts = products.stream()
                    .filter(product -> Objects.equals(category, "") || product.getGeneralInformation().getCategory().equalsIgnoreCase(category))
                    .filter(product -> Objects.equals(subCategory, "") || product.getGeneralInformation().getSubcategory().equalsIgnoreCase(subCategory))
                    .filter(product -> !isDiscount || !Objects.equals(product.getPricing().getDiscountType(), "")) // Assuming `isDiscounted` is a method or a field
                    .filter(product -> !isNew || product.getCreatedAt().isAfter(newItemDate))
                    .toList();

            return ResponseEntity.status(HttpStatus.OK).body(filteredProducts);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail", Map.of("Database_error", e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail", Map.of("error", e.getMessage())));
        }
    }

    public ResponseEntity getProductById(Long productId) {
        try {
            Optional<Product> product = productRepo.findById(productId);
            if (product.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Product Can't find", new HashMap<>()));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Product Found", response));
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("Database_error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail", response));
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse("fail", response));
        }

    }
}
