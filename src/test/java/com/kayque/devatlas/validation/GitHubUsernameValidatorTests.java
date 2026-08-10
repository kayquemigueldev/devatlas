package com.kayque.devatlas.validation;

import com.kayque.devatlas.exception.InvalidGitHubUsernameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GitHubUsernameValidatorTests {

    private final GitHubUsernameValidator validator =
            new GitHubUsernameValidator();

    @Test
    void shouldNormalizeValidUsername() {
        String result =
                validator.validateAndNormalize(
                        "  kayquemigueldev  "
                );

        assertEquals(
                "kayquemigueldev",
                result
        );
    }

    @Test
    void shouldAcceptValidUsernameWithHyphenAndNumbers() {
        String result =
                validator.validateAndNormalize(
                        "kayque-dev123"
                );

        assertEquals(
                "kayque-dev123",
                result
        );
    }

    @Test
    void shouldAcceptUsernameWithMaximumLength() {
        String username = "a".repeat(39);

        assertEquals(
                username,
                validator.validateAndNormalize(username)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "-kayque",
            "kayque-",
            "kayque--dev",
            "kayque_miguel",
            "kayque.dev",
            "<script>"
    })
    void shouldRejectInvalidUsername(
            String username
    ) {
        assertThrows(
                InvalidGitHubUsernameException.class,
                () -> validator.validateAndNormalize(
                        username
                )
        );
    }

    @Test
    void shouldRejectUsernameLongerThanMaximum() {
        String username = "a".repeat(40);

        assertThrows(
                InvalidGitHubUsernameException.class,
                () -> validator.validateAndNormalize(
                        username
                )
        );
    }
}