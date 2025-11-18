package com.venturebiz.in.BusinessConnect.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import com.venturebiz.in.BusinessConnect.security.JwtAuthFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                // ENABLE CORS AND CONNECT TO OUR CONFIG
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT BASED AUTH (STATELESS)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // PUBLIC AUTH ENDPOINTS
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/send-otp",
                                "/api/auth/verify-otp"
                        ).permitAll()

                        // ADMIN – COMPLETE ACCESS
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // USER + ADMIN CAN ACCESS USER ROUTES
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")

                        // ALL OTHER ENDPOINTS REQUIRE LOGIN
                        .anyRequest().authenticated()
                )

                // JWT FILTER BEFORE USERNAME/PASSWORD AUTH
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Authentication Manager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // FINAL, CLEAN, ERROR-FREE CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ALLOW ONLY YOUR FRONTENDS (SAFE & PRODUCTION READY)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:5174"
        ));

        // ALLOW CREDENTIALS (JWT HEADERS)
        config.setAllowCredentials(true);

        // ALLOW HEADERS & METHODS
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // EXPOSE AUTHORIZATION HEADER FOR JWT
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
