package com.stylit.online.service;

import com.stylit.online.dto.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String , Object> destroyLoginSession(String refreshToken){
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
}
