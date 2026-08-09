package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubReadmeResponse;
import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.ActivityAnalysis;
import com.kayque.devatlas.model.ReadmeAnalysis;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import com.kayque.devatlas.model.ScoreCriterion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryAnalysisService {

    private final ReadmeAnalysisService
            readmeAnalysisService;

    private final RepositoryActivityService
            repositoryActivityService;

    public RepositoryAnalysisService(
            ReadmeAnalysisService readmeAnalysisService,
            RepositoryActivityService repositoryActivityService
    ) {
        this.readmeAnalysisService =
                readmeAnalysisService;

        this.repositoryActivityService =
                repositoryActivityService;
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
                Optional.empty(),
                0
        );
    }

    public RepositoryAnalysis analyze(
            GitHubRepositoryResponse repository,
            Optional<GitHubReadmeResponse> readme
    ) {
        return analyze(
                repository,
                readme,
                0
        );
    }

    public RepositoryAnalysis analyze(
            GitHubRepositoryResponse repository,
            Optional<GitHubReadmeResponse> readme,
            int recentCommitCount
    ) {
        ReadmeAnalysis readmeAnalysis =
                readmeAnalysisService.analyze(
                        readme
                );

        ActivityAnalysis activityAnalysis =
                repositoryActivityService.analyze(
                        repository.pushedAt(),
                        recentCommitCount
                );

        List<ScoreCriterion> scoreBreakdown =
                createScoreBreakdown(
                        repository,
                        readmeAnalysis,
                        activityAnalysis
                );

        int score =
                scoreBreakdown
                        .stream()
                        .mapToInt(ScoreCriterion::score)
                        .sum();

        RepositoryScoreLevel level =
                RepositoryScoreLevel.fromScore(score);

        List<String> recommendations =
                createRecommendations(
                        repository,
                        readmeAnalysis,
                        activityAnalysis
                );

        return new RepositoryAnalysis(
                repository,
                score,
                level,
                readmeAnalysis,
                activityAnalysis,
                scoreBreakdown,
                recommendations
        );
    }

    private List<ScoreCriterion> createScoreBreakdown(
            GitHubRepositoryResponse repository,
            ReadmeAnalysis readmeAnalysis,
            ActivityAnalysis activityAnalysis
    ) {
        int descriptionScore =
                hasText(repository.description())
                        ? 15
                        : 0;

        int topicCount =
                repository.topics() == null
                        ? 0
                        : repository.topics().size();

        int topicsScore;

        if (topicCount >= 3) {
            topicsScore = 15;
        } else if (topicCount > 0) {
            topicsScore = 8;
        } else {
            topicsScore = 0;
        }

        int homepageScore =
                hasText(repository.homepage())
                        ? 10
                        : 0;

        int languageScore =
                hasText(repository.language())
                        ? 10
                        : 0;

        int projectStatusScore =
                repository.archived()
                        ? 0
                        : 10;

        return List.of(
                new ScoreCriterion(
                        "Descrição",
                        descriptionScore,
                        15
                ),
                new ScoreCriterion(
                        "Tópicos",
                        topicsScore,
                        15
                ),
                new ScoreCriterion(
                        "Deploy",
                        homepageScore,
                        10
                ),
                new ScoreCriterion(
                        "Linguagem",
                        languageScore,
                        10
                ),
                new ScoreCriterion(
                        "Status do projeto",
                        projectStatusScore,
                        10
                ),
                new ScoreCriterion(
                        "Atividade recente",
                        activityAnalysis.score(),
                        20
                ),
                new ScoreCriterion(
                        "README",
                        readmeAnalysis.score(),
                        20
                )
        );
    }

    private List<String> createRecommendations(
            GitHubRepositoryResponse repository,
            ReadmeAnalysis readmeAnalysis,
            ActivityAnalysis activityAnalysis
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

        recommendations.addAll(
                activityAnalysis.recommendations()
        );

        recommendations.addAll(
                readmeAnalysis.recommendations()
        );

        return List.copyOf(recommendations);
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }
}