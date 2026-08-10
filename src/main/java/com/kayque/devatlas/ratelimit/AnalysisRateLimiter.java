package com.kayque.devatlas.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AnalysisRateLimiter {

    private static final long MAXIMUM_REQUESTS = 5;

    private static final Duration REFILL_DURATION =
            Duration.ofMinutes(10);

    private final Cache<String, Bucket> buckets;

    public AnalysisRateLimiter() {
        this.buckets =
                Caffeine
                        .newBuilder()
                        .maximumSize(10_000)
                        .expireAfterAccess(
                                Duration.ofHours(1)
                        )
                        .build();
    }

    public boolean tryAcquire(
            String clientIdentifier
    ) {
        Bucket bucket =
                buckets.get(
                        clientIdentifier,
                        ignored -> createBucket()
                );

        return bucket.tryConsume(1);
    }

    private Bucket createBucket() {
        return Bucket
                .builder()
                .addLimit(limit ->
                        limit
                                .capacity(
                                        MAXIMUM_REQUESTS
                                )
                                .refillGreedy(
                                        MAXIMUM_REQUESTS,
                                        REFILL_DURATION
                                )
                )
                .build();
    }
}