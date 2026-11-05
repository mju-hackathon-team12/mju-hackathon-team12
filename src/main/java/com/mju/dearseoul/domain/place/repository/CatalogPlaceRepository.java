package com.mju.dearseoul.domain.place.repository;

import com.mju.dearseoul.domain.place.entity.CatalogPlace;
import com.mju.dearseoul.domain.place.dto.LetterPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CatalogPlaceRepository extends JpaRepository<CatalogPlace, String> {
    @Query("select new com.mju.dearseoul.domain.place.dto.LetterPlace(p.id, p.name, p.district, p.lat, p.lon, p.info, p.src, p.mood) from CatalogPlace p order by p.name, p.id")
    List<LetterPlace> findCatalogPlaces();
    long countBySourceName(String sourceName);
}
