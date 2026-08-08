package com.kayque.devatlas.controller;

import com.kayque.devatlas.dto.GitHubRepositoryResponse;
import com.kayque.devatlas.dto.GitHubUserResponse;
import com.kayque.devatlas.service.GitHubProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final GitHubProfileService gitHubProfileService;

    public HomeController(
            GitHubProfileService gitHubProfileService
    ) {
        this.gitHubProfileService = gitHubProfileService;
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

        model.addAttribute("username", normalizedUsername);
        model.addAttribute("user", user);
        model.addAttribute("repositories", repositories);

        return "index";
    }
}