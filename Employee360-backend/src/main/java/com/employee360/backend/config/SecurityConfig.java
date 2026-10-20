
package com.employee360.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.employee360.backend.security.CustomUserDetailsService;
import com.employee360.backend.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(allowedOrigins.split(",")));

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"));

        configuration.setAllowedHeaders(
                Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // SECURITY FILTER CHAIN
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource()))

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))

            .authenticationProvider(authenticationProvider())

            .exceptionHandling(exceptions ->
                    exceptions.authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

            .authorizeHttpRequests(auth -> {

                // AUTHENTICATION AND OTP
                auth.requestMatchers("/auth/**").permitAll();

                // CORS PREFLIGHT
                auth.requestMatchers(
                        HttpMethod.OPTIONS, "/**").permitAll();

                // EMPLOYEE MANAGEMENT
                auth.requestMatchers(
                        HttpMethod.GET, "/Employee/**")
                    .hasAnyRole(
                        "ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers("/Employee/**")
                    .hasAnyRole("ADMIN", "HR");

                // EMPLOYEE V2
                auth.requestMatchers(
                        HttpMethod.GET, "/api/v2/employees/me")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/api/v2/employees",
                        "/api/v2/employees/**")
                    .hasAnyRole(
                        "ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers(
                        HttpMethod.POST,
                        "/api/v2/employees",
                        "/api/v2/employees/**")
                    .hasAnyRole("ADMIN", "HR");

                auth.requestMatchers(
                        HttpMethod.PUT,
                        "/api/v2/employees/**")
                    .hasAnyRole("ADMIN", "HR");

                auth.requestMatchers(
                        HttpMethod.DELETE,
                        "/api/v2/employees/**")
                    .hasAnyRole("ADMIN", "HR");

                // DEPARTMENTS
                auth.requestMatchers(
                        HttpMethod.GET, "/Department/**")
                    .hasAnyRole(
                        "ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers("/Department/**")
                    .hasAnyRole("ADMIN", "HR");

                // LEAVE
                auth.requestMatchers(
                        HttpMethod.GET, "/Leave/my", "/Leave/history/**")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.POST, "/Leave/my")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.GET,
                        "/Leave/getAll",
                        "/Leave/getById/**")
                    .hasAnyRole("ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers(
                        HttpMethod.POST, "/Leave/apply/**")
                    .hasAnyRole("ADMIN", "HR");

                auth.requestMatchers(
                        HttpMethod.PUT, "/Leave/approve/**", "/Leave/reject/**")
                    .hasAnyRole("ADMIN", "HR", "MANAGER");

                auth.requestMatchers(
                        HttpMethod.PUT, "/Leave/update/**")
                    .hasAnyRole("ADMIN", "HR");

                auth.requestMatchers(
                        HttpMethod.DELETE, "/Leave/delete/**")
                    .hasAnyRole("ADMIN", "HR");

                auth.requestMatchers("/Leave/**").authenticated();

                // ATTENDANCE
                auth.requestMatchers(
                        HttpMethod.GET, "/Attendance/my")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.GET, "/Attendance/**")
                    .hasAnyRole(
                        "ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers("/Attendance/**")
                    .hasAnyRole("ADMIN", "HR", "MANAGER");

                // PAYROLL
                auth.requestMatchers(
                        HttpMethod.GET, "/Payroll/**")
                    .hasAnyRole(
                        "ADMIN", "CEO", "HR", "MANAGER");

                auth.requestMatchers("/Payroll/**")
                    .hasAnyRole("ADMIN", "HR");

                // NOTIFICATIONS
                // Employee ownership is enforced in the service.
                auth.requestMatchers("/api/notifications/**")
                    .authenticated();

                // DOCUMENTS
                // Employee ownership is enforced in the service.
                auth.requestMatchers("/api/documents/**")
                    .authenticated();

                // QUIZZES
                // Fine-grained role checks are applied in the controller.
                auth.requestMatchers("/api/quizzes/**")
                    .authenticated();

                // SALARY PAYMENTS
                // Fine-grained role checks use @PreAuthorize.
                auth.requestMatchers("/api/salary-payments/**")
                    .authenticated();

                // REIMBURSEMENTS
                // Fine-grained role checks use @PreAuthorize.
                auth.requestMatchers("/api/reimbursements/**")
                    .authenticated();

                // TASKS
                auth.requestMatchers(
                        HttpMethod.GET,
                        "/api/tasks", "/api/tasks/**")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.POST, "/api/tasks")
                    .hasAnyRole("ADMIN", "HR", "MANAGER");

                auth.requestMatchers(
                        HttpMethod.PUT, "/api/tasks/**")
                    .authenticated();

                auth.requestMatchers(
                        HttpMethod.DELETE, "/api/tasks/**")
                    .hasAnyRole("ADMIN", "HR");

                // LEAVE CANCEL (employee cancels own pending leave)
                auth.requestMatchers(
                        HttpMethod.DELETE, "/Leave/cancel/**")
                    .authenticated();

                // DASHBOARD AND REPORTS
                auth.requestMatchers("/api/dashboard/**")
                    .authenticated();

                auth.requestMatchers("/api/reports/**")
                    .hasAnyRole("ADMIN", "CEO", "HR", "MANAGER");

                // ALL OTHER ENDPOINTS
                auth.anyRequest().authenticated();
            })

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
