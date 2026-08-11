package com.kayque.devatlas.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityHeadersFilterTests {

    private final SecurityHeadersFilter filter =
            new SecurityHeadersFilter();

    @Test
    void shouldAddSecurityHeaders() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (currentRequest, currentResponse) -> {
                };

        filter.doFilter(
                request,
                response,
                filterChain
        );

        String contentSecurityPolicy =
                response.getHeader(
                        "Content-Security-Policy"
                );

        assertNotNull(contentSecurityPolicy);

        assertTrue(
                contentSecurityPolicy.contains(
                        "script-src 'none'"
                )
        );

        assertTrue(
                contentSecurityPolicy.contains(
                        "https://avatars.githubusercontent.com"
                )
        );

        assertEquals(
                "nosniff",
                response.getHeader(
                        "X-Content-Type-Options"
                )
        );

        assertEquals(
                "DENY",
                response.getHeader(
                        "X-Frame-Options"
                )
        );

        assertEquals(
                "strict-origin-when-cross-origin",
                response.getHeader(
                        "Referrer-Policy"
                )
        );

        assertEquals(
                "camera=(), microphone=(), geolocation=()",
                response.getHeader(
                        "Permissions-Policy"
                )
        );
    }
}