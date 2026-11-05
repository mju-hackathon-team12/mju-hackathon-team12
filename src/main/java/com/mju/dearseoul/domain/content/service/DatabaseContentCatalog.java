package com.mju.dearseoul.domain.content.service;

import com.mju.dearseoul.domain.content.dto.ContentItem;
import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.content.entity.CatalogContent;
import com.mju.dearseoul.domain.content.repository.CatalogContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DatabaseContentCatalog implements ContentCatalog {
    private final CatalogContentRepository contents;

    public DatabaseContentCatalog(CatalogContentRepository contents) {
        this.contents = contents;
    }

    @Override
    public List<ContentItem> all() {
        return contents.findCatalogItems();
    }

    @Override
    public String tag(ContentType type, String title) {
        return contents.findFirstByTypeAndTitle(type, title).map(CatalogContent::getTag).orElse("comfort");
    }
}
