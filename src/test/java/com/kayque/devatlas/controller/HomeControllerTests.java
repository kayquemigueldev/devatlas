package com.kayque.devatlas.controller;

import com.kayque.devatlas.exception.InvalidGitHubUsernameException;
import com.kayque.devatlas.service.GitHubProfileService;
import com.kayque.devatlas.service.LanguageAnalysisService;
import com.kayque.devatlas.service.ProfileAnalysisService;
import com.kayque.devatlas.service.ProfileHistoryService;
import com.kayque.devatlas.service.ProfileRecommendationService;
import com.kayque.devatlas.service.RepositoryAnalysisService;
import com.kayque.devatlas.validation.GitHubUsernameValidator;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class HomeControllerTests {

    @Test
    void shouldRejectInvalidUsernameBeforeCallingGitHub() {
        GitHubProfileService gitHubProfileService =
                mock(GitHubProfileService.class);

        HomeController controller =
                new HomeController(
                        gitHubProfileService,
                        mock(RepositoryAnalysisService.class),
                        mock(ProfileAnalysisService.class),
                        mock(LanguageAnalysisService.class),
                        mock(ProfileRecommendationService.class),
                        mock(ProfileHistoryService.class),
                        new GitHubUsernameValidator()
                );

        assertThrows(
                InvalidGitHubUsernameException.class,
                () -> controller.analyzeProfile(
                        "<script>",
                        new ConcurrentModel()
                )
        );

        verifyNoInteractions(
                gitHubProfileService
        );
    }
}