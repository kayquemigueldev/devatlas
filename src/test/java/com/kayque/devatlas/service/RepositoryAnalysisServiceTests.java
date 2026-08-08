package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryAnalysisServiceTests {

    private final RepositoryAnalysisService service =
            new RepositoryAnalysisService();

    @Test
    void shouldReturnMaximumScoreForCompleteRepository() {
        GitHubRepositoryResponse repository =
                createRepository(
                        "Projeto completo",
                        "https://project.example.com",
                        "Java",
                        List.of(
                                "java",
                                "spring-boot",
                                "mysql"
                        ),
                        false,
                        Instant.now().minus(
                                5,
                                ChronoUnit.DAYS
                        )
                );

        RepositoryAnalysis analysis =
                service.analyze(repository);

        assertEquals(100, analysis.score());
        assertEquals(
                RepositoryScoreLevel.EXCELLENT,
                analysis.level()
        );
        assertTrue(analysis.recommendations().isEmpty());
    }

    @Test
    void shouldRecommendMissingInformation() {
        GitHubRepositoryResponse repository =
                createRepository(
                        null,
                        null,
                        "Java",
                        List.of(),
                        false,
                        Instant.now().minus(
                                5,
                                ChronoUnit.DAYS
                        )
                );

        RepositoryAnalysis analysis =
                service.analyze(repository);

        assertEquals(45, analysis.score());
        assertEquals(
                RepositoryScoreLevel.DEVELOPING,
                analysis.level()
        );
        assertEquals(
                3,
                analysis.recommendations().size()
        );
    }

    private GitHubRepositoryResponse createRepository(
            String description,
            String homepage,
            String language,
            List<String> topics,
            boolean archived,
            Instant pushedAt
    ) {
        Instant now = Instant.now();

        return new GitHubRepositoryResponse(
                "example-project",
                description,
                "https://github.com/example/project",
                homepage,
                language,
                0,
                0,
                0,
                false,
                archived,
                topics,
                now.minus(365, ChronoUnit.DAYS),
                now,
                pushedAt
        );
    }
}