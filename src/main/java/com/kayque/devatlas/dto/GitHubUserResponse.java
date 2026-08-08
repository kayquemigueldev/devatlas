package com.kayque.devatlas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubUserResponse(

        String login,
        String name,

        @JsonProperty("avatar_url")
        String avatarUrl,

        String bio,

        @JsonProperty("public_repos")
        int publicRepos,

        int followers,
        int following,

        @JsonProperty("html_url")
        String profileUrl,

        String location,

        @JsonProperty("created_at")
        Instant createdAt

) {
}