package game.gamegoodgood.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long JWT_EXPIRATION = 3600000L;  // 토큰 만료 시간 (1시간)
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public JwtTokenProvider(@Lazy UserDetailsService userDetailsService, @Lazy PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;

        // 512비트 비밀 키 생성
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);  // HS512에 적합한 512비트 키 생성
    }

    // 기존의 generateToken 메서드
    public String generateToken(Authentication authentication) {
        String username;

        // OAuth2 인증
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

            // OAuth2 사용자 이메일을 username으로 사용
            username = oAuth2User.getAttribute("email");  // 이메일을 토큰의 주체로 사용
        } else {
            // 일반 로그인 (UsernamePasswordAuthenticationToken)
            username = authentication.getName();
        }

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(username)  // 토큰의 주체(사용자)
                .setIssuedAt(new Date())  // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // 만료 시간
                .signWith(secretKey)  // 안전한 비밀 키로 서명
                .compact();
    }

    // generateTokenFromUserInfo 메서드
    public String generateTokenFromUserInfo(Map<String, Object> userInfo) {
        String username = (String) userInfo.get("email");  // 사용자 정보에서 이메일을 username으로 사용

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(username)  // 토큰의 주체(사용자)
                .setIssuedAt(new Date())  // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // 만료 시간
                .signWith(secretKey)  // 안전한 비밀 키로 서명
                .compact();
    }

    // JWT 토큰에서 사용자 이름 (주체) 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)  // 안전한 비밀 키로 토큰 검증
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JWT 토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 안전한 비밀 키로 토큰 검증
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;  // 예외가 발생하면 유효하지 않은 토큰
        }
    }

    // 인증 정보를 기반으로 Authentication 객체 반환
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // UserDetailsService 사용

        // 일반 사용자와 OAuth2 사용자를 구분하여 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Authentication authenticateUser(String username, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
