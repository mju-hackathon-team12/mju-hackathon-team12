package com.mju.dearseoul.domain.content.service;

import com.mju.dearseoul.domain.content.dto.ContentItem;
import com.mju.dearseoul.domain.content.dto.ContentType;
import java.util.List;

/** Content catalog contract shared by the API and preference validation. */
public interface ContentCatalog {
    List<ContentItem> all();

    default boolean temporary() {
        return false;
    }

    default String tag(ContentType type, String title) {
        return all().stream().filter(item -> item.type() == type && item.title().equals(title))
                .map(ContentItem::tag).findFirst().orElse("comfort");
    }
}
