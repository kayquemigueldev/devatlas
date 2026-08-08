package com.kayque.devatlas.controller;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.model.RepositoryAnalysis;
import com.kayque.devatlas.service.GitHubProfileService;
import com.kayque.devatlas.service.RepositoryAnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final GitHubProfileService gitHubProfileService;
    private final RepositoryAnalysisService repositoryAnalysisService;

    public HomeController(
            GitHubProfileService gitHubProfileService,
            RepositoryAnalysisService repositoryAnalysisService
    ) {
        this.gitHubProfileService = gitHubProfileService;
        this.repositoryAnalysisService =
                repositoryAnalysisService;
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
        String normalizedUsername = username.trim();

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
                repositoryAnalysisService.analyze(
                        repositories
                );

        model.addAttribute("username", normalizedUsername);
        model.addAttribute("user", user);
        model.addAttribute(
                "repositoryAnalyses",
                repositoryAnalyses
        );

        return "index";
    }
}