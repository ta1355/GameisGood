package game.gamegoodgood.config.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.gamegoodgood.config.jwt.JwtTokenProvider;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OAuth2LoginController {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginController.class);
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final OAuth2LoginService oAuth2LoginService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final String tokenUri = "https://oauth2.googleapis.com/token";

    private final String userInfoUri = "https://www.googleapis.com/oauth2/v3/userinfo";

    public OAuth2LoginController(UserRepository userRepository,
                                 JwtTokenProvider jwtTokenProvider,
                                 OAuth2LoginService oAuth2LoginService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.oAuth2LoginService = oAuth2LoginService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/login")
    public ResponseEntity<?> handleLoginGet(@RequestParam(required = false) String error) {
        logger.info("로그인 GET 요청 수신" + (error != null ? "오류 발생" : ""));

        Map<String, Object> response = new HashMap<>();

        if (error != null) {
            response.put("error", true);
            response.put("message", "로그인 처리 중 오류가 발생했습니다.");
        } else {
            response.put("error", false);
            response.put("message", "로그인 페이지에 접근했습니다.");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/login/oauth2/code/google")
    public ResponseEntity<?> handleGoogleLogin(@RequestBody GoogleLoginRequest request) {
        logger.info("POST 요청 수신 : {}", request.getCode());
        logger.info("수신된 리다이렉트 URI : {}", request.getRedirectUri());
        try {
            // 1. 액세스 토큰 요청
            String accessToken = getAccessTokenFromGoogle(request.getCode(), request.getRedirectUri());
            logger.info("액세스 토큰 수신 : {}", accessToken);

            // 2. 사용자 정보 요청
            String userInfo = getGoogleUserInfo(accessToken);
            logger.info("사용자 정보 수신 : {}", userInfo);

            // 3. 사용자 정보 파싱 및 저장
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode userNode = objectMapper.readTree(userInfo);
            String userEmail = userNode.get("email").asText();
            String username = userNode.get("name").asText();

            // OAuth2LoginService를 통해 사용자 저장/업데이트
            Users user = oAuth2LoginService.saveOrUpdateUser(userEmail, username, "USER");

            // 4. JWT 토큰 생성
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", userEmail);
            claims.put("username", username);
            claims.put("role", "USER");
            String token = jwtTokenProvider.generateTokenFromUserInfo(claims);

            // 5. 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", username);
            response.put("email", userEmail);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {

            logger.error("구글 로그인 처리 중 오류 발생 : ", e);
            String errorMessage = e.getMessage();
            Throwable cause = e.getCause();
            if (cause != null) {
                logger.error("오류 원인은 : ", cause);
                errorMessage += " (Caused by: " + cause.getMessage() + ")";
            }

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", errorMessage);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

    }

    private String getAccessTokenFromGoogle(String code, String frontendRedirectUri) {
        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            params.add("code", code);

            params.add("client_id", clientId);

            params.add("client_secret", clientSecret);

            params.add("redirect_uri", frontendRedirectUri);

            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            logger.info("구글에 토큰 요청 전송. 파라미터 : {}", params);

            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);

            logger.info("구글 토큰 응답 상태 : {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK) {

                ObjectMapper objectMapper = new ObjectMapper();

                String responseBody = response.getBody();

                logger.info("Google token response body: {}", responseBody);

                JsonNode responseJson = objectMapper.readTree(responseBody);

                return responseJson.get("access_token").asText();
            } else {
                throw new RuntimeException("Failed to get access token from Google. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("구글에서 액세스 토큰 받기 실패", e);
            if (e.getCause() != null) {
                logger.error("원인은 : ", e.getCause());
            }
            throw new RuntimeException("Failed to get access token from Google: " + e.getMessage(), e);

        }

    }

    private String getGoogleUserInfo(String accessToken) {
        try {

            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get user info from Google. Status: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            logger.error("구글에서 사용자 정보 받기 실패", e);
            throw new RuntimeException("Failed to get user info from Google", e);
        }
    }
}


class GoogleLoginRequest {

    private String code;

    private String redirectUri;

    public String getCode() {

        return code;

    }

    public void setCode(String code) {

        this.code = code;

    }

    public String getRedirectUri() {

        return redirectUri;

    }

    public void setRedirectUri(String redirectUri) {

        this.redirectUri = redirectUri;

    }

}
