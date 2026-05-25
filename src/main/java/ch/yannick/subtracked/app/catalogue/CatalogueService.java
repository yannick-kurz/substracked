package ch.yannick.subtracked.app.catalogue;

import ch.yannick.subtracked.app.catalogue.dto.CatalogueResponse;
import ch.yannick.subtracked.app.exception.ResourceNotFoundException;
import ch.yannick.subtracked.domain.catalogue.CatalogueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogueService {

    private final CatalogueConverter converter;
    private final CatalogueRepository repository;

    public CatalogueService(CatalogueConverter converter,
                            CatalogueRepository repository) {
        this.converter = converter;
        this.repository = repository;
    }

    public List<CatalogueResponse> search(String query) {
        if (query == null || query.isBlank() || query.trim().length() < 2) {
            return List.of();
        }

        String trimmed = query.trim();

        return repository
                .findByNameContainingIgnoreCaseAndActiveTrueOrProviderContainingIgnoreCaseAndActiveTrue(
                        trimmed, trimmed)
                .stream()
                .map(converter::convert)
                .toList();
    }

    public CatalogueResponse findById(Long id) {
        return repository.findById(id)
                .map(converter::convert)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catalogue entry not found: " + id));
    }
}
