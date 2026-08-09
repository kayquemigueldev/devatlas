package com.kayque.devatlas.persistence.repository;

import com.kayque.devatlas.persistence.entity.ProfileAnalysisHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileAnalysisHistoryRepository
        extends JpaRepository<
        ProfileAnalysisHistoryEntity,
        Long
        > {

    List<ProfileAnalysisHistoryEntity>
    findTop10ByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
            String username
    );

    Optional<ProfileAnalysisHistoryEntity>
    findFirstByUsernameIgnoreCaseOrderByAnalyzedAtDesc(
            String username
    );
}