package com.employee360.backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.employee360.backend.security.CustomUserDetailsService;
import com.employee360.backend.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =========================================================
    // CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            // -------------------------------------------------
            // CORS
            // -------------------------------------------------
            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()
                    )
            )

            // -------------------------------------------------
            // CSRF
            // -------------------------------------------------
            .csrf(csrf ->
                    csrf.disable()
            )

            // -------------------------------------------------
            // SESSION
            // JWT = STATELESS
            // -------------------------------------------------
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // -------------------------------------------------
            // AUTHENTICATION PROVIDER
            // -------------------------------------------------
            .authenticationProvider(
                    authenticationProvider()
            )

            // -------------------------------------------------
            // AUTHORIZATION
            // -------------------------------------------------
            .authorizeHttpRequests(auth -> {

                // =================================================
                // AUTHENTICATION
                // =================================================

                auth.requestMatchers(
                        "/auth/**"
                ).permitAll();

                // =================================================
                // CORS OPTIONS
                // =================================================

                auth.requestMatchers(
                        HttpMethod.OPTIONS,
                        "/**"
                ).permitAll();

                // =================================================
                // EMPLOYEE
                // =================================================

                // ADMIN + HR + USER + EMPLOYEE
                // can VIEW employee records

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Employee/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Only ADMIN + HR
                // can CREATE / UPDATE / DELETE employees

                auth.requestMatchers(
                        "/Employee/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // =================================================
                // DEPARTMENT
                // =================================================

                // Everyone can VIEW departments

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Department/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Only ADMIN + HR
                // can CREATE / UPDATE / DELETE departments

                auth.requestMatchers(
                        "/Department/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // =================================================
                // LEAVE
                // =================================================

                // USER / EMPLOYEE can APPLY for leave
                // HR + ADMIN can also apply if needed

                auth.requestMatchers(
                        "/Leave/apply/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Everyone can VIEW leaves

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Leave/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Only ADMIN + HR
                // can APPROVE leave

                auth.requestMatchers(
                        "/Leave/approve/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // Only ADMIN + HR
                // can REJECT leave

                auth.requestMatchers(
                        "/Leave/reject/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // Only ADMIN + HR
                // can UPDATE leave

                auth.requestMatchers(
                        "/Leave/update/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // Only ADMIN + HR
                // can DELETE leave

                auth.requestMatchers(
                        "/Leave/delete/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // =================================================
                // ATTENDANCE
                // =================================================

                // Everyone can VIEW attendance

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Attendance/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Only ADMIN + HR
                // can CREATE / UPDATE / DELETE attendance

                auth.requestMatchers(
                        "/Attendance/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // =================================================
                // PAYROLL
                // =================================================

                // Everyone can VIEW payroll records

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Payroll/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR",
                        "EMPLOYEE",
                        "USER"
                );

                // Only ADMIN + HR
                // can CREATE / UPDATE / DELETE payroll

                auth.requestMatchers(
                        "/Payroll/**"
                ).hasAnyRole(
                        "ADMIN",
                        "HR"
                );

                // =================================================
                // EVERYTHING ELSE
                // =================================================

                auth.anyRequest().authenticated();
            })

            // =================================================
            // JWT FILTER
            // =================================================

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}