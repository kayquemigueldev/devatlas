package com.kayque.devatlas.controller;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubClient gitHubClient;

    public GitHubController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @GetMapping("/users/{username}")
    public GitHubUserResponse findUser(
            @PathVariable String username
    ) {
        return gitHubClient.findUser(username);
    }

    @GetMapping("/users/{username}/repositories")
    public List<GitHubRepositoryResponse> findRepositories(
            @PathVariable String username
    ) {
        return gitHubClient.findRepositories(username);
    }
}