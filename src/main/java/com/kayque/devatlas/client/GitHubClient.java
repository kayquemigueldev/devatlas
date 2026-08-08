package com.kayque.devatlas.client;

import com.kayque.devatlas.dto.GitHubUserResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubClient {

    private static final String GITHUB_API_URL = "https://api.github.com";

    private final RestClient restClient;

    public GitHubClient(RestClient.Builder builder) {
        this.restClient = builder
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
                )
                .build();
    }

    public GitHubUserResponse findUser(String username) {
        return restClient
                .get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubUserResponse.class);
    }
}