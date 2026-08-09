package com.kayque.devatlas.model;

public record ProfileRecommendation(

        RecommendationCategory category,
        RecommendationPriority priority,
        String title,
        String description

) {
}