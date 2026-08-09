package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.model.ReadmeAnalysis;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadmeAnalysisServiceTests {

    private final ReadmeAnalysisService service =
            new ReadmeAnalysisService();

    @Test
    void shouldReturnMaximumScoreForCompleteReadme() {
        String content = """
                # DevAtlas

                Intelligent GitHub profile analyzer.

                ## Tecnologias

                Java, Spring Boot e Thymeleaf.

                ## Instalação

                Instale o Java 21 e o Maven.

                ## Como usar

                Execute o projeto e informe um usuário.

                ![Dashboard](docs/dashboard.png)
                """;

        ReadmeAnalysis analysis =
                service.analyze(
                        createReadme(content)
                );

        assertTrue(analysis.present());
        assertTrue(analysis.titlePresent());

        assertTrue(
                analysis.installationSectionPresent()
        );

        assertTrue(
                analysis.usageSectionPresent()
        );

        assertTrue(
                analysis.technologiesSectionPresent()
        );

        assertTrue(analysis.imagePresent());

        assertEquals(20, analysis.score());

        assertTrue(
                analysis.recommendations().isEmpty()
        );
    }

    @Test
    void shouldRecommendMissingReadmeSections() {
        String content = """
                # Projeto simples

                Pequena descrição do projeto.
                """;

        ReadmeAnalysis analysis =
                service.analyze(
                        createReadme(content)
                );

        assertTrue(analysis.present());
        assertTrue(analysis.titlePresent());

        assertFalse(
                analysis.installationSectionPresent()
        );

        assertFalse(
                analysis.usageSectionPresent()
        );

        assertFalse(
                analysis.technologiesSectionPresent()
        );

        assertFalse(analysis.imagePresent());

        assertEquals(8, analysis.score());

        assertEquals(
                4,
                analysis.recommendations().size()
        );
    }

    @Test
    void shouldHandleMissingReadme() {
        ReadmeAnalysis analysis =
                service.analyze(
                        Optional.empty()
                );

        assertFalse(analysis.present());
        assertEquals(0, analysis.size());
        assertEquals(0, analysis.score());

        assertEquals(
                1,
                analysis.recommendations().size()
        );
    }

    @Test
    void shouldHandleInvalidBase64Content() {
        GitHubReadmeResponse readme =
                new GitHubReadmeResponse(
                        "README.md",
                        "README.md",
                        50,
                        "base64",
                        "conteudo-invalido@@@",
                        "https://github.com/example/project/blob/main/README.md"
                );

        ReadmeAnalysis analysis =
                service.analyze(
                        Optional.of(readme)
                );

        assertTrue(analysis.present());
        assertEquals(5, analysis.score());

        assertEquals(
                5,
                analysis.recommendations().size()
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
}