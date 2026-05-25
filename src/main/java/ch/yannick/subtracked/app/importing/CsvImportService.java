package ch.yannick.subtracked.app.importing;

import ch.yannick.subtracked.app.importing.dto.CsvTransaction;
import ch.yannick.subtracked.app.importing.dto.ImportSuggestionRequest;
import ch.yannick.subtracked.app.importing.dto.ImportSuggestionResponse;
import ch.yannick.subtracked.domain.importing.ImportSuggestion;
import ch.yannick.subtracked.domain.importing.ImportSuggestionRepository;
import ch.yannick.subtracked.domain.importing.ImportSuggestionStatus;
import ch.yannick.subtracked.domain.subscription.Subscription;
import ch.yannick.subtracked.domain.subscription.SubscriptionRepository;
import ch.yannick.subtracked.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CsvImportService {

    private final CsvParser csvParser;
    private final RecurrenceDetector recurrenceDetector;
    private final ImportSuggestionRepository suggestionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ImportSuggestionConverter converter;

    public CsvImportService(CsvParser csvParser,
                            RecurrenceDetector recurrenceDetector,
                            ImportSuggestionRepository suggestionRepository, SubscriptionRepository subscriptionRepository, ImportSuggestionConverter converter) {
        this.csvParser = csvParser;
        this.recurrenceDetector = recurrenceDetector;
        this.suggestionRepository = suggestionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.converter = converter;
    }

    @Transactional
    public List<ImportSuggestionResponse> importCsv(User user, MultipartFile file) {
        List<CsvTransaction> transactions = csvParser.parse(file);

        List<CsvTransaction> expenses = transactions.stream()
                .filter(CsvTransaction::isExpense)
                .toList();

        List<ImportSuggestionRequest> suggestions =
                recurrenceDetector.detect(expenses);

        suggestions.forEach(suggestion -> saveSuggestion(user, suggestion));

        return suggestionRepository
                .findByUserIdAndStatus(user.getId(), ImportSuggestionStatus.PENDING)
                .stream()
                .map(converter::convert)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ImportSuggestionResponse> getPendingSuggestions(User user) {
        return suggestionRepository
                .findByUserIdAndStatus(user.getId(),
                        ImportSuggestionStatus.PENDING)
                .stream()
                .map(converter::convert)
                .toList();
    }

    private void saveSuggestion(User user, ImportSuggestionRequest suggestion) {
        boolean alreadyExists = suggestionRepository
                .existsByUserIdAndMerchantNameAndStatus(
                        user.getId(),
                        suggestion.merchantName(),
                        ImportSuggestionStatus.PENDING);

        if (alreadyExists) return;

        ImportSuggestion entity = new ImportSuggestion();
        entity.setUser(user);
        entity.setMerchantName(suggestion.merchantName());
        entity.setAmount(suggestion.amount());
        entity.setCurrency(suggestion.currency());
        entity.setDetectedCycle(suggestion.detectedCycle());
        entity.setEstimatedNextRenewal(suggestion.estimatedNextRenewal());
        entity.setOccurrenceCount(suggestion.occurrenceCount());
        suggestionRepository.save(entity);
    }

    @Transactional
    public ImportSuggestionResponse confirmSuggestion(User user,
                                                      ImportSuggestion suggestion) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setName(suggestion.getMerchantName());
        subscription.setAmount(suggestion.getAmount());
        subscription.setCurrency(suggestion.getCurrency());
        subscription.setBillingCycle(suggestion.getDetectedCycle());
        subscription.setNextRenewalDate(suggestion.getEstimatedNextRenewal());
        subscription.setActive(true);
        subscriptionRepository.save(subscription);

        suggestion.setStatus(ImportSuggestionStatus.CONFIRMED);
        suggestionRepository.save(suggestion);

        return converter.convert(suggestion);
    }

    @Transactional
    public ImportSuggestionResponse dismissSuggestion(ImportSuggestion suggestion) {
        suggestion.setStatus(ImportSuggestionStatus.DISMISSED);
        suggestionRepository.save(suggestion);

        return converter.convert(suggestion);
    }
}