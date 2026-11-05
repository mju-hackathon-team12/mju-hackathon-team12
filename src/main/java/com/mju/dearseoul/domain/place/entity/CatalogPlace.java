package com.mju.dearseoul.domain.place.entity;

import com.mju.dearseoul.domain.place.dto.LetterPlace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "catalog_place", uniqueConstraints = @UniqueConstraint(columnNames = {"source_name", "source_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CatalogPlace {
    @Id
    private String id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false)
    private String district;

    private double lat;
    private double lon;

    @Column(length = 2000)
    private String address;

    private String contentTypeId;

    @Lob
    private String info;

    @Column(length = 2000)
    private String src;

    @Column(length = 2000)
    private String imageUrl;

    @Column(length = 500)
    private String mood;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Lob
    @Column(nullable = false)
    private String sourceJson;

    public LetterPlace toPlace() {
        return new LetterPlace(id, name, district, lat, lon, info, src, mood);
    }
}
