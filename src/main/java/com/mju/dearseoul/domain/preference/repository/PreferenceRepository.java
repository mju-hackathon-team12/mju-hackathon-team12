package com.mju.dearseoul.domain.preference.repository;

import com.mju.dearseoul.domain.preference.entity.PreferenceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<PreferenceSession, String> {
    Optional<PreferenceSession> findByIdAndOwner(String id, String owner);
}
