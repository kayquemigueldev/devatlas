package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryAnalysisServiceTests {

    private final RepositoryAnalysisService service =
            new RepositoryAnalysisService(
                    new ReadmeAnalysisService()
            );

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
                service.analyze(
                        repository,
                        createCompleteReadme()
                );

        assertEquals(100, analysis.score());

        assertEquals(
                7,
                analysis.scoreBreakdown().size()
        );

        assertEquals(
                100,
                analysis
                        .scoreBreakdown()
                        .stream()
                        .mapToInt(criterion ->
                                criterion.score()
                        )
                        .sum()
        );

        assertEquals(
                RepositoryScoreLevel.EXCELLENT,
                analysis.level()
        );

        assertEquals(
                20,
                analysis.readmeAnalysis().score()
        );

        assertTrue(
                analysis.recommendations().isEmpty()
        );
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
                service.analyze(
                        repository,
                        Optional.empty()
                );

        assertEquals(40, analysis.score());

        assertEquals(
                RepositoryScoreLevel.DEVELOPING,
                analysis.level()
        );

        assertEquals(
                4,
                analysis.recommendations().size()
        );
    }

    @Test
    void shouldRecommendImprovingIncompleteReadme() {
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
                service.analyze(
                        repository,
                        createReadme(
                                """
                                # Projeto simples

                                Pequena descrição.
                                """
                        )
                );

        assertEquals(88, analysis.score());

        assertEquals(
                8,
                analysis.readmeAnalysis().score()
        );

        assertEquals(
                4,
                analysis.recommendations().size()
        );

        assertTrue(
                analysis.recommendations().contains(
                        "Documente como instalar ou preparar o projeto."
                )
        );
    }

    private Optional<GitHubReadmeResponse>
    createCompleteReadme() {
        return createReadme(
                """
                # Projeto completo

                ## Tecnologias

                Java, Spring Boot e MySQL.

                ## Instalação

                Instale o Java 21.

                ## Como executar localmente

                Execute a aplicação.

                ![Dashboard](docs/dashboard.png)
                """
        );
    }

    private Optional<GitHubReadmeResponse> createReadme(
            String content
    ) {
        byte[] contentBytes =
                content.getBytes(
                        StandardCharsets.UTF_8
                );

        String encodedContent =
                Base64
                        .getEncoder()
                        .encodeToString(contentBytes);

        GitHubReadmeResponse readme =
                new GitHubReadmeResponse(
                        "README.md",
                        "README.md",
                        contentBytes.length,
                        "base64",
                        encodedContent,
                        "https://github.com/example/project/blob/main/README.md"
                );

        return Optional.of(readme);
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
                now.minus(
                        365,
                        ChronoUnit.DAYS
                ),
                now,
                pushedAt
        );
    }
}