package ch.yannick.subtracked.controller;

import ch.yannick.subtracked.app.catalogue.CatalogueService;
import ch.yannick.subtracked.app.catalogue.dto.CatalogueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final CatalogueService service;

    public CatalogueController(CatalogueService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CatalogueResponse>> search(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(service.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogueResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}