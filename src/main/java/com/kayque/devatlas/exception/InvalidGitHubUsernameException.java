package com.kayque.devatlas.exception;

public class InvalidGitHubUsernameException
        extends RuntimeException {

    private final String username;

    public InvalidGitHubUsernameException(
            String username
    ) {
        super("Invalid GitHub username");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}