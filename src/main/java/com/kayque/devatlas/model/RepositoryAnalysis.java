package com.kayque.devatlas.model;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;

import java.util.List;

public record RepositoryAnalysis(

        GitHubRepositoryResponse repository,
        int score,
        RepositoryScoreLevel level,
        boolean readmePresent,
        int readmeSize,
        List<String> recommendations

) {
}