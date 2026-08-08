package com.kayque.devatlas.model;

public enum RepositoryScoreLevel {

    EXCELLENT("Excelente", "excellent"),
    GOOD("Bom", "good"),
    DEVELOPING("Em evolução", "developing");

    private final String label;
    private final String cssClass;

    RepositoryScoreLevel(
            String label,
            String cssClass
    ) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static RepositoryScoreLevel fromScore(int score) {
        if (score >= 80) {
            return EXCELLENT;
        }

        if (score >= 60) {
            return GOOD;
        }

        return DEVELOPING;
    }
}