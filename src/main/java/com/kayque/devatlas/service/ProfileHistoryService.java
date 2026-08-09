package com.kayque.devatlas.service;

import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileHistoryEntry;
import com.kayque.devatlas.persistence.entity.ProfileAnalysisHistoryEntity;
import com.kayque.devatlas.persistence.repository.ProfileAnalysisHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProfileHistoryService {

    private final ProfileAnalysisHistoryRepository
            historyRepository;

    public ProfileHistoryService(
            ProfileAnalysisHistoryRepository historyRepository
    ) {
        this.historyRepository =
                historyRepository;
    }

    @Transactional
    public boolean saveIfChanged(
            String username,
            ProfileAnalysis profileAnalysis
    ) {
        String normalizedUsername =
                normalizeUsername(username);

        Optional<ProfileAnalysisHistoryEntity> latestHistory =
                historyRepository
                        .findFirstByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                normalizedUsername
                        );

        if (latestHistory.isPresent()
                && !hasRelevantChange(
                latestHistory.get(),
                profileAnalysis
        )) {
            return false;
        }

        ProfileAnalysisHistoryEntity history =
                new ProfileAnalysisHistoryEntity(
                        normalizedUsername,
                        profileAnalysis,
                        Instant.now()
                );

        historyRepository.save(history);

        return true;
    }

    @Transactional(readOnly = true)
    public List<ProfileHistoryEntry> findHistory(
            String username
    ) {
        String normalizedUsername =
                normalizeUsername(username);

        List<ProfileAnalysisHistoryEntity> history =
                historyRepository
                        .findTop10ByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
                                normalizedUsername
                        );

        List<ProfileHistoryEntry> entries =
                new ArrayList<>();

        for (int index = 0;
             index < history.size();
             index++) {

            ProfileAnalysisHistoryEntity current =
                    history.get(index);

            Integer scoreChange = null;

            if (index + 1 < history.size()) {
                ProfileAnalysisHistoryEntity previous =
                        history.get(index + 1);

                scoreChange =
                        current.getOverallScore()
                                - previous.getOverallScore();
            }

            entries.add(
                    new ProfileHistoryEntry(
                            current.getId(),
                            current.getOverallScore(),
                            current.getLevel(),
                            current.getTotalRepositories(),
                            current.getStrongestRepository(),
                            current.getAnalyzedAt(),
                            scoreChange
                    )
            );
        }

        return List.copyOf(entries);
    }

    private boolean hasRelevantChange(
            ProfileAnalysisHistoryEntity latestHistory,
            ProfileAnalysis currentAnalysis
    ) {
        return latestHistory.getOverallScore()
                != currentAnalysis.overallScore()

                || latestHistory.getLevel()
                != currentAnalysis.level()

                || latestHistory.getTotalRepositories()
                != currentAnalysis.totalRepositories()

                || latestHistory.getExcellentRepositories()
                != currentAnalysis.excellentRepositories()

                || latestHistory.getGoodRepositories()
                != currentAnalysis.goodRepositories()

                || latestHistory.getDevelopingRepositories()
                != currentAnalysis.developingRepositories()

                || !Objects.equals(
                latestHistory.getStrongestRepository(),
                currentAnalysis.strongestRepository()
        );
    }

    private String normalizeUsername(
            String username
    ) {
        if (username == null
                || username.isBlank()) {
            throw new IllegalArgumentException(
                    "O username não pode estar vazio."
            );
        }

        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}