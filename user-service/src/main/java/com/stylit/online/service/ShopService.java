package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.IsEmailExistDTO;
import com.stylit.online.dto.shop.*;
import com.stylit.online.model.courier.Courier;
import com.stylit.online.model.shop.*;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;

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

    @Value("${storage.images.shop.logo}")
    private String logoPath;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private final Base64ToFile base64ToFile;

    public ResponseEntity addShop(ShopDTO shopDTO) {
        try {
            // Validation
            Map<String , Object> errors = new HashMap<>();
            if(shopRepo.existsByShopEmail(shopDTO.getShopEmail())){
                errors.put("email" , "Email already have registered");
            }
            if(!errors.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Email Already have registered" , errors));
            }

            // Shop Information
            List<CategoryDTO> categoryDTOList = shopDTO.getShopInformation().getCategories();
            List<Category> categories = categoryDTOList.stream()
                    .map(categoryDTO -> Category.builder()
                            .title(categoryDTO.getTitle())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build())
                    .toList();
            ShopInformationDTO shopInformationDTO = shopDTO.getShopInformation();

            // Save logo to Storage
            base64ToFile.saveImage(shopInformationDTO.getLogo() , logoPath+"/"+shopDTO.getShopEmail());

            ShopInformation shopInformation = ShopInformation.builder()
                    .shopDescription(shopInformationDTO.getShopDescription())
                    .categories(categories)
                    .facebookLink(shopInformationDTO.getFacebookLink())
                    .instagramLink(shopInformationDTO.getInstagramLink())
                    .logo(logoPath)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now()).build();

            // Shop Location
            ShopLocationDTO locationDTO = shopDTO.getShopLocation();
            ShopLocation shopLocation = ShopLocation.builder()
                    .addressLine1(locationDTO.getAddressLine1())
                    .addressLine2(locationDTO.getAddressLine2())
                    .city(locationDTO.getCity())
                    .postalCode(locationDTO.getPostalCode())
                    .longitude(locationDTO.getLongitude())
                    .latitude(locationDTO.getLatitude())
                    .province(locationDTO.getProvince())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now()).build();

            // Shop Business Data
            ShopBusinessDataDTO shopBusinessDataDTO = shopDTO.getShopBusinessData();
            ShopBusinessData shopBusinessData = ShopBusinessData.builder()
                    .businessEmail(shopBusinessDataDTO.getBusinessEmail())
                    .businessRegDate(shopBusinessDataDTO.getBusinessRegDate())
                    .businessRegNo(shopBusinessDataDTO.getBusinessRegNo())
                    .businessType(shopBusinessDataDTO.getBusinessType())
                    .businessDocument(shopBusinessDataDTO.getBusinessDocument())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now()).build();

            String hashedPassword = passwordEncoder.encode(shopDTO.getPassword());

            // Shop
            Shop shop = Shop.builder()
                    .shopName(shopDTO.getShopName())
                    .shopEmail(shopDTO.getShopEmail())
                    .shopContactNumber(shopDTO.getShopContactNumber())
                    .password(hashedPassword)
                    .shopInformation(shopInformation)
                    .shopLocation(shopLocation)
                    .status(Shop.Status.valueOf("pending"))
                    .shopBusinessData(shopBusinessData)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now()).build();

            // Create Keycloak Shop User

            String userName = shop.getShopEmail() + "shop";
            String keyCloakResponse = createUser(userName, shop.getShopEmail() , shopDTO.getPassword());

            String responseStatus;
            if(Objects.equals(keyCloakResponse, "success")){
                Shop createdShop = shopRepo.save(shop);

                Map<String , Object> responseData = new HashMap<>();
                responseData.put("keyCloak_response" , keyCloakResponse);
                responseData.put("id" , String.valueOf(createdShop.getId()));
                responseData.put("name" , createdShop.getShopName());
                responseData.put("email" , shop.getShopEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Account Created Successfully" , responseData));
            }else{
                Map<String , Object> responseData = new HashMap<>();
                responseData.put("keyCloak_response" , keyCloakResponse);
                responseData.put("id" , String.valueOf(shop.getId()));
                responseData.put("name" , shop.getShopName());
                responseData.put("email" , shop.getShopEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("KeyCloak Server Error" , responseData));
            }

        }catch (DataAccessException e){
            Map<String , Object> errors = new HashMap<>();
            errors.put("database" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Database Error Occur" , errors));
        } catch (Exception e) {
            Map<String , Object> errors = new HashMap<>();
            errors.put("exception" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Something Went Wrong!" , errors));
        }
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

    public ResponseEntity isEmailExist(IsEmailExistDTO isEmailExistDTO) {
        try {
            String email = isEmailExistDTO.getEmail();
            if (shopRepo.existsByShopEmail(isEmailExistDTO.getEmail())) {
                Map<String, Object> data = new HashMap<>();
                data.put("isExistEmail", true);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Email have Already Registered", data));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("isExistEmail", false);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Email haven't Registered yet", data));
            }
        } catch (Exception e) {
            Map<String, Object> erros = new HashMap<>();
            erros.put("Exception", e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiErrorResponse("Email haven't Registered yet", erros));
        }
    }
}
