package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.importing.CsvImportService;
import ch.yannick.subtracked.domain.importing.ImportSuggestionRepository;
import ch.yannick.subtracked.domain.importing.ImportSuggestionStatus;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping("/import")
public class ImportWebController {

    private final CsvImportService csvImportService;
    private final ImportSuggestionRepository suggestionRepository;

    public ImportWebController(CsvImportService csvImportService,
                               ImportSuggestionRepository suggestionRepository) {
        this.csvImportService = csvImportService;
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping
    public String importPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "import");
        model.addAttribute("suggestions",
                suggestionRepository.findByUserIdAndStatus(
                        user.getId(), ImportSuggestionStatus.PENDING));
        return "import";
    }

    @PostMapping("/csv")
    public String uploadCsv(@AuthenticationPrincipal User user,
                            @RequestParam("file") MultipartFile file,
                            Model model) {
        try {
            csvImportService.importCsv(user, file);
        } catch (Exception e) {
            model.addAttribute("currentUser", user);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("activePage", "import");
            return "import";
        }
        return "redirect:/import";
    }

    @PostMapping("/suggestions/{id}/confirm")
    public String confirm(@AuthenticationPrincipal User user,
                          @PathVariable UUID id) {
        suggestionRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .ifPresent(s -> csvImportService.confirmSuggestion(user, s));
        return "redirect:/import";
    }

    @PostMapping("/suggestions/{id}/dismiss")
    public String dismiss(@AuthenticationPrincipal User user,
                          @PathVariable UUID id) {
        suggestionRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .ifPresent(csvImportService::dismissSuggestion);
        return "redirect:/import";
    }
}