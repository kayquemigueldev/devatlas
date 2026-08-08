package com.kayque.devatlas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepositoryResponse(

        String name,
        String description,

        @JsonProperty("html_url")
        String repositoryUrl,

        String homepage,
        String language,

        @JsonProperty("stargazers_count")
        int stars,

        @JsonProperty("forks_count")
        int forks,

        @JsonProperty("open_issues_count")
        int openIssues,

        boolean fork,
        boolean archived,

        List<String> topics,

        @JsonProperty("created_at")
        Instant createdAt,

        @JsonProperty("updated_at")
        Instant updatedAt,

        @JsonProperty("pushed_at")
        Instant pushedAt

) {
}