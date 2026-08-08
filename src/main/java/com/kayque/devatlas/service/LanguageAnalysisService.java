package com.kayque.devatlas.service;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.model.LanguageUsage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LanguageAnalysisService {

    public List<LanguageUsage> analyze(
            List<GitHubRepositoryResponse> repositories
    ) {
        Map<String, Long> repositoriesByLanguage =
                repositories
                        .stream()
                        .map(GitHubRepositoryResponse::language)
                        .filter(this::hasText)
                        .collect(
                                Collectors.groupingBy(
                                        Function.identity(),
                                        Collectors.counting()
                                )
                        );

        long totalRepositoriesWithLanguage =
                repositoriesByLanguage
                        .values()
                        .stream()
                        .mapToLong(Long::longValue)
                        .sum();

        if (totalRepositoriesWithLanguage == 0) {
            return List.of();
        }

        List<Map.Entry<String, Long>> sortedLanguages =
                repositoriesByLanguage
                        .entrySet()
                        .stream()
                        .sorted(
                                Comparator
                                        .comparingLong(
                                                (
                                                        Map.Entry<String, Long>
                                                                entry
                                                ) -> entry.getValue()
                                        )
                                        .reversed()
                                        .thenComparing(
                                                Map.Entry::getKey
                                        )
                        )
                        .toList();

        List<LanguageUsage> languageUsage =
                new ArrayList<>();

        int accumulatedPercentage = 0;

        for (
                int index = 0;
                index < sortedLanguages.size();
                index++
        ) {
            Map.Entry<String, Long> language =
                    sortedLanguages.get(index);

            boolean lastLanguage =
                    index == sortedLanguages.size() - 1;

            int percentage;

            if (lastLanguage) {
                percentage =
                        100 - accumulatedPercentage;
            } else {
                percentage =
                        calculatePercentage(
                                language.getValue(),
                                totalRepositoriesWithLanguage
                        );
            }

            accumulatedPercentage += percentage;

            languageUsage.add(
                    new LanguageUsage(
                            language.getKey(),
                            language.getValue(),
                            percentage
                    )
            );
        }

        return List.copyOf(languageUsage);
    }

    private int calculatePercentage(
            long repositories,
            long totalRepositories
    ) {
        return (int) Math.round(
                repositories * 100.0
                        / totalRepositories
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}