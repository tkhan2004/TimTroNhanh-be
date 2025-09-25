package com.phongtro247backend.service.ServiceImp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.GoogleAuthService;
import com.phongtro247backend.util.JwtUtil;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceIml implements GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PasswordEncoder passwordEncoder;

    private final NetHttpTransport transport = new NetHttpTransport();

    private final JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    @Override
    public String getGoogleLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile";
    }

    @Override
    public AuthResponse handleGoogleCallBack(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String tokenResponse = restTemplate.postForObject(tokenUrl, request, String.class);

        JsonNode tokenJson;
        try {
            tokenJson = objectMapper.readTree(tokenResponse);
        } catch (Exception e) {
            throw new RuntimeException("Không parse được token từ Google: " + tokenResponse);
        }

        String accessToken = tokenJson.get("access_token").asText();

        // Lấy user info
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
        String userInfoResponse = restTemplate.getForObject(userInfoUrl, String.class);

        JsonNode userInfoJson;
        try {
            userInfoJson = objectMapper.readTree(userInfoResponse);
        } catch (Exception e) {
            throw new RuntimeException("Không parse được thông tin từ Google: " + userInfoResponse);
        }

        String email = userInfoJson.get("email").asText();
        String name = userInfoJson.get("name").asText();
        String avatar = userInfoJson.get("picture").asText();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .fullName(name)
                        .avatarUrl(avatar)
                        .role(UserRole.RENTER)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 👈 thêm dòng này
                        .build());

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return AuthResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Override
    public User verifyGoogleToken(String idToken) {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken googleIdToken = null;
        try {
            googleIdToken = verifier.verify(idToken);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid ID token.");
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        // Tìm user trong DB, nếu chưa có thì tạo mới
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    newUser.setFullName(name);
                    newUser.setAvatarUrl(picture);
                    newUser.setRole(UserRole.RENTER);
                    return userRepository.save(newUser);
                });
    }
}
