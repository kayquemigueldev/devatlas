package com.kayque.devatlas.ratelimit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalysisRateLimiterTests {

    @Test
    void shouldAllowRequestsWithinLimit() {
        AnalysisRateLimiter rateLimiter =
                new AnalysisRateLimiter();

        for (int request = 0; request < 5; request++) {
            assertTrue(
                    rateLimiter.tryAcquire(
                            "192.168.0.10"
                    )
            );
        }
    }

    @Test
    void shouldRejectRequestAfterLimit() {
        AnalysisRateLimiter rateLimiter =
                new AnalysisRateLimiter();

        for (int request = 0; request < 5; request++) {
            rateLimiter.tryAcquire(
                    "192.168.0.10"
            );
        }

        assertFalse(
                rateLimiter.tryAcquire(
                        "192.168.0.10"
                )
        );
    }

    @Test
    void shouldMaintainIndependentLimitPerClient() {
        AnalysisRateLimiter rateLimiter =
                new AnalysisRateLimiter();

        for (int request = 0; request < 5; request++) {
            rateLimiter.tryAcquire(
                    "192.168.0.10"
            );
        }

        assertTrue(
                rateLimiter.tryAcquire(
                        "192.168.0.20"
                )
        );
    }
}