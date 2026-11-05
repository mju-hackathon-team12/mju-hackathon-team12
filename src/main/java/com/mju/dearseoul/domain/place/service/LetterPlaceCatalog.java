package com.mju.dearseoul.domain.place.service;

import com.mju.dearseoul.common.exception.BusinessException;
import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.domain.place.dto.LetterPlace;
import com.mju.dearseoul.domain.place.entity.CatalogPlace;
import com.mju.dearseoul.domain.place.repository.CatalogPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LetterPlaceCatalog {
    private final CatalogPlaceRepository places;

    public LetterPlaceCatalog(CatalogPlaceRepository places) {
        this.places = places;
    }

    public List<LetterPlace> all() {
        return places.findCatalogPlaces();
    }

    public LetterPlace get(String id) {
        if (id == null) throw new BusinessException(ErrorCode.NOT_FOUND, "우체통 장소를 찾을 수 없습니다.");
        return places.findById(id).map(CatalogPlace::toPlace)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "우체통 장소를 찾을 수 없습니다."));
    }

    public boolean contains(String id) {
        return id != null && places.existsById(id);
    }
}
