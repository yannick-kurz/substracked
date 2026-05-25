package ch.yannick.subtracked.app.catalogue;

//import ch.yannick.subtracked.app.catalogue.dto.CatalogueRequest;
import ch.yannick.subtracked.app.catalogue.dto.CatalogueResponse;
import ch.yannick.subtracked.domain.catalogue.CatalogueEntry;
//import ch.yannick.subtracked.domain.category.Category;
//import ch.yannick.subtracked.domain.category.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CatalogueConverter {

//    private final CategoryRepository repository;
//
//    public CatalogueConverter(CategoryRepository repository) {
//        this.repository = repository;
//    }

//    public CatalogueEntry toEntity(CatalogueRequest request) {
//        if (request == null) return null;
//
//        CatalogueEntry entry = new CatalogueEntry();
//        updateEntity(entry, request);
//        return entry;
//    }
//
//    public void updateEntity(CatalogueEntry catalogue, CatalogueRequest request) {
//
//        catalogue.setName(request.name());
//        catalogue.setProvider(request.provider());
//        catalogue.setTypicalAmount(request.typicalAmount());
//        catalogue.setCurrency(request.currency());
//        catalogue.setBillingCycle(request.billingCycle());
//
//        if (request.categoryId() != null) {
//            Category category = repository.findById(request.categoryId())
//                    .orElseThrow(() -> new IllegalArgumentException(
//                            "Category not found" + request.categoryId()
//                    ));
//            catalogue.setCategory(category);
//        }
//
//        catalogue.setLogoUrl(request.logoUrl());
//        catalogue.setWebsite(request.website());
//        catalogue.setActive(request.active());
//
//    }

    public CatalogueResponse convert(CatalogueEntry entity) {
        if (entity == null) return null;

        Long categoryId = entity.getCategory() != null
                ? entity.getCategory().getId()
                : null;

        return new CatalogueResponse(
                entity.getId(),
                entity.getName(),
                entity.getProvider(),
                entity.getTypicalAmount(),
                entity.getCurrency(),
                entity.getBillingCycle(),
                categoryId,
                entity.getLogoUrl(),
                entity.getWebsite(),
                entity.isActive()
        );
    }
}
