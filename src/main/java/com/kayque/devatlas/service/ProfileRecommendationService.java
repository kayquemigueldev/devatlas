package com.kayque.devatlas.service;

import com.kayque.devatlas.model.LanguageUsage;
import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileRecommendation;
import com.kayque.devatlas.model.RecommendationCategory;
import com.kayque.devatlas.model.RecommendationPriority;
import com.kayque.devatlas.model.RepositoryAnalysis;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProfileRecommendationService {

    private static final int MAX_RECOMMENDATIONS = 5;

    public List<ProfileRecommendation> analyze(
            ProfileAnalysis profileAnalysis,
            List<RepositoryAnalysis> repositoryAnalyses,
            List<LanguageUsage> languageUsage
    ) {
        if (repositoryAnalyses.isEmpty()) {
            return List.of(
                    new ProfileRecommendation(
                            RecommendationCategory.QUALITY,
                            RecommendationPriority.HIGH,
                            "Publique projetos analisáveis",
                            "O perfil ainda não possui projetos próprios disponíveis para análise."
                    )
            );
        }

        List<ProfileRecommendation> recommendations =
                new ArrayList<>();

        addQualityRecommendation(
                profileAnalysis,
                recommendations
        );

        addDocumentationRecommendation(
                repositoryAnalyses,
                recommendations
        );

        addDeployRecommendation(
                repositoryAnalyses,
                recommendations
        );

        addDiscoverabilityRecommendation(
                repositoryAnalyses,
                recommendations
        );

        addActivityRecommendation(
                repositoryAnalyses,
                recommendations
        );

        addTechnologyRecommendation(
                repositoryAnalyses,
                languageUsage,
                recommendations
        );

        if (recommendations.isEmpty()) {
            recommendations.add(
                    new ProfileRecommendation(
                            RecommendationCategory.QUALITY,
                            RecommendationPriority.LOW,
                            "Mantenha o padrão de qualidade",
                            "O perfil apresenta projetos completos, ativos e bem documentados."
                    )
            );
        }

        return recommendations
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                recommendation ->
                                        -recommendation
                                                .priority()
                                                .getWeight()
                        )
                )
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    private void addQualityRecommendation(
            ProfileAnalysis profileAnalysis,
            List<ProfileRecommendation> recommendations
    ) {
        if (profileAnalysis.overallScore() < 60) {
            recommendations.add(
                    new ProfileRecommendation(
                            RecommendationCategory.QUALITY,
                            RecommendationPriority.HIGH,
                            "Eleve a qualidade geral do perfil",
                            "A média atual é "
                                    + profileAnalysis.overallScore()
                                    + "/100. Priorize os projetos que ainda estão em evolução."
                    )
            );

            return;
        }

        if (profileAnalysis.excellentRepositories() == 0) {
            recommendations.add(
                    new ProfileRecommendation(
                            RecommendationCategory.QUALITY,
                            RecommendationPriority.MEDIUM,
                            "Transforme um projeto em referência",
                            "Nenhum projeto alcançou o nível excelente. Escolha o projeto mais forte e complete os critérios restantes."
                    )
            );
        }
    }

    private void addDocumentationRecommendation(
            List<RepositoryAnalysis> analyses,
            List<ProfileRecommendation> recommendations
    ) {
        long documentedRepositories =
                analyses
                        .stream()
                        .filter(analysis ->
                                analysis
                                        .readmeAnalysis()
                                        .score() >= 14
                        )
                        .count();

        int coverage =
                calculateCoverage(
                        documentedRepositories,
                        analyses.size()
                );

        if (coverage >= 70) {
            return;
        }

        RecommendationPriority priority =
                coverage < 40
                        ? RecommendationPriority.HIGH
                        : RecommendationPriority.MEDIUM;

        recommendations.add(
                new ProfileRecommendation(
                        RecommendationCategory.DOCUMENTATION,
                        priority,
                        "Fortaleça a documentação",
                        documentedRepositories
                                + " de "
                                + analyses.size()
                                + " projetos possuem README bom ou completo."
                )
        );
    }

    private void addDeployRecommendation(
            List<RepositoryAnalysis> analyses,
            List<ProfileRecommendation> recommendations
    ) {
        long deployedRepositories =
                analyses
                        .stream()
                        .filter(analysis ->
                                hasText(
                                        analysis
                                                .repository()
                                                .homepage()
                                )
                        )
                        .count();

        int coverage =
                calculateCoverage(
                        deployedRepositories,
                        analyses.size()
                );

        if (coverage >= 50) {
            return;
        }

        RecommendationPriority priority =
                deployedRepositories == 0
                        ? RecommendationPriority.HIGH
                        : RecommendationPriority.MEDIUM;

        recommendations.add(
                new ProfileRecommendation(
                        RecommendationCategory.DEPLOY,
                        priority,
                        "Publique demonstrações dos projetos",
                        deployedRepositories
                                + " de "
                                + analyses.size()
                                + " projetos possuem um deploy vinculado."
                )
        );
    }

    private void addDiscoverabilityRecommendation(
            List<RepositoryAnalysis> analyses,
            List<ProfileRecommendation> recommendations
    ) {
        long discoverableRepositories =
                analyses
                        .stream()
                        .filter(analysis ->
                                analysis
                                        .repository()
                                        .topics() != null
                        )
                        .filter(analysis ->
                                analysis
                                        .repository()
                                        .topics()
                                        .size() >= 3
                        )
                        .count();

        int coverage =
                calculateCoverage(
                        discoverableRepositories,
                        analyses.size()
                );

        if (coverage >= 70) {
            return;
        }

        recommendations.add(
                new ProfileRecommendation(
                        RecommendationCategory.DISCOVERABILITY,
                        RecommendationPriority.MEDIUM,
                        "Melhore a descoberta dos projetos",
                        discoverableRepositories
                                + " de "
                                + analyses.size()
                                + " projetos possuem pelo menos três tópicos."
                )
        );
    }

    private void addActivityRecommendation(
            List<RepositoryAnalysis> analyses,
            List<ProfileRecommendation> recommendations
    ) {
        int averageActivityScore =
                (int) Math.round(
                        analyses
                                .stream()
                                .mapToInt(analysis ->
                                        analysis
                                                .activityAnalysis()
                                                .score()
                                )
                                .average()
                                .orElse(0)
                );

        if (averageActivityScore >= 14) {
            return;
        }

        RecommendationPriority priority =
                averageActivityScore < 8
                        ? RecommendationPriority.HIGH
                        : RecommendationPriority.MEDIUM;

        recommendations.add(
                new ProfileRecommendation(
                        RecommendationCategory.ACTIVITY,
                        priority,
                        "Aumente a consistência de desenvolvimento",
                        "A pontuação média de atividade é "
                                + averageActivityScore
                                + "/20 nos projetos analisados."
                )
        );
    }

    private void addTechnologyRecommendation(
            List<RepositoryAnalysis> analyses,
            List<LanguageUsage> languageUsage,
            List<ProfileRecommendation> recommendations
    ) {
        if (analyses.size() < 3
                || languageUsage.isEmpty()) {
            return;
        }

        LanguageUsage dominantLanguage =
                languageUsage
                        .stream()
                        .max(
                                Comparator.comparingInt(
                                        LanguageUsage::percentage
                                )
                        )
                        .orElse(null);

        if (dominantLanguage == null
                || dominantLanguage.percentage() < 80) {
            return;
        }

        recommendations.add(
                new ProfileRecommendation(
                        RecommendationCategory.TECHNOLOGY,
                        RecommendationPriority.LOW,
                        "Diversifique as tecnologias",
                        dominantLanguage.language()
                                + " representa "
                                + dominantLanguage.percentage()
                                + "% dos projetos analisados."
                )
        );
    }

    private int calculateCoverage(
            long matchingRepositories,
            int totalRepositories
    ) {
        if (totalRepositories == 0) {
            return 0;
        }

        return (int) Math.round(
                matchingRepositories
                        * 100.0
                        / totalRepositories
        );
    }

    private boolean hasText(
            String value
    ) {
        return value != null
                && !value.isBlank();
    }
}