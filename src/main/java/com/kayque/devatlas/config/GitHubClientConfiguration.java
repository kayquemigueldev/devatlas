package com.kayque.devatlas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
public class GitHubClientConfiguration {

    private static final String GITHUB_API_URL =
            "https://api.github.com";

    @Bean
    public RestClient githubRestClient(
            RestClient.Builder builder,
            @Value("${github.token:}") String token,
            @Value("${github.client.connect-timeout}")
            Duration connectTimeout,
            @Value("${github.client.read-timeout}")
            Duration readTimeout
    ) {
        HttpClient httpClient =
                HttpClient
                        .newBuilder()
                        .connectTimeout(
                                connectTimeout
                        )
                        .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(
                        httpClient
                );

        requestFactory.setReadTimeout(
                readTimeout
        );

        RestClient.Builder configuredBuilder =
                builder
                        .baseUrl(GITHUB_API_URL)
                        .requestFactory(
                                requestFactory
                        )
                        .defaultHeader(
                                HttpHeaders.ACCEPT,
                                "application/vnd.github+json"
                        )
                        .defaultHeader(
                                HttpHeaders.USER_AGENT,
                                "DevAtlas"
                        )
                        .defaultHeader(
                                "X-GitHub-Api-Version",
                                "2026-03-10"
                        );

        if (!token.isBlank()) {
            configuredBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + token
            );
        }

        return configuredBuilder.build();
    }
}