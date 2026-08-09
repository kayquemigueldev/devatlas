package com.kayque.devatlas.model;

import java.util.List;

public record ActivityAnalysis(

        int recentCommitCount,
        int recencyScore,
        int frequencyScore,
        int score,
        String label,
        List<String> recommendations

) {
}