package ch.yannick.subtracked.domain.catalogue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogueRepository extends JpaRepository<CatalogueEntry, Long> {

    List<CatalogueEntry> findByNameContainingIgnoreCaseAndActiveTrueOrProviderContainingIgnoreCaseAndActiveTrue(
            String name, String provider
    );
}