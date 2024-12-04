package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.IsEmailExistDTO;
import com.stylit.online.dto.courier.*;
import com.stylit.online.model.courier.*;
import com.stylit.online.model.shop.Shop;
import com.stylit.online.repository.courier.CourierRepo;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.io.Files.getFileExtension;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final CourierRepo courierRepo;

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final WebClient.Builder webClientBuilder;

    @Transactional
    public ResponseEntity createCourier(CourierDTO courierDTO) {
        try {

            // Validation
            Map<String, Object> errors = new HashMap<>();
            if (courierRepo.existsByCourierEmail(courierDTO.getCourierEmail())) {
                errors.put("email", "Email have already Registered");
            }
            if (!courierDTO.getPassword().equals(courierDTO.getConfirm_password())) {
                errors.put("password", "Password and Confirm Password are not Match");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Email have already Registered", errors));
            }

            CourierBusinessDataDTO courierBusinessDataDTO = courierDTO.getCourierBusinessData();
            CourierBusinessData courierBusinessData = CourierBusinessData.builder()
                    .businessRegNo(courierBusinessDataDTO.getBusinessRegNo())
                    .businessType(courierBusinessDataDTO.getBusinessType())
                    .businessRegDate(courierBusinessDataDTO.getBusinessRegDate())
                    .businessEmail(courierBusinessDataDTO.getBusinessEmail())
                    .businessDocument(courierBusinessDataDTO.getBusinessDocument())
                    .availableLocations(courierBusinessDataDTO.getAvailableLocations())
                    .build();

            CourierLocationDTO courierLocationDTO = courierDTO.getCourierLocation();
            CourierLocation courierLocation = CourierLocation.builder()
                    .city(courierLocationDTO.getCity())
                    .addressLine2(courierLocationDTO.getAddressLine2())
                    .addressLine1(courierLocationDTO.getAddressLine1())
                    .postalCode(courierLocationDTO.getPostalCode())
                    .latitude(courierLocationDTO.getLatitude())
                    .longitude(courierLocationDTO.getLongitude())
                    .province(courierLocationDTO.getProvince())
                    .build();

            String hashedPassword = passwordEncoder.encode(courierDTO.getPassword());

            Courier courier = Courier.builder()
                    .courierName(courierDTO.getCourierName())
                    .courierContactNumber(courierDTO.getCourierContactNumber())
                    .courierEmail(courierDTO.getCourierEmail())
                    .password(hashedPassword)
                    .courierLocation(courierLocation)
                    .courierBusinessData(courierBusinessData)
                    .status(Courier.Status.valueOf("pending"))
                    .build();

            String userName = courier.getCourierEmail() + "courier";
            String keyCloakResponse = createUser(userName, courier.getCourierEmail(), courierDTO.getPassword());


            if (Objects.equals(keyCloakResponse, "success")) {
                Courier createdCourier = courierRepo.save(courier);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("keyCloak_response", keyCloakResponse);
                responseData.put("id", String.valueOf(createdCourier.getId()));
                responseData.put("name", createdCourier.getCourierContactNumber());
                responseData.put("email", createdCourier.getCourierEmail());

                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("Account Created Successfully", responseData));
            } else {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("keyCloak_response", keyCloakResponse);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((new ApiErrorResponse("KeyCloak Error Occur", responseData)));
            }

        } catch (DataAccessException e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("database", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Database error Occur", errors));
        } catch (Exception e) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("exception", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Something Went Wrong", errors));
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
                assignClientRoleToUser(userId, "courier");
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
            if (courierRepo.existsByCourierEmail(email)) {
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

    public ResponseEntity getAllCourier() {
        try{
            List<Courier> allCourier = courierRepo.findAll();
            Map<String , Object> data = new HashMap<>();
            data.put("courier" , allCourier);
            data.put("status" , "success");
            data.put("message" , "Fetch All Courier data");
            return ResponseEntity.status(HttpStatus.OK).body(data);
        }catch (Exception e){
            Map<String , Object> errors = new HashMap<>();
            errors.put("Exception" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Can't fetch data" , errors));
        }

    }

    public ResponseEntity updateCourierStatus(Long shopId, Courier.Status status) {
        try{
            System.out.println(shopId);
            System.out.println(status);
            Optional<Courier> optionalCourier = courierRepo.findById(shopId);
            if (optionalCourier.isPresent()) {
                Courier courier = optionalCourier.get();
                courier.setStatus(status);  // Set the new status
                courierRepo.save(courier);  // Save the updated shop
                Map<String , Object> data = new HashMap<>();
                return ResponseEntity.status(HttpStatus.OK).body(new ApiSuccessResponse("Changed Status", data));
            }
            Map<String , Object> errors = new HashMap<>();
            errors.put("Exception" , "Courier Can't find");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Can't find Courier" , errors));
        }catch (Exception e){
            Map<String , Object> errors = new HashMap<>();
            errors.put("Exception" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Can't fetch data" , errors));
        }

    }

    public ResponseEntity getAllActiveCourier(){
        try{
            List<Courier> activeCourier = courierRepo.findByStatus(Courier.Status.valueOf("active"));

            return ResponseEntity.status(HttpStatus.OK).body(activeCourier);

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
