package game.gamegoodgood.config;



import game.gamegoodgood.config.auth.JwtTokenProvider;
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
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;

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

                        .requestMatchers(HttpMethod.GET, "/posts").permitAll()

                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()

                        .anyRequest().authenticated()

                )

                .sessionManagement(session -> session

                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                );



        return http.build();

    }



    @Bean

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("*"));

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
