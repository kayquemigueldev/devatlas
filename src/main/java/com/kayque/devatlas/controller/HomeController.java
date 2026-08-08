package com.kayque.devatlas.controller;

import com.kayque.devatlas.client.GitHubClient;
import com.kayque.devatlas.dto.GitHubUserResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final GitHubClient gitHubClient;

    public HomeController(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
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
                gitHubClient.findUser(normalizedUsername);

        model.addAttribute("username", normalizedUsername);
        model.addAttribute("user", user);

        return "index";
    }
}