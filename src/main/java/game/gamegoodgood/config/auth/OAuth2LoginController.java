package game.gamegoodgood.config.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.gamegoodgood.user.UserRepository;
import game.gamegoodgood.user.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@RestController
public class OAuth2LoginController {

    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public OAuth2LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 구글 로그인 후 인증 코드 처리
    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> googleLogin(@RequestParam("code") String code) throws IOException {
        // 구글의 OAuth2 토큰 엔드포인트
        String tokenUri = "https://oauth2.googleapis.com/token";

        // 구글에 액세스 토큰을 요청하기 위한 HTTP 요청 본문
        String requestBody = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("code", code)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        // 액세스 토큰을 요청하기 위한 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 구글 서버에서 액세스 토큰을 요청
        ResponseEntity<String> response = restTemplate.exchange(
                requestBody,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 액세스 토큰을 응답에서 추출
        String accessToken = response.getBody();

        // 토큰을 사용하여 구글 사용자 정보 가져오기
        String userInfo = getGoogleUserInfo(accessToken);

        // 구글에서 받은 사용자 정보를 JSON으로 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode userNode = objectMapper.readTree(userInfo);
        String userEmail = userNode.get("email").asText();  // 구글에서 받은 이메일 정보
        String username = userNode.get("name").asText();  // 구글에서 받은 이름 정보
        String role = "USER";  // 기본 역할 설정

        // 사용자 정보를 DB에 저장하거나 업데이트
        Users existingUser = userRepository.findByUserEmail(userEmail).orElseGet(() -> new Users());
        existingUser.setUsername(username);
        existingUser.setUserEmail(userEmail);
        existingUser.setRole(role);

        // 사용자 저장
        userRepository.save(existingUser);

        // 이제 JWT 토큰 생성과 리디렉션은 OAuth2LoginSuccessHandler에서 처리
        return ResponseEntity.ok("구글 로그인 완료. 리디렉션을 준비합니다.");
    }

    // 액세스 토큰을 사용하여 구글 사용자 정보 가져오기
    private String getGoogleUserInfo(String accessToken) {
        String userInfoUri = "https://www.googleapis.com/oauth2/v3/userinfo";

        // 사용자 정보 요청을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate을 사용하여 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, entity, String.class
        );

        // 응답에서 사용자 정보 파싱
        return response.getBody();  // 사용자 정보를 JSON 형식으로 반환
    }
}