package game.gamegoodgood.config.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@RestController
public class OAuth2LoginController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginController.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 의존성 주입

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final String tokenUri = "https://oauth2.googleapis.com/token";
    private final String userInfoUri = "https://www.googleapis.com/oauth2/v3/userinfo";

    public OAuth2LoginController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 구글 로그인 후 인증 코드 처리
    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<String> googleLogin(@RequestParam("code") String code) {
        try {
            // 로그 추가: 인증 코드
            logger.info("Received Google OAuth2 code: {}", code);

            // 1. 액세스 토큰을 구글에서 요청
            String accessToken = getAccessTokenFromGoogle(code);
            // 로그 추가: 액세스 토큰
            logger.info("Received Access Token: {}", accessToken);

            // 2. 액세스 토큰을 사용하여 구글 사용자 정보 가져오기
            String userInfo = getGoogleUserInfo(accessToken);
            // 로그 추가: 사용자 정보
            logger.info("Received User Info: {}", userInfo);

            // 3. 사용자 정보 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode userNode = objectMapper.readTree(userInfo);
            String userEmail = userNode.get("email").asText();
            String username = userNode.get("name").asText();
            String role = "USER";

            // 로그 추가: 사용자 정보
            logger.info("Parsed User Info - Email: {}, Username: {}", userEmail, username);

            // 4. 사용자 정보 저장 또는 업데이트
            Users existingUser = userRepository.findByUserEmail(userEmail).orElseGet(() -> new Users());
            existingUser.setUsername(username);
            existingUser.setUserEmail(userEmail);
            existingUser.setRole(role);
            userRepository.save(existingUser);

            // 5. JWT 토큰 발급
            Map<String, Object> userInfoMap = new HashMap<>();
            userInfoMap.put("email", userEmail);  // 사용자 정보를 map에 추가
            String jwtToken = jwtTokenProvider.generateTokenFromUserInfo(userInfoMap);

            // 로그 추가: JWT 토큰
            logger.info("Generated JWT Token: {}", jwtToken);

            // 6. JWT 토큰을 클라이언트에게 반환
            return ResponseEntity.ok(jwtToken); // JWT 토큰을 클라이언트에게 반환

        } catch (Exception e) {
            // 에러 로그
            logger.error("Error occurred during Google login: ", e);
            return ResponseEntity.status(500).body("구글 로그인 처리 중 오류가 발생했습니다.");
        }
    }

    // 구글에서 액세스 토큰을 받아오는 메서드
    private String getAccessTokenFromGoogle(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String requestBody = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("code", code)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        // 로그 추가: 요청 URL 및 파라미터
        logger.info("Requesting Access Token from Google with body: {}", requestBody);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                entity,
                String.class
        );

        if (response.getStatusCodeValue() == 200) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                // 로그 추가: 응답 받은 액세스 토큰
                logger.info("Received Access Token Response: {}", responseJson);
                return responseJson.get("access_token").asText();
            } catch (IOException e) {
                logger.error("Failed to parse access token response", e);
                throw new RuntimeException("액세스 토큰을 파싱하는 데 실패했습니다.", e);
            }
        } else {
            logger.error("Google OAuth2 Server error: {}", response.getStatusCode());
            throw new RuntimeException("구글 OAuth2 서버 응답 오류: " + response.getStatusCode());
        }
    }

    // 액세스 토큰을 사용하여 구글 사용자 정보 가져오기
    private String getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                String.class
        );

        // 로그 추가: 구글 사용자 정보 응답
        logger.info("Received User Info Response: {}", response.getBody());

        return response.getBody();
    }
}
