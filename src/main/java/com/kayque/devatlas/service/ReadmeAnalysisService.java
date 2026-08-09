package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.model.ReadmeAnalysis;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ReadmeAnalysisService {

    public ReadmeAnalysis analyze(
            Optional<GitHubReadmeResponse> readme
    ) {
        if (readme.isEmpty()) {
            return createMissingReadmeAnalysis();
        }

        GitHubReadmeResponse response =
                readme.get();

        String content = decodeContent(response);

        boolean titlePresent =
                hasMainTitle(content);

        boolean installationSectionPresent =
                hasHeading(
                        content,
                        "instalacao",
                        "installation",
                        "como executar",
                        "como rodar",
                        "getting started",
                        "setup"
                );

        boolean usageSectionPresent =
                hasHeading(
                        content,
                        "uso",
                        "usage",
                        "utilizacao",
                        "execucao",
                        "running",
                        "como usar",
                        "como executar"
                );

        boolean technologiesSectionPresent =
                hasHeading(
                        content,
                        "tecnologias",
                        "technologies",
                        "tech stack",
                        "stack utilizada",
                        "ferramentas"
                );

        boolean imagePresent =
                hasImage(content);

        int score = calculateScore(
                titlePresent,
                installationSectionPresent,
                usageSectionPresent,
                technologiesSectionPresent,
                imagePresent
        );

        List<String> recommendations =
                createRecommendations(
                        titlePresent,
                        installationSectionPresent,
                        usageSectionPresent,
                        technologiesSectionPresent,
                        imagePresent
                );

        return new ReadmeAnalysis(
                true,
                response.size(),
                score,
                titlePresent,
                installationSectionPresent,
                usageSectionPresent,
                technologiesSectionPresent,
                imagePresent,
                recommendations
        );
    }

    private ReadmeAnalysis createMissingReadmeAnalysis() {
        return new ReadmeAnalysis(
                false,
                0,
                0,
                false,
                false,
                false,
                false,
                false,
                List.of(
                        "Adicione um README para documentar o projeto."
                )
        );
    }

    private String decodeContent(
            GitHubReadmeResponse readme
    ) {
        if (readme.content() == null
                || readme.content().isBlank()
                || !"base64".equalsIgnoreCase(
                readme.encoding()
        )) {
            return "";
        }

        try {
            byte[] decodedContent =
                    Base64
                            .getMimeDecoder()
                            .decode(readme.content());

            return new String(
                    decodedContent,
                    StandardCharsets.UTF_8
            );

        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private boolean hasMainTitle(String content) {
        return content
                .lines()
                .map(String::strip)
                .anyMatch(line ->
                        line.matches("^#\\s+.+")
                );
    }

    private boolean hasHeading(
            String content,
            String... expectedHeadings
    ) {
        String normalizedContent =
                normalize(content);

        return normalizedContent
                .lines()
                .map(String::strip)
                .filter(line ->
                        line.matches("^#{1,6}\\s+.+")
                )
                .anyMatch(heading ->
                        Arrays
                                .stream(expectedHeadings)
                                .anyMatch(heading::contains)
                );
    }

    private boolean hasImage(String content) {
        String normalizedContent =
                content.toLowerCase(Locale.ROOT);

        return normalizedContent.contains("![")
                || normalizedContent.contains("<img");
    }

    private int calculateScore(
            boolean titlePresent,
            boolean installationSectionPresent,
            boolean usageSectionPresent,
            boolean technologiesSectionPresent,
            boolean imagePresent
    ) {
        int score = 5;

        if (titlePresent) {
            score += 3;
        }

        if (installationSectionPresent) {
            score += 3;
        }

        if (usageSectionPresent) {
            score += 3;
        }

        if (technologiesSectionPresent) {
            score += 3;
        }

        if (imagePresent) {
            score += 3;
        }

        return score;
    }

    private List<String> createRecommendations(
            boolean titlePresent,
            boolean installationSectionPresent,
            boolean usageSectionPresent,
            boolean technologiesSectionPresent,
            boolean imagePresent
    ) {
        List<String> recommendations =
                new ArrayList<>();

        if (!titlePresent) {
            recommendations.add(
                    "Adicione um título principal ao README."
            );
        }

        if (!installationSectionPresent) {
            recommendations.add(
                    "Documente como instalar ou preparar o projeto."
            );
        }

        if (!usageSectionPresent) {
            recommendations.add(
                    "Explique como executar ou utilizar o projeto."
            );
        }

        if (!technologiesSectionPresent) {
            recommendations.add(
                    "Liste as principais tecnologias utilizadas."
            );
        }

        if (!imagePresent) {
            recommendations.add(
                    "Adicione uma imagem ou screenshot do projeto."
            );
        }

        return List.copyOf(recommendations);
    }

    private String normalize(String value) {
        String normalized =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                );

        return normalized
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }
}