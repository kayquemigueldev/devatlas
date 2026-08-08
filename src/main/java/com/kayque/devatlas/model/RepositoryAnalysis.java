package com.kayque.devatlas.model;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;

import java.util.List;

public record RepositoryAnalysis(

        GitHubRepositoryResponse repository,
        int score,
        RepositoryScoreLevel level,
        List<String> recommendations

) {
}