package com.mju.dearseoul.domain.content.entity;

import com.mju.dearseoul.domain.content.dto.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "catalog_content", uniqueConstraints = @UniqueConstraint(columnNames = {"source_name", "source_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CatalogContent {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @Column(nullable = false, length = 120)
    private String title;

    private String tag;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Column(name = "ranking_position")
    private Integer ranking;

    @Column(length = 500)
    private String artist;

    @Column(length = 500)
    private String album;

    private String channel;

    @Column(length = 2000)
    private String imageUrl;

    @Lob
    @Column(nullable = false)
    private String sourceJson;

}
