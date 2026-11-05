package com.mju.dearseoul.domain.preference.controller;

import com.mju.dearseoul.domain.preference.dto.PreferenceRequest;
import com.mju.dearseoul.domain.preference.service.PreferenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {
    private final PreferenceService preferences;

    public PreferenceController(PreferenceService preferences) {
        this.preferences = preferences;
    }

    @PostMapping
    public Map<String, String> create(HttpServletRequest request, @RequestBody PreferenceRequest body) {
        return Map.of("preferenceId", preferences.create((String) request.getAttribute("anonymousOwner"), body));
    }
}
