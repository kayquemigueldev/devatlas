package com.kayque.devatlas.service;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubCommitResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        properties = {
                "spring.cache.type=caffeine",
                "spring.cache.cache-names="
                        + "github-users,"
                        + "github-repositories,"
                        + "github-readmes,"
                        + "github-commits",
                "spring.cache.caffeine.spec="
                        + "maximumSize=100,"
                        + "expireAfterWrite=600s"
        }
)
class GitHubProfileCacheTests {

    @MockitoBean
    private GitHubClient gitHubClient;

    @Autowired
    private GitHubProfileService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        cacheManager
                .getCacheNames()
                .forEach(cacheName ->
                        cacheManager
                                .getCache(cacheName)
                                .clear()
                );
    }

    @Test
    void shouldReuseCachedRecentCommits() {
        List<GitHubCommitResponse> commits =
                List.of(
                        new GitHubCommitResponse(
                                "abc123"
                        )
                );

        when(
                gitHubClient.findRecentCommits(
                        eq("KayqueMiguelDev"),
                        eq("DevAtlas"),
                        any(Instant.class)
                )
        ).thenReturn(commits);

        List<GitHubCommitResponse> firstResult =
                service.findRecentCommits(
                        "KayqueMiguelDev",
                        "DevAtlas"
                );

        List<GitHubCommitResponse> secondResult =
                service.findRecentCommits(
                        "kayquemigueldev",
                        "devatlas"
                );

        assertEquals(
                firstResult,
                secondResult
        );

        verify(
                gitHubClient,
                times(1)
        ).findRecentCommits(
                eq("KayqueMiguelDev"),
                eq("DevAtlas"),
                any(Instant.class)
        );
    }
}