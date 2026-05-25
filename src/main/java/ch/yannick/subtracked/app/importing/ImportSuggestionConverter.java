// app/importing/ImportSuggestionConverter.java
package ch.yannick.subtracked.app.importing;

import ch.yannick.subtracked.app.importing.dto.ImportSuggestionResponse;
import ch.yannick.subtracked.domain.importing.ImportSuggestion;
import org.springframework.stereotype.Component;

@Component
public class ImportSuggestionConverter {

    public ImportSuggestionResponse convert(ImportSuggestion entity) {
        if (entity == null) return null;

        return new ImportSuggestionResponse(
                entity.getId(),
                entity.getMerchantName(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDetectedCycle(),
                entity.getEstimatedNextRenewal(),
                entity.getOccurrenceCount(),
                entity.getStatus()
        );
    }
}