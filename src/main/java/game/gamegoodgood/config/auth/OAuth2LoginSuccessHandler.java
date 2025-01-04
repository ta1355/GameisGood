package game.gamegoodgood.config.auth;

import game.gamegoodgood.config.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // OAuth2 인증이 성공적으로 완료된 후 인증된 사용자의 정보 얻기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();  // OAuth2User 객체

        // OAuth2User에서 정보를 추출하여 Map<String, Object> 형식으로 변환
        Map<String, Object> userInfo = oAuth2User.getAttributes();  // OAuth2User에서 사용자 정보 얻기

        // JWT 토큰 생성
        String jwtToken = jwtTokenProvider.generateTokenFromUserInfo(userInfo);  // Map<String, Object> 전달

        // JWT 토큰을 응답 헤더에 설정
        response.setHeader("Authorization", "Bearer " + jwtToken);

        // 리디렉션 URL 설정 (로그인 후 사용자가 이동할 곳)
        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:3000/login")  // React 앱의 로그인 페이지
                .queryParam("token", jwtToken)  // JWT 토큰을 쿼리 파라미터로 전달
                .build()
                .toUriString();

        // 리디렉션 수행
        response.sendRedirect(redirectUri);
    }
}