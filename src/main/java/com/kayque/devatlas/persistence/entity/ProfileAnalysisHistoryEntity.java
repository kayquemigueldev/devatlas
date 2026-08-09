package com.kayque.devatlas.persistence.entity;

import com.kayque.devatlas.model.ProfileAnalysis;
import com.kayque.devatlas.model.RepositoryScoreLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "profile_analysis_history")
public class ProfileAnalysisHistoryEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            length = 100
    )
    private String username;

    @Column(
            name = "overall_score",
            nullable = false
    )
    private int overallScore;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "level",
            nullable = false,
            length = 30
    )
    private RepositoryScoreLevel level;

    @Column(
            name = "total_repositories",
            nullable = false
    )
    private int totalRepositories;

    @Column(
            name = "excellent_repositories",
            nullable = false
    )
    private long excellentRepositories;

    @Column(
            name = "good_repositories",
            nullable = false
    )
    private long goodRepositories;

    @Column(
            name = "developing_repositories",
            nullable = false
    )
    private long developingRepositories;

    @Column(
            name = "strongest_repository",
            length = 255
    )
    private String strongestRepository;

    @Column(
            name = "analyzed_at",
            nullable = false
    )
    private Instant analyzedAt;

    protected ProfileAnalysisHistoryEntity() {
    }

    public ProfileAnalysisHistoryEntity(
            String username,
            ProfileAnalysis profileAnalysis,
            Instant analyzedAt
    ) {
        this.username = username;
        this.overallScore =
                profileAnalysis.overallScore();

        this.level =
                profileAnalysis.level();

        this.totalRepositories =
                profileAnalysis.totalRepositories();

        this.excellentRepositories =
                profileAnalysis.excellentRepositories();

        this.goodRepositories =
                profileAnalysis.goodRepositories();

        this.developingRepositories =
                profileAnalysis.developingRepositories();

        this.strongestRepository =
                profileAnalysis.strongestRepository();

        this.analyzedAt = analyzedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public RepositoryScoreLevel getLevel() {
        return level;
    }

    public int getTotalRepositories() {
        return totalRepositories;
    }

    public long getExcellentRepositories() {
        return excellentRepositories;
    }

    public long getGoodRepositories() {
        return goodRepositories;
    }

    public long getDevelopingRepositories() {
        return developingRepositories;
    }

    public String getStrongestRepository() {
        return strongestRepository;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }
}