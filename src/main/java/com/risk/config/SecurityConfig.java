package com.risk.config;

import com.risk.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // enables @PreAuthorize
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ❌ Disable CSRF (important for APIs + Swagger)
            .csrf(AbstractHttpConfigurer::disable)

            // ❌ No session (JWT based)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Authorization rules
            .authorizeHttpRequests(auth -> auth

                // ✅ Public endpoints
                .requestMatchers(
                        "/auth/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                // ✅ Allow GET APIs for all authenticated users
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                // ❌ Restrict POST (create)
                .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasAnyAuthority("ADMIN", "MANAGER")

                // ❌ Restrict PUT (update)
                .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasAnyAuthority("ADMIN", "MANAGER")

                // ❌ Restrict DELETE
                .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasAuthority("ADMIN")

                // ✅ everything else must be authenticated
                .anyRequest().authenticated()
            )

            // ✅ Add JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}