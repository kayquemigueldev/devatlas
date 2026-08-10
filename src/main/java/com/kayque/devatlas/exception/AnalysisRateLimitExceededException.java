package com.kayque.devatlas.exception;

public class AnalysisRateLimitExceededException
        extends RuntimeException {

    private final String username;

    public AnalysisRateLimitExceededException(
            String username
    ) {
        super("Analysis rate limit exceeded");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}