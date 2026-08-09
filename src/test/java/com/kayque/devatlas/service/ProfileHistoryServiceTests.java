package com.kayque.devatlas.service;

import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileHistoryEntry;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import com.kayque.devatlas.persistence.entity.ProfileAnalysisHistoryEntity;
import com.kayque.devatlas.persistence.repository.ProfileAnalysisHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileHistoryServiceTests {

    @Mock
    private ProfileAnalysisHistoryRepository
            historyRepository;

    @InjectMocks
    private ProfileHistoryService service;

    @Test
    void shouldSaveFirstProfileAnalysis() {
        ProfileAnalysis profileAnalysis =
                createAnalysis(
                        84,
                        "investlab"
                );

        when(
                historyRepository
                        .findFirstByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                "kayquemigueldev"
                        )
        ).thenReturn(Optional.empty());

        boolean saved =
                service.saveIfChanged(
                        " KayqueMiguelDev ",
                        profileAnalysis
                );

        ArgumentCaptor<ProfileAnalysisHistoryEntity> captor =
                ArgumentCaptor.forClass(
                        ProfileAnalysisHistoryEntity.class
                );

        verify(historyRepository)
                .save(captor.capture());

        assertTrue(saved);

        assertEquals(
                "kayquemigueldev",
                captor.getValue().getUsername()
        );

        assertEquals(
                84,
                captor.getValue().getOverallScore()
        );
    }

    @Test
    void shouldNotSaveUnchangedAnalysis() {
        ProfileAnalysis profileAnalysis =
                createAnalysis(
                        84,
                        "investlab"
                );

        ProfileAnalysisHistoryEntity latestHistory =
                new ProfileAnalysisHistoryEntity(
                        "kayquemigueldev",
                        profileAnalysis,
                        Instant.parse(
                                "2026-08-09T12:00:00Z"
                        )
                );

        when(
                historyRepository
                        .findFirstByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                "kayquemigueldev"
                        )
        ).thenReturn(
                Optional.of(latestHistory)
        );

        boolean saved =
                service.saveIfChanged(
                        "kayquemigueldev",
                        profileAnalysis
                );

        assertFalse(saved);

        verify(
                historyRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldSaveChangedAnalysis() {
        ProfileAnalysis previousAnalysis =
                createAnalysis(
                        70,
                        "project-a"
                );

        ProfileAnalysis currentAnalysis =
                createAnalysis(
                        84,
                        "investlab"
                );

        ProfileAnalysisHistoryEntity latestHistory =
                new ProfileAnalysisHistoryEntity(
                        "kayquemigueldev",
                        previousAnalysis,
                        Instant.parse(
                                "2026-08-01T12:00:00Z"
                        )
                );

        when(
                historyRepository
                        .findFirstByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                "kayquemigueldev"
                        )
        ).thenReturn(
                Optional.of(latestHistory)
        );

        boolean saved =
                service.saveIfChanged(
                        "kayquemigueldev",
                        currentAnalysis
                );

        assertTrue(saved);

        verify(historyRepository)
                .save(any());
    }

    @Test
    void shouldCalculateScoreChanges() {
        ProfileAnalysisHistoryEntity newest =
                createHistory(
                        84,
                        "2026-08-09T12:00:00Z"
                );

        ProfileAnalysisHistoryEntity previous =
                createHistory(
                        70,
                        "2026-08-05T12:00:00Z"
                );

        ProfileAnalysisHistoryEntity oldest =
                createHistory(
                        65,
                        "2026-08-01T12:00:00Z"
                );

        when(
                historyRepository
                        .findTop10ByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                "kayquemigueldev"
                        )
        ).thenReturn(
                List.of(
                        newest,
                        previous,
                        oldest
                )
        );

        List<ProfileHistoryEntry> history =
                service.findHistory(
                        "KAYQUEMIGUELDEV"
                );

        assertEquals(
                3,
                history.size()
        );

        assertEquals(
                14,
                history.get(0).scoreChange()
        );

        assertEquals(
                5,
                history.get(1).scoreChange()
        );

        assertNull(
                history.get(2).scoreChange()
        );

        assertTrue(
                history.getFirst().improved()
        );
    }

    private ProfileAnalysisHistoryEntity createHistory(
            int score,
            String analyzedAt
    ) {
        return new ProfileAnalysisHistoryEntity(
                "kayquemigueldev",
                createAnalysis(
                        score,
                        "investlab"
                ),
                Instant.parse(analyzedAt)
        );
    }

    private ProfileAnalysis createAnalysis(
            int score,
            String strongestRepository
    ) {
        return new ProfileAnalysis(
                score,
                RepositoryScoreLevel.fromScore(score),
                8,
                6,
                1,
                1,
                strongestRepository
        );
    }
}