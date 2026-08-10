package com.kayque.devatlas.validation;

import com.kayque.devatlas.exception.InvalidGitHubUsernameException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GitHubUsernameValidator {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile(
                    "^(?!-)(?!.*--)[A-Za-z0-9-]{1,39}(?<!-)$"
            );

    public String validateAndNormalize(
            String username
    ) {
        String normalizedUsername =
                username == null
                        ? ""
                        : username.trim();

        if (!USERNAME_PATTERN
                .matcher(normalizedUsername)
                .matches()) {

            throw new InvalidGitHubUsernameException(
                    normalizedUsername
            );
        }

        return normalizedUsername;
    }
}