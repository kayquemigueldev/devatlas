package com.kayque.devatlas.service;

import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProfileAnalysisService {

    public ProfileAnalysis analyze(
            List<RepositoryAnalysis> repositoryAnalyses
    ) {
        int overallScore = calculateOverallScore(
                repositoryAnalyses
        );

        RepositoryScoreLevel level =
                RepositoryScoreLevel.fromScore(
                        overallScore
                );

        long excellentRepositories =
                countByLevel(
                        repositoryAnalyses,
                        RepositoryScoreLevel.EXCELLENT
                );

        long goodRepositories =
                countByLevel(
                        repositoryAnalyses,
                        RepositoryScoreLevel.GOOD
                );

        long developingRepositories =
                countByLevel(
                        repositoryAnalyses,
                        RepositoryScoreLevel.DEVELOPING
                );

        String strongestRepository =
                findStrongestRepository(
                        repositoryAnalyses
                );

        return new ProfileAnalysis(
                overallScore,
                level,
                repositoryAnalyses.size(),
                excellentRepositories,
                goodRepositories,
                developingRepositories,
                strongestRepository
        );
    }

    private int calculateOverallScore(
            List<RepositoryAnalysis> repositoryAnalyses
    ) {
        return (int) Math.round(
                repositoryAnalyses
                        .stream()
                        .mapToInt(RepositoryAnalysis::score)
                        .average()
                        .orElse(0)
        );
    }

    private long countByLevel(
            List<RepositoryAnalysis> repositoryAnalyses,
            RepositoryScoreLevel level
    ) {
        return repositoryAnalyses
                .stream()
                .filter(analysis ->
                        analysis.level() == level
                )
                .count();
    }

    private String findStrongestRepository(
            List<RepositoryAnalysis> repositoryAnalyses
    ) {
        return repositoryAnalyses
                .stream()
                .max(
                        Comparator.comparingInt(
                                RepositoryAnalysis::score
                        )
                )
                .map(analysis ->
                        analysis.repository().name()
                )
                .orElse(null);
    }
}