package com.kayque.devatlas.service;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubProfileServiceTests {

    @Mock
    private GitHubClient gitHubClient;

    private GitHubProfileService service;

    @BeforeEach
    void setUp() {
        service =
                new GitHubProfileService(
                        gitHubClient
                );
    }

    @Test
    void shouldLimitRepositoriesAfterFiltering() {
        List<GitHubRepositoryResponse> repositories =
                new ArrayList<>();

        repositories.add(
                createRepository(
                        "kayque",
                        false
                )
        );

        repositories.add(
                createRepository(
                        "forked-project",
                        true
                )
        );

        for (int index = 1; index <= 30; index++) {
            repositories.add(
                    createRepository(
                            "project-" + index,
                            false
                    )
            );
        }

        when(
                gitHubClient.findRepositories("kayque")
        ).thenReturn(repositories);

        List<GitHubRepositoryResponse> result =
                service.findAnalyzableRepositories(
                        "kayque"
                );

        assertEquals(
                25,
                result.size()
        );

        assertEquals(
                "project-1",
                result.getFirst().name()
        );

        assertEquals(
                "project-25",
                result.getLast().name()
        );

        assertFalse(
                result
                        .stream()
                        .anyMatch(repository ->
                                repository.name()
                                        .equalsIgnoreCase("kayque")
                                        || repository.fork()
                        )
        );
    }

    @Test
    void shouldKeepAllRepositoriesWhenBelowLimit() {
        List<GitHubRepositoryResponse> repositories =
                List.of(
                        createRepository("project-1", false),
                        createRepository("project-2", false),
                        createRepository("project-3", false)
                );

        when(
                gitHubClient.findRepositories("kayque")
        ).thenReturn(repositories);

        List<GitHubRepositoryResponse> result =
                service.findAnalyzableRepositories(
                        "kayque"
                );

        assertEquals(
                3,
                result.size()
        );
    }

    private GitHubRepositoryResponse createRepository(
            String name,
            boolean fork
    ) {
        Instant timestamp =
                Instant.parse(
                        "2026-08-12T00:00:00Z"
                );

        return new GitHubRepositoryResponse(
                name,
                "Description",
                "https://github.com/kayque/" + name,
                null,
                "Java",
                0,
                0,
                0,
                fork,
                false,
                List.of(),
                timestamp,
                timestamp,
                timestamp
        );
    }
}