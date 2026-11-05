package com.mju.dearseoul.domain.content.repository;

import com.mju.dearseoul.domain.content.dto.ContentType;
import com.mju.dearseoul.domain.content.dto.ContentItem;
import com.mju.dearseoul.domain.content.entity.CatalogContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CatalogContentRepository extends JpaRepository<CatalogContent, String> {
    @Query("select new com.mju.dearseoul.domain.content.dto.ContentItem(c.id, c.type, c.title, c.tag) from CatalogContent c order by c.type, c.ranking, c.id")
    List<ContentItem> findCatalogItems();
    Optional<CatalogContent> findFirstByTypeAndTitle(ContentType type, String title);
    long countByType(ContentType type);
}
