package com.kayque.devatlas.service;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubCommitResponse;
import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class GitHubProfileService {

    private static final int MAX_ANALYZABLE_REPOSITORIES =
            25;

    private final GitHubClient gitHubClient;

    public GitHubProfileService(
            GitHubClient gitHubClient
    ) {
        this.gitHubClient = gitHubClient;
    }

    @Cacheable(
            cacheNames = "github-users",
            key = "#username.trim().toLowerCase()"
    )
    public GitHubUserResponse findUser(
            String username
    ) {
        return gitHubClient.findUser(username);
    }

    @Cacheable(
            cacheNames = "github-repositories",
            key = "#username.trim().toLowerCase()"
    )
    public List<GitHubRepositoryResponse>
    findAnalyzableRepositories(
            String username
    ) {
        return gitHubClient
                .findRepositories(username)
                .stream()
                .filter(repository ->
                        !repository.name()
                                .equalsIgnoreCase(username)
                )
                .filter(repository ->
                        !repository.fork()
                )
                .limit(
                        MAX_ANALYZABLE_REPOSITORIES
                )
                .toList();
    }


    @Cacheable(
            cacheNames = "github-readmes",
            key = "(#owner.trim() + '/' + #repository.trim()).toLowerCase()"
    )
    public Optional<GitHubReadmeResponse> findReadme(
            String owner,
            String repository
    ) {
        return gitHubClient.findReadme(
                owner,
                repository
        );
    }

    @Cacheable(
            cacheNames = "github-commits",
            key = "(#owner.trim() + '/' + #repository.trim()).toLowerCase()"
    )
    public List<GitHubCommitResponse> findRecentCommits(
            String owner,
            String repository
    ) {
        Instant ninetyDaysAgo =
                Instant.now()
                        .minus(
                                90,
                                ChronoUnit.DAYS
                        );

        return gitHubClient.findRecentCommits(
                owner,
                repository,
                ninetyDaysAgo
        );
    }
}