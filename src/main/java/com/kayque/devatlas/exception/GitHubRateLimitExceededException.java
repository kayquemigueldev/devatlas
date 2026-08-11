package com.kayque.devatlas.exception;

public class GitHubRateLimitExceededException
        extends RuntimeException {

    private final String username;

    public GitHubRateLimitExceededException(
            String username,
            Throwable cause
    ) {
        super("GitHub API rate limit exceeded", cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}