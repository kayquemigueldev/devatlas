package com.kayque.devatlas.exception;

public class GitHubApiUnavailableException
        extends RuntimeException {

    private final String username;

    public GitHubApiUnavailableException(
            String username,
            Throwable cause
    ) {
        super("GitHub API is unavailable", cause);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}