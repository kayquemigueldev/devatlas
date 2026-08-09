package com.kayque.devatlas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubReadmeResponse(

        String name,
        String path,
        int size,
        String encoding,
        String content,

        @JsonProperty("html_url")
        String htmlUrl

) {

        public GitHubReadmeResponse(
                String name,
                String path,
                int size,
                String htmlUrl
        ) {
                this(
                        name,
                        path,
                        size,
                        null,
                        null,
                        htmlUrl
                );
        }
}