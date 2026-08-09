package com.kayque.devatlas.persistence.repository;

import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import com.kayque.devatlas.persistence.entity.ProfileAnalysisHistoryEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ProfileAnalysisHistoryRepositoryTests {

    @Autowired
    private ProfileAnalysisHistoryRepository repository;

    @Test
    void shouldFindLatestAnalysesByUsername() {
        ProfileAnalysis firstAnalysis =
                new ProfileAnalysis(
                        70,
                        RepositoryScoreLevel.GOOD,
                        4,
                        0,
                        4,
                        0,
                        "project-a"
                );

        ProfileAnalysis secondAnalysis =
                new ProfileAnalysis(
                        84,
                        RepositoryScoreLevel.EXCELLENT,
                        8,
                        6,
                        1,
                        1,
                        "project-b"
                );

        ProfileAnalysisHistoryEntity firstHistory =
                repository.save(
                        new ProfileAnalysisHistoryEntity(
                                "kayquemigueldev",
                                firstAnalysis,
                                Instant.parse(
                                        "2026-08-01T12:00:00Z"
                                )
                        )
                );

        repository.save(
                new ProfileAnalysisHistoryEntity(
                        "kayquemigueldev",
                        secondAnalysis,
                        Instant.parse(
                                "2026-08-09T12:00:00Z"
                        )
                )
        );

        List<ProfileAnalysisHistoryEntity> history =
                repository
                        .findTop10ByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                "KAYQUEMIGUELDEV"
                        );

        assertNotNull(
                firstHistory.getId()
        );

        assertEquals(
                2,
                history.size()
        );

        assertEquals(
                84,
                history.getFirst()
                        .getOverallScore()
        );

        assertEquals(
                "project-b",
                history.getFirst()
                        .getStrongestRepository()
        );
    }
}
