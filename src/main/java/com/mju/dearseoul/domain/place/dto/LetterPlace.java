package com.mju.dearseoul.domain.place.dto;

public record LetterPlace(String id, String name, String district, double lat, double lon,
                          String info, String src, String mood) {}
