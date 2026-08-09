package com.kayque.devatlas.model;

public enum RecommendationPriority {

    HIGH(
            "Alta",
            "high",
            3
    ),

    MEDIUM(
            "Média",
            "medium",
            2
    ),

    LOW(
            "Baixa",
            "low",
            1
    );

    private final String label;
    private final String cssClass;
    private final int weight;

    RecommendationPriority(
            String label,
            String cssClass,
            int weight
    ) {
        this.label = label;
        this.cssClass = cssClass;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public int getWeight() {
        return weight;
    }
}