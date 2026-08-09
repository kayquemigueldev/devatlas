package com.kayque.devatlas.model;

import java.util.List;

public record ReadmeAnalysis(

        boolean present,
        int size,
        int score,
        boolean titlePresent,
        boolean installationSectionPresent,
        boolean usageSectionPresent,
        boolean technologiesSectionPresent,
        boolean imagePresent,
        List<String> recommendations

) {
}