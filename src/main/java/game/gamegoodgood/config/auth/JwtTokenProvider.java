package game.gamegoodgood.config.auth;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String JWT_SECRET = "yourSecretKey";  // JWT 서명에 사용할 비밀 키
    private final long JWT_EXPIRATION = 86400000L;  // 토큰 만료 시간 (24시간)
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())  // 토큰의 주체(사용자)
                .setIssuedAt(new Date())  // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))  // 만료 시간
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)  // 서명 방식
                .compact();
    }

    // JWT 토큰에서 사용자 이름 (주체) 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JWT 토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(JWT_SECRET)
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
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
