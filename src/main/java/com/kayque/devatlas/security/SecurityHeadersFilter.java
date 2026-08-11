package com.kayque.devatlas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter
        extends OncePerRequestFilter {

    private static final String CONTENT_SECURITY_POLICY =
            "default-src 'self'; "
                    + "img-src 'self' "
                    + "https://avatars.githubusercontent.com; "
                    + "style-src 'self'; "
                    + "script-src 'none'; "
                    + "font-src 'self'; "
                    + "connect-src 'self'; "
                    + "object-src 'none'; "
                    + "base-uri 'self'; "
                    + "frame-ancestors 'none'; "
                    + "form-action 'self'";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader(
                "Content-Security-Policy",
                CONTENT_SECURITY_POLICY
        );

        response.setHeader(
                "X-Content-Type-Options",
                "nosniff"
        );

        response.setHeader(
                "X-Frame-Options",
                "DENY"
        );

        response.setHeader(
                "Referrer-Policy",
                "strict-origin-when-cross-origin"
        );

        response.setHeader(
                "Permissions-Policy",
                "camera=(), microphone=(), geolocation=()"
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}