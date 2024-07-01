package com.stylit.online.service;

import com.stylit.online.ApiResponse.ApiErrorResponse;
import com.stylit.online.ApiResponse.ApiSuccessResponse;
import com.stylit.online.dto.shopper.AddressRequest;
import com.stylit.online.dto.shopper.UserRequest;
import com.stylit.online.model.shopper.Address;
import com.stylit.online.model.shopper.User;
import com.stylit.online.repository.shopper.UserRepo;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ShopperService {

    @Autowired
    private final UserRepo userRepo;
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

    @Transactional
    public ResponseEntity saveUser(UserRequest userRequest) {
        // Create Address object
        try{
            // Validation
            Map<String , String> errors = new HashMap<>();
            if(userRepo.existsByEmail(userRequest.getEmail())){
                errors.put("email" , "Email is already registered");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("Validation failed", errors));
            }

            Address address = createAddressFromRequest(userRequest.getAddress());

            // Create User object
            User user = createUserFromRequest(userRequest, address);

            // Save user
            User createdUser = userRepo.save(user);

            String userName = userRequest.getEmail() + "shopper";
            String keyCloakResponse =  createUser(userName , userRequest.getEmail() ,userRequest.getPassword());


            Map<String , String> responseData = new HashMap<>();
            responseData.put("id" , String.valueOf(createdUser.getId()));
            responseData.put("name" , createdUser.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse("User registered Successfully" , responseData));

        }catch (DataAccessException e){
            System.err.println("Error Creating User:" + e.getMessage());
            Map<String , String> errors = new HashMap<>();
            errors.put("Database" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse("User Registration Unsuccessful" , errors));
        }
    }

    private Address createAddressFromRequest(AddressRequest addressRequest) {
        return Address.builder()
                .addressLine1(addressRequest.getAddressLine1())
                .addressLine2(addressRequest.getAddressLine2())
                .city(addressRequest.getCity())
                .postalCode(addressRequest.getPostalCode())
                .province(addressRequest.getProvince())
                .country(addressRequest.getCountry())
                .build();
    }

    private User createUserFromRequest(UserRequest userRequest, Address address) {
        return User.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .birthday(userRequest.getBirthday())
                .gender(userRequest.getGender())
                .password(passwordEncoder.encode(userRequest.getPassword())) // Encode password
                .mobileNumber(userRequest.getMobileNumber())
                .address(address)
                .build();
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
                assignClientRoleToUser(userId, "shopper");
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
