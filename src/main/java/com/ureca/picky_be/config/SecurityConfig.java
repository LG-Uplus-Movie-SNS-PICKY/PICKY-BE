package com.ureca.picky_be.config;

import com.ureca.picky_be.global.web.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] AUTH_WHITELIST = {
            //기본 세팅
            "/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",

            //로그인 없이도 접근 가능
            "/api/v1/oauth/*/user",
            "/api/v1/oauth/*/login",

            //장르
            "/api/v1/user/genres",

            //플레이리스트
            "/api/v1/playlist/all"
    };

    private static final String[] AUTH_WHITELIST_GET = {
            //영화
            "/api/v1/movie/**",

            //user
            "/api/v1/user"
    };

    private static final String[] AUTH_USER = {
            //user
            "/api/v1/user",
            "/api/v1/user/movies-by-genres",
            "/api/v1/user/nickname-validation",

            //영화
            "/api/v1/movie/*/like"
    };

    private static final String[] AUTH_USER_PATCH = {
            //user
            "/api/v1/user"
    };

    private static final String[] AUTH_ADMIN = {
        "/api/v1/admin/**"
    };

    private static final String[] AUTH_ADMIN_POST = {
            "/api/v1/movie/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, AUTH_WHITELIST_GET).permitAll()
                        .requestMatchers(AUTH_USER).hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, AUTH_USER_PATCH).hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers(AUTH_ADMIN).hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, AUTH_ADMIN_POST).hasAuthority("ADMIN")
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://d3hxz5yj62y98w.cloudfront.net"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}