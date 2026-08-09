package com.kayque.devatlas.service;

import com.kayque.devatlas.model.ActivityAnalysis;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositoryActivityServiceTests {

    private final RepositoryActivityService service =
            new RepositoryActivityService();

    @Test
    void shouldIdentifyHighActivity() {
        ActivityAnalysis analysis =
                service.analyze(
                        Instant.now()
                                .minus(
                                        10,
                                        ChronoUnit.DAYS
                                ),
                        10
                );

        assertEquals(
                10,
                analysis.recencyScore()
        );

        assertEquals(
                10,
                analysis.frequencyScore()
        );

        assertEquals(
                20,
                analysis.score()
        );

        assertEquals(
                "Alta",
                analysis.label()
        );

        assertTrue(
                analysis.recommendations().isEmpty()
        );
    }

    @Test
    void shouldIdentifyModerateActivity() {
        ActivityAnalysis analysis =
                service.analyze(
                        Instant.now()
                                .minus(
                                        60,
                                        ChronoUnit.DAYS
                                ),
                        5
                );

        assertEquals(
                7,
                analysis.recencyScore()
        );

        assertEquals(
                8,
                analysis.frequencyScore()
        );

        assertEquals(
                15,
                analysis.score()
        );

        assertEquals(
                "Moderada",
                analysis.label()
        );

        assertTrue(
                analysis.recommendations().isEmpty()
        );
    }

    @Test
    void shouldRecommendImprovingLowActivity() {
        ActivityAnalysis analysis =
                service.analyze(
                        Instant.now()
                                .minus(
                                        120,
                                        ChronoUnit.DAYS
                                ),
                        1
                );

        assertEquals(
                4,
                analysis.recencyScore()
        );

        assertEquals(
                2,
                analysis.frequencyScore()
        );

        assertEquals(
                6,
                analysis.score()
        );

        assertEquals(
                "Baixa",
                analysis.label()
        );

        assertEquals(
                2,
                analysis.recommendations().size()
        );
    }

    @Test
    void shouldHandleRepositoryWithoutActivity() {
        ActivityAnalysis analysis =
                service.analyze(
                        null,
                        0
                );

        assertEquals(
                0,
                analysis.recencyScore()
        );

        assertEquals(
                0,
                analysis.frequencyScore()
        );

        assertEquals(
                0,
                analysis.score()
        );

        assertEquals(
                "Sem atividade recente",
                analysis.label()
        );

        assertEquals(
                2,
                analysis.recommendations().size()
        );
    }
}