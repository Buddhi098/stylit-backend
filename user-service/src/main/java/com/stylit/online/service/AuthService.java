package com.stylit.online.service;

import com.stylit.online.configuration.WebClientConfig;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
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

    public Map getAccessToken(String username, String password , String userRole) {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        String keyCloakUserName = username + userRole;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        formData.add("username", keyCloakUserName);
        formData.add("password", password);
        formData.add(OAuth2Constants.CLIENT_ID, clientId);
        formData.add(OAuth2Constants.CLIENT_SECRET , clientSecret);

        try{
            return webClientBuilder.build()
                    .post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        }catch (WebClientResponseException e) {
            Map<String , String> response = new HashMap<>();
            response.put("status" , "fail");
            response.put("access_token" , "");
            response.put("error" , e.getResponseBodyAsString());
            return response;

        } catch (Exception e) {
            Map<String , String> response = new HashMap<>();
            response.put("status" , "fail");
            response.put("access_token" , "");
            response.put("error" , e.getMessage());
            return response;
        }
    }

    public Map<String , String> destroyLoginSession(String refreshToken){
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
            Map<String, String> response = new HashMap<>();
            response.put("error" , "");
            response.put("status" , "success");
            return response;

        } catch (WebClientResponseException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error" , e.getMessage());
            response.put("status" , "fail");
            return response;
        }
    }
}
