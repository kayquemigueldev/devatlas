package com.kayque.devatlas.model;

public enum RecommendationCategory {

    DOCUMENTATION("Documentação"),
    DEPLOY("Deploy"),
    DISCOVERABILITY("Descoberta"),
    ACTIVITY("Atividade"),
    TECHNOLOGY("Tecnologias"),
    QUALITY("Qualidade");

    private final String label;

    RecommendationCategory(
            String label
    ) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}