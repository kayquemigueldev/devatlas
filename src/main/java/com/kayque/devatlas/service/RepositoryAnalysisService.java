package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.ReadmeAnalysis;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryAnalysisService {

    private final ReadmeAnalysisService
            readmeAnalysisService;

    public RepositoryAnalysisService(
            ReadmeAnalysisService readmeAnalysisService
    ) {
        this.readmeAnalysisService =
                readmeAnalysisService;
    }

    public List<RepositoryAnalysis> analyze(
            List<GitHubRepositoryResponse> repositories
    ) {
        return repositories
                .stream()
                .map(this::analyze)
                .toList();
    }

    public RepositoryAnalysis analyze(
            GitHubRepositoryResponse repository
    ) {
        return analyze(
                repository,
                Optional.empty()
        );
    }

    public RepositoryAnalysis analyze(
            GitHubRepositoryResponse repository,
            Optional<GitHubReadmeResponse> readme
    ) {
        ReadmeAnalysis readmeAnalysis =
                readmeAnalysisService.analyze(
                        readme
                );

        int score = calculateScore(
                repository,
                readmeAnalysis
        );

        RepositoryScoreLevel level =
                RepositoryScoreLevel.fromScore(score);

        List<String> recommendations =
                createRecommendations(
                        repository,
                        readmeAnalysis
                );

        return new RepositoryAnalysis(
                repository,
                score,
                level,
                readmeAnalysis,
                recommendations
        );
    }

    private int calculateScore(
            GitHubRepositoryResponse repository,
            ReadmeAnalysis readmeAnalysis
    ) {
        int score = 0;

        if (hasText(repository.description())) {
            score += 15;
        }

        int topicCount = repository.topics() == null
                ? 0
                : repository.topics().size();

        if (topicCount >= 3) {
            score += 15;
        } else if (topicCount > 0) {
            score += 8;
        }

        if (hasText(repository.homepage())) {
            score += 10;
        }

        if (hasText(repository.language())) {
            score += 10;
        }

        if (!repository.archived()) {
            score += 10;
        }

        score += calculateActivityScore(
                repository.pushedAt()
        );

        score += readmeAnalysis.score();

        return score;
    }

    private int calculateActivityScore(Instant pushedAt) {
        if (pushedAt == null) {
            return 0;
        }

        long daysSinceLastPush =
                ChronoUnit.DAYS.between(
                        pushedAt,
                        Instant.now()
                );

        if (daysSinceLastPush <= 30) {
            return 20;
        }

        if (daysSinceLastPush <= 90) {
            return 15;
        }

        if (daysSinceLastPush <= 180) {
            return 10;
        }

        if (daysSinceLastPush <= 365) {
            return 5;
        }

        return 0;
    }

    private List<String> createRecommendations(
            GitHubRepositoryResponse repository,
            ReadmeAnalysis readmeAnalysis
    ) {
        List<String> recommendations =
                new ArrayList<>();

        if (!hasText(repository.description())) {
            recommendations.add(
                    "Adicione uma descrição clara ao repositório."
            );
        }

        if (repository.topics() == null
                || repository.topics().size() < 3) {
            recommendations.add(
                    "Adicione pelo menos três tópicos relevantes."
            );
        }

        if (!hasText(repository.homepage())) {
            recommendations.add(
                    "Vincule o deploy na homepage do repositório."
            );
        }

        if (!hasText(repository.language())) {
            recommendations.add(
                    "Adicione código para identificar a linguagem."
            );
        }

        if (repository.archived()) {
            recommendations.add(
                    "Revise se este projeto deve continuar arquivado."
            );
        }

        if (repository.pushedAt() == null
                || repository.pushedAt().isBefore(
                Instant.now().minus(
                        90,
                        ChronoUnit.DAYS
                )
        )) {
            recommendations.add(
                    "Atualize o projeto para demonstrar manutenção."
            );
        }

        recommendations.addAll(
                readmeAnalysis.recommendations()
        );

        return List.copyOf(recommendations);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}