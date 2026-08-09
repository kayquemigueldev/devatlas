package com.kayque.devatlas.model;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;

import java.util.List;

public record RepositoryAnalysis(

        GitHubRepositoryResponse repository,
        int score,
        RepositoryScoreLevel level,
        ReadmeAnalysis readmeAnalysis,
        ActivityAnalysis activityAnalysis,
        List<ScoreCriterion> scoreBreakdown,
        List<String> recommendations

) {

    public boolean readmePresent() {
        return readmeAnalysis.present();
    }

    public int readmeSize() {
        return readmeAnalysis.size();
    }
}