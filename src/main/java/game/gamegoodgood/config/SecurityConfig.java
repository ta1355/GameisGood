package game.gamegoodgood.config;

import game.gamegoodgood.config.auth.JwtTokenProvider;
import game.gamegoodgood.config.auth.JwtAuthenticationFilter;
import game.gamegoodgood.config.auth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    // 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/**").permitAll()
                .requestMatchers("/post/create").authenticated()
                .requestMatchers("/posts/**/comments").authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)  // JWT 필터 설정
                .oauth2Login()
                .successHandler(oAuth2LoginSuccessHandler)  // OAuth2 로그인 성공 후 실행할 핸들러 설정
                .and()
                .logout()
                .logoutUrl("/logout")  // 로그아웃 URL 설정
                .logoutSuccessUrl("/")  // 로그아웃 후 리디렉션될 URL
                .invalidateHttpSession(true)  // 세션 무효화
                .clearAuthentication(true)  // 인증 정보 삭제
                .deleteCookies("JSESSIONID", "JWT")  // 쿠키 삭제
                .and()
                .cors().configurationSource(corsConfigurationSource());  // CORS 설정 추가

        return http.build();
    }

    // CORS 설정
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));  // 프론트엔드의 URL을 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 허용되는 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));  // 허용되는 헤더
        configuration.setAllowCredentials(true);  // 쿠키 및 인증 정보 허용
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        return authenticationManagerBuilder.build();
    }

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}