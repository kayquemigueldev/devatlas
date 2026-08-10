package com.kayque.devatlas.client;

import com.kayque.devatlas.dto.GitHubCommitResponse;
import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.exception.GitHubApiUnavailableException;
import com.kayque.devatlas.exception.GitHubUserNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(
            @Qualifier("githubRestClient")
            RestClient restClient
    ) {
        this.restClient = restClient;
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

    public Optional<GitHubReadmeResponse> findReadme(
            String owner,
            String repository
    ) {
        try {
            GitHubReadmeResponse readme =
                    restClient
                            .get()
                            .uri(
                                    "/repos/{owner}/{repository}/readme",
                                    owner,
                                    repository
                            )
                            .retrieve()
                            .body(GitHubReadmeResponse.class);

            return Optional.ofNullable(readme);

        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty();

        } catch (RestClientException exception) {
            throw new GitHubApiUnavailableException(
                    owner,
                    exception
            );
        }
    }

    public List<GitHubCommitResponse> findRecentCommits(
            String owner,
            String repository,
            Instant since
    ) {
        try {
            GitHubCommitResponse[] commits =
                    restClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(
                                            "/repos/{owner}/{repository}/commits"
                                    )
                                    .queryParam(
                                            "since",
                                            since.toString()
                                    )
                                    .queryParam(
                                            "per_page",
                                            100
                                    )
                                    .build(
                                            owner,
                                            repository
                                    )
                            )
                            .retrieve()
                            .body(
                                    GitHubCommitResponse[].class
                            );

            if (commits == null) {
                return List.of();
            }

            return List.of(commits);

        } catch (HttpClientErrorException exception) {
            int statusCode =
                    exception
                            .getStatusCode()
                            .value();

            if (statusCode == 404
                    || statusCode == 409) {
                return List.of();
            }

            throw new GitHubApiUnavailableException(
                    owner + "/" + repository,
                    exception
            );

        } catch (RestClientException exception) {
            throw new GitHubApiUnavailableException(
                    owner + "/" + repository,
                    exception
            );
        }
    }

}