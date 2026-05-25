package ch.yannick.subtracked.domain.importing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImportSuggestionRepository extends JpaRepository<ImportSuggestion, UUID> {

    List<ImportSuggestion> findByUserIdAndStatus(UUID userId, ImportSuggestionStatus status);

    boolean existsByUserIdAndMerchantNameAndStatus(
            UUID userId,
            String merchantName,
            ImportSuggestionStatus status
    );
}