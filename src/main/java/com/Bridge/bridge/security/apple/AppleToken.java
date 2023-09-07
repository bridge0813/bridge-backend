package com.Bridge.bridge.security.apple;

import com.Bridge.bridge.dto.response.apple.AppleTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;



@Component
@RequiredArgsConstructor
public class AppleToken {

    @Value("${apple.client.id}")
    private String APPLE_CLIENT_ID;

    @Value("${apple.redirect.url}")
    private String APPLE_REDIRECT_URL;

    private final ObjectMapper objectMapper;

    private final AppleUtils appleUtils;

    public AppleTokenResponse getAccessToken(String code)  {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", APPLE_CLIENT_ID);
        params.add("redirect_uri", APPLE_REDIRECT_URL);
        params.add("code", code);
        params.add("client_secret", appleUtils.createClientSecret());

        HttpEntity<MultiValueMap<String, String>> appleTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://appleid.apple.com/auth/token",
                HttpMethod.POST,
                appleTokenRequest,
                String.class
        );

        AppleTokenResponse response = null;
        try {
            response = objectMapper.readValue(accessTokenResponse.getBody(), AppleTokenResponse.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }
}
