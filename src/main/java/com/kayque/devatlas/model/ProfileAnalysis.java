package com.kayque.devatlas.model;

public record ProfileAnalysis(

        int overallScore,
        RepositoryScoreLevel level,
        int totalRepositories,
        long excellentRepositories,
        long goodRepositories,
        long developingRepositories,
        String strongestRepository

) {
}