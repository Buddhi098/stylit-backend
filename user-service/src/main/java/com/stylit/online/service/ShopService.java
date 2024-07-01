package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.shop.*;
import com.stylit.online.model.shop.*;
import com.stylit.online.model.shop.ShopAddress;
import com.stylit.online.repository.shop.ShopRepo;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ResponseEntity addShop(ShopDTO shopDTO) {
        try {
            // Validation
            Map<String , String> errors = new HashMap<>();
            if(shopRepo.existsByEmail(shopDTO.getEmail())){
                errors.put("email" , "Email already have registered");
            }
            if(!errors.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Email Already have registered" , errors));
            }

            // Business Information
            BusinessInformationDTO businessInformationDTO = shopDTO.getBusinessInformation();
            BusinessInformation businessInformation = BusinessInformation.builder()
                    .businessType(businessInformationDTO.getBusinessType())
                    .businessDocumentPath(businessInformationDTO.getBusinessDocumentPath())
                    .taxIdentificationNumber(businessInformationDTO.getTaxIdentificationNumber())
                    .operatingHours(businessInformationDTO.getOperatingHours())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Shop Address
            ShopAddressDTO shopAddressDTO = shopDTO.getShopAddress();
            ShopAddress shopAddress = ShopAddress.builder()
                    .addressLine1(shopAddressDTO.getAddressLine1())
                    .addressLine2(shopAddressDTO.getAddressLine2())
                    .province(shopAddressDTO.getProvince())
                    .city(shopAddressDTO.getCity())
                    .postalCode(shopAddressDTO.getPostalCode())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Cloth Categories
            Set<ClothCategory> clothCategories = shopDTO.getClothCategories().stream()
                    .map(category -> ClothCategory.builder().categoryName(category.getCategoryName()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build())
                    .collect(Collectors.toSet());

            // Owner Details
            OwnerDTO ownerDTO = shopDTO.getOwner();
            Owner owner = Owner.builder()
                    .email(ownerDTO.getEmail())
                    .contactNumber(ownerDTO.getContactNumber())
                    .fullName(ownerDTO.getFullName())
                    .position(ownerDTO.getPosition())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Payment Details
            PaymentDetailsDTO paymentDetailsDTO = shopDTO.getPaymentDetails();
            PaymentDetails paymentDetails = PaymentDetails.builder()
                    .bankPassBookImageBase64(paymentDetailsDTO.getBankPassBookImageBase64())
                    .accountNumber(paymentDetailsDTO.getAccountNumber())
                    .bankName(paymentDetailsDTO.getBankName())
                    .branch(paymentDetailsDTO.getBranch())
                    .branchCode(paymentDetailsDTO.getBranchCode())
                    .accountHolderName(paymentDetailsDTO.getAccountHolderName())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Storefront Information
            StoreFrontInformationDTO storeFrontInformationDTO = shopDTO.getStorefrontInformation();
            StoreFrontInformation storeFrontInformation = StoreFrontInformation.builder()
                    .logoPath(storeFrontInformationDTO.getLogoPath())
                    .bannerImagePath(storeFrontInformationDTO.getBannerImagePath())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Shop
            String hashedPassword = passwordEncoder.encode(shopDTO.getPassword());
            Shop shop = Shop.builder()
                    .shopName(shopDTO.getShopName())
                    .shopAddress(shopAddress)
                    .email(shopDTO.getEmail())
                    .password(hashedPassword)
                    .contactNumber(shopDTO.getContactNumber())
                    .clothCategories(clothCategories)
                    .businessInformation(businessInformation)
                    .paymentDetails(paymentDetails)
                    .storefrontInformation(storeFrontInformation)
                    .owner(owner)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Create Keycloak Shop User

            String userName = shop.getEmail() + "shop";
            String keyCloakResponse = createUser(userName, shop.getEmail() , shopDTO.getPassword());
            Shop createdShop = shopRepo.save(shop);

            Map<String , String> responseData = new HashMap<>();
            responseData.put("keyCloak_response" , keyCloakResponse);
            responseData.put("id" , String.valueOf(createdShop.getId()));
            responseData.put("name" , createdShop.getShopName());
            responseData.put("email" , createdShop.getEmail());

            String responseStatus;
            if(Objects.equals(keyCloakResponse, "success")){
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Account Created Successfully" , responseData));
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("KeyCloak Server Error" , responseData));
            }

        }catch (DataAccessException e){
            Map<String , String> errors = new HashMap<>();
            errors.put("database" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Database Error Occur" , errors));
        } catch (Exception e) {
            Map<String , String> errors = new HashMap<>();
            errors.put("exception" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Something Went Wrong!" , errors));
        }
    }

    public String saveFile(MultipartFile multipartFile, String filename, String path) {
        try {
            // Clean the filename and ensure it has a valid extension
            String cleanFilename = StringUtils.cleanPath(filename);
            String extension = getFileExtension(multipartFile.getOriginalFilename());
            if (!cleanFilename.toLowerCase().endsWith("." + extension.toLowerCase())) {
                cleanFilename += "." + extension;
            }

            // Create directories if they don't exist
            File storageDir = new File(path);
            if (!storageDir.exists()) {
                boolean dirsCreated = storageDir.mkdirs();
                if (!dirsCreated) {
                    throw new RuntimeException("Failed to create directories: " + storageDir.getAbsolutePath());
                }
            }

            // Save the file to the specified path
            File file = new File(storageDir.getAbsolutePath() + File.separator + cleanFilename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }

            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage(), e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }

    public String createUser(String username, String email, String password) {
        UsersResource usersResource = keycloak.realm(realm).users();
        Logger logger = Logger.getLogger(getClass().getName());

        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(true);

            // Set password credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);

            user.setCredentials(List.of(credential));

            // Create user
            Response response = usersResource.create(user);
            System.out.println(response.getStatusInfo());

            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                logger.info("User created successfully with ID: " + userId);
                System.out.println(userId);
                // Optionally, assign roles to the user
                assignClientRoleToUser(userId, "shop");
                return "success";
            } else {
                return "Failed to create user. Status: " + response.getStatus();
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // Optional method to assign a client role to a user
    private void assignClientRoleToUser(String userId, String roleName) {
        try {
            UsersResource usersResource = keycloak.realm(realm).users();
            List<ClientRepresentation> clientRepresentations = keycloak.realm(realm).clients().findByClientId(clientId);

            if (clientRepresentations.isEmpty()) {
                throw new RuntimeException("Client with clientId " + clientId + " not found.");
            }

            ClientRepresentation clientRepresentation = clientRepresentations.get(0);

            // Get client ID
            String clientUUID = clientRepresentation.getId();

            // Get the ClientResource using the client ID
            ClientResource clientResource = keycloak.realm(realm).clients().get(clientUUID);

            // Get the role representation
            RoleRepresentation clientRole = clientResource.roles().get(roleName).toRepresentation();

            // Assign the client role to the user
            usersResource.get(userId).roles().clientLevel(clientUUID).add(List.of(clientRole));

            Logger.getLogger(getClass().getName()).info("Client role '" + roleName + "' assigned to user with ID: " + userId);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception occurred while assigning client role to user: ", e);
            throw new RuntimeException("Error assigning client role to user: " + e.getMessage(), e);
        }
    }
}
