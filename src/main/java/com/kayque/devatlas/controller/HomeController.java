package com.kayque.devatlas.controller;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.model.LanguageUsage;
import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.ProfileRecommendation;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.service.GitHubProfileService;
import com.kayque.devatlas.service.LanguageAnalysisService;
import com.kayque.devatlas.service.ProfileAnalysisService;
import com.kayque.devatlas.service.ProfileRecommendationService;
import com.kayque.devatlas.service.RepositoryAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    public HomeController(
            GitHubProfileService gitHubProfileService,
            RepositoryAnalysisService repositoryAnalysisService,
            ProfileAnalysisService profileAnalysisService,
            LanguageAnalysisService languageAnalysisService,
            ProfileRecommendationService profileRecommendationService
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
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/analisar")
    public String analyzeProfile(
            @RequestParam String username,
            Model model
    ) {
        String normalizedUsername =
                username.trim();

        if (normalizedUsername.isEmpty()) {
            return "redirect:/";
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

        return "index";
    }
}