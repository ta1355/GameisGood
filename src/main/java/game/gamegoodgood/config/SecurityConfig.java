package game.gamegoodgood.config;

import game.gamegoodgood.config.jwt.JwtAuthenticationFilter;
import game.gamegoodgood.config.jwt.JwtTokenProvider;
import game.gamegoodgood.config.auth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          UserDetailsService userDetailsService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/login/oauth2/code/google").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login/oauth2/code/google").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/signup/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/post/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/post/{id}/view").permitAll()
                        .requestMatchers(HttpMethod.POST, "/createpost").authenticated()
                        .requestMatchers(HttpMethod.POST, "/posts/{postId}/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts/{postId}/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/find-username").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/change-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/create").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/specials").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/top_sellers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/coming_soon").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/new_releases").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/search/{search}").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

}

