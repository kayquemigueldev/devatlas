package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import com.kayque.devatlas.model.ReadmeAnalysis;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProfileAnalysisServiceTests {

    private final ProfileAnalysisService service =
            new ProfileAnalysisService();

    @Test
    void shouldCalculateProfileAnalysis() {
        List<RepositoryAnalysis> analyses = List.of(
                createAnalysis("project-a", 100),
                createAnalysis("project-b", 70),
                createAnalysis("project-c", 50)
        );

        ProfileAnalysis profileAnalysis =
                service.analyze(analyses);

        assertEquals(73, profileAnalysis.overallScore());

        assertEquals(
                RepositoryScoreLevel.GOOD,
                profileAnalysis.level()
        );

        assertEquals(
                3,
                profileAnalysis.totalRepositories()
        );

        assertEquals(
                1,
                profileAnalysis.excellentRepositories()
        );

        assertEquals(
                1,
                profileAnalysis.goodRepositories()
        );

        assertEquals(
                1,
                profileAnalysis.developingRepositories()
        );

        assertEquals(
                "project-a",
                profileAnalysis.strongestRepository()
        );
    }

    @Test
    void shouldHandleEmptyRepositoryList() {
        ProfileAnalysis profileAnalysis =
                service.analyze(List.of());

        assertEquals(0, profileAnalysis.overallScore());
        assertEquals(0, profileAnalysis.totalRepositories());
        assertNull(profileAnalysis.strongestRepository());
    }

    private RepositoryAnalysis createAnalysis(
            String name,
            int score
    ) {
        GitHubRepositoryResponse repository =
                new GitHubRepositoryResponse(
                        name,
                        "Description",
                        "https://github.com/example/" + name,
                        null,
                        "Java",
                        0,
                        0,
                        0,
                        false,
                        false,
                        List.of(),
                        Instant.now(),
                        Instant.now(),
                        Instant.now()
                );

        ReadmeAnalysis readmeAnalysis =
                new ReadmeAnalysis(
                        true,
                        1500,
                        20,
                        true,
                        true,
                        true,
                        true,
                        true,
                        List.of()
                );

        return new RepositoryAnalysis(
                repository,
                score,
                RepositoryScoreLevel.fromScore(score),
                readmeAnalysis,
                List.of()
        );
    }
}