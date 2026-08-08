package com.kayque.devatlas.client;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.exception.GitHubApiUnavailableException;
import com.kayque.devatlas.exception.GitHubUserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Component
public class GitHubClient {

    private static final String GITHUB_API_URL =
            "https://api.github.com";

    private final RestClient restClient;

    public GitHubClient(
            RestClient.Builder builder,
            @Value("${github.token:}") String token
    ) {
        RestClient.Builder configuredBuilder = builder
                .baseUrl(GITHUB_API_URL)
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        "application/vnd.github+json"
                )
                .defaultHeader(
                        HttpHeaders.USER_AGENT,
                        "DevAtlas"
                )
                .defaultHeader(
                        "X-GitHub-Api-Version",
                        "2026-03-10"
                );

        if (!token.isBlank()) {
            configuredBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + token
            );
        }

        this.restClient = configuredBuilder.build();
    }

    public GitHubUserResponse findUser(String username) {
        try {
            return restClient
                    .get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GitHubUserResponse.class);

        } catch (HttpClientErrorException.NotFound exception) {
            throw new GitHubUserNotFoundException(username);

        } catch (RestClientException exception) {
            throw new GitHubApiUnavailableException(
                    username,
                    exception
            );
        }
    }

    public List<GitHubRepositoryResponse> findRepositories(
            String username
    ) {
        try {
            GitHubRepositoryResponse[] repositories =
                    restClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(
                                            "/users/{username}/repos"
                                    )
                                    .queryParam("type", "owner")
                                    .queryParam("sort", "updated")
                                    .queryParam("direction", "desc")
                                    .queryParam("per_page", 100)
                                    .build(username)
                            )
                            .retrieve()
                            .body(
                                    GitHubRepositoryResponse[].class
                            );

            if (repositories == null) {
                return List.of();
            }

            return List.of(repositories);

        } catch (HttpClientErrorException.NotFound exception) {
            throw new GitHubUserNotFoundException(username);

        } catch (RestClientException exception) {
            throw new GitHubApiUnavailableException(
                    username,
                    exception
            );
        }
    }
}