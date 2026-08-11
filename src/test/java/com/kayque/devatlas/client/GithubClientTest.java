package com.kayque.devatlas.client;

import com.kayque.devatlas.exception.GitHubApiUnavailableException;
import com.kayque.devatlas.exception.GitHubRateLimitExceededException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class GitHubClientTests {

    private MockRestServiceServer server;

    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder =
                RestClient
                        .builder()
                        .baseUrl("https://api.github.com");

        server =
                MockRestServiceServer
                        .bindTo(builder)
                        .build();

        gitHubClient =
                new GitHubClient(
                        builder.build()
                );
    }

    @AfterEach
    void verifyServer() {
        server.verify();
    }

    @Test
    void shouldIdentifyTooManyRequestsAsRateLimit() {
        server
                .expect(
                        requestTo(
                                "https://api.github.com/users/kayque"
                        )
                )
                .andRespond(
                        withStatus(
                                HttpStatus.TOO_MANY_REQUESTS
                        )
                );

        GitHubRateLimitExceededException exception =
                assertThrows(
                        GitHubRateLimitExceededException.class,
                        () -> gitHubClient.findUser("kayque")
                );

        assertEquals(
                "kayque",
                exception.getUsername()
        );
    }

    @Test
    void shouldIdentifyForbiddenWithNoRemainingRequests() {
        server
                .expect(
                        requestTo(
                                "https://api.github.com/users/kayque"
                        )
                )
                .andRespond(
                        withStatus(HttpStatus.FORBIDDEN)
                                .header(
                                        "X-RateLimit-Remaining",
                                        "0"
                                )
                );

        assertThrows(
                GitHubRateLimitExceededException.class,
                () -> gitHubClient.findUser("kayque")
        );
    }

    @Test
    void shouldIdentifySecondaryRateLimitMessage() {
        server
                .expect(
                        requestTo(
                                "https://api.github.com/users/kayque"
                        )
                )
                .andRespond(
                        withStatus(HttpStatus.FORBIDDEN)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "message":
                                          "You have exceeded a secondary rate limit."
                                        }
                                        """
                                )
                );

        assertThrows(
                GitHubRateLimitExceededException.class,
                () -> gitHubClient.findUser("kayque")
        );
    }

    @Test
    void shouldTreatOrdinaryForbiddenAsUnavailable() {
        server
                .expect(
                        requestTo(
                                "https://api.github.com/users/kayque"
                        )
                )
                .andRespond(
                        withStatus(HttpStatus.FORBIDDEN)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        """
                                        {
                                          "message":
                                          "Resource not accessible"
                                        }
                                        """
                                )
                );

        assertThrows(
                GitHubApiUnavailableException.class,
                () -> gitHubClient.findUser("kayque")
        );
    }
}