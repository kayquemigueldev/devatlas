package com.kayque.devatlas.model;

public record ScoreCriterion(

        String name,
        int score,
        int maximumScore

) {

    public boolean complete() {
        return score == maximumScore;
    }

    public int percentage() {
        if (maximumScore == 0) {
            return 0;
        }

        return (int) Math.round(
                score * 100.0
                        / maximumScore
        );
    }
}