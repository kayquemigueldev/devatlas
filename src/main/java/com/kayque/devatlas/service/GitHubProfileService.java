package com.kayque.devatlas.service;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.dto.GitHubReadmeResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class GitHubProfileService {

    private final GitHubClient gitHubClient;

    public GitHubProfileService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public GitHubUserResponse findUser(String username) {
        return gitHubClient.findUser(username);
    }

    public List<GitHubRepositoryResponse>
    findAnalyzableRepositories(String username) {

        return gitHubClient
                .findRepositories(username)
                .stream()
                .filter(repository ->
                        !repository.name()
                                .equalsIgnoreCase(username)
                )
                .filter(repository -> !repository.fork())
                .toList();
    }

    public Optional<GitHubReadmeResponse> findReadme(
            String owner,
            String repository
    ) {
        return gitHubClient.findReadme(
                owner,
                repository
        );
    }

}