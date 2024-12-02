package game.gamegoodgood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()  // CSRF 공격에 대한 옵션 비활성화
                .authorizeHttpRequests((authorizeRequests) -> {
                    // 인증된 사용자만 /user/** 경로에 접근 가능
                    authorizeRequests.requestMatchers("/user/**").permitAll(); // 회원가입 경로는 인증 없이 접근 허용

                    // 관리자가 아닌 경우 /manager/** 경로에 접근 불가
                    authorizeRequests.requestMatchers("/manager/**")
                            .hasAnyRole("ADMIN", "MANAGER");
                    // 관리자만 /admin/** 경로에 접근 가능
                    authorizeRequests.requestMatchers("/admin/**")
                            .hasRole("ADMIN");
                    // 나머지 경로는 모두 허용
                    authorizeRequests.anyRequest().permitAll();
                })
                .formLogin((formLogin) -> {
                    formLogin.loginPage("/login")  // 로그인 페이지 URL 설정
                            .loginProcessingUrl("/login")  // 로그인 처리 URL
                            .defaultSuccessUrl("/", true)  // 로그인 성공 후 리디렉션
                            .failureUrl("/login?error=true");  // 로그인 실패 시 리디렉션
                })
                .logout((logout) -> {
                    logout.logoutUrl("/logout")
                            .logoutSuccessUrl("/")  // 로그아웃 후 리디렉션
                            .invalidateHttpSession(true)
                            .clearAuthentication(true);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
