package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.model.User;
import org.example.service.UserService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService) {

        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        // No Authorization header
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            // Extract username from JWT
            String username =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT username: " + username
            );

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                var userOptional =
                        userService.findByUsername(username);

                if (userOptional.isPresent()) {

                    User user =
                            userOptional.get();

                    String role =
                            user.isAdmin()
                                    ? "ROLE_ADMIN"
                                    : "ROLE_CUSTOMER";

                    var authority =
                            new SimpleGrantedAuthority(role);

                    var authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(authority)
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    System.out.println(
                            "JWT authentication successful: "
                                    + username
                                    + " | "
                                    + role
                    );

                } else {

                    System.out.println(
                            "JWT user not found in database: "
                                    + username
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT authentication failed: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}