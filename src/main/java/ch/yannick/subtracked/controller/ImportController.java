package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.exception.ResourceNotFoundException;
import ch.yannick.subtracked.app.importing.CsvImportService;
import ch.yannick.subtracked.app.importing.dto.ImportSuggestionResponse;
import ch.yannick.subtracked.domain.importing.ImportSuggestion;
import ch.yannick.subtracked.domain.importing.ImportSuggestionRepository;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final CsvImportService csvImportService;
    private final ImportSuggestionRepository suggestionRepository;

    public ImportController(CsvImportService csvImportService,
                            ImportSuggestionRepository suggestionRepository) {
        this.csvImportService = csvImportService;
        this.suggestionRepository = suggestionRepository;
    }

    @PostMapping("/csv")
    public ResponseEntity<List<ImportSuggestionResponse>> uploadCsv(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvImportService.importCsv(user, file));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ImportSuggestionResponse>> getPendingSuggestions(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(csvImportService.getPendingSuggestions(user));
    }

    @PostMapping("/suggestions/{id}/confirm")
    public ResponseEntity<ImportSuggestionResponse> confirmSuggestion(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        ImportSuggestion suggestion = getSuggestionForUser(user, id);
        return ResponseEntity.ok(csvImportService.confirmSuggestion(user, suggestion));
    }

    @PostMapping("/suggestions/{id}/dismiss")
    public ResponseEntity<ImportSuggestionResponse> dismissSuggestion(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        ImportSuggestion suggestion = getSuggestionForUser(user, id);
        return ResponseEntity.ok(csvImportService.dismissSuggestion(suggestion));
    }

    private ImportSuggestion getSuggestionForUser(User user, UUID id) {
        ImportSuggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Suggestion not found: " + id));

        if (!suggestion.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Suggestion not found: " + id);
        }

        return suggestion;
    }
}