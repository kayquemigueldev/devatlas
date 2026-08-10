package com.kayque.devatlas.controller;

import com.kayque.devatlas.handler.HomeExceptionHandler;
import com.kayque.devatlas.ratelimit.AnalysisRateLimiter;
import com.kayque.devatlas.service.GitHubProfileService;
import com.kayque.devatlas.service.LanguageAnalysisService;
import com.kayque.devatlas.service.ProfileAnalysisService;
import com.kayque.devatlas.service.ProfileHistoryService;
import com.kayque.devatlas.service.ProfileRecommendationService;
import com.kayque.devatlas.service.RepositoryAnalysisService;
import com.kayque.devatlas.validation.GitHubUsernameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class HomeControllerTests {

    private GitHubProfileService
            gitHubProfileService;

    private AnalysisRateLimiter
            analysisRateLimiter;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        gitHubProfileService =
                mock(GitHubProfileService.class);

        analysisRateLimiter =
                mock(AnalysisRateLimiter.class);

        HomeController controller =
                new HomeController(
                        gitHubProfileService,
                        mock(RepositoryAnalysisService.class),
                        mock(ProfileAnalysisService.class),
                        mock(LanguageAnalysisService.class),
                        mock(ProfileRecommendationService.class),
                        mock(ProfileHistoryService.class),
                        new GitHubUsernameValidator(),
                        analysisRateLimiter
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .setControllerAdvice(
                                new HomeExceptionHandler()
                        )
                        .build();
    }

    @Test
    void shouldRejectInvalidUsernameBeforeCallingGitHub()
            throws Exception {

        mockMvc
                .perform(
                        get("/analisar")
                                .param(
                                        "username",
                                        "<script>"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("index")
                )
                .andExpect(
                        model().attribute(
                                "errorTitle",
                                "Nome de usuário inválido"
                        )
                );

        verifyNoInteractions(
                gitHubProfileService,
                analysisRateLimiter
        );
    }

    @Test
    void shouldReturnTooManyRequestsWhenLimitIsExceeded()
            throws Exception {

        when(
                analysisRateLimiter.tryAcquire(
                        anyString()
                )
        ).thenReturn(false);

        mockMvc
                .perform(
                        get("/analisar")
                                .param(
                                        "username",
                                        "kayquemigueldev"
                                )
                )
                .andExpect(
                        status().isTooManyRequests()
                )
                .andExpect(
                        view().name("index")
                )
                .andExpect(
                        model().attribute(
                                "errorTitle",
                                "Limite de análises atingido"
                        )
                );

        verifyNoInteractions(
                gitHubProfileService
        );
    }
}