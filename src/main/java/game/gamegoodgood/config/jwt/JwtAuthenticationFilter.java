package game.gamegoodgood.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;  // 추가
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청에서 JWT 토큰을 추출
        String token = getTokenFromRequest(request);

        if (token != null) {
            // JWT 토큰이 존재하면 유효성 검사
            boolean isValid = jwtTokenProvider.validateToken(token);
            if (isValid) {
                // 유효한 토큰인 경우, 인증 정보 설정
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 디버깅: 유효한 토큰과 사용자 정보 출력
                System.out.println("유효한 jwt 토큰 : " + authentication.getName());
            } else {
                // 유효하지 않은 토큰인 경우
                System.out.println("유효하지 않은 토큰입니다.");
            }
        } else {
            System.out.println("토큰이 없습니다.");
        }

        // 필터 체인을 계속 진행
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 JWT 토큰 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 외부에서 사용
    public String extractToken(HttpServletRequest request) {
        return getTokenFromRequest(request);
    }



}