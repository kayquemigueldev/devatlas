package com.kayque.devatlas.model;

import java.time.Instant;

public record ProfileHistoryEntry(

        Long id,
        int overallScore,
        RepositoryScoreLevel level,
        int totalRepositories,
        String strongestRepository,
        Instant analyzedAt,
        Integer scoreChange

) {

    public boolean hasPreviousAnalysis() {
        return scoreChange != null;
    }

    public boolean improved() {
        return scoreChange != null
                && scoreChange > 0;
    }

    public boolean declined() {
        return scoreChange != null
                && scoreChange < 0;
    }

    public boolean remainedStable() {
        return scoreChange != null
                && scoreChange == 0;
    }
}