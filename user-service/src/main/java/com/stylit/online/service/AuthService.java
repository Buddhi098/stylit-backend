package com.stylit.online.service;

import com.stylit.online.dto.auth.LoginRequest;
import com.stylit.online.dto.auth.RefreshToken;
import com.stylit.online.model.courier.Courier;
import com.stylit.online.model.shop.Shop;
import com.stylit.online.model.shopper.User;
import com.stylit.online.repository.courier.CourierRepo;
import com.stylit.online.repository.shop.ShopRepo;
import com.stylit.online.repository.shopper.UserRepo;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
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

    @Autowired
    private final ShopRepo shopRepo;

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final CourierRepo courierRepo;

    public ResponseEntity getAccessToken(LoginRequest loginRequest) {
        String username = loginRequest.getUserName();
        String password = loginRequest.getPassword();
        String userRole = String.valueOf(loginRequest.getUserRole());

        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        String keyCloakUserName = username + userRole;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        formData.add("username", keyCloakUserName);
        formData.add("password", password);
        formData.add(OAuth2Constants.CLIENT_ID, clientId);
        formData.add(OAuth2Constants.CLIENT_SECRET , clientSecret);

        try{
            Map loginData = webClientBuilder.build()
                    .post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();


            if(Objects.equals(userRole, "shop")){
                Optional<Shop> shop = shopRepo.findByShopEmail(username);
                Long id = shop.map(Shop::getId).orElse(null);
                loginData.put("id" , id);
            } else if (Objects.equals(userRole, "shopper")) {
                Optional<User> shopper = userRepo.findByEmail(username);
                Long id = shopper.map(User::getId).orElse(null);
                loginData.put("id" , id);
            } else if( Objects.equals(userRole, "courier" )){
                Optional<Courier> courier = courierRepo.findByCourierEmail(username);
                Long id = courier.map(Courier::getId).orElse(null);
                loginData.put("id" , id);
            }

            return ResponseEntity.status(HttpStatus.OK).body(loginData);

        }catch (WebClientResponseException e) {
            Map<String , Object> response = new HashMap<>();
            response.put("status" , "fail");
            response.put("access_token" , "");
            response.put("error" , e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            Map<String , Object> response = new HashMap<>();
            response.put("status" , "fail");
            response.put("access_token" , "");
            response.put("error" , e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public Map<String, Object> destroyLoginSession(String refreshToken){
        String logoutUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        try {
            webClientBuilder.build().post()
                    .uri(logoutUrl)
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("refresh_token", refreshToken))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            Map<String, Object> response = new HashMap<>();
            response.put("error" , "");
            response.put("status" , "success");
            return response;

        } catch (WebClientResponseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            response.put("status" , "fail");
            return response;
        }
    }

    public Map<String, Object> getAccessTokenUsingRefreshToken(RefreshToken refreshToken) {
        String refreshTokenInString = refreshToken.getRefreshToken();
        String tokenEndpoint =  keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        try {
           Map accessToken =  webClientBuilder.build().post()
                    .uri(tokenEndpoint)
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("refresh_token", refreshTokenInString)
                            .with("grant_type" , "refresh_token"))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return accessToken;

        } catch (WebClientResponseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error" , e.getMessage());
            return response;
        }
    }
}
