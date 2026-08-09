package com.kayque.devatlas.service;

import com.kayque.devatlas.model.ActivityAnalysis;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RepositoryActivityService {

    public ActivityAnalysis analyze(
            Instant pushedAt,
            int recentCommitCount
    ) {
        int safeCommitCount =
                Math.max(recentCommitCount, 0);

        int recencyScore =
                calculateRecencyScore(pushedAt);

        int frequencyScore =
                calculateFrequencyScore(safeCommitCount);

        int score =
                recencyScore + frequencyScore;

        return new ActivityAnalysis(
                safeCommitCount,
                recencyScore,
                frequencyScore,
                score,
                determineLabel(score),
                createRecommendations(
                        pushedAt,
                        safeCommitCount
                )
        );
    }

    private int calculateRecencyScore(
            Instant pushedAt
    ) {
        if (pushedAt == null) {
            return 0;
        }

        long daysSinceLastUpdate =
                Math.max(
                        Duration.between(
                                pushedAt,
                                Instant.now()
                        ).toDays(),
                        0
                );

        if (daysSinceLastUpdate <= 30) {
            return 10;
        }

        if (daysSinceLastUpdate <= 90) {
            return 7;
        }

        if (daysSinceLastUpdate <= 180) {
            return 4;
        }

        if (daysSinceLastUpdate <= 365) {
            return 2;
        }

        return 0;
    }

    private int calculateFrequencyScore(
            int recentCommitCount
    ) {
        if (recentCommitCount >= 10) {
            return 10;
        }

        if (recentCommitCount >= 5) {
            return 8;
        }

        if (recentCommitCount >= 2) {
            return 5;
        }

        if (recentCommitCount == 1) {
            return 2;
        }

        return 0;
    }

    private String determineLabel(
            int score
    ) {
        if (score >= 16) {
            return "Alta";
        }

        if (score >= 10) {
            return "Moderada";
        }

        if (score > 0) {
            return "Baixa";
        }

        return "Sem atividade recente";
    }

    private List<String> createRecommendations(
            Instant pushedAt,
            int recentCommitCount
    ) {
        List<String> recommendations =
                new ArrayList<>();

        if (pushedAt == null
                || pushedAt.isBefore(
                Instant.now()
                        .minusSeconds(
                                90L * 24 * 60 * 60
                        )
        )) {
            recommendations.add(
                    "Atualize o projeto para demonstrar manutenção."
            );
        }

        if (recentCommitCount < 2) {
            recommendations.add(
                    "Faça commits mais frequentes para demonstrar evolução contínua."
            );
        }

        return List.copyOf(recommendations);
    }
}