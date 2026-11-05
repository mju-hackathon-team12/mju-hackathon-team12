package com.mju.dearseoul.domain.content.controller;

import com.mju.dearseoul.domain.content.dto.ContentItem;
import com.mju.dearseoul.domain.content.service.ContentCatalog;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentController {
    public record CatalogResponse(boolean temporary, List<ContentItem> items) {}
    private final ContentCatalog catalog;

    public ContentController(ContentCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping
    public CatalogResponse all() {
        return new CatalogResponse(catalog.temporary(), catalog.all());
    }
}
