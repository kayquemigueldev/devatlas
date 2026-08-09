package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.ActivityAnalysis;
import com.kayque.devatlas.model.LanguageUsage;
import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileRecommendation;
import com.kayque.devatlas.model.ReadmeAnalysis;
import com.kayque.devatlas.model.RecommendationCategory;
import com.kayque.devatlas.model.RecommendationPriority;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileRecommendationServiceTests {

    private final ProfileRecommendationService service =
            new ProfileRecommendationService();

    @Test
    void shouldRecommendPublishingProjectsForEmptyProfile() {
        ProfileAnalysis profileAnalysis =
                new ProfileAnalysis(
                        0,
                        RepositoryScoreLevel.DEVELOPING,
                        0,
                        0,
                        0,
                        0,
                        null
                );

        List<ProfileRecommendation> recommendations =
                service.analyze(
                        profileAnalysis,
                        List.of(),
                        List.of()
                );

        assertEquals(
                1,
                recommendations.size()
        );

        assertEquals(
                RecommendationPriority.HIGH,
                recommendations.getFirst().priority()
        );

        assertEquals(
                RecommendationCategory.QUALITY,
                recommendations.getFirst().category()
        );
    }

    @Test
    void shouldPrioritizeWeakProfileRecommendations() {
        List<RepositoryAnalysis> analyses =
                List.of(
                        createAnalysis(
                                "project-a",
                                50,
                                0,
                                null,
                                List.of(),
                                0
                        ),
                        createAnalysis(
                                "project-b",
                                50,
                                0,
                                null,
                                List.of(),
                                0
                        )
                );

        ProfileAnalysis profileAnalysis =
                new ProfileAnalysis(
                        50,
                        RepositoryScoreLevel.DEVELOPING,
                        2,
                        0,
                        0,
                        2,
                        "project-a"
                );

        List<ProfileRecommendation> recommendations =
                service.analyze(
                        profileAnalysis,
                        analyses,
                        List.of()
                );

        assertEquals(
                5,
                recommendations.size()
        );

        assertEquals(
                RecommendationPriority.HIGH,
                recommendations.getFirst().priority()
        );

        assertTrue(
                recommendations
                        .stream()
                        .anyMatch(recommendation ->
                                recommendation.category()
                                        == RecommendationCategory.DOCUMENTATION
                        )
        );

        assertTrue(
                recommendations
                        .stream()
                        .anyMatch(recommendation ->
                                recommendation.category()
                                        == RecommendationCategory.ACTIVITY
                        )
        );
    }

    @Test
    void shouldRecommendTechnologyDiversification() {
        List<RepositoryAnalysis> analyses =
                createStrongAnalyses();

        ProfileAnalysis profileAnalysis =
                createExcellentProfileAnalysis();

        List<ProfileRecommendation> recommendations =
                service.analyze(
                        profileAnalysis,
                        analyses,
                        List.of(
                                new LanguageUsage(
                                        "Java",
                                        3,
                                        100
                                )
                        )
                );

        assertEquals(
                1,
                recommendations.size()
        );

        assertEquals(
                RecommendationCategory.TECHNOLOGY,
                recommendations.getFirst().category()
        );

        assertEquals(
                RecommendationPriority.LOW,
                recommendations.getFirst().priority()
        );
    }

    @Test
    void shouldRecognizeWellStructuredProfile() {
        List<RepositoryAnalysis> analyses =
                createStrongAnalyses();

        ProfileAnalysis profileAnalysis =
                createExcellentProfileAnalysis();

        List<ProfileRecommendation> recommendations =
                service.analyze(
                        profileAnalysis,
                        analyses,
                        List.of(
                                new LanguageUsage(
                                        "Java",
                                        1,
                                        34
                                ),
                                new LanguageUsage(
                                        "CSS",
                                        1,
                                        33
                                ),
                                new LanguageUsage(
                                        "JavaScript",
                                        1,
                                        33
                                )
                        )
                );

        assertEquals(
                1,
                recommendations.size()
        );

        assertEquals(
                "Mantenha o padrão de qualidade",
                recommendations.getFirst().title()
        );

        assertEquals(
                RecommendationPriority.LOW,
                recommendations.getFirst().priority()
        );
    }

    private List<RepositoryAnalysis>
    createStrongAnalyses() {
        return List.of(
                createAnalysis(
                        "project-a",
                        100,
                        20,
                        "https://project-a.example.com",
                        List.of(
                                "java",
                                "spring-boot",
                                "api"
                        ),
                        20
                ),
                createAnalysis(
                        "project-b",
                        100,
                        20,
                        "https://project-b.example.com",
                        List.of(
                                "java",
                                "spring-boot",
                                "mysql"
                        ),
                        20
                ),
                createAnalysis(
                        "project-c",
                        100,
                        20,
                        "https://project-c.example.com",
                        List.of(
                                "java",
                                "thymeleaf",
                                "web"
                        ),
                        20
                )
        );
    }

    private ProfileAnalysis
    createExcellentProfileAnalysis() {
        return new ProfileAnalysis(
                100,
                RepositoryScoreLevel.EXCELLENT,
                3,
                3,
                0,
                0,
                "project-a"
        );
    }

    private RepositoryAnalysis createAnalysis(
            String name,
            int score,
            int readmeScore,
            String homepage,
            List<String> topics,
            int activityScore
    ) {
        GitHubRepositoryResponse repository =
                new GitHubRepositoryResponse(
                        name,
                        "Project description",
                        "https://github.com/example/" + name,
                        homepage,
                        "Java",
                        0,
                        0,
                        0,
                        false,
                        false,
                        topics,
                        Instant.now(),
                        Instant.now(),
                        Instant.now()
                );

        boolean readmePresent =
                readmeScore > 0;

        ReadmeAnalysis readmeAnalysis =
                new ReadmeAnalysis(
                        readmePresent,
                        readmePresent ? 1500 : 0,
                        readmeScore,
                        readmePresent,
                        readmePresent,
                        readmePresent,
                        readmePresent,
                        readmePresent,
                        List.of()
                );

        int recencyScore =
                Math.min(
                        activityScore,
                        10
                );

        int frequencyScore =
                Math.max(
                        activityScore - recencyScore,
                        0
                );

        ActivityAnalysis activityAnalysis =
                new ActivityAnalysis(
                        activityScore > 0 ? 10 : 0,
                        recencyScore,
                        frequencyScore,
                        activityScore,
                        activityScore >= 16
                                ? "Alta"
                                : "Baixa",
                        List.of()
                );

        return new RepositoryAnalysis(
                repository,
                score,
                RepositoryScoreLevel.fromScore(score),
                readmeAnalysis,
                activityAnalysis,
                List.of(),
                List.of()
        );
    }
}