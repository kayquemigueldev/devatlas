package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.LanguageUsage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageAnalysisServiceTests {

    private final LanguageAnalysisService service =
            new LanguageAnalysisService();

    @Test
    void shouldCalculateLanguageUsage() {
        List<GitHubRepositoryResponse> repositories =
                List.of(
                        createRepository(
                                "project-a",
                                "Java"
                        ),
                        createRepository(
                                "project-b",
                                "Java"
                        ),
                        createRepository(
                                "project-c",
                                "CSS"
                        ),
                        createRepository(
                                "project-d",
                                null
                        )
                );

        List<LanguageUsage> languages =
                service.analyze(repositories);

        assertEquals(2, languages.size());

        LanguageUsage java = languages.get(0);

        assertEquals("Java", java.language());
        assertEquals(2, java.repositories());
        assertEquals(67, java.percentage());

        LanguageUsage css = languages.get(1);

        assertEquals("CSS", css.language());
        assertEquals(1, css.repositories());
        assertEquals(33, css.percentage());
    }

    @Test
    void shouldHandleRepositoriesWithoutLanguage() {
        List<GitHubRepositoryResponse> repositories =
                List.of(
                        createRepository(
                                "project-a",
                                null
                        ),
                        createRepository(
                                "project-b",
                                ""
                        )
                );

        List<LanguageUsage> languages =
                service.analyze(repositories);

        assertTrue(languages.isEmpty());
    }

    private GitHubRepositoryResponse createRepository(
            String name,
            String language
    ) {
        Instant now = Instant.now();

        return new GitHubRepositoryResponse(
                name,
                "Project description",
                "https://github.com/example/" + name,
                null,
                language,
                0,
                0,
                0,
                false,
                false,
                List.of(),
                now,
                now,
                now
        );
    }

    @Test
    void shouldKeepRoundedPercentagesAtOneHundred() {
        List<GitHubRepositoryResponse> repositories =
                new ArrayList<>();

        for (int index = 1; index <= 7; index++) {
            repositories.add(
                    createRepository(
                            "java-project-" + index,
                            "Java"
                    )
            );
        }

        repositories.add(
                createRepository(
                        "css-project",
                        "CSS"
                )
        );

        List<LanguageUsage> languages =
                service.analyze(repositories);

        int totalPercentage =
                languages
                        .stream()
                        .mapToInt(LanguageUsage::percentage)
                        .sum();

        assertEquals(100, totalPercentage);
        assertEquals(88, languages.get(0).percentage());
        assertEquals(12, languages.get(1).percentage());
    }
}