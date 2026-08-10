package com.kayque.devatlas.controller;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.model.LanguageUsage;
import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileRecommendation;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.model.ProfileHistoryEntry;
import com.kayque.devatlas.service.GitHubProfileService;
import com.kayque.devatlas.service.LanguageAnalysisService;
import com.kayque.devatlas.service.ProfileAnalysisService;
import com.kayque.devatlas.service.ProfileRecommendationService;
import com.kayque.devatlas.service.RepositoryAnalysisService;
import com.kayque.devatlas.service.ProfileHistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kayque.devatlas.validation.GitHubUsernameValidator;
import com.kayque.devatlas.exception.AnalysisRateLimitExceededException;
import com.kayque.devatlas.ratelimit.AnalysisRateLimiter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
public class HomeController {

    private final GitHubProfileService
            gitHubProfileService;

    private final RepositoryAnalysisService
            repositoryAnalysisService;

    private final ProfileAnalysisService
            profileAnalysisService;

    private final LanguageAnalysisService
            languageAnalysisService;

    private final ProfileRecommendationService
            profileRecommendationService;

    private final ProfileHistoryService
            profileHistoryService;

    private final GitHubUsernameValidator
            gitHubUsernameValidator;

    private final AnalysisRateLimiter
            analysisRateLimiter;

    public HomeController(
            GitHubProfileService gitHubProfileService,
            RepositoryAnalysisService repositoryAnalysisService,
            ProfileAnalysisService profileAnalysisService,
            LanguageAnalysisService languageAnalysisService,
            ProfileRecommendationService profileRecommendationService,
            ProfileHistoryService profileHistoryService,
            GitHubUsernameValidator gitHubUsernameValidator,
            AnalysisRateLimiter analysisRateLimiter
    ) {
        this.gitHubProfileService =
                gitHubProfileService;

        this.repositoryAnalysisService =
                repositoryAnalysisService;

        this.profileAnalysisService =
                profileAnalysisService;

        this.languageAnalysisService =
                languageAnalysisService;

        this.profileRecommendationService =
                profileRecommendationService;

        this.profileHistoryService =
                profileHistoryService;

        this.gitHubUsernameValidator =
                gitHubUsernameValidator;

        this.analysisRateLimiter =
                analysisRateLimiter;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/analisar")
    public String analyzeProfile(
            @RequestParam(defaultValue = "") String username,
            HttpServletRequest request,
            Model model
    ) {
        String normalizedUsername =
                gitHubUsernameValidator
                        .validateAndNormalize(
                                username
                        );

        if (!analysisRateLimiter.tryAcquire(
                request.getRemoteAddr()
        )) {
            throw new AnalysisRateLimitExceededException(
                    normalizedUsername
            );
        }

        GitHubUserResponse user =
                gitHubProfileService.findUser(
                        normalizedUsername
                );

        List<GitHubRepositoryResponse> repositories =
                gitHubProfileService
                        .findAnalyzableRepositories(
                                normalizedUsername
                        );

        List<RepositoryAnalysis> repositoryAnalyses =
                repositories
                        .stream()
                        .map(repository ->
                                repositoryAnalysisService.analyze(
                                        repository,
                                        gitHubProfileService.findReadme(
                                                normalizedUsername,
                                                repository.name()
                                        ),
                                        gitHubProfileService
                                                .findRecentCommits(
                                                        normalizedUsername,
                                                        repository.name()
                                                )
                                                .size()
                                )
                        )
                        .toList();

        List<LanguageUsage> languageUsage =
                languageAnalysisService.analyze(
                        repositories
                );

        ProfileAnalysis profileAnalysis =
                profileAnalysisService.analyze(
                        repositoryAnalyses
                );

        profileHistoryService.saveIfChanged(
                normalizedUsername,
                profileAnalysis
        );

        List<ProfileHistoryEntry> profileHistory =
                profileHistoryService.findHistory(
                        normalizedUsername
                );

        List<ProfileRecommendation> profileRecommendations =
                profileRecommendationService.analyze(
                        profileAnalysis,
                        repositoryAnalyses,
                        languageUsage
                );

        model.addAttribute(
                "username",
                normalizedUsername
        );

        model.addAttribute(
                "user",
                user
        );

        model.addAttribute(
                "repositoryAnalyses",
                repositoryAnalyses
        );

        model.addAttribute(
                "profileAnalysis",
                profileAnalysis
        );

        model.addAttribute(
                "languageUsage",
                languageUsage
        );

        model.addAttribute(
                "profileRecommendations",
                profileRecommendations
        );

        model.addAttribute(
                "profileHistory",
                profileHistory
        );

        return "index";
    }
}