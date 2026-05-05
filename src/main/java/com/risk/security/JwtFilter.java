package com.risk.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        // ✅ Skip authentication for public endpoints
        if (path.startsWith("/auth") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars")) {

            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // ✅ Check Bearer token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                // ✅ Validate token & ensure no existing authentication
                if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null &&
                    jwtUtil.isTokenValid(token)) {

                    // ✅ Ensure ROLE_ prefix (Spring requirement)
                    String finalRole = role.startsWith("ROLE_")
                            ? role
                            : "ROLE_" + role;

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(new SimpleGrantedAuthority(finalRole))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                // ⚠️ Log error (do not break request flow)
                System.out.println("JWT Error: " + e.getMessage());
            }
        }

        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}